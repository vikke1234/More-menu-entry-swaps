package com.hotkeyablemenuswaps;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

import static com.hotkeyablemenuswaps.OccultAltarSwap.*;

@ConfigGroup("hotkeyablemenuswaps")
public interface HotkeyableMenuSwapsConfig extends Config
{
	@ConfigSection(
			name = "Bank",
			description = "All options that swap entries in the bank",
			position = 0
	)
	String bankSection = "bank";

	@ConfigItem(
			keyName = "bankSwap1Hotkey",
			name = "Bank 1",
			description = "The hotkey which, when held, swaps the bank's withdraw/deposit 1 option",
			section = bankSection,
			position = 0
	)
	default Keybind getBankSwap1Hotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "bankSwap5Hotkey",
			name = "Bank 5",
			description = "The hotkey which, when held, swaps the bank's withdraw/deposit 5 option",
			section = bankSection,
			position = 1
	)
	default Keybind getBankSwap5Hotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "bankSwap10Hotkey",
			name = "Bank 10",
			description = "The hotkey which, when held, swaps the bank's withdraw/deposit 10 option",
			section = bankSection,
			position = 2
	)
	default Keybind getBankSwap10Hotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "bankSwapXHotkey",
			name = "Bank X",
			description = "The hotkey which, when held, swaps the bank's withdraw/deposit X option",
			section = bankSection,
			position = 3
	)
	default Keybind getBankSwapXHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "bankSwapSetXHotkey",
			name = "Bank Set-X",
			description = "The hotkey which, when held, swaps the bank's withdraw/deposit SetX option",
			section = bankSection,
			position = 4
	)
	default Keybind getBankSwapSetXHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "bankSwapAllHotkey",
			name = "Bank All",
			description = "The hotkey which, when held, swaps the bank's withdraw/deposit All option",
			section = bankSection,
			position = 5
	)
	default Keybind getBankSwapAllHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "bankSwapAllBut1Hotkey",
			name = "Withdraw All-But-1",
			description = "The hotkey which, when held, swaps the bank's withdraw/deposit AllBut1 option",
			section = bankSection,
			position = 6
	)
	default Keybind getBankSwapAllBut1Hotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "bankSwapExtraOpHotkey",
			name = "Eat/Wield/Etc.",
			description = "The hotkey which, when held, swaps the bank's Eat/Wield/etc. option",
			section = bankSection,
			position = 7
	)
	default Keybind getBankSwapExtraOpHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigSection(
			name = "Spirit tree/Fairy ring",
			description = "All options that swap entries on a spirit tree or fairy ring",
			position = 1
	)
	String treeRingSection = "treeRing";

	@ConfigItem(
			keyName = "swapZanaris",
			name = "Zanaris",
			description = "The hotkey which swaps \"Zanaris\"",
			section = treeRingSection,
			position = 0
	)
	default Keybind getSwapZanarisHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "swapTree",
			name = "Tree",
			description = "The hotkey which swaps \"Tree\"",
			section = treeRingSection,
			position = 1
	)
	default Keybind getSwapTreeHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "swapConfigure",
			name = "Configure",
			description = "The hotkey which swaps \"Configure\"",
			section = treeRingSection,
			position = 2
	)
	default Keybind getSwapConfigureHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "swapLastDestination",
			name = "Last-destination",
			description = "The hotkey which swaps \"Last-destination\"",
			section = treeRingSection,
			position = 3
	)
	default Keybind getSwapLastDestinationHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigSection(
			name = "Occult Altar",
			description = "Occult altar menu entry swaps",
			position = 2
	)
	String occultAltarSection = "occultAltar";

	@Getter
	@RequiredArgsConstructor
	enum OccultAltarLeftClick {
		OFF(VENERATE, VENERATE),

		STANDARD_VENERATE(STANDARD, VENERATE),
		STANDARD_ANCIENT(STANDARD, ANCIENT),
		STANDARD_LUNAR(STANDARD, LUNAR),
		STANDARD_ARCEUUS(STANDARD, ARCEUUS),

		ANCIENT_VENERATE(ANCIENT, VENERATE),
		ANCIENT_STANDARD(ANCIENT, STANDARD),
		ANCIENT_LUNAR(ANCIENT, LUNAR),
		ANCIENT_ARCEUUS(ANCIENT, ARCEUUS),

		LUNAR_VENERATE(LUNAR, VENERATE),
		LUNAR_STANDARD(LUNAR, STANDARD),
		LUNAR_ANCIENT(LUNAR, ANCIENT),
		LUNAR_ARCEUUS(LUNAR, ARCEUUS),

		ARCEUUS_VENERATE(ARCEUUS, VENERATE),
		ARCEUUS_STANDARD(ARCEUUS, STANDARD),
		ARCEUUS_ANCIENT(ARCEUUS, ANCIENT),
		ARCEUUS_LUNAR(ARCEUUS, LUNAR),
		;

		private final OccultAltarSwap firstOption;
		private final OccultAltarSwap secondOption;
	}

	@ConfigItem(
			keyName = "occultAltarLeftClick",
			name = "Left click",
			description = "The occult altar's left-click option. The second option is used if you are already on the spellbook of the first option",
			section = occultAltarSection,
			position = 0
	)
	default OccultAltarLeftClick getOccultAltarLeftClickSwap()
	{
		return OccultAltarLeftClick.OFF;
	}

	@ConfigItem(
			keyName = "swapVenerateHotkey",
			name = "Venerate",
			description = "The hotkey which swaps \"Venerate\"",
			section = occultAltarSection,
			position = 1
	)
	default Keybind getSwapVenerateHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "swapStandardHotkey",
			name = "Standard",
			description = "The hotkey which swaps \"Standard\"",
			section = occultAltarSection,
			position = 2
	)
	default Keybind getSwapStandardHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "swapAncientHotkey",
			name = "Ancient",
			description = "The hotkey which swaps \"Ancient\"",
			section = occultAltarSection,
			position = 3
	)
	default Keybind getSwapAncientHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "swapLunarHotkey",
			name = "Lunar",
			description = "The hotkey which swaps \"Lunar\"",
			section = occultAltarSection,
			position = 4
	)
	default Keybind getSwapLunarHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "swapArceuusHotkey",
			name = "Arceuus",
			description = "The hotkey which swaps \"Arceuus\"",
			section = occultAltarSection,
			position = 5
	)
	default Keybind getSwapArceuusHotkey()
	{
		return Keybind.NOT_SET;
	}

}
