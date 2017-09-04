package xyz.upperlevel.uppercore.command.arguments;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class BooleanArgumentParser implements ArgumentParser {
    @Override
    public List<Class<?>> getParsable() {
        return asList(Boolean.class, boolean.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        switch (args.get(0).toLowerCase()) {
            case "true":
                return true;
            case "false":
                return false;
            default:
                throw new ParseException(args.get(0), "boolean");
        }
    }

    @Override
    public List<String> onTabCompletion(CommandSender sender, Class<?> type, List<java.lang.String> args) {
        if(args.size() == 0)
            return Arrays.asList("true", "false");
        String arg = args.get(0);
        switch (arg.charAt(0)) {
            case 't':
            case 'T':
                return Collections.singletonList("true");
            case 'f':
            case 'F':
                return Collections.singletonList("false");
            default:
                return null;
        }
    }
}
