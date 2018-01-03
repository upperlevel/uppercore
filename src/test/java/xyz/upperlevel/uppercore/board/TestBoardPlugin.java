package xyz.upperlevel.uppercore.board;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple plugin with the only scope of opening a scoreboard per player
 * with holder's information and seconds passed since the enable of the plugin.
 */
public class TestBoardPlugin extends JavaPlugin implements Listener {
    private static TestBoardPlugin instance;
    private long startedAt;

    // The board class
    private TestBoard myTestBoard;

    // We'll store here all the boards opened to the players
    private List<BoardView> views = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        // When the plugin enables, we save the millis
        startedAt = System.currentTimeMillis();

        // Scoreboard initialization
        myTestBoard = new TestBoard();

        // Opens the board to current online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            BoardView viewForPlayer = myTestBoard.create(player);
            views.add(viewForPlayer);
        }

        Bukkit.getPluginManager().registerEvents(this, this);

        // Each second we want to update the timer contained in the board
        new BukkitRunnable() {
            @Override
            public void run() {
                for (BoardView view : views) {
                    view.update();
                }
            }
        }.runTaskTimer(this, 0, 20);
    }

    /**
     * Simply counts seconds passed since plugin enable.
     */
    public int getSecondsSinceStart() {
        return (int) Math.floor((System.currentTimeMillis() - startedAt) / 1000.0);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        // Opens the board to joined players
        BoardView viewForJoinedPlayer = myTestBoard.create(e.getPlayer());
        views.add(viewForJoinedPlayer);
    }

    public static TestBoardPlugin getInstance() {
        return instance;
    }
}
