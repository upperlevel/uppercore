package xyz.upperlevel.uppercore.gui;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.*;

public final class InputFilters {

    public static AnvilGUI.ClickHandler plain(BiConsumer<Player, String> consumer) {
        return (p, m) -> {
            consumer.accept(p, m);
            return null;
        };
    }

    public static AnvilGUI.ClickHandler filterInt(BiConsumer<Player, Integer> consumer, IntPredicate filter) {
        return (p, m) -> {
            int parsed;
            try {
                parsed = Integer.parseInt(m);
            } catch (NumberFormatException e) {
                return "Invalid number!";
            }
            if(!filter.test(parsed))
                return "Invalid number!";
            consumer.accept(p, parsed);
            return null;
        };
    }

    public static AnvilGUI.ClickHandler filterInt(BiConsumer<Player, Integer> consumer) {
        return filterInt(consumer, n -> true);
    }

    public static AnvilGUI.ClickHandler filterLong(BiConsumer<Player, Long> consumer, LongPredicate filter) {
        return (p, m) -> {
            long parsed;
            try {
                parsed = Long.parseLong(m);
            } catch (NumberFormatException e) {
                return "Invalid number!";
            }
            if(!filter.test(parsed))
                return "Invalid number";
            consumer.accept(p, parsed);
            return null;
        };
    }

    public static AnvilGUI.ClickHandler filterLong(BiConsumer<Player, Long> consumer) {
        return filterLong(consumer, n -> true);
    }

    interface FloatPredicate {
        boolean test(float res);
    }

    public static AnvilGUI.ClickHandler filterFloat(BiConsumer<Player, Float> consumer, FloatPredicate filter) {
        return (p, m) -> {
            float parsed;
            try {
                parsed = Float.parseFloat(m);
            } catch (NumberFormatException e) {
                return "Invalid number!";
            }
            if(!filter.test(parsed))
                return "Invalid number!";
            consumer.accept(p, parsed);
            return null;
        };
    }

    public static AnvilGUI.ClickHandler filterFloat(BiConsumer<Player, Float> consumer) {
        return filterFloat(consumer, n -> true);
    }

    public static AnvilGUI.ClickHandler filterDouble(BiConsumer<Player, Double> consumer, DoublePredicate filter) {
        return (p, m) -> {
            double parsed;
            try {
                parsed = Double.parseDouble(m);
            } catch (NumberFormatException e) {
                return "Invalid number!";
            }
            if(!filter.test(parsed))
                return "Invalid number!";
            consumer.accept(p, parsed);

            return null;
        };
    }

    public static AnvilGUI.ClickHandler filterDouble(BiConsumer<Player, Double> consumer) {
        return filterDouble(consumer, n -> true);
    }

    public static AnvilGUI.ClickHandler filterBoolean(BiConsumer<Player, Boolean> consumer) {
        return (p, m) -> {
            try {
                consumer.accept(p, parseBool(m));
            } catch (NumberFormatException e) {
                return "Invalid number!";
            }
            return null;
        };
    }

    public static AnvilGUI.ClickHandler filterPlayer(BiConsumer<Player, Player> consumer, Predicate<Player> filter) {
        return (p, m) -> {
            Player player = Bukkit.getPlayer(m);
            if (player != null && filter.test(player)) {
                consumer.accept(p, player);
                return null;
            } else return "Invalid name!";
        };
    }

    public static AnvilGUI.ClickHandler filterPlayer(BiConsumer<Player, Player> consumer) {
        return filterPlayer(consumer, always());
    }


    private static boolean parseBool(String s) {
        switch (s.toLowerCase()) {
            case "0":
            case "f":
            case "false":
            case "no":
                return false;
            case "1":
            case "t":
            case "true":
            case "yes":
                return true;
            default:
                throw new NumberFormatException();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Predicate<T> always() {
        return TRUE;
    }

    public static Predicate TRUE = o -> true;
}
