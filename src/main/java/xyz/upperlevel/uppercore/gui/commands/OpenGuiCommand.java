package xyz.upperlevel.uppercore.gui.commands;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.gui.Gui;

import static xyz.upperlevel.uppercore.Uppercore.guis;

@WithPermission(value = "open", desc = "Allows you to open a specific GUI via command")
public class OpenGuiCommand extends Command {

    public OpenGuiCommand() {
        super("open");
        setDescription("Opens a gui.");
    }

    @Executor(sender = Sender.PLAYER)
    public void run(CommandSender sender, @Argument("gui") Gui gui, @Argument("player") @Optional(sender = Sender.PLAYER) Player player) {
        if (player == null) {
            player = (Player) sender;
        }
        guis().open(player, gui);
    }
}
