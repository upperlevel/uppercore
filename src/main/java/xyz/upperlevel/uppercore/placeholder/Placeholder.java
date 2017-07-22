package xyz.upperlevel.uppercore.placeholder;

import org.bukkit.entity.Player;

public interface Placeholder {

    String getId();

    String resolve(Player player, String arg);
}
