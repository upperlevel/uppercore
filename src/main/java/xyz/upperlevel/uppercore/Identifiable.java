package xyz.upperlevel.uppercore;

import org.bukkit.plugin.Plugin;

import java.util.Locale;

import static java.util.Locale.ENGLISH;

public interface Identifiable {

    Plugin getPlugin();

    String getId();

    default boolean isIdentifiable() {
        return getPlugin() != null && getId() != null;
    }

    default String getGlobalId() {
        if (!isIdentifiable()) return null;
        return getGlobalId(getPlugin(), getId());
    }

    static String getGlobalId(Plugin plugin, String id) {
        return (plugin.getName() + ":" + id).toLowerCase(ENGLISH);
    }
}
