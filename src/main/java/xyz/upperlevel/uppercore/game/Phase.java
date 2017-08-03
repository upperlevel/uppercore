package xyz.upperlevel.uppercore.game;

public interface Phase {

    void onEnable(Phase previous);

    void onDisable(Phase next);
}
