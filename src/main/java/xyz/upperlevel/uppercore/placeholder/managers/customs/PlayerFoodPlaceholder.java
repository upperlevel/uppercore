package xyz.upperlevel.uppercore.placeholder.managers.customs;

import org.bukkit.entity.Player;

public class PlayerFoodPlaceholder implements CustomPlaceholder {

    @Override
    public String id() {
        return "player_food_level";
    }

    @Override
    public String get(Player player) {
        return Float.toString(player.getFoodLevel());
    }
}
