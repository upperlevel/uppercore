package xyz.upperlevel.uppercore.scoreboard.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.Optional;
import xyz.upperlevel.uppercore.scoreboard.Scoreboard;
import xyz.upperlevel.uppercore.scoreboard.ScoreboardSystem;

import static org.bukkit.ChatColor.GREEN;

public class ScoreboardSetCommand extends Command {

    public ScoreboardSetCommand() {
        super("set");
        setDescription("Sets scoreboard to a player.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("scoreboard") Scoreboard scoreboard, @Argument("player") @Optional Player player) {
        if (player == null)
            player = (Player) sender;
        ScoreboardSystem.remove(player);
        sender.sendMessage(GREEN + "Scoreboard \"" + scoreboard.getGlobalId() + "\" set to \"" + player.getName() + "\".");
    }
}
