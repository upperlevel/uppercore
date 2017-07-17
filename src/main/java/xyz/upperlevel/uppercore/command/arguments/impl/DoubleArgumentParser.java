package xyz.upperlevel.uppercore.command.arguments.impl;

import xyz.upperlevel.uppercore.command.arguments.ArgumentParser;
import xyz.upperlevel.uppercore.command.arguments.exceptions.ParseException;

import java.util.List;

import static java.lang.Byte.parseByte;
import static java.lang.Double.parseDouble;
import static java.util.Arrays.asList;

public class DoubleArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return asList(Double.class, double.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        try {
            return parseDouble(args.get(0));
        } catch (Exception e) {
            throw new ParseException(args.get(0), "number");
        }
    }
}
