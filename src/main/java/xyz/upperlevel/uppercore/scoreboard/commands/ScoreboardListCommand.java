package xyz.upperlevel.uppercore.scoreboard.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.Optional;
import xyz.upperlevel.uppercore.gui.hotbar.Hotbar;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarSystem;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarRegistry;
import xyz.upperlevel.uppercore.scoreboard.Scoreboard;
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
        Collection<Scoreboard> scoreboards;
        if (plugin != null) {
            ScoreboardRegistry reg = ScoreboardSystem.get(plugin);
            scoreboards = reg.getScoreboards();
        } else
            scoreboards = ScoreboardSystem.getScoreboards();
        StringJoiner str = new StringJoiner(GRAY + ", ");
        for (Scoreboard scoreboard : scoreboards)
            str.add(AQUA + scoreboard.getGlobalId());
        if (scoreboards.size() > 0)
            sender.sendMessage(GOLD + "Showing " + scoreboards.size() + " scoreboards: " + str);
        else
            sender.sendMessage(RED + "No scoreboard found.");
    }
}
