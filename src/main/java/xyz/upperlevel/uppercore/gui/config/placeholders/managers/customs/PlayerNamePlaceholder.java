package xyz.upperlevel.uppercore.gui.config.placeholders.managers.customs;

import org.bukkit.entity.Player;

public class PlayerNamePlaceholder implements CustomPlaceholder {
    @Override
    public String id() {
        return "player_name";
    }

    @Override
    public String get(Player player) {
        return player.getName();
    }
}
