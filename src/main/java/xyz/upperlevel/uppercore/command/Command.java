package xyz.upperlevel.uppercore.command;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import xyz.upperlevel.uppercore.Uppercore;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class Command {
    @Getter
    NodeCommand parent;

    @Getter
    private final String name;

    private Set<String> aliases = new HashSet<>();

    @Getter
    @Setter
    private String description = "No description";

    @Getter
    @Setter
    private Permission permissionPortion;

    @Getter
    @Setter
    private PermissionCompleter permissionCompleter = PermissionCompleter.NONE;

    @Getter
    private Permission permission;

    @Getter
    private SenderType senderType = SenderType.ALL; // non null

    public Command(String name) {
        this.name = name.toLowerCase(Locale.ENGLISH);

        // Default 'permissionPortion' corresponds to command name
        this.permissionPortion = new Permission(this.name, PermissionDefault.NOT_OP);
        this.permissionCompleter = PermissionCompleter.INHERIT;
    }

    void setParent(NodeCommand parent) {
        this.parent = parent;
    }

    public void completePermission() {
        Permission parentPermission = null;
        if (parent != null) {
            parentPermission = parent.getPermission();
        }
        permission = permissionCompleter.complete(parentPermission, permissionPortion);
    }

    public void registerPermission(PluginManager pluginManager) {
        if (permission != null) {
            pluginManager.addPermission(permission);
        }
    }

    public void addAlias(String alias) {
        aliases.add(alias.toLowerCase(Locale.ENGLISH));
    }

    public void addAliases(String... aliases) {
        for (String alias : aliases) {
            this.aliases.add(alias.toLowerCase(Locale.ENGLISH));
        }
    }

    /**
     * Gets a copy of the aliases set.
     */
    public Set<String> getAliases() {
        return Sets.newHashSet(aliases);
    }

    /**
     * Gets the usage based on the sender.
     * The result may respect the following format:
     * {@code "<arg1> <arg2> [optArg3=defVal]"}
     * <br>
     * <b>Note:</b> no command name!
     */
    public abstract String getUsage(CommandSender sender);

    /**
     * Gets the usage plus the command path based on the sender.
     * An example of helpline would be:
     * {@code "my foo command <arg1> <arg2> [optArg3=defVal]"}
     */
    public String getHelpline(CommandSender sender) {
        // Builds up the command path
        StringBuilder helpline = new StringBuilder();
        NodeCommand high = parent;
        while (high != null) {
            if (high.getParent() != null) {
                helpline.insert(0, high.getName() + " ");
            } else {
                helpline.insert(0, high.getName());
            }
            high = high.getParent();
        }
        // Appends the command usage in the end
        helpline.append(name)
                .append(" ")
                .append(getUsage(sender));
        return helpline.toString();
    }

    public boolean call(CommandSender sender, List<String> arguments) {
        // Here we check the permission
        if (permission != null && sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You do not have enough permissions to run this command.");
            return false;
        }
        onCall(sender, arguments);
        return true;
    }

    protected abstract boolean onCall(CommandSender sender, List<String> args);

    /**
     * Called when the command may be tab-completed.
     *
     * @param sender    the sender
     * @param arguments the arguments
     */
    public abstract List<String> suggest(CommandSender sender, List<String> arguments);
}
