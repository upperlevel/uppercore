package xyz.upperlevel.uppercore.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardSystem {

    private static final Map<String, Scoreboard> scoreboardsById = new HashMap<>();
    private static final Map<Plugin, ScoreboardRegistry> registriesByPlugin = new HashMap<>();

    private static final Map<Player, ScoreboardView> views = new HashMap<>();

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
        ScoreboardView res = views.computeIfAbsent(player, p -> new ScoreboardView(player, scoreboard));
        res.setScoreboard(scoreboard);
        return res;
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
}
