package xyz.upperlevel.uppercore.placeholder.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderManager;

import java.util.Map;

public class OfficialPlaceholderManager implements PlaceholderManager {

    private static class OfficialPlaceholderAdapter extends EZPlaceholderHook {

        private final Placeholder placeholder;

        public OfficialPlaceholderAdapter(Plugin plugin, Placeholder placeholder) {
            super(plugin, placeholder.getId());
            this.placeholder = placeholder;
        }

        @Override
        public String onPlaceholderRequest(Player player, String identifier) {
            return placeholder.resolve(player, identifier);
        }
    }

    @Override
    public void register(Plugin plugin, Placeholder placeholder) {
        new OfficialPlaceholderAdapter(plugin, placeholder).hook();
    }

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
