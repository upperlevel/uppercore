package xyz.upperlevel.uppercore.gui.config.placeholders;

import org.bukkit.entity.Player;

public interface PlaceholderManager {
    boolean hasPlaceholders(String str);

    String apply(Player player, String str);

    default String single(Player player, String str) {
        return apply(player, '%' + str + '%');
    }
}
