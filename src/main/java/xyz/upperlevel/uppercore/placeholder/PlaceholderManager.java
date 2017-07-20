package xyz.upperlevel.uppercore.placeholder;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface PlaceholderManager {

    void register(Plugin plugin, Placeholder placeholder);

    boolean hasPlaceholders(String string);

    String apply(Player player, String string);

    default String single(Player player, String string) {
        return apply(player, '%' + string + '%');
    }
}
