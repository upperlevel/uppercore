package xyz.upperlevel.uppercore.registry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;
import xyz.upperlevel.uppercore.registry.RegistryVisitor.VisitResult;
import xyz.upperlevel.uppercore.util.CollectionUtil;
import xyz.upperlevel.uppercore.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public class Registry<T> {
    @Getter
    private final RegistryRoot root;

    @Getter
    private final Class<?> type;

    @Getter
    private final String name;

    @Getter
    private final Registry<?> parent;

    @Getter
    private Plugin plugin = null;

    private Map<String, Child> children = new HashMap<>();

    public Registry(@NonNull RegistryRoot root, Class<?> type, @NonNull String name, Registry<?> parent) {
        this.root = root;
        this.type = type;
        this.name = name;
        this.parent = parent;
        if (type != null) {
            root.onChildCreate(this);// Update type registers
        }
        if (parent != null) {
            plugin = parent.getPlugin();
        }
    }

    public Registry(Class<?> type, String name, Registry parent) {
        this(parent.getRoot(), type, name, parent);
    }

    public Registry(RegistryRoot root, Class<?> type, String name) {
        this(root, type, name, null);
    }

    public boolean isFolder() {
        return type == null;
    }

    public void  setPlugin(@NonNull Plugin plugin) {
        if (this.plugin != null) {
            throw new IllegalStateException();
        }
        this.plugin = plugin;
    }

    public Registry<?> registerChild(@NonNull String registryName) {
        return registerChild(registryName, null);
    }

    public <O> Registry<O> registerChild(@NonNull String registryName, Class<O> type) {
        registryName = registryName.toLowerCase();
        Registry<O> child = new Registry<>(root, type, registryName, this);
        Child entry = new Child(false, child);
        boolean conflict = children.putIfAbsent(registryName, entry) != null;

        if (conflict) {
            throw new IllegalArgumentException("Child with name '" + registryName + "' already present");
        }
        return child;
    }

    public void registerChild(@NonNull Registry<?> registry) {
        if (registry.parent != this) {
            throw new IllegalArgumentException("registry parent is not this");
        }
        Child entry = new Child(false, registry);
        boolean conflict = children.putIfAbsent(registry.name, entry) != null;

        if (conflict) {
            throw new IllegalArgumentException("Child with name '" + registry.name + "' already present");
        }
    }

    public void register(@NonNull String name, @NonNull T object) {
        if (isFolder()) throw new IllegalStateException("Cannot register object in a folder registry (" + getPath() + ")");
        name = name.toLowerCase();
        Child entry = new Child(true, object);
        boolean conflict = children.putIfAbsent(name, entry) != null;
        if (conflict) {
            throw new IllegalArgumentException("Entry with name '" + name + "' already present");
        }
    }

    public void register(String id, Reader in, RegistryLoader<? extends T> loader) {
        T object = loader.load(this, id, in);
        register(id, object);
    }

    public void registerFile(File file, RegistryLoader<? extends T> loader) {
        String id = FileUtil.getName(file).toLowerCase();
        try {
            register(id, new FileReader(file), loader);
        } catch (InvalidConfigException e) {
            e.addLocation("in file " + file.getPath());
            e.addLocation("from registry " + getPath());
            throw e;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void registerFolder(@NonNull File file, RegistryLoader<? extends T> loader, boolean recursive) {
        File[] files = file.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                if (!recursive) continue;
                registerFolder(f, loader, true);
            } else {
                registerFile(file, loader);
            }
        }
    }
    public void registerFolder(@NonNull File file, RegistryLoader<? extends T> loader) {
        registerFolder(file, loader, false);
    }


    @SuppressWarnings("unchecked")
    public T get(@NonNull String name) {
        name = name.toLowerCase();
        Child entry = children.get(name);
        return entry == null ? null : entry.leaf ? (T) entry.value : null;
    }

    public Registry<?> getChild(@NonNull String name) {
        name = name.toLowerCase();
        Child entry = children.get(name);
        return entry == null ? null : entry.leaf ? null : (Registry<?>) entry.value;
    }

    public Collection<Registry<?>> getChildren() {
        return children.values().stream()
                .filter(c -> !c.leaf)
                .map(c -> (Registry<?>) c.value)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public Map<String, T> getRegistered() {
        return children.entrySet().stream()
                .filter(c -> c.getValue().leaf)
                .map(c -> new AbstractMap.SimpleEntry<>(c.getKey(), (T) c.getValue().value))
                .collect(CollectionUtil.toMap());
    }

    public Object find(@NonNull String path) {
        if (path.indexOf(RegistryRoot.PLUGIN_PATH_DIVIDER) > 0) {
            return root.find(path);
        }
        int dividerIndex = path.indexOf('.');
        if (dividerIndex < 0) {
            return get(path);
        } else {
            String localPath = path.substring(0, dividerIndex);
            Child entry = children.get(localPath);
            if (entry == null) {
                throw new IllegalArgumentException("Cannot find registry '" + localPath + "':" + children.keySet());
            }
            if (entry.leaf) {
                throw new IllegalArgumentException("'" + localPath + "' is not a Registry");
            }
            return ((Registry<?>)entry.value).find(path.substring(dividerIndex + 1));
        }
    }

    public <R> R find(@NonNull String path, @NonNull Class<R> expected) {
        Object raw = find(path);
        if (raw == null) return null;
        if (!expected.isInstance(raw)) {
            throw new IllegalArgumentException("Illegal registry type, found: " + raw.getClass().getSimpleName() + " while expected: " + expected.getSimpleName());
        }
        return (R) raw;
    }

    public VisitResult visit(@NonNull RegistryVisitor visitor) {
        VisitResult selfRes = visitor.preVisitRegistry(this);
        if (selfRes == VisitResult.TERMINATE) return VisitResult.TERMINATE;
        if (selfRes == VisitResult.SKIP) return VisitResult.CONTINUE;
        // Get folders and files
        // Sort the stream so that folders come before files
        List<Map.Entry<String, Child>> l = children.entrySet().stream()
                .sorted((a, b) -> Boolean.compare(a.getValue().leaf, b.getValue().leaf))
                .collect(Collectors.toList());
        // Iterate all the "folders" (registries)
        Iterator<Map.Entry<String, Child>> i = l.iterator();
        Map.Entry<String, Child> e;
        while (!(e = i.next()).getValue().leaf) {
            VisitResult res = ((Registry<?>)e.getValue().value).visit(visitor);
            if (res == VisitResult.TERMINATE) return VisitResult.TERMINATE;
            if (!i.hasNext()) return visitor.postVisitRegistry(this);
        }
        // Iterate all the "files" (entries)
        while (e != null) {
            VisitResult res = visitor.visitEntry(e.getKey(), e.getValue().value);
            if (res == VisitResult.TERMINATE) return VisitResult.TERMINATE;
            e = i.hasNext() ? i.next() : null;
        }
        return visitor.postVisitRegistry(this);
    }

    public String getPath() {
        Deque<String> names = new ArrayDeque<>();
        Registry<?> current = this;
        while (current.getParent() != null) {
            names.push(current.getName());
            current = current.getParent();
        }
        // When the cycle is done the only register left is
        // the plugin root (because it's the only one without a parent)
        return current.getName() + RegistryRoot.PLUGIN_PATH_DIVIDER + String.join(".", names);
    }

    @Override
    public String toString() {
        return "{" +
                children.entrySet().stream()
                        .map(c -> c.getKey() + "=" + c.getValue().value)
                        .collect(Collectors.joining(",")) +
                "}";
    }


    @RequiredArgsConstructor
    @Getter
    private static final class Child {
        private final boolean leaf;
        private final Object value;

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Child && Objects.equals(((Child)other).value, value);
        }
    }
}
