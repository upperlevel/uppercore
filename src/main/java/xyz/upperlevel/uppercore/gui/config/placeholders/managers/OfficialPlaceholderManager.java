package xyz.upperlevel.uppercore.gui.config.placeholders.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.config.placeholders.PlaceholderManager;

import java.util.Map;

public class OfficialPlaceholderManager implements PlaceholderManager {
    @Override
    public boolean hasPlaceholders(String str) {
        return PlaceholderAPI.containsPlaceholders(str);
    }

    @Override
    public String apply(Player player, String str) {
        return PlaceholderAPI.setPlaceholders(player, str);
    }

    @Override
    public String single(Player player, String str) {
        Map<String, PlaceholderHook> placeholders = PlaceholderAPI.getPlaceholders();

        int index = str.indexOf('_');
        String pl = str.substring(0, index);
        Map.Entry<String, PlaceholderHook> p = placeholders.entrySet().stream().filter(h -> pl.equalsIgnoreCase(h.getKey())).findFirst().orElse(null);
        if(p == null)
            return null;
        else
            return p.getValue().onPlaceholderRequest(player, str.substring(index + 1));
    }
}
