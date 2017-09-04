package xyz.upperlevel.uppercore.hotbar.commands;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Registry;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.hotbar.HotbarId;

import java.util.Collection;
import java.util.StringJoiner;

import static org.bukkit.ChatColor.*;
import static xyz.upperlevel.uppercore.Uppercore.hotbars;

@WithPermission(value = "list", desc = "Allows you to list all available hotbars")
public class HotbarListCommand extends Command {

    public HotbarListCommand() {
        super("list");
        setDescription("Shows hotbars.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("plugin") @Optional Plugin plugin) {
        Collection<HotbarId> hotbars;
        if (plugin != null) {
            Registry<HotbarId> reg = hotbars().get(plugin);
            hotbars = reg.get();
        } else
            hotbars = hotbars().get();
        StringJoiner str = new StringJoiner(GRAY + ", ");
        for (HotbarId hotbar : hotbars)
            str.add(AQUA + hotbar.getGlobalId());
        if (hotbars.size() > 0)
            sender.sendMessage(GOLD + "Showing " + hotbars.size() + " hotbars: " + str);
        else
            sender.sendMessage(RED + "No hotbar found.");
    }
}
