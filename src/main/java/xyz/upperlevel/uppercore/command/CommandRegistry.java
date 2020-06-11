package xyz.upperlevel.uppercore.command;

import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.command.functional.WithPermission;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.bukkit.ChatColor.GREEN;

public class CommandRegistry {
    private static final List<Command> commands = new ArrayList<>();

    private CommandRegistry() {
    }

    public static void register(Command command) {
        Uppercore.logger().info("Registering command: " + command.getFullName());

        if (command instanceof NodeCommand) // Sadly we can generate the md only if the registered command is a node.
            ((NodeCommand) command).append(FunctionalCommand.load(new PrintMdCommand(command)));

        Permission root = new Permission(Uppercore.getPlugin().getName().toLowerCase(Locale.ENGLISH));
        command.completePermission(root);
        command.registerPermission();

        CommandMap map = NativeCommandUtil.getCommandMap();
        if (map == null)
            throw new IllegalStateException("Could not retrieve CommandMap from Bukkit. Command not registered.");

        map.register(command.getName(), NativeCommandUtil.wrap(command));
        commands.add(command);
    }

    public static void register(List<Command> commands) {
        commands.forEach(CommandRegistry::register);
    }
}
