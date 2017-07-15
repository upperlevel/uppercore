package xyz.upperlevel.uppercore.gui.config.placeholders.managers.customs;

import org.bukkit.entity.Player;

public class PlayerLevelPlaceholder implements CustomPlaceholder {
    @Override
    public String id() {
        return "player_level";
    }

    @Override
    public String get(Player player) {
        return Integer.toString(player.getLevel());
    }
}
