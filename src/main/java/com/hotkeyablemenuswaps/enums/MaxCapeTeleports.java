package com.hotkeyablemenuswaps.enums;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.runelite.api.widgets.ComponentID;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public enum MaxCapeTeleports {
    WARRIORS_GUILD("Warrior's Guild", ComponentID.ADVENTURE_LOG_OPTIONS, 0),
    FISHING_GUILD("Fishing Guild", ComponentID.ADVENTURE_LOG_OPTIONS, 1),
    CRAFTING_GUILD("Crafting Guild", ComponentID.ADVENTURE_LOG_OPTIONS, 2),
    FARMING_GUILD("Farming Guild", ComponentID.ADVENTURE_LOG_OPTIONS, 3),
    OTTOS_GROTTO("Otto's Grotto", ComponentID.ADVENTURE_LOG_OPTIONS, 4),
    RED_CHINS("Red Chinchompas", ComponentID.ADVENTURE_LOG_OPTIONS, 5, 1),
    BLACK_CHINS("Black Chinchompas", ComponentID.ADVENTURE_LOG_OPTIONS, 5, 2),
    HUNTER_GUILD("Hunter Guild", ComponentID.ADVENTURE_LOG_OPTIONS, 5, 3),
    HOME("Home", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 0),
    RIMMINGTON("Rimmington", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 1),
    TAVERLEY("Taverley", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 2),
    POLLNIVNEACH("Pollnivneach", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 3),
    HOSIDIUS("Hosidius", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 4),
    RELLEKA("Rellekka", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 5),
    BRIMHAVEN("Brimhaven", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 6),
    YANILLE("Yanille", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 7),
    PRIFDDINAS("Prifddinas", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 8),
    ;

    static Map<String, MaxCapeTeleports> mapper;
    final String name;

    @Getter
    final int id;

    @Getter
    final int []ops;

    static {
        Map<String, MaxCapeTeleports> map = new HashMap<>();
        for (MaxCapeTeleports tele : values()) {
            map.put(tele.toString().toLowerCase(), tele);
        }
        mapper = Collections.unmodifiableMap(map);
    }

    MaxCapeTeleports(String name, int id, int ...ops)
    {
        this.name = name;
        this.id = id;
        this.ops = new int[ops.length];
        System.arraycopy(ops, 0, this.ops, 0, ops.length);
    }

    public static boolean isValid(String name) {
        return mapper.containsKey(name);
    }

    public static MaxCapeTeleports getTeleport(String name) {
        return mapper.get(name);
    }

    @Override
    public String toString()
    {
        return name;
    }
}