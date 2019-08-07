package xyz.upperlevel.uppercore.arena;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import xyz.upperlevel.uppercore.Uppercore;

/**
 * Every game is composed by one or multiple phases,
 * every phase can even contain multiple phases (a {@link NodePhase}) forming a tree.
 * <p>
 * Here's an example of a common phase structure.
 * A common game (like quake for example) is composed of 3 main phases:
 * - Lobby: The joined players wait for other players to play with
 * - Countdown: The countdown is running, other players can join and the game isn't started yet
 * - Playing: The real game, everyone is ported to the playing field and the game starts
 * - Ending: The game ended, the results are shown to the players.
 * <p>
 * This can be drawn as a state machine making it easier to design and plan the actual game.
 * A NodePhase is used to group together phases with common rules,
 * (ex: both in the game and the ending phases no player can join)
 */
public class Phase implements Listener {
    @Getter
    private final String id;

    @Getter
    private PhaseManager phaseManager;

    public Phase(String id) {
        this.id = id;
    }

    void setPhaseManager(PhaseManager phaseManager) {
        this.phaseManager = phaseManager;
    }

    public void onEnable(Phase previous) {
        Uppercore.logger().info("Enabling phase: " + id);
        Bukkit.getPluginManager().registerEvents(this, Uppercore.getPlugin());
    }

    public void onDisable(Phase next) {
        Uppercore.logger().info("Disabling phase: " + id);
        HandlerList.unregisterAll(this);
    }

    public boolean onJoin(Player player) {
        Uppercore.logger().info("Player joined phase: " + id);
        return false;
    }

    public boolean onQuit(Player player) {
        Uppercore.logger().info("Player quit phase: " + id);
        return false;
    }
}
