package xyz.upperlevel.uppercore.board.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.Optional;
import xyz.upperlevel.uppercore.board.BoardManager;

import static org.bukkit.ChatColor.GREEN;
import static xyz.upperlevel.uppercore.Uppercore.boards;

public class BoardRemoveCommand extends Command {

    public BoardRemoveCommand() {
        super("remove");
        setDescription("Removes opened board.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("player") @Optional Player player) {
        if (player == null)
            player = (Player) sender;
        boards().view(player);
        sender.sendMessage(GREEN  + "Board clear to \"" + player.getName() + "\".");
    }
}
