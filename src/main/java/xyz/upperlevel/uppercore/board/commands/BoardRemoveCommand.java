package xyz.upperlevel.uppercore.board.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.Optional;
import xyz.upperlevel.uppercore.board.BoardSystem;

import static org.bukkit.ChatColor.GREEN;

public class ScoreboardRemoveCommand extends Command {

    public ScoreboardRemoveCommand() {
        super("remove");
        setDescription("Removes opened board.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("player") @Optional Player player) {
        if (player == null)
            player = (Player) sender;
        BoardSystem.remove(player);
        sender.sendMessage(GREEN  + "Board clear to \"" + player.getName() + "\".");
    }
}
