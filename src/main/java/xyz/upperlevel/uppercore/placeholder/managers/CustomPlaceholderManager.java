package xyz.upperlevel.uppercore.placeholder.managers;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderManager;
import xyz.upperlevel.uppercore.placeholder.managers.customs.*;

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
    public boolean hasPlaceholders(String string) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(string);
        while (matcher.find())
            if (placeholders.containsKey(matcher.group(1).toLowerCase()))
                return true;
            else
                System.out.println("Cannot find " + matcher.group(1));
        return false;
    }

    @Override
    public String apply(Player player, String string) {
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(string);
        while (matcher.find()) {
            final String key = matcher.group(1);
            final String replacement = findReplacement(player, key);
            if (replacement != null)
                matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    @Override
    public String single(Player player, String string) {
        return findReplacement(player, string);
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
