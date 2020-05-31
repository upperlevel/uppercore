package xyz.upperlevel.uppercore.board;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class BoardContainer {
    private final BoardModel model;

    private final Map<Player, ArrayDeque<Board>> boardsHistory = new HashMap<>();

    public BoardContainer(BoardModel model) {
        this.model = model;
    }

    /**
     * Opens the board for the player and renders it using the given placeholders.
     * The previous board is memorized and restored when {@link #close(Player)} is called.
     *
     * @param player The player.
     * @param placeholders The placeholders used to render the board.
     *
     * @return The board created.
     */
    public Board open(Player player, PlaceholderRegistry<?> placeholders) {
        Board board = new Board();
        board.set(model, player); // Applies the model to the board.

        ArrayDeque<Board> queue = boardsHistory.get(player);
        // If the queue wasn't created, creates a new queue and adds the current scoreboard to it.
        if (queue == null) {
            queue = new ArrayDeque<>();
            queue.addFirst(new Board(player.getScoreboard()));
            boardsHistory.put(player, queue);
        }

        board.open(player, placeholders);
        queue.push(board); // Adds the board to the boards history. TODO placeholders may be remembered?

        return board;
    }

    /**
     * Updates the board referred to the player using the given placeholders.
     *
     * @param player The player.
     * @param placeholders The placeholders used to render the board.
     */
    public void update(Player player, PlaceholderRegistry<?> placeholders) {
        ArrayDeque<Board> queue = boardsHistory.get(player);
        if (queue != null) {
            Board board = queue.getFirst();
            board.update(player, placeholders);
        }
    }

    /**
     * Closes the current player's board and pops back to the previous one registered.
     *
     * @param player The player.
     */
    public void close(Player player) {
        ArrayDeque<Board> queue = boardsHistory.get(player);
        if (queue != null) { // Restores the previous board according to the history.
            queue.removeFirst();

            Board active = queue.pop();
            active.open(player);

            if (queue.isEmpty()) {
                boardsHistory.remove(player);
            }
        }
    }

    /**
     * Deletes the history referred to the player and sets his board to the one used before entering the container.
     *
     * @param player The player.
     */
    public void forgetHistory(Player player) {
        ArrayDeque<Board> history = boardsHistory.remove(player);
        if (history != null) {
            history.getLast().open(player);
        }
    }

    /**
     * Clears all the boards setting all players' board to the initial one.
     */
    public void dispose() {
        // Sets all the players' board to their first opened board (before entering the BoardContainer).
        boardsHistory.forEach((player, history) -> forgetHistory(player));
        boardsHistory.clear(); // Finally, clears all the boards.
    }
}
