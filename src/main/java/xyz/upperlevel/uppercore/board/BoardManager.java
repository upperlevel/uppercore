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

public class BoardManager extends Manager<Board> implements Listener {
    private final Map<Player, BoardView> views = new HashMap<>();

    public BoardManager() {
        Bukkit.getPluginManager().registerEvents(this, Uppercore.get());
        Bukkit.getOnlinePlayers().forEach(this::initialize);
    }

    private void initialize(Player player) {
        views.put(player, new BoardView(player));
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
    public void on(PlayerJoinEvent event) {
        initialize(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        views.remove(event.getPlayer());
    }
}
