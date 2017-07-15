package xyz.upperlevel.uppercore.gui.config.placeholders.managers.customs;

import org.bukkit.entity.Player;

public class PlayerWorldPlaceholder implements CustomPlaceholder {
    @Override
    public String id() {
        return "player_world";
    }

    @Override
    public String get(Player player) {
        return player.getWorld().getName();
    }
}
