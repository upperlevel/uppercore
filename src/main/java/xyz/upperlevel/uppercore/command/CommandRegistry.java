package xyz.upperlevel.uppercore.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CommandRegistry {
    private static CommandMap commandMap;

    private CommandRegistry() {
    }

    private static CommandMap getCommandMap() {
        try {
            if (commandMap == null) {
                Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                field.setAccessible(true);
                commandMap = (CommandMap) field.get(Bukkit.getServer());
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return commandMap;
    }

    public static BukkitCommand wrap(Command command) {
        return new BukkitCommand(command.getName(), command.getDescription(), command.getUsage(null), new ArrayList<>(command.getAliases())) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                command.call(sender, Arrays.asList(args));
                return true;
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
                return command.suggest(sender, Arrays.asList(args));
            }
        };
    }

    public static boolean register(Command command) {
        command.completePermission();
        command.registerPermission(Bukkit.getPluginManager());

        CommandMap map = getCommandMap();
        if (map == null) {
            return false;
        }
        map.register(command.getName(), wrap(command));
        return true;
    }
}
