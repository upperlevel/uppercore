package xyz.upperlevel.uppercore.command;

import org.bukkit.command.CommandMap;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommandRegistry {
    private final Plugin plugin;
    private final List<Command> commands = new ArrayList<>();

    public CommandRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean register(Command command) {
        Permission root = new Permission(plugin.getName().toLowerCase(Locale.ENGLISH));
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

    private void printMarkdown(FileWriter writer, List<Command> commands) throws IOException {
        for (Command command : commands) {
            writer.write(command.getFullName() + " | " + command.getPermission().getName() + " | " + command.getSenderType().name() + "\n");

            if (command instanceof NodeCommand) {
                printMarkdown(writer, ((NodeCommand) command).getCommands());
            }
        }
    }

    public void printMarkdown(File file) {
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

    public void printMarkdown() {
        printMarkdown(new File(plugin.getDataFolder(), "COMMANDS.md"));
    }

    public static CommandRegistry create(Plugin plugin) {
        return new CommandRegistry(plugin);
    }
}
