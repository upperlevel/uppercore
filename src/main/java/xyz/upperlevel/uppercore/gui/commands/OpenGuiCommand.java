package xyz.upperlevel.uppercore.gui.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.gui.GuiManager;

public class OpenGuiCommand extends Command {

    public OpenGuiCommand() {
        super("open");
        setDescription("Opens a gui.");
    }

    @Executor(sender = Sender.PLAYER)
    public void run(CommandSender sender, @Argument("gui") Gui gui, @Argument("player") @Optional(sender = Sender.PLAYER) Player player) {
        if (player == null)
            player = (Player) sender;
        GuiManager.openGui(player, gui);
    }
}
