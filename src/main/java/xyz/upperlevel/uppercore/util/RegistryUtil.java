package xyz.upperlevel.uppercore.util;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.plugin.Plugin;

import java.util.Locale;

public class RegistryUtil {
    public static final String DIVIDER = ":";

    public static String adaptId(String id) {
        return id.toLowerCase(Locale.ENGLISH);
    }

    public static String obtainId(Plugin plugin, String id) {
        return adaptId((plugin.getName() + DIVIDER + id));
    }
}
