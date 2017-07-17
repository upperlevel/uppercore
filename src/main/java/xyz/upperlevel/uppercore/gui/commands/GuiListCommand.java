package xyz.upperlevel.uppercore.gui.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.Optional;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.gui.GuiRegistry;

import java.util.Collection;
import java.util.StringJoiner;

import static org.bukkit.ChatColor.*;

public class GuiListCommand extends Command {

    public GuiListCommand() {
        super("list");
        setDescription("Shows guis.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("plugin") @Optional Plugin plugin) {
        Collection<Gui> guis;
        if (plugin != null) {
            GuiRegistry reg = GuiManager.getRegistry(plugin);
            guis = reg.getGuis();
        } else
            guis = GuiManager.getGuis();
        StringJoiner str = new StringJoiner(GRAY + ", ");
        for (Gui gui : guis)
            str.add(AQUA + gui.getGlobalId());
        if (guis.size() > 0)
            sender.sendMessage(GOLD + "Showing " + guis.size() + " guis: " + str.toString());
        else
            sender.sendMessage(RED + "No gui found.");
    }
}
