package xyz.upperlevel.uppercore.placeholder.managers.customs;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.Placeholder;

public class PlayerHealthPlaceholder implements Placeholder {

    @Override
    public String getId() {
        return "player_health";
    }

    @Override
    public String resolve(Player player, String id) {
        return Double.toString(player.getHealth());
    }
}
