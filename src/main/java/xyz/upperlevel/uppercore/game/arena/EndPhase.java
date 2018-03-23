package xyz.upperlevel.uppercore.game.arena;

import lombok.Getter;

public abstract class EndPhase implements Phase {
    @Getter
    private final Arena arena;

    public EndPhase(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void onEnable(Phase previous) {
    }

    @Override
    public void onDisable(Phase next) {
    }
}
