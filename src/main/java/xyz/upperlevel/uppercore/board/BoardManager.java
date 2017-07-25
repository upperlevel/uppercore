package xyz.upperlevel.uppercore.board;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifier;
import xyz.upperlevel.uppercore.Uppercore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BoardSystem extends System<Board> {

    private static final Map<Player, BoardView> views = new HashMap<>();

    private static void onPlayerJoin(Player p) {
        views.put(p, new BoardView(p));
    }

    public static void initialize() {
        Bukkit.getOnlinePlayers().forEach(BoardSystem::onPlayerJoin);
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent e) {
                BoardSystem.onPlayerJoin(e.getPlayer());
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                views.remove(e.getPlayer());
            }
        }, Uppercore.get());
    }

    static void register(BoardRegistry registry) {
        registriesByPlugin.put(registry.getPlugin(), registry);
    }

    static void register(Identifier<Board> board) {
        scoreboardsById.put(board.getGlobalId(), board.getHandle());
    }

    public static BoardRegistry get(Plugin plugin) {
        return registriesByPlugin.get(plugin);
    }

    public static Board get(String id) {
        return scoreboardsById.get(id);
    }

    public static Identifier<Board> get(Plugin plugin, String id) {
        BoardRegistry registry = get(plugin);
        if (registry != null)
            return registry.get(id);
        return null;
    }

    public static BoardView view(Player player) {
        return views.get(player);
    }

    public static BoardView set(Player player, Board board) {
        BoardView result = views.computeIfAbsent(player, BoardView::new);
        result.setBoard(board);
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

    public static Collection<BoardRegistry> getRegistries() {
        return registriesByPlugin.values();
    }

    public static BoardRegistry subscribe(Plugin plugin) {
        return new BoardRegistry(plugin);
    }
}
