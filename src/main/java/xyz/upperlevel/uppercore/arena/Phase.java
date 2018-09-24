package xyz.upperlevel.uppercore.arena;

public interface Phase {
    void onEnable(Phase previousPhase);

    void onDisable(Phase nextPhase);
}
