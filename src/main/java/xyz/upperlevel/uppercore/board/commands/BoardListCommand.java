package xyz.upperlevel.uppercore.board.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifier;
import xyz.upperlevel.uppercore.Registry;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.board.BoardId;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.Optional;

import java.util.Collection;
import java.util.StringJoiner;

import static org.bukkit.ChatColor.*;
import static xyz.upperlevel.uppercore.Uppercore.boards;

public class BoardListCommand extends Command {

    public BoardListCommand() {
        super("list");
        setDescription("Shows registered scoreboards.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("plugin") @Optional Plugin plugin) {
        Collection<BoardId> boards;
        if (plugin != null) {
            Registry<BoardId> reg = boards().get(plugin);
            if (reg == null) {
                sender.sendMessage(RED + "No boards registry found for: \"" + plugin.getName() + "\"");
                return;
            }
            boards = reg.get();
        } else
            boards = boards().get();
        StringJoiner str = new StringJoiner(GRAY + ", ");
        for (Identifier<Board> board : boards)
            str.add(AQUA + board.getGlobalId());
        if (boards.size() > 0)
            sender.sendMessage(GOLD + "Showing " + boards.size() + " boards: " + str);
        else
            sender.sendMessage(RED + "No board found.");
    }
}
