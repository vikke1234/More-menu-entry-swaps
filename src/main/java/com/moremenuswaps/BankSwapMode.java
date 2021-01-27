package com.moremenuswaps;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.MenuAction;
import net.runelite.client.config.Keybind;
import net.runelite.client.plugins.menuentryswapper.ShiftDepositMode;
import net.runelite.client.plugins.menuentryswapper.ShiftWithdrawMode;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
enum BankSwapMode {
    OFF(ShiftDepositMode.OFF, ShiftWithdrawMode.OFF, config -> Keybind.NOT_SET),
    SWAP_1(ShiftDepositMode.DEPOSIT_1, ShiftWithdrawMode.WITHDRAW_1, MoreMenuSwapsConfig::getBankSwap1Hotkey),
    SWAP_5(ShiftDepositMode.DEPOSIT_5, ShiftWithdrawMode.WITHDRAW_5, MoreMenuSwapsConfig::getBankSwap5Hotkey),
    SWAP_10(ShiftDepositMode.DEPOSIT_10, ShiftWithdrawMode.WITHDRAW_10, MoreMenuSwapsConfig::getBankSwap10Hotkey),
    SWAP_X(ShiftDepositMode.DEPOSIT_X, ShiftWithdrawMode.WITHDRAW_X, MoreMenuSwapsConfig::getBankSwapXHotkey),
    SWAP_SET_X(7, 6, 5, MenuAction.CC_OP_LOW_PRIORITY, 6, 5, MoreMenuSwapsConfig::getBankSwapSetXHotkey),
    SWAP_ALL(ShiftDepositMode.DEPOSIT_ALL, ShiftWithdrawMode.WITHDRAW_ALL, MoreMenuSwapsConfig::getBankSwapAllHotkey),
    SWAP_ALL_BUT_1(-1, -1, -1, ShiftWithdrawMode.WITHDRAW_ALL_BUT_1.getMenuAction(), ShiftWithdrawMode.WITHDRAW_ALL_BUT_1.getIdentifier(), ShiftWithdrawMode.WITHDRAW_ALL_BUT_1.getIdentifierChambersStorageUnit(), MoreMenuSwapsConfig::getBankSwapAllBut1Hotkey),
    SWAP_EXTRA_OP(ShiftDepositMode.EXTRA_OP.getIdentifier(), ShiftDepositMode.EXTRA_OP.getIdentifierDepositBox(), ShiftDepositMode.EXTRA_OP.getIdentifierChambersStorageUnit(), null, -1, -1, MoreMenuSwapsConfig::getBankSwapExtraOpHotkey),
    ;

    private final int depositIdentifier;
    private final int depositIdentifierDepositBox;
    private final int depositIdentifierChambersStorageUnit;

    private final MenuAction withdrawMenuAction;
    private final int withdrawIdentifier;
    private final int withdrawIdentifierChambersStorageUnit;

    private final Function<MoreMenuSwapsConfig, Keybind> keybindFunction;

    BankSwapMode(ShiftDepositMode depositMode, ShiftWithdrawMode withdrawMode, Function<MoreMenuSwapsConfig, Keybind> keybindFunction) {
        depositIdentifier = depositMode.getIdentifier();
        depositIdentifierDepositBox = depositMode.getIdentifierDepositBox();
        depositIdentifierChambersStorageUnit = depositMode.getIdentifierChambersStorageUnit();
        withdrawMenuAction = withdrawMode.getMenuAction();
        withdrawIdentifier = withdrawMode.getIdentifier();
        withdrawIdentifierChambersStorageUnit = withdrawMode.getIdentifierChambersStorageUnit();
        this.keybindFunction = keybindFunction;
    }

    public Keybind getKeybind(MoreMenuSwapsConfig config) {
        return keybindFunction.apply(config);
    }
}
