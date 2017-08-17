package xyz.upperlevel.uppercore.hotbar.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.hotbar.HotbarId;

import static org.bukkit.ChatColor.GREEN;
import static xyz.upperlevel.uppercore.Uppercore.hotbars;

@WithPermission(value = "remove", desc = "Allows you to remove a player's hotbar")
public class RemoveHotbarCommand extends Command {

    public RemoveHotbarCommand() {
        super("remove");
        setDescription("Removes a hotbar.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("hotbar") HotbarId hotbar, @Argument("player") @Optional(sender = Sender.PLAYER) Player player) {
        if (player == null)
            player = (Player) sender;
        hotbars().view(player).removeHotbar(hotbar.get());
        sender.sendMessage(GREEN + "Hotbar \"" + hotbar.getGlobalId() + "\" removed from \"" + player.getName() + "\".");
    }
}
