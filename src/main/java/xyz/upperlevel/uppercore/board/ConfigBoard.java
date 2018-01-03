package xyz.upperlevel.uppercore.board;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.List;

public interface ConfigBoard {
    /**
     * Gets the title of the board based on the player and placeholders.
     *
     * @param holder              the player
     * @param placeholderRegistry the placeholders
     * @return the title
     */
    String getTitle(Player holder, PlaceholderRegistry placeholderRegistry);

    /**
     * Gets the lines of the board based on the player and placeholders.
     *
     * @param holder              the player
     * @param placeholderRegistry the placeholders
     * @return the title
     */
    List<String> getLines(Player holder, PlaceholderRegistry placeholderRegistry);

    default BoardView create(Player player, PlaceholderRegistry placeholderRegistry) {
        return new View(player, this, placeholderRegistry);
    }

    class View extends BoardView {
        private final ConfigBoard configBoard;
        private final PlaceholderRegistry placeholderRegistry;

        public View(Player player, ConfigBoard configBoard, PlaceholderRegistry placeholderRegistry) {
            super(player);
            this.configBoard = configBoard;
            this.placeholderRegistry = placeholderRegistry;
        }

        @Override
        public String getTitle() {
            return configBoard.getTitle(getHolder(), placeholderRegistry);
        }

        @Override
        public List<String> getLines() {
            return configBoard.getLines(getHolder(), placeholderRegistry);
        }
    }
}
