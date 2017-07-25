package xyz.upperlevel.uppercore.board;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifiable;
import xyz.upperlevel.uppercore.Registry;

public class BoardRegistry extends Registry<Board> {
    public BoardRegistry(Plugin plugin) {
        super(plugin, "boards");
        BoardSystem.register(this);
    }

    @Override
    public Identifiable<Board> register(String id, Board board) {
        Identifiable<Board> result = super.register(id, board);
        BoardSystem.register(result);
        return result;
    }
}