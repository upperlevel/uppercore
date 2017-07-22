package xyz.upperlevel.uppercore.placeholder.managers;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.managers.customs.*;

import java.util.HashMap;
import java.util.Map;

import static xyz.upperlevel.uppercore.Uppercore.get;

public class CustomPlaceholderManager extends BasePlaceholderManager {

    private Map<String, Placeholder> placeholders = new HashMap<>();

    public CustomPlaceholderManager() {
        addDefaults();
    }

    @Override
    public void register(Plugin plugin, Placeholder placeholder) {
        placeholders.put(placeholder.getId(), placeholder);
    }

    @Override
    protected Placeholder find(String id) {
        return placeholders.get(id);
    }

    @Override
    public boolean has(String string) {
        return placeholders.containsKey(string);
    }


    public void addDefaults() {
        register(get(), new PlayerDisplayNamePlaceholder());
        register(get(), new PlayerFoodPlaceholder());
        register(get(), new PlayerHealthPlaceholder());
        register(get(), new PlayerLevelPlaceholder());
        register(get(), new PlayerNamePlaceholder());
        register(get(), new PlayerSaturationPlaceholder());
        register(get(), new VaultBalancePlaceholder());
        register(get(), new PlayerWorldPlaceholder());
    }
}
