package xyz.upperlevel.uppercore.placeholder.managers;

import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderManager;
import xyz.upperlevel.uppercore.placeholder.managers.customs.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.upperlevel.uppercore.Uppercore.get;

public class CustomPlaceholderManager implements PlaceholderManager {

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%(.*?)%");

    private Map<String, Placeholder> placeholders = new HashMap<>();

    public CustomPlaceholderManager() {
        addDefaults();
    }

    @Override
    public void register(Plugin plugin, Placeholder placeholder) {
        placeholders.put(placeholder.getId(), placeholder);
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
    public String apply(Player player, String text) {
        if (text != null && placeholders != null && !placeholders.isEmpty()) {
            Matcher placeholderMatcher = PLACEHOLDER_PATTERN.matcher(text);
            Collection<Placeholder> values = placeholders.values();
            while (true) {
                String format;
                int index;
                do {
                    do {
                        if (!placeholderMatcher.find()) {
                            return ChatColor.translateAlternateColorCodes('&', text);
                        }

                        format = placeholderMatcher.group(1);
                        index = format.indexOf("_");
                    } while (index <= 0);
                } while (index >= format.length());

                String pl = format.substring(0, index);
                String identifier = format.substring(index + 1);

                for (Placeholder value : values) {
                    if (pl.equalsIgnoreCase(value.getId()))
                        text = value.resolve(player, identifier);
                }
            }
        } else {
            return text;
        }
    }

    @Override
    public String single(Player player, String string) {
        return findReplacement(player, string);
    }

    public String findReplacement(Player player, String key) {
        Placeholder pl = placeholders.get(key.toLowerCase());
        return pl != null ? pl.resolve(player, "") : null;
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
