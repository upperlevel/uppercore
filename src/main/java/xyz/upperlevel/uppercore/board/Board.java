package xyz.upperlevel.uppercore.board;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.stream.Collectors;

public interface Board {
    String getTitle(Player player, PlaceholderRegistry placeholderRegistry);

    List<String> getLines(Player player, PlaceholderRegistry placeholderRegistry);

    int getAutoUpdateInterval();

    static Board simple(String title, List<String> lines) {
        return new Board() {
            @Override
            public String getTitle(Player player, PlaceholderRegistry placeholderRegistry) {
                return title;
            }

            @Override
            public List<String> getLines(Player player, PlaceholderRegistry placeholderRegistry) {
                return lines;
            }

            @Override
            public int getAutoUpdateInterval() {
                return -1;
            }
        };
    }
}
