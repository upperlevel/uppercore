package xyz.upperlevel.uppercore.board;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Registry;

import static xyz.upperlevel.uppercore.Uppercore.boards;

public class BoardRegistry extends Registry<BoardId> {
    public BoardRegistry(Plugin plugin) {
        super(plugin, "boards");
        boards().register(this);
    }

    @Override
    public void register(BoardId board) {
        super.register(board);
        boards().register(board);
    }

    @Override
    public BoardId unregister(String id) {
        BoardId result = super.unregister(id);
        if (result != null)
            boards().unregister(result);
        return result;
    }
}