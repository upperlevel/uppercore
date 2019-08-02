package xyz.upperlevel.uppercore.board;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.HashMap;
import java.util.Map;

public class BoardManager implements Listener  {
    private static final Map<Player, BoardViewer> players = new HashMap<>();
    private static final BoardManager eventHandler = new BoardManager();

    static {
        Bukkit.getPluginManager().registerEvents(eventHandler, Uppercore.plugin());
    }

    private BoardManager() {
    }

    public static void open(Player player, Board board, PlaceholderRegistry placeholderRegistry) {
        BoardViewer viewer = players.get(player);
        if (viewer == null) {
            viewer = BoardViewer.create(player);
            players.putIfAbsent(player, viewer);
        }
        viewer.setBoard(board, placeholderRegistry);
    }

    public static boolean update(Player player, PlaceholderRegistry placeholderRegistry) {
        BoardViewer viewer = players.get(player);
        if (viewer != null) {
            viewer.update(placeholderRegistry);
            return true;
        }
        return false;
    }

    public static boolean close(Player player) {
        BoardViewer viewer = players.get(player);
        if (viewer != null) {
            viewer.close();
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        players.remove(e.getPlayer());
    }
}
