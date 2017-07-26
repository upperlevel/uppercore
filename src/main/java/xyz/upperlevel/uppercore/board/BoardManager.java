package xyz.upperlevel.uppercore.board;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.uppercore.Manager;
import xyz.upperlevel.uppercore.Uppercore;

import java.util.HashMap;
import java.util.Map;

public class BoardManager extends Manager<BoardId> implements Listener {
    private final Map<Player, BoardView> views = new HashMap<>();
    private BoardId onJoin;

    public BoardManager() {
        Bukkit.getPluginManager().registerEvents(this, Uppercore.get());
        Bukkit.getOnlinePlayers().forEach(this::initialize);
    }

    @Override
    public void register(BoardId board) {
        super.register(board);
        if (board.isOnJoin())
            onJoin = board;
    }

    @Override
    public BoardId unregister(String id) {
        BoardId result = super.unregister(id);
        if (onJoin != null && onJoin.equals(result))
            onJoin = null;
        return result;
    }

    private void initialize(Player player) {
        BoardView view = new BoardView(player);
        views.put(player, view);
        if (onJoin != null)
            view.setBoard(onJoin.get());
    }

    private void quitPlayer(Player player) {
        BoardView v = views.remove(player);
        if (v != null) v.clear();
    }

    public BoardView open(Player player, Board board) {
        BoardView view = view(player);
        view.setBoard(board);
        return view;
    }

    public BoardView close(Player player) {
        BoardView view = views.get(player);
        if (view != null)
            view.clear();
        return view;
    }

    public BoardView view(Player player) {
        return views.computeIfAbsent(player, BoardView::new);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        initialize(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        quitPlayer(event.getPlayer());
    }
}
