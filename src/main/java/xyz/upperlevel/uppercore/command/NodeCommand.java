package xyz.upperlevel.uppercore.command;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import xyz.upperlevel.uppercore.command.functional.WithEveryPermission;

import java.util.*;
import java.util.stream.Collectors;

public class NodeCommand extends Command {
    private final Map<String, Command> commands = new HashMap<>();

    @Getter
    private Permission everyPermission; // the * permission

    public NodeCommand(String name) {
        super(name);
        addCommand(new HelpCommand());
    }

    public void addCommand(Command command) {
        if (command.getParent() != null) {
            throw new IllegalArgumentException("The same instance of " + command.getClass().getSimpleName() + " is registered in more than one NodeCommand");
        }

        Set<String> keys = command.getAliases();
        keys.add(command.getName());
        for (String key : keys) {
            if (commands.containsKey(key)) {
                throw new IllegalArgumentException("Trying to register command's key '" + key + "' that was already registered node: " + getName());
            }
        }

        for (String key : keys) {
            commands.put(key, command);
        }

        command.setParent(this);
    }

    public void addCommands(List<Command> commands) {
        commands.forEach(this::addCommand);
    }

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase(Locale.ENGLISH));
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

    @Override
    public void completePermission(Permission root) {
        super.completePermission(root);
        if (getPermission() != null) {
            WithEveryPermission annotation = getClass().getAnnotation(WithEveryPermission.class);
            String path = getPermission().getName() + ".*";
            if (annotation != null) {
                everyPermission = new Permission(path, annotation.description(), annotation.defaultUser().get(this));
            } else {
                everyPermission = new Permission(path, DefaultPermissionUser.INHERIT.get(this));
            }
            if (getParent() != null) {
                everyPermission.addParent(getParent().everyPermission, true);
            }
        }
        for (Command command : commands.values()) {
            command.completePermission(root);
        }
    }

    @Override
    public void registerPermission() {
        super.registerPermission();
        for (Command command : commands.values()) { // registers all sub commands permission
            command.registerPermission();
        }
    }

    @Override
    public String getUsage(CommandSender sender, boolean colored) {
        return "<command>";
    }

    @Override
    public boolean call(CommandSender sender, List<String> args) {
        super.call(sender, args);
        if (args.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments. You must specify the command name.");
            return false;
        }
        Command cmd = getCommand(args.get(0));
        if (cmd == null || !sender.hasPermission(cmd.getPermission())) {
            sender.sendMessage(ChatColor.RED + "No command found for: " + args.get(0));
            return false;
        }
        cmd.call(sender, args.subList(1, args.size()));
        return true;
    }

    @Override
    protected boolean onCall(CommandSender sender, List<String> args) {
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, List<String> arguments) {
        if (arguments.isEmpty()) { // if there is no argument we list all runnable commands
            return commands.values()
                    .stream()
                    .filter(command -> sender.hasPermission(command.getPermission()))
                    .map(Command::getName)
                    .collect(Collectors.toList());
        } else if (arguments.size() > 1) { // if there are more than one argument we get sub command and ask a suggestion
            Command command = getCommand(arguments.get(0));
            if (command != null) {
                return command.suggest(sender, arguments.subList(1, arguments.size()));
            }
            return Collections.emptyList();
        } else { // if there is just one argument we need to get the commands that starts with it
            String argument = arguments.get(0);
            return commands.values()
                    .stream()
                    .filter(command -> sender.hasPermission(command.getPermission()))
                    .map(Command::getName)
                    .filter(name -> StringUtil.startsWithIgnoreCase(name, argument))
                    .collect(Collectors.toList());
        }
    }
}
