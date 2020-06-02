package xyz.upperlevel.uppercore.placeholder.managers.customs;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.Placeholder;

public class PlayerPlaceholder implements Placeholder {

    @Override
    public String getId() {
        return "player";
    }

    @Override
    public String resolve(Player player, String arg) {
        switch (arg) {
            case "display_name":
                return player.getDisplayName();
            case "food_level":
                return Integer.toString(player.getFoodLevel());
            case "health":
                return Double.toString(player.getHealth());
            case "level":
                return Integer.toString(player.getLevel());
            case "saturation":
                return Float.toString(player.getSaturation());
            case "world":
                return player.getWorld().getName();
        }
        return null;
    }
}
