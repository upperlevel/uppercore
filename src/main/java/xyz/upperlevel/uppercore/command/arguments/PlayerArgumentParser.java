package xyz.upperlevel.uppercore.command.arguments;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return Collections.singletonList(Player.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        Player player = Bukkit.getPlayer(args.get(0));
        if (player == null)
            throw new ParseException(args.get(0), "player");
        return player;
    }

    @Override
    public List<String> onTabCompletion(CommandSender sender, Class<?> type, List<String> args) {
        Stream<? extends Player> players = Bukkit.getOnlinePlayers().stream();
        if(sender instanceof Player) {
            Player player = (Player) sender;
            players = players.filter(player::canSee);
        }
        Stream<String> strStream = players.map(Player::getName);
        if(args.isEmpty())
            return strStream.collect(Collectors.toList());
        String partial = args.get(0);
        return strStream
                .filter(s -> StringUtil.startsWithIgnoreCase(s, partial))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }
}
