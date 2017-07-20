package xyz.upperlevel.uppercore.scoreboard.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.Optional;
import xyz.upperlevel.uppercore.scoreboard.Board;
import xyz.upperlevel.uppercore.scoreboard.ScoreboardRegistry;
import xyz.upperlevel.uppercore.scoreboard.ScoreboardSystem;

import java.util.Collection;
import java.util.StringJoiner;

import static org.bukkit.ChatColor.*;

public class ScoreboardListCommand extends Command {

    public ScoreboardListCommand() {
        super("list");
        setDescription("Shows registered scoreboards.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("plugin") @Optional Plugin plugin) {
        Collection<Board> boards;
        if (plugin != null) {
            ScoreboardRegistry reg = ScoreboardSystem.get(plugin);
            boards = reg.getScoreboards();
        } else
            boards = ScoreboardSystem.getScoreboards();
        StringJoiner str = new StringJoiner(GRAY + ", ");
        for (Board board : boards)
            str.add(AQUA + board.getGlobalId());
        if (boards.size() > 0)
            sender.sendMessage(GOLD + "Showing " + boards.size() + " boards: " + str);
        else
            sender.sendMessage(RED + "No scoreboard found.");
    }
}
