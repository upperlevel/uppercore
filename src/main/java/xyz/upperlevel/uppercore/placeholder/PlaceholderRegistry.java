package xyz.upperlevel.uppercore.placeholder;

import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public interface PlaceholderRegistry<T extends PlaceholderRegistry<T>> {

    PlaceholderRegistry<?> getParent();

    void setParent(PlaceholderRegistry<?> parent);

    Placeholder getLocal(String key);

    default Placeholder get(String key) {
        Placeholder p = getLocal(key);
        if (p != null) return p;
        else {
            PlaceholderRegistry<?> parent = getParent();
            return parent != null ? parent.get(key) : null;
        }
    }

    T set(Placeholder placeholder);

    default boolean has(String id) {
        return get(id) != null;
    }

    default boolean hasLocal(String id) {
        return getLocal(id) != null;
    }

    default T set(String id, Object obj) {
        set(Placeholder.of(id, String.valueOf(obj)));
        return (T) this;
    }

    default T set(String id, Supplier<String> obj) {
       set(Placeholder.of(id, obj));
        return (T) this;
    }

    default T set(String id, Function<Player, String> obj) {
        set(Placeholder.of(id, obj));
        return (T) this;
    }

    default T set(String id, BiFunction<Player, String, String> obj) {
        set(Placeholder.of(id, obj));
        return (T) this;
    }

    default T set(Map<String, Placeholder> placeholders) {
        for(Map.Entry<String, Placeholder> p : placeholders.entrySet())
            set(p.getKey(), p.getValue());
        return (T) this;
    }

    static SimplePlaceholderRegistry wrap(Map<String, Placeholder> handle) {
        return new SimplePlaceholderRegistry(handle);
    }

    static SimplePlaceholderRegistry wrap(String k1, String v1) {
        return wrap(ImmutableMap.of(k1, Placeholder.of(v1)));
    }

    static SimplePlaceholderRegistry wrap(String k1, String v1, String k2, String v2) {
        return wrap(ImmutableMap.of(
                k1, Placeholder.of(v1),
                k2, Placeholder.of(v2)
        ));
    }

    static SimplePlaceholderRegistry wrap(String k1, String v1, String k2, String v2, String k3, String v3) {
        return wrap(ImmutableMap.of(
                k1, Placeholder.of(v1),
                k2, Placeholder.of(v2),
                k3, Placeholder.of(v3)
        ));
    }

    static SimplePlaceholderRegistry wrap(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4) {
        return wrap(ImmutableMap.of(
                k1, Placeholder.of(v1),
                k2, Placeholder.of(v2),
                k3, Placeholder.of(v3),
                k4, Placeholder.of(v4)
        ));
    }

    static PlaceholderRegistry<?> def() {
        return PlaceholderUtil.getRegistry();
    }

    static SimplePlaceholderRegistry create() {
        return new SimplePlaceholderRegistry();
    }

    static SimplePlaceholderRegistry create(PlaceholderRegistry<?> parent) {
        return new SimplePlaceholderRegistry(parent);
    }
}
