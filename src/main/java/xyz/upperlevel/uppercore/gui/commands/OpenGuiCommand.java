package xyz.upperlevel.uppercore.gui.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.command.function.WithCommand;
import xyz.upperlevel.uppercore.command.function.WithName;
import xyz.upperlevel.uppercore.command.function.WithOptional;
import xyz.upperlevel.uppercore.command.function.WithPermission;
import xyz.upperlevel.uppercore.gui.Gui;

import static xyz.upperlevel.uppercore.Uppercore.guis;

@WithPermission(value = "open", description = "Allows you to openBoard a specific GUI via command")
public class OpenGuiCommand extends Command {

    public OpenGuiCommand() {
        super("open");
        setDescription("Opens a gui.");
    }

    @WithCommand(sender = SenderType.PLAYER)
    public void run(CommandSender sender, @WithName("gui") Gui gui, @WithName("player") @WithOptional(senderType = SenderType.PLAYER) Player player) {
        if (player == null) {
            player = (Player) sender;
        }
        guis().open(player, gui);
    }
}
