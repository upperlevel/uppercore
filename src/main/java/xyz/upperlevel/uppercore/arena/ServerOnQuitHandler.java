package xyz.upperlevel.uppercore.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ServerOnQuitHandler implements OnQuitHandler {
    @Getter
    @Setter
    private Location hub;

    public ServerOnQuitHandler(Location hub) {
        this.hub = hub;
    }

    @Override
    public void handle(Player player) {
        player.teleport(hub);
    }
}
