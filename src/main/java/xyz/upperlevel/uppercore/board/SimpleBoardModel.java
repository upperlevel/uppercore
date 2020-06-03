package xyz.upperlevel.uppercore.board;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class represents an implementation of the BoardModel that supports config loading
 * and uses placeholders for both title and lines.
 */
public class SimpleBoardModel implements BoardModel {
    @Getter
    private PlaceholderValue<String> title;

    @Getter
    private List<PlaceholderValue<String>> lines;

    @Getter
    private int updateInterval;

    @ConfigConstructor
    public SimpleBoardModel(
            @ConfigProperty("title") PlaceholderValue<String> title,
            @ConfigProperty("lines") List<PlaceholderValue<String>> lines,
            @ConfigProperty("update-interval") Optional<Integer> updateInterval
    ) {
        this.title = title;
        this.lines = lines;
        updateInterval.ifPresent(value -> this.updateInterval = value);
    }

    @Override
    public void apply(Board board, Player player, PlaceholderRegistry<?> placeholders) {
        board.setTitle(title.resolve(player, placeholders));
        board.setLines(
                lines.stream()
                        .map(line -> line.resolve(player, placeholders))
                        .collect(Collectors.toList())
        );
    }
}
