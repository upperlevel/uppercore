package xyz.upperlevel.uppercore.arena;

import org.bukkit.entity.Player;

/**
 * Every game is composed by one or multiple phases,
 * every phase can even contain multiple phases (a {@link NodePhase}) forming a tree.
 *
 * Here's an example of a common phase structure.
 * A common game (like quake for example) is composed of 3 main phases:
 * - Lobby: The joined players wait for other players to play with
 * - Countdown: The countdown is running, other players can join and the game isn't started yet
 * - Playing: The real game, everyone is ported to the playing field and the game starts
 * - Ending: The game ended, the results are shown to the players.
 *
 * This can be drawn as a state machine making it easier to design and plan the actual game.
 * A NodePhase is used to group together phases with common rules,
 * (ex: both in the game and the ending phases no player can join)
 */
public interface Phase {
    void onEnable(Phase previousPhase);

    void onDisable(Phase nextPhase);

    void onPlayerJoin(Player player);

    void onPlayerQuit(Player player);
}
