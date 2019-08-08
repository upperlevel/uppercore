package xyz.upperlevel.uppercore.board;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

public class BoardHolder {
    @Getter
    private final Player player;

    @Getter
    private final BoardModel model;

    @Getter
    private final Board board;

    public BoardHolder(Player player, BoardModel model) {
        this.player = player;
        this.model = model;
        this.board = new Board();
    }

    public void update(PlaceholderRegistry placeholders) {
        board.set(model, player);
        board.update(player, placeholders);
    }

    public void update() {
        update(PlaceholderRegistry.def());
    }

    public void open(PlaceholderRegistry placeholders) {
        update(placeholders);
        board.open(player);
    }

    public void open() {
        open(PlaceholderRegistry.def());
    }
}
