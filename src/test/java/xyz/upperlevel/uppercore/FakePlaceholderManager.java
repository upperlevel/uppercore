package xyz.upperlevel.uppercore;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderManager;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

public class FakePlaceholderManager implements PlaceholderManager {
    @Override
    public PlaceholderRegistry getRegistry() {
        return null;
    }

    @Override
    public void register(Plugin plugin, Placeholder placeholder) {
    }

    @Override
    public boolean hasPlaceholders(String str) {
        return false;
    }

    @Override
    public String apply(Player player, String text, PlaceholderRegistry registry) {
        return text;
    }
}
