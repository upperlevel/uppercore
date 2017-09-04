package xyz.upperlevel.uppercore.game;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;

public class PhaseManager {

    @Getter
    private Phase phase;

    public void setPhase(Phase phase) {
        Phase old = this.phase;
        if (old != null)
            old.onDisable(phase);
        this.phase = phase;
        if (phase != null)
            phase.onEnable(old);
    }
}
