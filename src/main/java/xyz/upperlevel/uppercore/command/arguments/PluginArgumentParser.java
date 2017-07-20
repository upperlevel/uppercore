package xyz.upperlevel.uppercore.command.arguments;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;

import java.util.Collections;
import java.util.List;

public class PluginArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return Collections.singletonList(Plugin.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(args.get(0));
        if (plugin == null)
            throw new ParseException(args.get(0), "plugin");
        return plugin;
    }
}
