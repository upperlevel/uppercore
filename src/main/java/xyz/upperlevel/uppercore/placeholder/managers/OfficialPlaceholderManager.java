package xyz.upperlevel.uppercore.placeholder.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.placeholder.Placeholder;

import java.util.Map;

public class OfficialPlaceholderManager extends BasePlaceholderManager {

    private final Map<String, PlaceholderHook> placeholders;

    public OfficialPlaceholderManager() {
        this.placeholders = PlaceholderAPI.getPlaceholders();
    }

    @Override
    public void register(Plugin plugin, Placeholder placeholder) {
        new OfficialPlaceholderAdapter(plugin, placeholder).hook();
    }

    public boolean has(String id) {
        return placeholders.containsKey(id);
    }

    public Placeholder find(String id) {
        PlaceholderHook hook = placeholders.get(id);
        return new Placeholder() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String resolve(Player player, String arg) {
                return hook.onPlaceholderRequest(player, arg);
            }
        };
    }


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
}
