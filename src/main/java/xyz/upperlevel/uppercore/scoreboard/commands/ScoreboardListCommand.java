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
        Collection<Hotbar> hotbars;
        if (plugin != null) {
            HotbarRegistry reg = HotbarSystem.getRegistry(plugin);
            hotbars = reg.getHotbars();
        } else
            hotbars = HotbarSystem.getHotbars();
        StringJoiner str = new StringJoiner(GRAY + ", ");
        for (Hotbar hotbar : hotbars)
            str.add(AQUA + hotbar.getGlobalId());
        if (hotbars.size() > 0)
            sender.sendMessage(GOLD + "Showing " + hotbars.size() + " hotbars: " + str);
        else
            sender.sendMessage(RED + "No hotbar found.");
    }
}
