package xyz.upperlevel.uppercore.board.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.board.BoardId;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.Optional;
import xyz.upperlevel.uppercore.board.Board;

import static org.bukkit.ChatColor.GREEN;

public class BoardSetCommand extends Command {

    public BoardSetCommand() {
        super("set");
        setDescription("Sets board to a player.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("board") BoardId board, @Argument("player") @Optional Player player) {
        if (player == null)
            player = (Player) sender;
        sender.sendMessage(GREEN + "Board \"" + board.getGlobalId() + "\" set to: \"" + player.getName() + "\".");
    }
}
