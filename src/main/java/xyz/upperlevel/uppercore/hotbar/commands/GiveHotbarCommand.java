package xyz.upperlevel.uppercore.hotbar.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.hotbar.HotbarId;

import static org.bukkit.ChatColor.GREEN;
import static xyz.upperlevel.uppercore.Uppercore.hotbars;

@WithPermission(value = "give", desc = "Allows you to give a specific hotbar via command")
public class GiveHotbarCommand extends Command {

    private Permission other;

    public GiveHotbarCommand() {
        super("give");
        setDescription("Gives a hotbar.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("hotbar") HotbarId hotbar, @Argument("player") @Optional(sender = Sender.PLAYER) Player player) {
        if (player == null)
            player = (Player) sender;
        else if(other != null && other != sender && !sender.hasPermission(other)) {
            sender.sendMessage(ChatColor.RED + "You can't give hotbars to other players");
            return;
        }
        hotbars().view(player).addHotbar(hotbar.get());
        sender.sendMessage(GREEN + "Hotbar \"" + hotbar.getGlobalId() + "\" added to \"" + player.getName() + "\".");
    }

    @Override
    public void calcPermissions() {
        super.calcPermissions();
        Permission def = getPermission();
        if(def != null) {
            other = new Permission(def.getName() + ".other", "Allows you to give hotbars to other players", PermissionDefault.OP);
            if(getParent() != null)
                other.addParent(getParent().getAnyPerm(), true);
        } else other = null;
    }

    @Override
    public void registerPermissions(PluginManager manager) {
        super.registerPermissions(manager);
        if(other != null)
            manager.addPermission(other);
    }
}
