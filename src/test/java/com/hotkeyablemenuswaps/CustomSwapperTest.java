package com.hotkeyablemenuswaps;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.hotkeyablemenuswaps.HotkeyableMenuSwapsPlugin.CustomSwap;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import static net.runelite.api.MenuAction.*;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.events.PostMenuSort;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.menuentryswapper.MenuEntrySwapperConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomSwapperTest
{
	@Inject
	HotkeyableMenuSwapsPlugin plugin;

	@Mock
	@Bind
	ConfigManager configManager;

	@Mock
	@Bind
	HotkeyableMenuSwapsConfig config;

	@Mock
	@Bind
	Client client;

	@Mock
	@Bind
	private KeyManager keyManager;

	@Mock
	@Bind
	private MenuEntrySwapperConfig menuEntrySwapperConfig;

	@Getter
	String customSwapConfig = "";

	@Getter
	String customShiftSwapConfig = "";

	@Getter
	String customHideConfig = "";

	void setConfig(String newSwapConfig, String newHideConfig) {
		setConfig(newSwapConfig, newHideConfig, "");
	}

	void setConfig(String newSwapConfig, String newHideConfig, String newShiftSwapConfig) {
		customSwapConfig = newSwapConfig;
		customHideConfig = newHideConfig;
		customShiftSwapConfig = newShiftSwapConfig;
		ConfigChanged configChanged = new ConfigChanged();
		configChanged.setGroup("hotkeyablemenuswaps");
		plugin.onConfigChanged(configChanged);
	}

	@Getter
	MenuEntry[] menuEntries = new MenuEntry[0];

	@Before
	public void setup() {
		Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
		Mockito.when(config.customSwaps()).thenAnswer(a -> getCustomSwapConfig());
		Mockito.when(config.customHides()).thenAnswer(a -> getCustomHideConfig());
		Mockito.when(config.customShiftSwaps()).thenAnswer(a -> getCustomShiftSwapConfig());
		Mockito.when(client.getMenuEntries()).thenAnswer(a -> getMenuEntries());
//		Mockito.when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		Mockito.doAnswer(a -> {
			if ((int) a.getArgument(0) == 2176) {
				return 1;
			} else {
				throw new UnsupportedOperationException();
			}
		}).when(client).getVarbitValue(Mockito.anyInt());
//		Mockito.when(client.isMenuOpen()).thenReturn(false);
		Mockito.doAnswer(a -> {
			menuEntries = a.getArgument(0);
			return null;
		}).when(client).setMenuEntries(Mockito.any());
	}

	@Test
	public void testMatching() {
		Assert.assertTrue(customSwap("sell,sanguinesti*").matches("sell", "sanguinesti staff"));
		Assert.assertTrue(customSwap("sell,sanguinesti*").matches("sell", "sanguinesti staff (empty)"));
		Assert.assertTrue(customSwap("sell,sanguinesti*").matches("sell", "sanguinesti"));
		Assert.assertFalse(customSwap("sell,sanguinesti*").matches("sell", "A sanguinesti"));
		Assert.assertFalse(customSwap("sell,sanguinesti*").matches("sell", "sanguinest"));

		Assert.assertTrue(customSwap("sell,*******").matches("sell", "A sanguinesti"));
		Assert.assertTrue(customSwap("sell,*******").matches("sell", ""));

		Assert.assertTrue(customSwap("sell,***cat***dog***").matches("sell", "catdog"));
		Assert.assertTrue(customSwap("sell,***cat***dog***").matches("sell", "a cat with dog"));
		Assert.assertTrue(customSwap("sell,***cat***dog***").matches("sell", "a cat with dog "));
		Assert.assertTrue(customSwap("sell,*cat*dog*").matches("sell", "a cat with dog "));
		Assert.assertFalse(customSwap("sell,*cat*dog*").matches("sell", "fish"));

		Assert.assertTrue(customSwap("sell,*").matches("sell", "sanguinesti staff"));
		Assert.assertTrue(customSwap("sell,*").matches("sell", "twisted bow"));
		Assert.assertTrue(customSwap("sell,*").matches("sell", "mr mammal"));
		Assert.assertTrue(customSwap("sell,*").matches("sell", ""));

		Assert.assertTrue(customSwap("*,cow*").matches("attack", "cow (level-2)"));
		Assert.assertTrue(customSwap("*,cow*").matches("examine", "cow (level-2)"));
		Assert.assertFalse(customSwap("*,cow*").matches("examine", "goblin (level-2)"));

		Assert.assertTrue(customSwap("*,*(4)").matches("drink", "prayer potion (4)"));
		Assert.assertTrue(customSwap("*,*(4)").matches("drink", "(4)"));
		Assert.assertFalse(customSwap("*,*(4)").matches("drink", "prayer potion (3)"));

		Assert.assertTrue(customSwap("*,*potion*").matches("drink", "prayer potion (3)"));
		Assert.assertTrue(customSwap("*,*potion*").matches("drink", "super restore potion (4)"));
		Assert.assertFalse(customSwap("*,*potion*").matches("drink", "prayer pot (4)"));

		Assert.assertTrue(customSwap("*,PRAYER*(4)").matches("drink", "prayer potion (4)"));
		Assert.assertTrue(customSwap("*,PRAYER*(4)").matches("drink", "prayer pot (4)"));
		Assert.assertFalse(customSwap("*,PRAYER*(4)").matches("drink", "prayer pot (3)"));
		Assert.assertTrue(customSwap("*,PRAYER POTION (*)").matches("drink", "prayer potion (4)"));
		Assert.assertTrue(customSwap("*,PRAYER POTION (*)").matches("drink", "prayer potion (3)"));
		Assert.assertFalse(customSwap("*,PRAYER POTION (*)").matches("drink", "super restore potion (3)"));

		Assert.assertTrue(customSwap("walk here,").matches("walk here", ""));
		Assert.assertFalse(customSwap("walk here,").matches("walk here", "here"));
		Assert.assertTrue(customSwap("walk here").matches("walk here", ""));
		Assert.assertFalse(customSwap("walk here").matches("walk here", "here"));

		Assert.assertTrue(customSwap("take,*,attack,brutal black dragon*").matches("take", "big dragon bones", "attack", "brutal black dragon (level-100)"));
		Assert.assertTrue(customSwap("use,cannonball -> dwarf*").matches("use", "cannonball -> dwarf multicannon"));
	}

	private CustomSwap customSwap(String s)
	{
		return CustomSwap.fromString(s);
	}

	@Test
	public void testRemoveAllOptionsAboveOptionThatMayNotBeLeftClick() {
		setConfig("", "attack,*\nlure,*\npickpocket,*\ntalk-to,*");
		NPC menaphiteThug = generateNpc(new String[]{"Talk-to", "Attack", "Pickpocket", "Lure", "Knock-Out"});
		menuEntries = new MenuEntry[]{
			new TestMenuEntry().setOption("Cancel").setTarget("").setIdentifier(0).setType(CANCEL).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Examine").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(EXAMINE_NPC).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Walk here").setTarget("").setIdentifier(0).setType(WALK).setParam0(371).setParam1(142).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Knock-Out").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_FIFTH_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Lure").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_FOURTH_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Pickpocket").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_THIRD_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Talk-to").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_FIRST_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Attack").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_SECOND_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug)
		};
		plugin.onPostMenuSort(new PostMenuSort());
		Mockito.verify(client, Mockito.never()).setMenuEntries(Mockito.any());
	}

	@Test
	public void testLookupSwapInFriendsListMenu() {
		setConfig("lookup,*", "");
		menuEntries = new MenuEntry[]{
			new TestMenuEntry().setOption("Cancel").setTarget("").setIdentifier(610).setType(CANCEL).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(true).setItemOp(-1).setItemId(-1).setActor(null),
			new TestMenuEntry().setOption("Message").setTarget("<col=ff9040>420�kc</col>").setIdentifier(2).setType(CC_OP).setParam0(39).setParam1(28114955).setForceLeftClick(false).setDeprioritized(true).setItemOp(-1).setItemId(-1)/*.setWidget(28114955)*/.setActor(null),
			new TestMenuEntry().setOption("Delete").setTarget("<col=ff9040>420�kc</col>").setIdentifier(3).setType(CC_OP).setParam0(39).setParam1(28114955).setForceLeftClick(false).setDeprioritized(true).setItemOp(-1).setItemId(-1)/*.setWidget(28114955)*/.setActor(null),
			new TestMenuEntry().setOption("Lookup").setTarget("<col=ff9040>420�kc</col>").setIdentifier(3).setType(RUNELITE).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setActor(null),
			new TestMenuEntry().setOption("Hide notifications").setTarget("<col=ff9040>420�kc</col>").setIdentifier(0).setType(RUNELITE).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setActor(null),
			new TestMenuEntry().setOption("Add Note").setTarget("<col=ff9040>420�kc</col>").setIdentifier(0).setType(RUNELITE).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setActor(null)
		};
		plugin.onPostMenuSort(new PostMenuSort());
		Assert.assertTrue(client.getMenuEntries()[client.getMenuEntries().length - 1].getOption().equals("Lookup"));
	}

	@Test
	public void swapEntriesWhileProtectedEntriesAreInMenu() {
		// TODO figure out priority system.
		setConfig("Knock-Out,*\nLure,*\nPickpocket,*", "");
		NPC menaphiteThug = generateNpc(new String[]{"Talk-to", "Attack", "Pickpocket", "Lure", "Knock-Out"});
		menuEntries = new MenuEntry[]{
			new TestMenuEntry().setOption("Cancel").setTarget("").setIdentifier(0).setType(CANCEL).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Examine").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(EXAMINE_NPC).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Walk here").setTarget("").setIdentifier(0).setType(WALK).setParam0(371).setParam1(142).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Knock-Out").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_FIFTH_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Lure").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_FOURTH_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Pickpocket").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_THIRD_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Talk-to").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_FIRST_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Attack").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_SECOND_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug)
		};
		plugin.onPostMenuSort(new PostMenuSort());
//		Mockito.verify(client, Mockito.calls(1)).setMenuEntries(Mockito.any());
		Assert.assertTrue(client.getMenuEntries()[client.getMenuEntries().length - 1].getOption().equals("Pickpocket"));
	}

	@Test
	public void cannotSwapBlackjackOptions() {
		setConfig("Knock-Out,*\nLure,*", "");
		NPC menaphiteThug = generateNpc(new String[]{"Talk-to", "Attack", "Pickpocket", "Lure", "Knock-Out"});
		menuEntries = new MenuEntry[]{
			new TestMenuEntry().setOption("Cancel").setTarget("").setIdentifier(0).setType(CANCEL).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Examine").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(EXAMINE_NPC).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Walk here").setTarget("").setIdentifier(0).setType(WALK).setParam0(371).setParam1(142).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Knock-Out").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_FIFTH_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Lure").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_FOURTH_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Pickpocket").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_THIRD_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Talk-to").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_FIRST_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug),
			new TestMenuEntry().setOption("Attack").setTarget("<col=ffff00>Menaphite Thug<col=ff00>  (level-55)").setIdentifier(14805).setType(NPC_SECOND_OPTION).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(menaphiteThug)
		};
		plugin.onPostMenuSort(new PostMenuSort());
		Mockito.verify(client, Mockito.never()).setMenuEntries(Mockito.any());
	}

	public NPC generateNpc(String[] actions) {
		NPC mock = Mockito.mock(NPC.class);
		Mockito.when(mock.getTransformedComposition()).thenAnswer(a -> {
			NPCComposition mock1 = Mockito.mock(NPCComposition.class);
			Mockito.when(mock1.getActions()).thenReturn(actions);
			return mock1;
		});
		return mock;
	}

	// TODO test that swaps can still happen while protected menu entries exist.

	@Test
	public void cannotSwapBuildModeFifthOption() {
		setConfig("Remove,*\nBuild,*", "");
		menuEntries = new MenuEntry[]{
			new TestMenuEntry().setOption("Cancel").setTarget("").setIdentifier(0).setType(CANCEL).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Build").setTarget("<col=ffff>Adventure log space").setIdentifier(29141).setType(GAME_OBJECT_FIFTH_OPTION).setParam0(48).setParam1(62).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Examine").setTarget("<col=ffff>Adventure log space").setIdentifier(29141).setType(EXAMINE_OBJECT).setParam0(48).setParam1(62).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Walk here").setTarget("").setIdentifier(0).setType(WALK).setParam0(351).setParam1(89).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null)
		};
		plugin.onPostMenuSort(new PostMenuSort());
		Mockito.verify(client, Mockito.never()).setMenuEntries(Mockito.any());

		setConfig("Remove,*\nBuild,*", "");
		menuEntries = new MenuEntry[]{
			new TestMenuEntry().setOption("Cancel").setTarget("").setIdentifier(0).setType(CANCEL).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Remove").setTarget("<col=ffff>Adventure log space").setIdentifier(29141).setType(GAME_OBJECT_FIFTH_OPTION).setParam0(48).setParam1(62).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Examine").setTarget("<col=ffff>Adventure log space").setIdentifier(29141).setType(EXAMINE_OBJECT).setParam0(48).setParam1(62).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Walk here").setTarget("").setIdentifier(0).setType(WALK).setParam0(351).setParam1(89).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null)
		};
		plugin.onPostMenuSort(new PostMenuSort());
		Mockito.verify(client, Mockito.never()).setMenuEntries(Mockito.any());

		setConfig("", "Burthorpe,*\nTeleport Menu,*\nwalk here\nexamine,*");
		menuEntries = new MenuEntry[]{
			new TestMenuEntry().setOption("Cancel").setTarget("").setIdentifier(16444).setType(CANCEL).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Remove").setTarget("<col=ffff>Ornate Jewellery Box").setIdentifier(29156).setType(GAME_OBJECT_FIFTH_OPTION).setParam0(49).setParam1(57).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Examine").setTarget("<col=ffff>Ornate Jewellery Box").setIdentifier(37524).setType(EXAMINE_OBJECT).setParam0(49).setParam1(57).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Walk here").setTarget("").setIdentifier(0).setType(WALK).setParam0(432).setParam1(265).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Burthorpe").setTarget("<col=ffff>Ornate Jewellery Box").setIdentifier(29156).setType(GAME_OBJECT_THIRD_OPTION).setParam0(49).setParam1(57).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Teleport Menu").setTarget("<col=ffff>Ornate Jewellery Box").setIdentifier(29156).setType(GAME_OBJECT_SECOND_OPTION).setParam0(49).setParam1(57).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null)
		};
		plugin.onPostMenuSort(new PostMenuSort());
		Mockito.verify(client, Mockito.never()).setMenuEntries(Mockito.any());

		setConfig("cancel", "examine,*");
		menuEntries = new MenuEntry[]{
			new TestMenuEntry().setOption("Cancel").setTarget("").setIdentifier(0).setType(CANCEL).setParam0(0).setParam1(0).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Remove").setTarget("<col=ffff>Adventure log space").setIdentifier(29141).setType(GAME_OBJECT_FIFTH_OPTION).setParam0(48).setParam1(62).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Examine").setTarget("<col=ffff>Adventure log space").setIdentifier(29141).setType(EXAMINE_OBJECT).setParam0(48).setParam1(62).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null),
			new TestMenuEntry().setOption("Walk here").setTarget("").setIdentifier(0).setType(WALK).setParam0(351).setParam1(89).setForceLeftClick(false).setDeprioritized(false).setItemOp(-1).setItemId(-1).setWidget(null).setActor(null)
		};
		plugin.onPostMenuSort(new PostMenuSort());
		Assert.assertFalse(client.getMenuEntries()[client.getMenuEntries().length - 1].getOption().equals("Remove"));
	}

}
