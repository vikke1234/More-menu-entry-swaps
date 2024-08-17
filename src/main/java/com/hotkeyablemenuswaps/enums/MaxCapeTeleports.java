package com.hotkeyablemenuswaps.enums;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.runelite.api.widgets.ComponentID;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum MaxCapeTeleports {
    // Normal usage
    WEAR("Wear", "Wear", 0),
    USE("Use", "Use", 0),
    EXAMINE("Examine", "Examine", 0),
    DROP("Drop", "Drop", 0),
    CANCEL("Cancel", "Cancel", 0),
    TELEPORTS("Teleports", "Teleports", 0),

    // Teleports
    WARRIORS_GUILD("Warrior's Guild", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 0),
    FISHING_GUILD("Fishing Guild", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 1),
    CRAFTING_GUILD("Crafting Guild", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 2),
    FARMING_GUILD("Farming Guild", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 3),
    OTTOS_GROTTO("Otto's Grotto", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 4),
    RED_CHINS("Red Chinchompas", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 5, 1),
    BLACK_CHINS("Black Chinchompas", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 5, 2),
    HUNTER_GUILD("Hunter Guild", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 5, 3),
    HOME("Home", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 0),
    RIMMINGTON("Rimmington", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 1),
    TAVERLEY("Taverley", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 2),
    POLLNIVNEACH("Pollnivneach", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 3),
    HOSIDIUS("Hosidius", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 4),
    RELLEKA("Rellekka", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 5),
    BRIMHAVEN("Brimhaven", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 6),
    YANILLE("Yanille", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 7),
    PRIFDDINAS("Prifddinas", "Teleports", ComponentID.ADVENTURE_LOG_OPTIONS, 6, 8),

    // Features
    PESTLE_AND_MORTAR("Pestle and Mortar", "Features", ComponentID.DIALOG_OPTION_OPTIONS, 1, 1),
    MITH_GRAPPLE("Mithril Grapple & Cross", "Features", ComponentID.DIALOG_OPTION_OPTIONS, 1, 2),
    SPELLBOOK("Spellbook", "Features", ComponentID.DIALOG_OPTION_OPTIONS, 4),
    STAMINA_BOOST("Stamina Boost", "Features", ComponentID.DIALOG_OPTION_OPTIONS, 5),
    ;

    static final Map<String, MaxCapeTeleports> mapper;
    final String name;

    @Getter
    final String option;

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

    MaxCapeTeleports(String name, String option, int id, int ...ops)
    {
        this.name = name;
        this.option = option;
        this.id = id;
        this.ops = new int[ops.length];
        System.arraycopy(ops, 0, this.ops, 0, ops.length);
    }

    public static List<MaxCapeTeleports> getTeleports(Pattern pattern) {
        List<MaxCapeTeleports> features = mapper.entrySet().stream()
                .filter(entry -> pattern.matcher(entry.getKey()).matches())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        return features;
    }

    @Override
    public String toString()
    {
        return name;
    }
}