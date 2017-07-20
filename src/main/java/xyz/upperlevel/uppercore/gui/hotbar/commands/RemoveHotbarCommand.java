package xyz.upperlevel.uppercore.gui.hotbar.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.gui.hotbar.Hotbar;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarSystem;

import static org.bukkit.ChatColor.GREEN;

public class RemoveHotbarCommand extends Command {

    public RemoveHotbarCommand() {
        super("remove");
        setDescription("Removes a hotbar.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("hotbar") Hotbar hotbar, @Argument("player") @Optional(sender = Sender.PLAYER) Player player) {
        if (player == null)
            player = (Player) sender;
        HotbarSystem.view(player).removeHotbar(hotbar);
        sender.sendMessage(GREEN + "Hotbar \"" + hotbar.getGlobalId() + "\" removed from \"" + player.getName() + "\".");
    }
}
