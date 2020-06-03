package xyz.upperlevel.uppercore.board;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

public interface BoardModel {
    /**
     * This function, depending on the implementation, may set the content of the board (title and lines).
     */
    void apply(Board board, Player player, PlaceholderRegistry<?> placeholders);

    /**
     * Hooks the given board with the model and creates a {@link Hook}.
     * Every time the instance is rendered, it also calls {@link #apply(Board, Player, PlaceholderRegistry)} in order to update the lines.
     */
    default Hook hook(Board board) {
        return new Hook(this, board);
    }

    /**
     * This class links the {@link BoardModel} with the {@link Board}.
     */
    class Hook {
        private final BoardModel model;
        private final Board board;

        private Hook(BoardModel model, Board board) {
            this.model = model;
            this.board = board;
        }

        /**
         * Applies the model and renders the {@link Board}.
         *
         * @param player       The player used as a reference for placeholders replacement.
         * @param placeholders The placeholders pool used for lines.
         */
        public void render(Player player, PlaceholderRegistry<?> placeholders) {
            if (model != null) { // Could be fake
                model.apply(board, player, placeholders);
            }
            board.render();
        }

        /**
         * Renders the board (to be sure it's not empty) and opens it to the player.
         *
         * @param player       The player used as a reference for placeholders replacement.
         * @param placeholders The placeholders pool used for lines.
         */
        public void open(Player player, PlaceholderRegistry<?> placeholders) {
            render(player, placeholders);
            board.open(player);
        }

        public static Hook fake(Board board) {
            return new Hook(null, board);
        }
    }
}
