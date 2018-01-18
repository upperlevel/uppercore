package xyz.upperlevel.uppercore.hotbar.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.command.function.WithCommand;
import xyz.upperlevel.uppercore.command.function.WithName;
import xyz.upperlevel.uppercore.command.function.WithOptional;
import xyz.upperlevel.uppercore.command.function.WithPermission;
import xyz.upperlevel.uppercore.hotbar.HotbarId;

import static org.bukkit.ChatColor.GREEN;
import static xyz.upperlevel.uppercore.Uppercore.hotbars;

@WithPermission(value = "remove", description = "Allows you to remove a player's hotbar")
public class RemoveHotbarCommand extends Command {

    public RemoveHotbarCommand() {
        super("remove");
        setDescription("Removes a hotbar.");
    }

    @WithCommand
    public void run(CommandSender sender, @WithName("hotbar") HotbarId hotbar, @WithName("player") @WithOptional(senderType = SenderType.PLAYER) Player player) {
        if (player == null)
            player = (Player) sender;
        hotbars().view(player).removeHotbar(hotbar.get());
        sender.sendMessage(GREEN + "Hotbar \"" + hotbar.getGlobalId() + "\" removed from \"" + player.getName() + "\".");
    }
}
