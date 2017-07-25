package xyz.upperlevel.uppercore.hotbar.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.hotbar.HotbarId;

import static org.bukkit.ChatColor.GREEN;
import static xyz.upperlevel.uppercore.Uppercore.hotbars;

public class GiveHotbarCommand extends Command {

    public GiveHotbarCommand() {
        super("give");
        setDescription("Gives a hotbar.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("hotbar") HotbarId hotbar, @Argument("player") @Optional(sender = Sender.PLAYER) Player player) {
        if (player == null)
            player = (Player) sender;
        hotbars().view(player).addHotbar(hotbar.get());
        sender.sendMessage(GREEN + "Hotbar \"" + hotbar.getGlobalId() + "\" added to \"" + player.getName() + "\".");
    }
}
