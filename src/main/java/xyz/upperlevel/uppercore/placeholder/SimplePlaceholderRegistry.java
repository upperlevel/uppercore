package xyz.upperlevel.uppercore.placeholder;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SimplePlaceholderRegistry implements PlaceholderRegistry<SimplePlaceholderRegistry> {

    private PlaceholderRegistry parent;

    private final Map<String, Placeholder> placeholders;

    public SimplePlaceholderRegistry(Map<String, Placeholder> handle) {
        this.placeholders = handle;
        this.parent = PlaceholderUtil.getRegistry();
    }

    public SimplePlaceholderRegistry() {
        this(new HashMap<>());
    }

    public SimplePlaceholderRegistry(PlaceholderRegistry parent) {
        this(new HashMap<>());
        this.parent = parent;
    }

    @Override
    public Placeholder getLocal(String key) {
        return placeholders.get(key);
    }

    @Override
    public Placeholder get(String key) {
        Placeholder p = placeholders.get(key);
        return p != null ? p : parent != null ? parent.get(key) : null;
    }

    @Override
    public boolean hasLocal(String key) {
        return placeholders.containsKey(key);
    }

    @Override
    public boolean has(String key) {
        return placeholders.containsKey(key) || (parent != null && parent.has(key));
    }

    @Override
    public SimplePlaceholderRegistry set(Placeholder placeholder) {
        placeholders.put(placeholder.getId(), placeholder);
        return this;
    }
}
