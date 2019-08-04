package xyz.upperlevel.uppercore.command;

import org.bukkit.command.CommandMap;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommandRegistry {
    private static final List<Command> commands = new ArrayList<>();

    private CommandRegistry() {
    }

    public static boolean register(Command command) {
        Uppercore.logger().info("[Uppercore] Registering command: " + command.getFullName());

        Permission root = new Permission(Uppercore.getPlugin().getName().toLowerCase(Locale.ENGLISH));
        command.completePermission(root);
        command.registerPermission();

        CommandMap map = NativeCommandUtil.getCommandMap();
        if (map == null) {
            return false;
        }
        map.register(command.getName(), NativeCommandUtil.wrap(command));
        commands.add(command);
        return true;
    }

    private static void printMarkdown(FileWriter writer, List<Command> commands) throws IOException {
        for (Command command : commands) {
            writer.write(command.getFullName() + " | " + command.getPermission().getName() + " | " + command.getSenderType().name() + "\n");

            if (command instanceof NodeCommand) {
                printMarkdown(writer, ((NodeCommand) command).getCommands());
            }
        }
    }

    public static void printMarkdown(File file) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write("Name | Permission | Sender type\n");
            writer.write("---- | ---------- | -----------\n");
            printMarkdown(writer, commands);
            writer.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }
}
