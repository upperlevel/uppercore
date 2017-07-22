package xyz.upperlevel.uppercore.placeholder;

import org.bukkit.entity.Player;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Placeholder {

    String getId();

    String resolve(Player player, String arg);

    static Placeholder of(String obj) {
        return new Placeholder() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String resolve(Player player, String arg) {
                return obj;
            }
        };
    }

    static Placeholder of(Supplier<String> obj) {
        return new Placeholder() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String resolve(Player player, String arg) {
                return obj.get();
            }
        };
    }

    static Placeholder of(Function<Player, String> obj) {
        return new Placeholder() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String resolve(Player player, String arg) {
                return obj.apply(player);
            }
        };
    }

    static Placeholder of(BiFunction<Player, String, String> obj) {
        return new Placeholder() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String resolve(Player player, String arg) {
                return obj.apply(player, arg);
            }
        };
    }
}
