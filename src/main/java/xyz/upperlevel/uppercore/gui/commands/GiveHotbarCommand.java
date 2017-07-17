package xyz.upperlevel.uppercore.gui.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.gui.hotbar.Hotbar;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarManager;

import static org.bukkit.ChatColor.GREEN;

public class GiveHotbarCommand extends Command {

    public GiveHotbarCommand() {
        super("give");
        setDescription("Gives a hotbar.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("hotbar") Hotbar hotbar, @Argument("player") @Optional(sender = Sender.PLAYER) Player player) {
        if (player == null)
            player = (Player) sender;
        HotbarManager.getView(player).addHotbar(hotbar);
        sender.sendMessage(GREEN + "Hotbar \"" + hotbar.getGlobalId() + "\" added to \"" + player.getName() + "\".");
    }
}
