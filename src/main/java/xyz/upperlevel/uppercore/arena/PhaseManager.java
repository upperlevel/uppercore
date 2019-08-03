package xyz.upperlevel.uppercore.arena;

import lombok.Getter;
import org.bukkit.entity.Player;

public class PhaseManager {
    @Getter
    private Phase phase;

    public PhaseManager() {
    }

    public void onPlayerJoin(Player player) {
        if (phase != null) {
            phase.onPlayerJoin(player);
        }
    }

    public void onPlayerQuit(Player player) {
        if (phase != null) {
            phase.onPlayerQuit(player);
        }
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
