package xyz.upperlevel.uppercore.game.arena;

import lombok.Getter;

public class PhaseManager {
    @Getter
    private Phase current;

    public PhaseManager() {
    }

    public void next(Phase phase) {
        Phase old = current;
        if (old != null) {
            old.onDisable(phase);
        }
        current = phase;
        if (phase != null) {
            phase.onEnable(old);
        }
    }
}
