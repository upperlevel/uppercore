package xyz.upperlevel.uppercore.board.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.Optional;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.board.BoardRegistry;
import xyz.upperlevel.uppercore.board.BoardSystem;

import java.util.Collection;
import java.util.StringJoiner;

import static org.bukkit.ChatColor.*;

public class BoardListCommand extends Command {

    public BoardListCommand() {
        super("list");
        setDescription("Shows registered scoreboards.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("plugin") @Optional Plugin plugin) {
        Collection<Board> boards;
        if (plugin != null) {
            BoardRegistry reg = BoardSystem.get(plugin);
            boards = reg.getScoreboards();
        } else
            boards = BoardSystem.getScoreboards();
        StringJoiner str = new StringJoiner(GRAY + ", ");
        for (Board board : boards)
            str.add(AQUA + board.getGlobalId());
        if (boards.size() > 0)
            sender.sendMessage(GOLD + "Showing " + boards.size() + " boards: " + str);
        else
            sender.sendMessage(RED + "No board found.");
    }
}
