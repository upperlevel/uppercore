package xyz.upperlevel.uppercore.game.arena;

/**
 * This is the core phase.
 * Should handle game logic.
 */
public abstract class GamePhase implements Phase {
    @Override
    public void onEnable(Phase previous) {
    }

    @Override
    public void onDisable(Phase next) {
    }

    /**
     * Gets {@link EndPhase} implementation.
     */
    public abstract EndPhase getEndPhase();
}
