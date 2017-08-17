package xyz.upperlevel.uppercore.gui.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.gui.Gui;

import static xyz.upperlevel.uppercore.Uppercore.guis;

@WithPermission(value = "open", desc = "Allows you to open a specific GUI via command")
public class OpenGuiCommand extends Command {

    private Permission other;

    public OpenGuiCommand() {
        super("open");
        setDescription("Opens a gui.");
    }

    @Executor(sender = Sender.PLAYER)
    public void run(CommandSender sender, @Argument("gui") Gui gui, @Argument("player") @Optional(sender = Sender.PLAYER) Player player) {
        if (player == null) {
            player = (Player) sender;
        } else if(other != null && other != sender && !sender.hasPermission(other)) {
            sender.sendMessage(ChatColor.RED + "You can't open guis to other players");
            return;
        }
        guis().open(player, gui);
    }

    @Override
    public void calcPermissions() {
        super.calcPermissions();
        Permission def = getPermission();
        if(def != null) {
            other = new Permission(def.getName() + ".other", "Allows you to open guis to other players", PermissionDefault.OP);
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
