package xyz.upperlevel.uppercore.placeholder;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.ConfigUtils;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface PlaceholderValue<T> {

    T get(Player player);

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

    static PlaceholderValue<String> stringValue(String string) {
        if (string == null) return null;
        if (PlaceholderUtil.hasPlaceholders(string))
            return new StringPlaceholderValue(string);
        else
            return new FalsePlaceholderValue<>(string);
    }

    static PlaceholderValue<Color> colorValue(String string) {
        return value(string, ConfigUtils::parseColor, Color.BLACK);
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
        public T get(Player player) {
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
        public T get(Player player) {
            final String real = PlaceholderUtil.resolvePlaceholders(player, value);
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
        public String get(Player player) {
            return PlaceholderUtil.resolvePlaceholders(player, value);
        }

        public String toString() {
            return value;
        }
    }
}
