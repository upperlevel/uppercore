package xyz.upperlevel.uppercore.placeholder.managers.customs;

import org.bukkit.entity.Player;

public interface CustomPlaceholder {

    String id();

    String get(Player player);
}
