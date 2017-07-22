package xyz.upperlevel.uppercore.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ScoreboardSystem {

    private static final Map<String, Board> scoreboardsById = new HashMap<>();
    private static final Map<Plugin, ScoreboardRegistry> registriesByPlugin = new HashMap<>();

    private static final Map<Player, BoardView> views = new HashMap<>();

    private static void onPlayerJoin(Player p) {
        views.put(p, new BoardView(p));
    }

    public static void initialize() {
        Bukkit.getOnlinePlayers().forEach(ScoreboardSystem::onPlayerJoin);
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent e) {
                ScoreboardSystem.onPlayerJoin(e.getPlayer());
                System.out.println("REGISTERED " + e.getPlayer().getName() + "...");
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                views.remove(e.getPlayer());
            }
        }, Uppercore.get());
    }

    static void register(Board board) {
        scoreboardsById.put(board.getGlobalId(), board);
    }

    static void register(ScoreboardRegistry registry) {
        registriesByPlugin.put(registry.getPlugin(), registry);
    }

    public static ScoreboardRegistry get(Plugin plugin) {
        return registriesByPlugin.get(plugin);
    }

    public static Board get(String id) {
        return scoreboardsById.get(id);
    }

    public static Board get(Plugin plugin, String id) {
        ScoreboardRegistry registry = get(plugin);
        if (registry != null)
            return registry.get(id);
        return null;
    }

    public static BoardView view(Player player) {
        return views.get(player);
    }

    public static BoardView set(Player player, Board board) {
        BoardView result = views.computeIfAbsent(player, BoardView::new);
        result.setScoreboard(board);
        return result;
    }

    public static void remove(Player player) {
        BoardView res = view(player);
        if (res == null) return;
        res.clear();
    }

    public static Collection<Board> getScoreboards() {
        return scoreboardsById.values();
    }

    public static Collection<ScoreboardRegistry> getRegistries() {
        return registriesByPlugin.values();
    }

    public static ScoreboardRegistry subscribe(Plugin plugin) {
        return new ScoreboardRegistry(plugin);
    }
}
