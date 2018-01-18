package xyz.upperlevel.uppercore.gui.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.command.function.WithCommand;
import xyz.upperlevel.uppercore.command.function.WithName;
import xyz.upperlevel.uppercore.command.function.WithPermission;

import static org.bukkit.ChatColor.GREEN;
import static xyz.upperlevel.uppercore.Uppercore.guis;

@WithPermission(value = "close", description = "Allows you to close a player's GUI")
public class CloseGuiCommand extends Command {

    public CloseGuiCommand() {
        super("close");
        setDescription("Closes a gui.");
    }

    @WithCommand
    public void run(CommandSender sender, @WithName("player") Player player) {
        guis().close(player);
        sender.sendMessage(GREEN + "Gui closed!");
    }
}
