package xyz.upperlevel.uppercore.board.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.Optional;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.board.BoardSystem;
import xyz.upperlevel.uppercore.board.BoardView;

import static org.bukkit.ChatColor.GREEN;

public class ScoreboardSetCommand extends Command {

    public ScoreboardSetCommand() {
        super("set");
        setDescription("Sets board to a player.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("board") Board board, @Argument("player") @Optional Player player) {
        if (player == null)
            player = (Player) sender;
        BoardView v = BoardSystem.set(player, board);
        v.getLine(0)
                .setText("aaaaaaaaaaaaaaa§bbbbbbbbb");
        sender.sendMessage(GREEN + "Board \"" + board.getGlobalId() + "\" set to \"" + player.getName() + "\".");
    }
}
