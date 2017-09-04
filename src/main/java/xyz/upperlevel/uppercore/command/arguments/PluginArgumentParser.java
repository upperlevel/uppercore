package xyz.upperlevel.uppercore.command.arguments;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.ArgumentParserSystem;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;
import xyz.upperlevel.uppercore.sound.CompatibleSound;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public List<String> onTabCompletion(CommandSender sender, Class<?> type, List<String> args) {
        return ArgumentParserSystem.tabComplete(Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName), args);
    }
}
