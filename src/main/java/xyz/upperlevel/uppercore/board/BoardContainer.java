package xyz.upperlevel.uppercore.board;

import lombok.val;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.HashMap;
import java.util.Map;

public class BoardContainer {
    private final BoardModel model;
    private final Map<Player, Board> boards = new HashMap<>();

    public BoardContainer(BoardModel model) {
        this.model = model;
    }

    public void update(Player player, PlaceholderRegistry placeholders) {
        Board board = boards.get(player);
        board.set(model, player);
        if (board.isOpened(player)) {
            board.update(player, placeholders);
        } else {
            board.open(player, placeholders);
        }
    }

    public void update(Player player) {
        update(player, PlaceholderRegistry.def());
    }

    public void update(PlaceholderRegistry placeholders) {
        boards.forEach((player, board) -> update(player, placeholders));
    }

    public void update() {
        update(PlaceholderRegistry.def());
    }

    public Board open(Player player, PlaceholderRegistry placeholders) {
        Board board = new Board();
        boards.put(player, board);
        update(player, placeholders);

        return board;
    }

    public Board open(Player player) {
        return open(player, PlaceholderRegistry.def());
    }

    public void close(Player player) {
        boards.remove(player);
    }
}
