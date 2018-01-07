package xyz.upperlevel.uppercore;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

import static java.util.Locale.ENGLISH;

public class Identifier<T> {
    @Getter
    private final Plugin plugin;
    @Getter
    private final String id;
    private final T handle;

    public Identifier(Plugin plugin, String id, T handle) {
        this.plugin = plugin;
        this.id = id.toLowerCase(Locale.ENGLISH);
        this.handle = handle;
    }

    public T get() {
        return handle;
    }

    public String getGlobalId() {
        return getGlobalId(getPlugin(), getId());
    }

    static String getGlobalId(Plugin plugin, String id) {
        return (plugin.getName() + ":" + id).toLowerCase(ENGLISH);
    }
}
