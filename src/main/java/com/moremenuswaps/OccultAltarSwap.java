package com.moremenuswaps;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.client.config.Keybind;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
enum OccultAltarSwap {
    VENERATE("venerate", MoreMenuSwapsConfig::getSwapVenerateHotkey),
    STANDARD("standard", MoreMenuSwapsConfig::getSwapStandardHotkey),
    ANCIENT("ancient", MoreMenuSwapsConfig::getSwapAncientHotkey),
    LUNAR("lunar", MoreMenuSwapsConfig::getSwapLunarHotkey),
    ARCEUUS("arceuus", MoreMenuSwapsConfig::getSwapArceuusHotkey),
    ;

    public static OccultAltarSwap getCurrentSpellbookMenuOption(Client client) {
        return values()[client.getVarbitValue(4070) + 1]; // 4070 indicated the player's current spellbook.
    }

    private final String menuOptionName;
    private final Function<MoreMenuSwapsConfig, Keybind> keybindFunction;

    public Keybind getKeybind(MoreMenuSwapsConfig config) {
        return keybindFunction.apply(config);
    }
}
