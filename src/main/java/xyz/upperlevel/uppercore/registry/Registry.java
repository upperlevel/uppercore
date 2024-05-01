package xyz.upperlevel.uppercore.registry;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
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

public class Registry {
    @Getter
    private final String name;

    @Getter
    private final Registry parent;

    private Map<String, Object> children = new HashMap<>();

    public Registry(@NonNull String name, Registry parent) {
        this.name = name;
        this.parent = parent;
    }

    private void registerLocal(String name, Object value) {
        name = name.toLowerCase(Locale.ENGLISH);
        boolean conflict = children.putIfAbsent(name, value) != null;
        if (conflict) {
            throw new IllegalArgumentException("Entry with name '" + name + "' already present in " + getPath());
        }
        if (value instanceof RegistryTraceable) {
            ((RegistryTraceable) value).setParentRegistry(this);
        }
    }

    public Registry registerFolder(@NonNull String registryName) {
        String[] parts = StringUtils.split(registryName, '.');

        if (parts.length == 0) throw new IllegalArgumentException("empty name");

        Registry current = this;
        Registry child = null;

        for (String part : parts) {
            Object childObj = current.get(part);

            if (childObj != null && !(childObj instanceof Registry)) {
                throw new IllegalArgumentException("Trying to register folder inside a non-folder " + current.getPath() + "." + part);
            }

            child = (Registry) childObj;
            if (child == null) {
                child = new Registry(part.toLowerCase(Locale.ENGLISH), this);
                current.register(part, child);
            }
            current = child;
        }

        return child;
    }

    public <T> T register(@NonNull String name, @NonNull T object) {
        if (name.startsWith("@")) {
            return root().register(name.substring(1), object);
        }

        int divIndex = name.lastIndexOf('.');
        if (divIndex == -1) {
            registerLocal(name, object);
        } else {
            registerFolder(name.substring(0, divIndex)).register(name.substring(divIndex + 1), object);
        }

        return object;
    }

    public <T> T load(String id, Reader in, RegistryLoader<? extends T> loader) {
        T object = loader.load(this, id, in);
        return register(id, object);
    }

    public <T> T loadFile(File file, RegistryLoader<? extends T> loader) {
        String id = FileUtil.getName(file);
        try {
            return load(id, new FileReader(file), loader);
        } catch (InvalidConfigException e) {
            e.addLocation("in file " + file.getPath());
            e.addLocation("from registry " + getPath());
            throw e;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void loadFolder(@NonNull File file, RegistryLoader<?> loader, boolean recursive) {
        File[] files = file.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                if (!recursive) continue;
                registerFolder(file.getName()).loadFolder(f, loader, true);
            } else {
                loadFile(file, loader);
            }
        }
    }

    public void loadFolder(@NonNull File file, RegistryLoader loader) {
        loadFolder(file, loader, false);
    }


    protected Object getUnchecked(String name) {
        return children.get(name.toLowerCase(Locale.ENGLISH));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull String name) {
        if (!name.isEmpty() && name.charAt(0) == '@') {
            return getRoot().get(name.substring(1));
        }

        Registry current = this;
        String[] parts = StringUtils.split(name, '.');

        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.getUnchecked(parts[i]);
            if (next == null) throw new IllegalArgumentException("Cannot find folder " + String.join(".", Arrays.copyOf(parts, i + 1)) +
                    " in " + getPath() + " available: " + entries().keySet());
            current = (Registry) next;
        }

        return (T) current.getUnchecked(parts[parts.length - 1]);
    }

    public List<Registry> getFolders() {
        return children.values().stream()
                .filter(c -> c instanceof Registry)
                .map(c -> (Registry) c)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> entries() {
        return Collections.unmodifiableMap(children);
    }

    public Registry getRoot() {
        Registry current = this;
        while (current.parent != null) current = current.parent;
        return current;
    }

    public VisitResult visit(@NonNull RegistryVisitor visitor) {
        VisitResult selfRes = visitor.preVisitRegistry(this);
        if (selfRes == VisitResult.TERMINATE) return VisitResult.TERMINATE;
        if (selfRes == VisitResult.SKIP) return VisitResult.CONTINUE;
        // Get folders and files
        for (Map.Entry<String, Object> entry : children.entrySet()) {
            VisitResult res;
            if (entry.getValue() instanceof Registry) {
                res = ((Registry) entry.getValue()).visit(visitor);
            } else {
                res = visitor.visitEntry(entry.getKey(), entry.getValue());
            }
            if (res == VisitResult.TERMINATE) return VisitResult.TERMINATE;
        }
        return visitor.postVisitRegistry(this);
    }

    public String getPath() {
        Deque<String> names = new ArrayDeque<>();
        Registry current = this;
        while (current.getParent() != null) {
            names.push(current.getName());
            current = current.getParent();
        }
        // When the cycle is done the only register left is
        // the plugin root (because it's the only one without a parent)
        return String.join(".", names);
    }

    @Override
    public String toString() {
        return "{" +
                children.entrySet().stream()
                        .map(c -> c.getKey() + "=" + c.getValue())
                        .collect(Collectors.joining(",")) +
                "}";
    }

    public static Registry root() {
        return new Registry("", null);
    }
}
