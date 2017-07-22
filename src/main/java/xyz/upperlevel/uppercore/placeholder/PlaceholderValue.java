package xyz.upperlevel.uppercore.placeholder;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.ConfigUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface PlaceholderValue<T> {

    /**
     * Removes placeholders from the value and parses it using the local PlaceHolders in addition to the normal ones
     * @param player the player that executes the placeholders
     * @param local the placeholders to add to the default ones
     * @return the parsed value without placeholders
     */
    T resolve(Player player, Map<String, Placeholder> local);

    /**
     * Removes placeholders from the value and parses it using the local values in addition to the normal placeholders
     * @param player the player that executes the placeholders
     * @param local the values to add to the default placeholders
     * @return the parsed value without placeholders
     */
    T resolveRaw(Player player, Map<String, String> local);

    /**
     * Removes placeholders from the value and parses it
     * @param player the player that executes the placeholders
     * @return the parsed value without placeholders
     */
    default T resolve(Player player) {
        return resolve(player, Collections.emptyMap());
    }

    /**
     * @see T resolveRaw(Player, Map)
     */
    default T resolve(Player player, String key1, String value1) {
        return resolveRaw(player, ImmutableMap.of(key1, value1));
    }

    /**
     * @see T resolveRaw(Player, Map)
     */
    default T resolve(Player player, String key1, String value1, String key2, String value2) {
        return resolveRaw(player, ImmutableMap.of(key1, value1, key2, value2));
    }

    /**
     * @see T resolveRaw(Player, Map)
     */
    default T resolve(Player player, String key1, String value1, String key2, String value2, String key3, String value3) {
        return resolveRaw(player, ImmutableMap.of(key1, value1, key2, value2, key3, value3));
    }

    String toString();



    static PlaceholderValue<Byte> byteValue(String string) {
        return value(string, Byte::parseByte, (byte) -1);
    }

    static PlaceholderValue<Short> shortValue(String string) {
        return value(string, Short::parseShort, (short) -1);
    }

    static PlaceholderValue<Integer> intValue(String string) {
        return value(string, Integer::parseInt, -1);
    }

    static PlaceholderValue<Long> longValue(String string) {
        return value(string, Long::parseLong, -1L);
    }

    static PlaceholderValue<Float> floatValue(String string) {
        return value(string, Float::parseFloat, -1.0f);
    }

    static PlaceholderValue<Double> doubleValue(String string) {
        return value(string, Double::parseDouble, -1.0);
    }

    static PlaceholderValue<String> stringValue(String string, Map<String, Placeholder> local) {
        if (string == null) return null;
        return stringValueKnown(string, PlaceholderUtil.hasPlaceholders(string, local));
    }

    static PlaceholderValue<String> stringValue(String string, Set<String> local) {
        if (string == null) return null;
        return stringValueKnown(string, PlaceholderUtil.hasPlaceholders(string, local));
    }

    static PlaceholderValue<String> stringValue(String string, String... local) {
        if (string == null) return null;
        return stringValueKnown(string, PlaceholderUtil.hasPlaceholders(string, local));
    }

    static PlaceholderValue<String> stringValue(String string) {
        if (string == null) return null;
        return stringValueKnown(string, PlaceholderUtil.hasPlaceholders(string));
    }

    static PlaceholderValue<String> stringValueKnown(String str, boolean placeholders) {
        if (placeholders)
            return new StringPlaceholderValue(str);
        else
            return new FalsePlaceholderValue<>(str);
    }

    static PlaceholderValue<Color> colorValue(String string) {
        return value(string, ConfigUtils::parseColor, Color.BLACK);
    }

    static <T> PlaceholderValue<T> fake(T value) {
        return new FalsePlaceholderValue<>(value);
    }

    static <T> PlaceholderValue<T> value(String string, Function<String, T> parser, T onError) {
        if (string == null) return null;
        T parsed;
        try {
            parsed = parser.apply(string);
        } catch (Exception e) {
            if (!PlaceholderUtil.hasPlaceholders(string)) {
                Uppercore.logger().severe("Invalid value: " + string);
                return new FalsePlaceholderValue<>(onError);
            }
            return new SimplePlaceholderValue<>(string, parser, (str, exc) -> Uppercore.logger().severe("Cannot parse value: '" + str + "' (from '" + string + "')"), onError);
        }
        return new FalsePlaceholderValue<>(parsed);
    }

    @Data
    class FalsePlaceholderValue<T> implements PlaceholderValue<T> {

        private final T value;

        @Override
        public T resolve(Player player, Map<String, Placeholder> local) {
            return value;
        }

        @Override
        public T resolveRaw(Player player, Map<String, String> local) {
            return value;
        }

        @Override
        public T resolve(Player player) {
            return value;
        }

        @Override
        public T resolve(Player player, String key1, String value1) {
            return value;
        }

        @Override
        public T resolve(Player player, String key1, String value1, String key2, String value2) {
            return value;
        }

        @Override
        public T resolve(Player player, String key1, String value1, String key2, String value2, String key3, String value3) {
            return value;
        }

        public String toString() {
            return String.valueOf(value);
        }
    }

    @Data
    class SimplePlaceholderValue<T> implements PlaceholderValue<T> {

        private final String value;

        private final Function<String, T> parser;
        private final BiConsumer<String, Exception> exceptionHandler;

        @Getter
        private final T onError;

        @Override
        public T resolve(Player player, Map<String, Placeholder> local) {
            return null;
        }

        @Override
        public T resolveRaw(Player player, Map<String, String> local) {
            return parse(PlaceholderUtil.resolveRaw(player, value, local));
        }

        @Override
        public T resolve(Player player) {
            return parse(PlaceholderUtil.resolve(player, value));
        }

        protected T parse(String real) {
            try {
                return parser.apply(real);
            } catch (Exception e) {
                exceptionHandler.accept(real, e);
            }
            return onError;
        }

        public String toString() {
            return value;
        }
    }

    @RequiredArgsConstructor
    class StringPlaceholderValue implements PlaceholderValue<String> {
        @Getter
        private final String value;

        @Override
        public String resolve(Player player, Map<String, Placeholder> local) {
            return PlaceholderUtil.resolve(player, value, local);
        }

        @Override
        public String resolveRaw(Player player, Map<String, String> local) {
            return PlaceholderUtil.resolveRaw(player, value, local);
        }

        @Override
        public String resolve(Player player) {
            return PlaceholderUtil.resolve(player, value);
        }

        public String toString() {
            return value;
        }
    }
}
