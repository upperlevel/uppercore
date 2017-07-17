package xyz.upperlevel.uppercore.gui;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public final class InputFilters {

    public static AnvilGUI.ClickHandler plain(BiConsumer<Player, String> consumer) {
        return (p, m) -> {
            consumer.accept(p, m);
            return null;
        };
    }

    public static AnvilGUI.ClickHandler filterInt(BiConsumer<Player, Integer> consumer) {
        return (p, m) -> {
            try {
                consumer.accept(p, Integer.parseInt(m));
            } catch (NumberFormatException e) {
                return "Invalid number!";
            }
            return null;
        };
    }

    public static AnvilGUI.ClickHandler filterLong(BiConsumer<Player, Long> consumer) {
        return (p, m) -> {
            try {
                consumer.accept(p, Long.parseLong(m));
            } catch (NumberFormatException e) {
                return "Invalid number!";
            }
            return null;
        };
    }

    public static AnvilGUI.ClickHandler filterFloat(BiConsumer<Player, Float> consumer) {
        return (p, m) -> {
            try {
                consumer.accept(p, Float.parseFloat(m));
            } catch (NumberFormatException e) {
                return "Invalid number!";
            }
            return null;
        };
    }

    public static AnvilGUI.ClickHandler filterDouble(BiConsumer<Player, Double> consumer) {
        return (p, m) -> {
            try {
                consumer.accept(p, Double.parseDouble(m));
            } catch (NumberFormatException e) {
                return "Invalid number!";
            }
            return null;
        };
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

    public static AnvilGUI.ClickHandler filterPlayer(BiConsumer<Player, Player> consumer) {
        return (p, m) -> {
            Player player = Bukkit.getPlayer(m);
            if (player != null) {
                consumer.accept(p, player);
                return null;
            } else return "Invalid name!";
        };
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
}
