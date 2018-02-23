package xyz.upperlevel.uppercore.game.arena;

public interface Phase {
    void onEnable(Phase previous);

    void onDisable(Phase next);
}
