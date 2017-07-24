package xyz.upperlevel.uppercore.placeholder;

import org.bukkit.entity.Player;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Placeholder {

    String getId();

    String resolve(Player player, String arg);

    static Placeholder of(String id, String obj) {
        return new Placeholder() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String resolve(Player player, String arg) {
                return obj;
            }
        };
    }

    static Placeholder of(String obj) {
        return of(null, obj);
    }

    static Placeholder of(String id, Supplier<String> obj) {
        return new Placeholder() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String resolve(Player player, String arg) {
                return obj.get();
            }
        };
    }

    static Placeholder of(Supplier<String> obj) {
        return of(null, obj);
    }

    static Placeholder of(String id, Function<Player, String> obj) {
        return new Placeholder() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String resolve(Player player, String arg) {
                return obj.apply(player);
            }
        };
    }

    static Placeholder of(Function<Player, String> obj) {
        return of(null, obj);
    }

    static Placeholder of(String id, BiFunction<Player, String, String> obj) {
        return new Placeholder() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String resolve(Player player, String arg) {
                return obj.apply(player, arg);
            }
        };
    }

    static Placeholder of(BiFunction<Player, String, String> obj) {
        return of(null, obj);
    }
}
