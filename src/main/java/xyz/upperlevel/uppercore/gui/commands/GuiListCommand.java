package xyz.upperlevel.uppercore.gui.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Registry;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.command.function.WithCommand;
import xyz.upperlevel.uppercore.command.function.WithName;
import xyz.upperlevel.uppercore.command.function.WithOptional;
import xyz.upperlevel.uppercore.command.function.WithPermission;
import xyz.upperlevel.uppercore.gui.GuiId;

import java.util.Collection;
import java.util.StringJoiner;

import static org.bukkit.ChatColor.*;
import static xyz.upperlevel.uppercore.Uppercore.guis;

@WithPermission(value = "list", description = "Allows you to list all available guis")
public class GuiListCommand extends Command {

    public GuiListCommand() {
        super("list");
        setDescription("Lists all available guis.");
    }

    @WithCommand
    public void run(CommandSender sender, @WithName("plugin") @WithOptional Plugin plugin) {
        Collection<GuiId> guis;
        if (plugin != null) {
            Registry<GuiId> reg = guis().get(plugin);
            guis = reg.get();
        } else
            guis = guis().get();
        StringJoiner str = new StringJoiner(GRAY + ", ");
        for (GuiId gui : guis)
            str.add(AQUA + gui.getGlobalId());
        if (guis.size() > 0)
            sender.sendMessage(GOLD + "Showing " + guis.size() + " guis: " + str.toString());
        else
            sender.sendMessage(RED + "No gui found.");
    }
}
