package xyz.upperlevel.uppercore.placeholder.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderManager;

import java.util.Map;

public class OfficialPlaceholderManager implements PlaceholderManager {

    @Override
    public boolean hasPlaceholders(String string) {
        return PlaceholderAPI.containsPlaceholders(string);
    }

    @Override
    public String apply(Player player, String string) {
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    @Override
    public String single(Player player, String string) {
        Map<String, PlaceholderHook> placeholders = PlaceholderAPI.getPlaceholders();

        int index = string.indexOf('_');
        String pl = string.substring(0, index);
        Map.Entry<String, PlaceholderHook> p = placeholders.entrySet().stream().filter(h -> pl.equalsIgnoreCase(h.getKey())).findFirst().orElse(null);
        if(p == null)
            return null;
        else
            return p.getValue().onPlaceholderRequest(player, string.substring(index + 1));
    }
}
