package xyz.upperlevel.uppercore.command.arguments.impl;

import xyz.upperlevel.uppercore.command.arguments.ArgumentParser;
import xyz.upperlevel.uppercore.command.arguments.exceptions.ParseException;

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
}
