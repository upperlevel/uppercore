package xyz.upperlevel.uppercore.board;

import org.bukkit.entity.Player;

import java.util.List;

public interface Board {
    /**
     * Gets the title of the board based on the player.
     *
     * @param holder the player
     * @return the title
     */
    String getTitle(Player holder);

    /**
     * Gets the lines of the board based on the player.
     *
     * @param holder the player
     * @return the lines
     */
    List<String> getLines(Player holder);

    /**
     * Shows the board to the player.
     *
     * @param player the player
     */
    default BoardView create(Player player) {
        return new View(player, this);
    }

    class View extends BoardView {
        private final Board board;

        public View(Player player, Board board) {
            super(player);
            this.board = board;
        }

        @Override
        public String getTitle() {
            return board.getTitle(getHolder());
        }

        @Override
        public List<String> getLines() {
            return board.getLines(getHolder());
        }
    }
}
