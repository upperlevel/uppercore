package xyz.upperlevel.uppercore;

import lombok.Data;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

import static java.util.Locale.ENGLISH;
import static lombok.AccessLevel.NONE;

@Data
public class Registrable<T> {
    private final Plugin plugin;
    private final String id;
    @Getter(NONE)
    private final T handle;

    public Registrable(Plugin plugin, String id, T handle) {
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
