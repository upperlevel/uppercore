package xyz.upperlevel.uppercore.command;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
    @NonNull
    private SenderType senderType = SenderType.ALL; // non null

    /**
     * The relative permission of this command (or absolute if PermissionCompleter.NONE).
     * By default is the same as the name of the command. Cannot be null.
     */
    @Getter
    @Setter
    @NonNull
    private Permission permissionPortion;

    @Getter
    @Setter
    @NonNull
    private PermissionCompleter permissionCompleter = PermissionCompleter.INHERIT;

    @Getter
    @Setter
    @NonNull
    private Permission permission;

    public Command(String name) {
        this.name = name.toLowerCase(Locale.ENGLISH);
        permissionPortion = new Permission(this.name, PermissionDefault.TRUE);
    }

    protected void setParent(NodeCommand parent) {
        this.parent = parent;
    }

    public void completePermission(Permission root) {
        if (parent != null) {
            permission = permissionCompleter.complete(parent.getPermission(), permissionPortion);
        } else {
            permission = permissionCompleter.complete(root, permissionPortion);
        }
    }

    public void registerPermission() {
        if (permission != null) {
            Bukkit.getPluginManager().addPermission(permission);
        }
    }

    public boolean hasPermission(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
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
     * Gets the usage for this command.
     * The usage must contain only command's parameters (without its name).
     * Usage format should respect: {@code <arg1> <arg2> [optional=value]}.
     * The colored variable changes the usage color based on the sender.
     */
    public abstract String getUsage(CommandSender sender, boolean colored);

    /**
     * Gets the helpline for this command.
     * The helpline is the path of the command (a chain of parents' commands) followed by command's name and usage.
     * An example: {@code parent1 parent2 command <arg1> <arg2> [optional=value]}.
     * The colored parameter changes the helpline color based on the sender.
     */
    public String getHelpline(CommandSender sender, boolean colored) {
        StringBuilder path = new StringBuilder();
        NodeCommand higher = parent;
        while (higher != null) {
            path.insert(0, higher.getName() + " ");
            higher = higher.getParent();
        }
        path.append(name);

        String usage = getUsage(sender, colored);
        if (usage.length() > 0) {
            path.append(" ").append(usage);
        }

        if (colored) {
            return path.insert(0, hasPermission(sender) ? ChatColor.GREEN : ChatColor.RED).toString();
        }

        return path.toString();
    }

    public boolean call(CommandSender sender, List<String> args) {
        if (!hasPermission(sender)) {
            sender.sendMessage("No permission: " + permission.getName()); // TODO
            return false;
        }
        if (!senderType.match(sender)) {
            sender.sendMessage("Sender mismatch! You may be a " + senderType.name() + "."); // TODO
            return false;
        }
        onCall(sender, args);
        return true;
    }

    protected abstract boolean onCall(CommandSender sender, List<String> args);

    public abstract List<String> suggest(CommandSender sender, List<String> arguments);

    public boolean subscribe(Plugin owner) {
        Permission root = new Permission(owner.getName().toLowerCase(Locale.ENGLISH));
        completePermission(root);
        registerPermission();

        CommandMap map = NativeCommandUtil.getCommandMap();
        if (map == null) {
            return false;
        }
        map.register(name, NativeCommandUtil.wrap(this));
        return true;
    }
}
