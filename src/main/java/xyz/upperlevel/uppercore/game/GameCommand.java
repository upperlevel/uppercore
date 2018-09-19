package xyz.upperlevel.uppercore.game;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.CommandContext;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.PermissionUser;
import xyz.upperlevel.uppercore.command.SenderType;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.WithPermission;
import xyz.upperlevel.uppercore.command.functional.WithSender;

import static org.bukkit.ChatColor.GREEN;

public class GameCommand extends NodeCommand {
    @Getter
    private final Game handle;

    public GameCommand(Game handle) {
        super("game");
        this.handle = handle;
    }

    @AsCommand(
            description = "Set the game hub.",
            sender = SenderType.PLAYER
    )
    @WithPermission(
            user  = PermissionUser.OP
    )
    public void sethub(CommandContext context) {
        handle.setHub(((Player) context.sender()).getLocation());
        context.send(GREEN + "Hub set.");
    }
}
