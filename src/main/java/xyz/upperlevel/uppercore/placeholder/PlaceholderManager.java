package xyz.upperlevel.uppercore.placeholder;

import org.bukkit.entity.Player;

public interface PlaceholderManager {

    boolean hasPlaceholders(String string);

    String apply(Player player, String string);

    default String single(Player player, String string) {
        return apply(player, '%' + string + '%');
    }
}
