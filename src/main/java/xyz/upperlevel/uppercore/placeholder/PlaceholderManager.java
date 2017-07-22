package xyz.upperlevel.uppercore.placeholder;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public interface PlaceholderManager {

    void register(Plugin plugin, Placeholder placeholder);

    boolean hasPlaceholders(String string);

    String apply(Player player, String text);

    String apply(Player player, String text, Map<String, Placeholder> local);

    default String single(Player player, String string) {
        return apply(player, '%' + string + '%');
    }

    default String single(Player player, String string, Map<String, Placeholder> local) {
        return apply(player, '%' + string + '%', local);
    }
}
