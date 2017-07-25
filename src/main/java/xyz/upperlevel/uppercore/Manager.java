package xyz.upperlevel.uppercore;

import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Locale.ENGLISH;

public class Manager<R extends Registrable<?>> {
    private final Map<Plugin, Registry<R>> registries = new HashMap<>();
    private final Map<String, R> entries = new HashMap<>();

    // REGISTRY

    public void register(Registry<R> registry) {
        registries.put(registry.getPlugin(), registry);
    }

    public Registry<R> unregister(Plugin plugin) {
        return registries.remove(plugin);
    }

    public Registry<R> get(Plugin plugin) {
        return registries.get(plugin);
    }

    public Collection<Registry<R>> getRegistries() {
        return registries.values();
    }

    // ENTRY

    public void register(R entry) {
        entries.put(entry.getGlobalId(), entry);
    }

    public R unregister(String id) {
        return entries.remove(id.toLowerCase(ENGLISH));
    }

    public R unregister(Plugin plugin, String id) {
        return unregister(Registrable.getGlobalId(plugin, id));
    }

    public R unregister(R entry) {
        return unregister(entry.getPlugin(), entry.getId());
    }

    public R get(String id) {
        return entries.get(id.toLowerCase(ENGLISH));
    }

    public  R get(Plugin plugin, String id) {
        Registry<R> registry = get(plugin);
        if (registry != null)
            return registry.get(id);
        return null;
    }

    public Collection<R> get() {
        return entries.values();
    }
}