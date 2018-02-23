package xyz.upperlevel.uppercore.game.arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * An arena entity is a group of players (or single one).
 */
public interface Entity {
    Entity getId();

    void restore();

    void teleport(Location location);
}
