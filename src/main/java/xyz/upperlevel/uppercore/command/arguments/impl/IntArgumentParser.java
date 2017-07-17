package xyz.upperlevel.uppercore.command.arguments.impl;

import xyz.upperlevel.uppercore.command.arguments.ArgumentParser;
import xyz.upperlevel.uppercore.command.arguments.exceptions.ParseException;

import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

public class IntArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return asList(Integer.class, int.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        try {
            return parseInt(args.get(0));
        } catch (Exception e) {
            throw new ParseException(args.get(0), "number");
        }
    }
}
