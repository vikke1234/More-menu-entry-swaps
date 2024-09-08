package com.hotkeyablemenuswaps.enums;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.runelite.api.widgets.ComponentID;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum MaxCapeTeleports {
    // Features, TODO: specific spellbook
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