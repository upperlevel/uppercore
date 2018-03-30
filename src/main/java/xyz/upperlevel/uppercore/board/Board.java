package xyz.upperlevel.uppercore.board;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.List;

/**
 * This is the base interface for any board.
 * A Board object should provide the title and the lines of the board
 * given the {@link Player} and the {@link PlaceholderRegistry}.
 * The Board class does not manage view by itself as it is the
 * duty of {@link BoardView}. If a Board wants to customize its view
 * she can override the {@link #create(Player, PlaceholderRegistry)}
 * method and instantiate a different {@link BoardView} implementation.
 */
public interface Board {
    /**
     * Gets the title of the board based on the player.
     *
     * @param holder the player
     * @return the title
     */
    String getTitle(Player holder, PlaceholderRegistry placeholders);

    /**
     * Gets the lines of the board based on the player.
     *
     * @param holder the player
     * @return the lines
     */
    List<String> getLines(Player holder, PlaceholderRegistry placeholders);

    /**
     * Shows the board to the player.
     *
     * @param player the player
     * @param placeholderRegistry the placeholderRegistry to use
     */
    default BoardView create(Player player, PlaceholderRegistry placeholderRegistry) {
        return new View(player, this, placeholderRegistry);
    }

    /**
     * Shows the board to the player.
     *
     * @param player the player
     */
    default BoardView create(Player player) {
        return create(player, PlaceholderRegistry.def());
    }

    class View extends BoardView {
        @Getter
        private final Board board;

        @Getter
        @Setter
        private PlaceholderRegistry placeholderRegistry;

        public View(Player player, Board board, PlaceholderRegistry placeholderRegistry) {
            super(player);
            this.board = board;
            this.placeholderRegistry = placeholderRegistry;
        }

        @Override
        public String getTitle() {
            return board.getTitle(getHolder(), placeholderRegistry);
        }

        @Override
        public List<String> getLines() {
            return board.getLines(getHolder(), placeholderRegistry);
        }
    }
}
