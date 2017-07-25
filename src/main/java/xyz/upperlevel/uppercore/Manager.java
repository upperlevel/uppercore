package xyz.upperlevel.uppercore;

import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Locale.ENGLISH;

public class Manager<T> {
    private final Map<Plugin, Registry<T>> registries = new HashMap<>();
    private final Map<String, Identifier<T>> entries = new HashMap<>();

    public void register(Registry<T> registry) {
        registries.put(registry.getPlugin(), registry);
    }

    public Registry<T> unregister(Plugin plugin) {
        return registries.remove(plugin);
    }

    public void register(Identifier<T> entry) {
        entries.put(entry.getGlobalId(), entry);
    }

    public Identifier<T> unregister(String id) {
        return entries.remove(id.toLowerCase(ENGLISH));
    }

    public Registry<T> get(Plugin plugin) {
        return registries.get(plugin);
    }

    public Identifier<T> get(String id) {
        return entries.get(id.toLowerCase(ENGLISH));
    }

    public Identifier<T> get(Plugin plugin, String id) {
        Registry<T> registry = get(plugin);
        if (registry != null)
            return registry.get(id);
        return null;
    }

    public Collection<Registry<T>> getRegistries() {
        return registries.values();
    }

    public Collection<Identifier<T>> get() {
        return entries.values();
    }
}