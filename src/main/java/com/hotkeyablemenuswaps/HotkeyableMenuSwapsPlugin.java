/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Kamiel
 * Copyright (c) 2019, Rami <https://github.com/Rami-J>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.hotkeyablemenuswaps;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Provides;
import lombok.Value;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.google.common.base.Predicates.alwaysTrue;

// TODO when you press a key, then press and release a different key, the original key is no longer used for the keybind.
// Also, modifier keys cannot be activated if they are pressed when the client does not have focus, which is annoying.
// TODO mystery bug where my "T" for tree keybind stopped working entirely.
// wont-fix - when using shift as a hotkey it prevents some other hotkeys (e.g. lowercase "t") from being activated while shift is down.

@PluginDescriptor(
	name = "Hotkeyable Menu Swaps",
	tags = {"entry", "swapper"}
)
public class HotkeyableMenuSwapsPlugin extends Plugin implements KeyListener
{
	@Inject
	private Client client;

	@Inject
	private HotkeyableMenuSwapsConfig config;

	@Inject
	private KeyManager keyManager;

	private BankSwapMode currentBankModeSwap = BankSwapMode.OFF;

	// If a hotkey corresponding to a swap is currently held, these variables will be non-null.
	private OccultAltarSwap hotkeyOccultAltarSwap = null;
	private TreeRingSwap hotkeyTreeRingSwap = null;
	private boolean swapUse = false;

	@Provides
	HotkeyableMenuSwapsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HotkeyableMenuSwapsConfig.class);
	}

	@Override
	protected void startUp() {
		currentBankModeSwap = BankSwapMode.OFF;
		hotkeyOccultAltarSwap = null;
		hotkeyTreeRingSwap = null;

		keyManager.registerKeyListener(this);

		swapContains("venerate", "altar of the occult"::equals, "standard", () -> getCurrentOccultAltarSwap() == OccultAltarSwap.STANDARD);
		swapContains("venerate", "altar of the occult"::equals, "ancient", () -> getCurrentOccultAltarSwap() == OccultAltarSwap.ANCIENT);
		swapContains("venerate", "altar of the occult"::equals, "lunar", () -> getCurrentOccultAltarSwap() == OccultAltarSwap.LUNAR);
		swapContains("venerate", "altar of the occult"::equals, "arceuus", () -> getCurrentOccultAltarSwap() == OccultAltarSwap.ARCEUUS);

		for (String option : new String[]{"last-destination", "zanaris", "configure", "tree"}) {
			swapContains(option, alwaysTrue(), "zanaris", () -> getCurrentTreeRingSwap() == TreeRingSwap.ZANARIS);
			swapContains(option, alwaysTrue(), "last-destination", () -> getCurrentTreeRingSwap() == TreeRingSwap.LAST_DESTINATION);
			swapContains(option, alwaysTrue(), "configure", () -> getCurrentTreeRingSwap() == TreeRingSwap.CONFIGURE);
			swapContains(option, alwaysTrue(), "tree", () -> getCurrentTreeRingSwap() == TreeRingSwap.TREE);
		}
	}

	@Override
	protected void shutDown() {
		keyManager.unregisterKeyListener(this);
	}

	private OccultAltarSwap getCurrentOccultAltarSwap() {
		OccultAltarSwap currentSpellbook = OccultAltarSwap.getCurrentSpellbookMenuOption(client);
		if (hotkeyOccultAltarSwap == null || hotkeyOccultAltarSwap == currentSpellbook) {
			HotkeyableMenuSwapsConfig.OccultAltarLeftClick leftClickSwap = config.getOccultAltarLeftClickSwap();
			return (leftClickSwap.getFirstOption() == currentSpellbook)
					? leftClickSwap.getSecondOption()
					: leftClickSwap.getFirstOption();
		} else {
			return hotkeyOccultAltarSwap;
		}
	}

	private TreeRingSwap getCurrentTreeRingSwap() {
		return hotkeyTreeRingSwap;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// not used.
	}

	private volatile int bankSwapVarbit = 0;

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		int setting = client.getVarbitValue(6590);

		if (bankSwapVarbit != setting)
		{
			bankSwapVarbit = client.getVarbitValue(6590);
		}
	}

	// indexes correspond to values of the varbit that represents the current left-click option.
	private static final BankSwapMode[] bankSwapModes = {
			BankSwapMode.SWAP_1,
			BankSwapMode.SWAP_5,
			BankSwapMode.SWAP_10,
			BankSwapMode.SWAP_X,
			BankSwapMode.SWAP_ALL
	};

	@Override
	public void keyPressed(KeyEvent e)
	{
		// ignoring the current left click option allows one keybind to be used for two different swaps - e.g. the
		// same keybind for both "1" and "all", which makes bank-1 accessible while the left-click option is set
		// to "all" via the vanilla bank interface, without requiring an additional keybind.
		BankSwapMode currentLeftClick = bankSwapModes[bankSwapVarbit];

		for (BankSwapMode swapMode : BankSwapMode.values()) {
			if (swapMode != currentLeftClick && (swapMode.getKeybind(config)).matches(e)) {
				currentBankModeSwap = swapMode;
				break;
			}
		}

		for (OccultAltarSwap altarOption : OccultAltarSwap.values()) {
			if ((altarOption.getKeybind(config)).matches(e)) {
				hotkeyOccultAltarSwap = altarOption;
				break;
			}
		}

		for (TreeRingSwap treeRingSwap : TreeRingSwap.values()) {
			if ((treeRingSwap.getKeybind(config)).matches(e)) {
				hotkeyTreeRingSwap = treeRingSwap;
				break;
			}
		}

		if (config.getSwapUseHotkey().matches(e)) {
			swapUse = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		for (BankSwapMode swapMode : BankSwapMode.values()) {
			if ((swapMode.getKeybind(config)).matches(e) && currentBankModeSwap.equals(swapMode)) {
				currentBankModeSwap = BankSwapMode.OFF;
				break;
			}
		}

		for (OccultAltarSwap altarOption : OccultAltarSwap.values()) {
			if ((altarOption.getKeybind(config)).matches(e) && altarOption.equals(hotkeyOccultAltarSwap)) {
				hotkeyOccultAltarSwap = null;
				break;
			}
		}

		for (TreeRingSwap treeRingSwap : TreeRingSwap.values()) {
			if ((treeRingSwap.getKeybind(config)).matches(e) && treeRingSwap.equals(hotkeyTreeRingSwap)) {
				hotkeyTreeRingSwap = null;
				break;
			}
		}

		if (config.getSwapUseHotkey().matches(e)) {
			swapUse = false;
		}
	}

	@Subscribe
	public void onFocusChanged(FocusChanged event)
	{
		if (!event.isFocused())
		{
			currentBankModeSwap = BankSwapMode.OFF;
		}
	}

	// Copy-pasted from the official runelite menu entry swapper plugin, with some modification.
	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded menuEntryAdded) {
		final int widgetGroupId = WidgetInfo.TO_GROUP(menuEntryAdded.getActionParam1());

		final boolean isDepositBoxPlayerInventory = widgetGroupId == WidgetID.DEPOSIT_BOX_GROUP_ID;
		final boolean isChambersOfXericStorageUnitPlayerInventory = widgetGroupId == WidgetID.CHAMBERS_OF_XERIC_STORAGE_UNIT_INVENTORY_GROUP_ID;
		// Deposit- op 1 is the current withdraw amount 1/5/10/x for deposit box interface and chambers of xeric storage unit.
		// Deposit- op 2 is the current withdraw amount 1/5/10/x for bank interface
		if (currentBankModeSwap != BankSwapMode.OFF && currentBankModeSwap != BankSwapMode.SWAP_ALL_BUT_1
				&& menuEntryAdded.getType() == MenuAction.CC_OP.getId()
				&& menuEntryAdded.getIdentifier() == (isDepositBoxPlayerInventory || isChambersOfXericStorageUnitPlayerInventory ? 1 : 2)
				&& (menuEntryAdded.getOption().startsWith("Deposit-") || menuEntryAdded.getOption().startsWith("Store") || menuEntryAdded.getOption().startsWith("Donate"))) {
			final int opId = isDepositBoxPlayerInventory ? currentBankModeSwap.getDepositIdentifierDepositBox()
					: isChambersOfXericStorageUnitPlayerInventory ? currentBankModeSwap.getDepositIdentifierChambersStorageUnit()
					: currentBankModeSwap.getDepositIdentifier();
			final int actionId = opId >= 6 ? MenuAction.CC_OP_LOW_PRIORITY.getId() : MenuAction.CC_OP.getId();
			bankModeSwap(actionId, opId);
		}

		// Deposit- op 1 is the current withdraw amount 1/5/10/x
		if (currentBankModeSwap != BankSwapMode.OFF && currentBankModeSwap != BankSwapMode.SWAP_EXTRA_OP
				&& menuEntryAdded.getType() == MenuAction.CC_OP.getId() && menuEntryAdded.getIdentifier() == 1
				&& menuEntryAdded.getOption().startsWith("Withdraw")) {
			boolean isChambersStorageUnit = widgetGroupId == WidgetID.CHAMBERS_OF_XERIC_STORAGE_UNIT_PRIVATE_GROUP_ID || widgetGroupId == WidgetID.CHAMBERS_OF_XERIC_STORAGE_UNIT_SHARED_GROUP_ID;
			final int actionId = isChambersStorageUnit ? MenuAction.CC_OP.getId()
					: currentBankModeSwap.getWithdrawMenuAction().getId();
			final int opId = isChambersStorageUnit ? currentBankModeSwap.getWithdrawIdentifierChambersStorageUnit()
					: currentBankModeSwap.getWithdrawIdentifier();
			bankModeSwap(actionId, opId);
		}
	}

	// Copy-pasted from the official runelite menu entry swapper plugin.
	private void bankModeSwap(int entryTypeId, int entryIdentifier)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();

		for (int i = menuEntries.length - 1; i >= 0; --i)
		{
			MenuEntry entry = menuEntries[i];

			if (entry.getType() == entryTypeId && entry.getIdentifier() == entryIdentifier)
			{
				// Raise the priority of the op so it doesn't get sorted later
				entry.setType(MenuAction.CC_OP.getId());

				menuEntries[i] = menuEntries[menuEntries.length - 1];
				menuEntries[menuEntries.length - 1] = entry;

				client.setMenuEntries(menuEntries);
				break;
			}
		}
	}

	// Copy-pasted from the official runelite menu entry swapper plugin.
	private final Multimap<String, Swap> swaps = LinkedHashMultimap.create();
	private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();

	// Copy-pasted from the official runelite menu entry swapper plugin, with some modification.
	@Subscribe(priority = -1) // This will run after the normal menu entry swapper, so it won't interfere with this plugin.
	public void onClientTick(ClientTick clientTick)
	{
		// The menu is not rebuilt when it is open, so don't swap or else it will
		// repeatedly swap entries
		if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen())
		{
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();

		// Build option map for quick lookup in findIndex
		int idx = 0;
		optionIndexes.clear();
		for (MenuEntry entry : menuEntries)
		{
			String option = Text.removeTags(entry.getOption()).toLowerCase();
			optionIndexes.put(option, idx++);
		}

		// Perform swaps
		idx = 0;
		for (MenuEntry entry : menuEntries)
		{
			swapMenuEntry(idx++, entry);
		}
	}

	// Copy-pasted from the official runelite menu entry swapper plugin.
	@Value
	class Swap
	{
		private Predicate<String> optionPredicate;
		private Predicate<String> targetPredicate;
		private String swappedOption;
		private Supplier<Boolean> enabled;
		private boolean strict;
	}

	// Copy-pasted from the official runelite menu entry swapper plugin, with some modification.
	private void swapMenuEntry(int index, MenuEntry menuEntry)
	{
		String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
		String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();

		// disgusting. But there isn't an easy way to implement some kind of custom predicate function or regex for the menu entry option without rewriting a lot of code.
		if (target.equals("fairy ring") || target.equals("spiritual fairy tree")) {
			if (option.startsWith("ring-")) {
				option = option.substring("ring-".length());
			}
			if (option.startsWith("last-destination")) {
				option = "last-destination";
			}
		}

		MenuAction menuAction = MenuAction.of(menuEntry.getType());
		if (menuAction == MenuAction.ITEM_FIRST_OPTION
				|| menuAction == MenuAction.ITEM_SECOND_OPTION
				|| menuAction == MenuAction.ITEM_THIRD_OPTION
				|| menuAction == MenuAction.ITEM_FOURTH_OPTION
				|| menuAction == MenuAction.ITEM_FIFTH_OPTION
				|| menuAction == MenuAction.ITEM_USE)
		{
			// Special case use shift click due to items not actually containing a "Use" option, making
			// the client unable to perform the swap itself.
			if (swapUse && !option.equals("use"))
			{
				swap("use", target, index, true);
			}

			// don't perform swaps on items when shift is held; instead prefer the client menu swap, which
			// we may have overwrote
			return;
		}

		Collection<Swap> swaps = this.swaps.get(option);
		for (Swap swap : swaps)
		{
			if (swap.getTargetPredicate().test(target) && swap.getEnabled().get())
			{
				if (swap(swap.getSwappedOption(), target, index, swap.isStrict()))
				{
					break;
				}
			}
		}
	}

	// Copy-pasted from the official runelite menu entry swapper plugin.
	private boolean swap(String option, String target, int index, boolean strict)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();

		// find option to swap with
		int optionIdx = findIndex(menuEntries, index, option, target, strict);

		if (optionIdx >= 0)
		{
			swap(optionIndexes, menuEntries, optionIdx, index);
			return true;
		}

		return false;
	}

	// Copy-pasted from the official runelite menu entry swapper plugin.
	private int findIndex(MenuEntry[] entries, int limit, String option, String target, boolean strict)
	{
		if (strict)
		{
			List<Integer> indexes = optionIndexes.get(option.toLowerCase());

			// We want the last index which matches the target, as that is what is top-most
			// on the menu
			for (int i = indexes.size() - 1; i >= 0; --i)
			{
				int idx = indexes.get(i);
				MenuEntry entry = entries[idx];
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				// Limit to the last index which is prior to the current entry
				if (idx < limit && entryTarget.equals(target))
				{
					return idx;
				}
			}
		}
		else
		{
			// Without strict matching we have to iterate all entries up to the current limit...
			for (int i = limit - 1; i >= 0; i--)
			{
				MenuEntry entry = entries[i];
				String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
				{
					return i;
				}
			}

		}

		return -1;
	}

	// Copy-pasted from the official runelite menu entry swapper plugin.
	private void swap(ArrayListMultimap<String, Integer> optionIndexes, MenuEntry[] entries, int index1, int index2)
	{
		MenuEntry entry1 = entries[index1],
				entry2 = entries[index2];

		entries[index1] = entry2;
		entries[index2] = entry1;

		client.setMenuEntries(entries);

		// Update optionIndexes
		String option1 = Text.removeTags(entry1.getOption()).toLowerCase(),
				option2 = Text.removeTags(entry2.getOption()).toLowerCase();

		List<Integer> list1 = optionIndexes.get(option1),
				list2 = optionIndexes.get(option2);

		// call remove(Object) instead of remove(int)
		list1.remove((Integer) index1);
		list2.remove((Integer) index2);

		sortedInsert(list1, index2);
		sortedInsert(list2, index1);
	}

	// Copy-pasted from the official runelite menu entry swapper plugin.
	private static <T extends Comparable<? super T>> void sortedInsert(List<T> list, T value) // NOPMD: UnusedPrivateMethod: false positive
	{
		int idx = Collections.binarySearch(list, value);
		list.add(idx < 0 ? -idx - 1 : idx, value);
	}

	// Copy-pasted from the official runelite menu entry swapper plugin.
	private void swapContains(String option, Predicate<String> targetPredicate, String swappedOption, Supplier<Boolean> enabled)
	{
		swaps.put(option, new Swap(alwaysTrue(), targetPredicate, swappedOption, enabled, false));
	}
}
