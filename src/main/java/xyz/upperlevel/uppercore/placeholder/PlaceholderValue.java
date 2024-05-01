package xyz.upperlevel.uppercore.placeholder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.ConfigUtil;
import xyz.upperlevel.uppercore.sound.SoundUtil;
import xyz.upperlevel.uppercore.util.TextUtil;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface PlaceholderValue<T> {

    /**
     * Removes placeholders from the value and parses it using the local placeholders in addition to the normal ones
     *
     * @param player the player that executes the placeholders
     * @param local  the placeholders to add to the default ones
     * @return the parsed value without placeholders
     */
    T resolve(Player player, PlaceholderRegistry local);

    /**
     * Removes placeholders from the value and parses it
     *
     * @param player the player that executes the placeholders
     * @return the parsed value without placeholders
     */
    default T resolve(Player player) {
        return resolve(player, PlaceholderUtil.getRegistry());
    }

    /**
     * @return true only if no real placeholders need to be resolved (a.k.a this is a fake PlaceholderValue)
     */
    default boolean hasPlaceholders() {
        return true;
    }

    default <N> PlaceholderValue<N> map(Function<T, N> mapper) {
        return (p, l) -> mapper.apply(resolve(p, l));
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

    static PlaceholderValue<String> rawStringValue(String string) {
        if (string == null) return null;
        if (PlaceholderUtil.hasPlaceholders(string))
            return new StringPlaceholderValue(string);
        else
            return new FalsePlaceholderValue<>(string);
    }

    static PlaceholderValue<String> stringValue(String string) {
        if (string == null) return null;
        string = TextUtil.translatePlain(string);
        if (PlaceholderUtil.hasPlaceholders(string))
            return new StringPlaceholderValue(string);
        else
            return new FalsePlaceholderValue<>(string);
    }

    static PlaceholderValue<Color> colorValue(String string) {
        return value(string, ConfigUtil::parseColor, Color.BLACK);
    }

    static PlaceholderValue<Sound> soundValue(String string) {
        return value(string, x -> SoundUtil.get(x).orElse(null), null);
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

    @RequiredArgsConstructor
    class FalsePlaceholderValue<T> implements PlaceholderValue<T> {
        @Getter
        private final T value;

        @Override
        public T resolve(Player player, PlaceholderRegistry local) {
            return value;
        }

        @Override
        public T resolve(Player player) {
            return value;
        }

        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public boolean hasPlaceholders() {
            return false;
        }

        @Override
        public <N> FalsePlaceholderValue<N> map(Function<T, N> mapper) {
            return new FalsePlaceholderValue<>(mapper.apply(value));
        }
    }

    class SimplePlaceholderValue<T> implements PlaceholderValue<T> {
        @Getter
        private final String value;

        private final Function<String, T> parser;
        private final BiConsumer<String, Exception> exceptionHandler;
        private final T onError;

        public SimplePlaceholderValue(String value, Function<String, T> parser, BiConsumer<String, Exception> exceptionHandler, T onError) {
            this.value = value;
            this.parser = parser;
            this.exceptionHandler = exceptionHandler;
            this.onError = onError;
        }

        @Override
        public T resolve(Player player, PlaceholderRegistry local) {
            return parse(PlaceholderUtil.resolve(player, value, local));
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

    class StringPlaceholderValue implements PlaceholderValue<String> {
        @Getter
        private final String value;

        public StringPlaceholderValue(String value) {
            this.value = value;
        }

        @Override
        public String resolve(Player player, PlaceholderRegistry local) {
            return PlaceholderUtil.resolve(player, value, local);
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
