package xyz.upperlevel.uppercore.command.arguments.impl;

import xyz.upperlevel.uppercore.command.arguments.ArgumentParser;
import xyz.upperlevel.uppercore.command.arguments.exceptions.ParseException;

import java.util.List;

import static java.util.Arrays.asList;

public class CharArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return asList(Character.class, char.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        String arg = args.get(0);
        if (arg.length() != 1)
            throw new ParseException(args.get(0), "character");
        return arg.charAt(0);
    }
}
