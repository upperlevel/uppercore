package xyz.upperlevel.uppercore;

import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Locale.ENGLISH;

public class Manager<T extends Identifier<?>> {
    private final Map<Plugin, Registry<T>> registries = new HashMap<>();
    private final Map<String, T> entries = new HashMap<>();

    // REGISTRY

    public void register(Registry<T> registry) {
        registries.put(registry.getPlugin(), registry);
    }

    public Registry<T> unregister(Plugin plugin) {
        return registries.remove(plugin);
    }

    public Registry<T> get(Plugin plugin) {
        return registries.get(plugin);
    }

    public Map<String, T> getEntries() {
        return Collections.unmodifiableMap(entries);
    }

    public Collection<Registry<T>> getRegistries() {
        return registries.values();
    }

    // ENTRY

    public void register(T entry) {
        entries.put(entry.getGlobalId(), entry);
    }

    public T unregister(String id) {
        return entries.remove(id.toLowerCase(ENGLISH));
    }

    public T unregister(Plugin plugin, String id) {
        return unregister(Identifier.getGlobalId(plugin, id));
    }

    public T unregister(T entry) {
        return unregister(entry.getPlugin(), entry.getId());
    }

    public T get(String id) {
        return entries.get(id.toLowerCase(ENGLISH));
    }

    public T get(Plugin plugin, String id) {
        Registry<T> registry = get(plugin);
        if (registry != null)
            return registry.get(id);
        return null;
    }

    public Collection<T> get() {
        return entries.values();
    }
}