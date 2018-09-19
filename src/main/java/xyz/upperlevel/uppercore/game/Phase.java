package xyz.upperlevel.uppercore.game;

public interface Phase {
    void onEnable(Phase previousPhase);

    void onDisable(Phase nextPhase);
}
