package xyz.upperlevel.uppercore.arena;

import lombok.Getter;

public class PhaseManager {
    @Getter
    private Phase phase;

    public PhaseManager() {
    }

    public void setPhase(Phase phase) {
        Phase old = this.phase;
        if (old != null) {
            old.onDisable(phase);
        }
        this.phase = phase;
        if (phase != null) {
            phase.onEnable(old);
        }
    }
}
