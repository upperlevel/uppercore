package xyz.upperlevel.uppercore.registry;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.registry.RegistryVisitor.VisitResult;

import java.util.*;
import java.util.stream.Collectors;

public class RegistryRoot {
    public static final char PLUGIN_PATH_DIVIDER = '@';
    private Map<Plugin, Registry<?>> pluginRoots = new HashMap<>();
    private Map<String, Registry<?>> rootsByName = new HashMap<>();
    private Map<Class<?>, List<Registry<?>>> registriesByType = new HashMap<>();

    public Registry<?> register(Plugin plugin) {
        return register(plugin, null);
    }

    public <T> Registry<T> register(Plugin plugin, Class<T> type) {
        Registry<T> pluginRoot = new Registry<>(this, type, plugin.getName().toLowerCase(), null);
        pluginRoot.setPlugin(plugin);
        boolean conflict = rootsByName.putIfAbsent(pluginRoot.getName(), pluginRoot) != null;
        // Check if there are name conflicts
        if (conflict) {
            // Check whether the conflict is made by the same plugin or another one with a similar name (case-insensitive)
            if (pluginRoots.containsKey(plugin)) {
                throw new IllegalArgumentException("Plugin already registered");
            } else {
                throw new IllegalStateException("Name conflict detected: multiple plugins are called '"
                        + pluginRoot.getName() + "' (case-insensitive)");
            }
        }
        // No conflicts occurred
        // Register the root by the plugin and return it
        pluginRoots.put(plugin, pluginRoot);
        return pluginRoot;
    }

    public Registry get(Plugin plugin) {
        return pluginRoots.get(plugin);
    }

    public Map<Plugin, Registry> getChildren() {
        return Collections.unmodifiableMap(pluginRoots);
    }

    public Map<Class<?>, List<Registry<?>>> getChildrenByType() {
        return Collections.unmodifiableMap(registriesByType);
    }

    public Object find(String location) {
        int divider = location.indexOf(PLUGIN_PATH_DIVIDER);
        if (divider == -1) {
            throw new IllegalArgumentException("Cannot find divider, check that the location is in format 'plugin" + PLUGIN_PATH_DIVIDER + "path'");
        }
        String plugin = location.substring(0, divider);
        String path = location.substring(divider + 1);
        return find(plugin, path);
    }

    public Object find(String plugin, String path) {
        Registry pluginRoot = rootsByName.get(plugin);
        if (pluginRoot == null) {
            throw new IllegalArgumentException("Cannot find plugin '" + plugin + "'");
        }
        return pluginRoot.find(path);
    }

    public VisitResult visit(RegistryVisitor visitor) {
        for (Registry<?> reg : pluginRoots.values()) {
            VisitResult res = reg.visit(visitor);
            if (res == VisitResult.TERMINATE) return VisitResult.TERMINATE;
        }
        return VisitResult.CONTINUE;
    }

    public void onChildCreate(Registry<?> child) {
        if (child.getRoot() != this) throw new IllegalArgumentException("Wrong root");
        List<Registry<?>> sameTypeRegistries = registriesByType.computeIfAbsent(child.getType(), (a) -> new ArrayList<>());
        if (!sameTypeRegistries.contains(child)) {
            sameTypeRegistries.add(child);
        }
    }

    @Override
    public String toString() {
        return "{" +
                rootsByName.entrySet().stream()
                        .map(c -> c.getKey() + "=" + c.getValue())
                        .collect(Collectors.joining(",")) +
                "}";
    }
}
