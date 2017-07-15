package xyz.upperlevel.uppercore.gui.config.placeholders.managers.customs;

import org.bukkit.entity.Player;

public interface CustomPlaceholder {
    String id();
    String get(Player player);
}
