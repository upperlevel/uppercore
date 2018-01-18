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

@WithPermission(value = "give", description = "Allows you to give a specific hotbar via command")
public class GiveHotbarCommand extends Command {

    public GiveHotbarCommand() {
        super("give");
        setDescription("Gives a hotbar.");
    }

    @WithCommand
    public void run(CommandSender sender, @WithName("hotbar") HotbarId hotbar, @WithName("player") @WithOptional(senderType = SenderType.PLAYER) Player player) {
        if (player == null)
            player = (Player) sender;
        hotbars().view(player).addHotbar(hotbar.get());
        sender.sendMessage(GREEN + "Hotbar \"" + hotbar.getGlobalId() + "\" added to \"" + player.getName() + "\".");
    }
}
