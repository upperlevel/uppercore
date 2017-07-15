package xyz.upperlevel.uppercore.gui.config.placeholders;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.config.util.ConfigUtils;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface PlaceholderValue<T> {
    T get(Player player);

    String toString();

    static PlaceholderValue<Long> longValue(String l) {
        return value(l, Long::parseLong, -1L);
    }

    static PlaceholderValue<Integer> intValue(String i) {
        return value(i, Integer::parseInt, -1);
    }

    static PlaceholderValue<Short> shortValue(String s) {
        return value(s, Short::parseShort, (short) -1);
    }

    static PlaceholderValue<Byte> byteValue(String b) {
        return value(b, Byte::parseByte, (byte) -1);
    }

    static PlaceholderValue<Float> floatValue(String b) {
        return value(b, Float::parseFloat, -1.0f);
    }

    static PlaceholderValue<Double> doubleValue(String b) {
        return value(b, Double::parseDouble, -1.0);
    }

    static PlaceholderValue<String> strValue(String str) {
        if(str == null) return null;
        if(PlaceHolderUtil.hasPlaceholders(str))
            return new StringPlaceholderValue(str);
        else
            return new FalsePlaceholderValue<>(str);
    }

    static PlaceholderValue<Color> colorValue(String c) {
        return value(c, ConfigUtils::parseColor, Color.BLACK);
    }


    static <T> PlaceholderValue<T> value(String i, Function<String, T> parser, T onError) {
        if(i == null) return null;
        T parsed;
        try {
            parsed = parser.apply(i);
        } catch (Exception e) {
            if(!PlaceHolderUtil.hasPlaceholders(i)) {
                Uppercore.logger().severe("Invalid value: " + i);
                return new FalsePlaceholderValue<>(onError);
            }
            return new SimplePlaceholderValue<>(i, parser, (str, exc) -> Uppercore.logger().severe("Cannot parse value: '" + str + "' (from '" + i + "')"), onError);
        }
        return new FalsePlaceholderValue<>(parsed);
    }

    @RequiredArgsConstructor
    class FalsePlaceholderValue<T> implements PlaceholderValue<T> {
        @Getter
        private final T value;

        @Override
        public T get(Player player) {
            return value;
        }

        public String toString() {
            return String.valueOf(value);
        }
    }

    @RequiredArgsConstructor
    class SimplePlaceholderValue<T> implements PlaceholderValue<T> {
        @Getter
        private final String value;
        @Getter
        private final Function<String, T> parser;
        private final BiConsumer<String, Exception> exceptionHandler;
        @Getter
        private final T onError;

        @Override
        public T get(Player player) {
            final String real = PlaceHolderUtil.resolvePlaceholders(player, value);
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
            return PlaceHolderUtil.resolvePlaceholders(player, value);
        }

        public String toString() {
            return value;
        }
    }
}
