package xyz.upperlevel.uppercore.placeholder;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public interface PlaceholderManager {

    /**
     * Returns a read-only registry that can read the manager's placeholders
     * @return the registry accessing the manager's placeholders
     */
    PlaceholderRegistry<?> getRegistry();

    void register(Plugin plugin, Placeholder placeholder);

    boolean hasPlaceholders(String str);

    String apply(Player player, String text, PlaceholderRegistry<?> registry);

    default String apply(Player player, String text) {
        return apply(player, text, getRegistry());
    }

    default String single(Player player, String string) {
        return apply(player, '%' + string + '%');
    }

    default String single(Player player, String string, PlaceholderRegistry<?> registry) {
        return apply(player, '%' + string + '%', registry);
    }
}
