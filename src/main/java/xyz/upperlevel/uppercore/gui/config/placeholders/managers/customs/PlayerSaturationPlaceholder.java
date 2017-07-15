package xyz.upperlevel.uppercore.gui.config.placeholders.managers.customs;

import org.bukkit.entity.Player;

public class PlayerSaturationPlaceholder implements CustomPlaceholder {
    @Override
    public String id() {
        return "player_saturation";
    }

    @Override
    public String get(Player player) {
        return Float.toString(player.getSaturation());
    }
}
