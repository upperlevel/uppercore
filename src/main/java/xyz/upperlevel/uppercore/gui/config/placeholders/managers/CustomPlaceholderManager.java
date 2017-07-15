package xyz.upperlevel.uppercore.gui.config.placeholders.managers;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.config.placeholders.PlaceholderManager;
import xyz.upperlevel.uppercore.gui.config.placeholders.managers.customs.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomPlaceholderManager implements PlaceholderManager {
    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%(.*?)%");

    private Map<String, CustomPlaceholder> placeholders = new HashMap<>();

    public CustomPlaceholderManager() {
        addDefaults();
    }

    @Override
    public boolean hasPlaceholders(String str) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(str);
        while(matcher.find())
            if(placeholders.containsKey(matcher.group(1).toLowerCase()))
                return true;
            else
                System.out.println("Cannot find " + matcher.group(1));
        return false;
    }

    @Override
    public String apply(Player player, String str) {
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(str);
        while(matcher.find()) {
            final String key = matcher.group(1);
            final String replacement = findReplacement(player, key);
            if(replacement != null)
                matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    @Override
    public String single(Player player, String str) {
        return findReplacement(player, str);
    }

    public String findReplacement(Player player, String key) {
        CustomPlaceholder pl = placeholders.get(key.toLowerCase());
        return pl != null ? pl.get(player) : null;
    }

    public void addPlaceholder(CustomPlaceholder placeholder) {
        placeholders.put(placeholder.id(), placeholder);
    }

    public void addDefaults() {
        addPlaceholder(new PlayerDisplayNamePlaceholder());
        addPlaceholder(new PlayerFoodPlaceholder());
        addPlaceholder(new PlayerHealthPlaceholder());
        addPlaceholder(new PlayerLevelPlaceholder());
        addPlaceholder(new PlayerNamePlaceholder());
        addPlaceholder(new PlayerSaturationPlaceholder());
        addPlaceholder(new VaultBalancePlaceholder());
        addPlaceholder(new PlayerWorldPlaceholder());
    }
}
