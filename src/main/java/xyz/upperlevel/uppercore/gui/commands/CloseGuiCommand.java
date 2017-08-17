package xyz.upperlevel.uppercore.gui.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;

import static org.bukkit.ChatColor.GREEN;
import static xyz.upperlevel.uppercore.Uppercore.guis;

@WithPermission(value = "close", desc = "Allows you to close a player's GUI")
public class CloseGuiCommand extends Command {

    public CloseGuiCommand() {
        super("close");
        setDescription("Closes a gui.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("player") Player player) {
        guis().close(player);
        sender.sendMessage(GREEN + "Gui closed!");
    }
}
