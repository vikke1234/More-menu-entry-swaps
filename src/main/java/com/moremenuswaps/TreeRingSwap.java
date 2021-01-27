package com.moremenuswaps;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.Keybind;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
enum TreeRingSwap {
    TREE("tree", MoreMenuSwapsConfig::getSwapTreeHotkey),
    ZANARIS("zanaris", MoreMenuSwapsConfig::getSwapZanarisHotkey),
    LAST_DESTINATION("last-destination", MoreMenuSwapsConfig::getSwapLastDestinationHotkey),
    CONFIGURE("configure", MoreMenuSwapsConfig::getSwapConfigureHotkey),
    ;

    private final String menuOptionName;
    private final Function<MoreMenuSwapsConfig, Keybind> keybindFunction;

    public Keybind getKeybind(MoreMenuSwapsConfig config) {
        return keybindFunction.apply(config);
    }
}
