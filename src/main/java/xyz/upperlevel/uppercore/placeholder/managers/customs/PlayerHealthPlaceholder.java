package xyz.upperlevel.uppercore.placeholder.managers.customs;

import org.bukkit.entity.Player;

public class PlayerHealthPlaceholder implements CustomPlaceholder {

    @Override
    public String id() {
        return "player_health";
    }

    @Override
    public String get(Player player) {
        return Double.toString(player.getHealth());
    }
}
