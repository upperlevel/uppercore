package xyz.upperlevel.uppercore.arena;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PhaseManager extends Phase {
    @Getter
    private Phase phase;

    public PhaseManager(String id) {
        super(id);
    }

    public void setPhase(Phase phase) {
        Phase old = this.phase;
        if (old != null) {
            old.onDisable(phase);
        }
        this.phase = phase;
        if (phase != null) {
            phase.setPhaseManager(this);
            phase.onEnable(old);
        }
    }

    public String getPhaseName() {
        if (this.phase == null) return "None";
        return this.phase.getName();
    }

    @Override
    public void onDisable(Phase next) {
        if (phase != null) {
            phase.onDisable(null); // Disables also the phase that is currently enabled within this manager.
        }
        super.onDisable(next);
    }

    @Override
    public boolean onJoin(Player player) {
        super.onJoin(player);
        if (phase != null) {
            return phase.onJoin(player);
        }
        return false;
    }

    @Override
    public boolean onQuit(Player player) {
        super.onQuit(player);
        if (phase != null) {
            return phase.onQuit(player);
        }
        return false;
    }
}
