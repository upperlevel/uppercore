package xyz.upperlevel.uppercore.gui.commands;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Registry;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.gui.GuiId;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.gui.GuiRegistry;

import java.util.Collection;
import java.util.StringJoiner;

import static org.bukkit.ChatColor.*;
import static xyz.upperlevel.uppercore.Uppercore.guis;

@WithPermission(value = "list", desc = "Allows you to list all available guis")
public class GuiListCommand extends Command {

    public GuiListCommand() {
        super("list");
        setDescription("Lists all available guis.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("plugin") @Optional Plugin plugin) {
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
