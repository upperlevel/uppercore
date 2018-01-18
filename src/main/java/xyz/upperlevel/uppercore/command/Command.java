package xyz.upperlevel.uppercore.command;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import xyz.upperlevel.uppercore.Uppercore;

import java.util.*;

public abstract class Command implements CommandExecutor, TabCompleter {
    @Getter
    NodeCommand parent;

    @Getter
    private final String name;

    private Set<String> aliases = new HashSet<>();

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private Permission permission;

    @Getter
    private SenderType senderType = SenderType.ALL; // non null

    public Command(String name) {
        this.name = name.toLowerCase(Locale.ENGLISH);
    }

    void setParent(NodeCommand parent) {
        this.parent = parent;
    }

    /**
     * Registers the permissions of this command.
     * By default registers the only one it has, but may be overridden.
     */
    public void registerPermissions(PluginManager pluginManager) {
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


    /**
     * Subscribes the commands to Bukkit commands list.
     * The commands must be registered in plugin.yml by its name.
     */
    public void subscribe() {
        PluginCommand cmd = Bukkit.getPluginCommand(getName());
        if (cmd == null) {
            Uppercore.logger().severe("Command not found in plugin.yml: \"" + getName() + "\"");
            return;
        }
        setDescription(cmd.getDescription());
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
        registerPermissions(Bukkit.getPluginManager());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Called when the command is invoked.
     *
     * @param sender    the sender
     * @param arguments the arguments
     */
    public abstract boolean call(CommandSender sender, List<String> arguments);

    /**
     * Called when the command may be tab-completed.
     *
     * @param sender    the sender
     * @param arguments the arguments
     */
    public abstract List<String> suggest(CommandSender sender, List<String> arguments);

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        call(sender, Arrays.asList(args));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        return suggest(sender, Arrays.asList(args));
    }
}
