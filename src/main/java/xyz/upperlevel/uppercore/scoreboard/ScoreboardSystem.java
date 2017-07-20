package xyz.upperlevel.uppercore.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ScoreboardSystem {

    private static final Map<String, Scoreboard> scoreboardsById = new HashMap<>();
    private static final Map<Plugin, ScoreboardRegistry> registriesByPlugin = new HashMap<>();

    private static final Map<Player, ScoreboardView> views = new HashMap<>();

    public static void initialize() {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                views.remove(e.getPlayer());
            }
        }, Uppercore.get());
    }

    static void register(Scoreboard scoreboard) {
        scoreboardsById.put(scoreboard.getGlobalId(), scoreboard);
    }

    static void register(ScoreboardRegistry registry) {
        registriesByPlugin.put(registry.getPlugin(), registry);
    }

    public static ScoreboardRegistry get(Plugin plugin) {
        return registriesByPlugin.get(plugin);
    }

    public static Scoreboard get(String id) {
        return scoreboardsById.get(id);
    }

    public static Scoreboard get(Plugin plugin, String id) {
        ScoreboardRegistry registry = get(plugin);
        if (registry != null)
            return registry.get(id);
        return null;
    }

    public static ScoreboardView getView(Player player) {
        return views.get(player);
    }

    public static ScoreboardView set(Player player, Scoreboard scoreboard) {
        ScoreboardView result = views.computeIfAbsent(player, ScoreboardView::new);
        result.setScoreboard(scoreboard);
        return result;
    }

    public static void remove(Player player) {
        ScoreboardView res = getView(player);
        if (res == null) return;
        res.clear();
    }

    public static Collection<Scoreboard> getScoreboards() {
        return scoreboardsById.values();
    }

    public static Collection<ScoreboardRegistry> getRegistries() {
        return registriesByPlugin.values();
    }

    public static ScoreboardRegistry subscribe(Plugin plugin) {
        return new ScoreboardRegistry(plugin);
    }
}
