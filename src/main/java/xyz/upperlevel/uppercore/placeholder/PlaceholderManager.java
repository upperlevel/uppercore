package xyz.upperlevel.uppercore.placeholder;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface PlaceholderManager {

    void register(Plugin plugin, Placeholder placeholder);

    boolean hasPlaceholders(String str, Set<String> localRaw);

    default boolean hasPlaceholders(String str, Map<String, Placeholder> local) {
        return hasPlaceholders(str, local.keySet());
    }

    default boolean hasPlaceholders(String str) {
        return hasPlaceholders(str, Collections.emptyMap());
    }

    String apply(Player player, String text);

    String apply(Player player, String text, Map<String, Placeholder> local);

    default String applyRaw(Player player, String text, Map<String, String> local) {
        return apply(
                player,
                text,
                local.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> Placeholder.constant(e.getValue())
                        ))
        );
    }

    default String single(Player player, String string) {
        return apply(player, '%' + string + '%');
    }

    default String single(Player player, String string, Map<String, Placeholder> local) {
        return apply(player, '%' + string + '%', local);
    }

    default String singleRaw(Player player, String string, Map<String, String> local) {
        return applyRaw(player, '%' + string + '%', local);
    }
}
