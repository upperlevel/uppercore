package xyz.upperlevel.uppercore.board;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifier;
import xyz.upperlevel.uppercore.Registry;
import xyz.upperlevel.uppercore.Uppercore;

import static xyz.upperlevel.uppercore.Uppercore.boards;

public class BoardRegistry extends Registry<Board> {
    public BoardRegistry(Plugin plugin) {
        super(plugin, "boards");
        boards().register(this);
    }

    @Override
    public Identifier<Board> register(String id, Board board) {
        Identifier<Board> result = super.register(id, board);
        boards().register(result);
        return result;
    }
}