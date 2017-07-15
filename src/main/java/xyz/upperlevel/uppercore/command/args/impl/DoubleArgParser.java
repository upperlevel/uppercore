package xyz.upperlevel.uppercore.command.args.impl;

import xyz.upperlevel.uppercore.command.args.ArgumentParser;
import xyz.upperlevel.uppercore.command.args.ArgumentParserManager;
import xyz.upperlevel.uppercore.command.args.exceptions.ParseException;

import java.util.Iterator;
import java.util.List;

import static java.lang.Byte.parseByte;
import static java.lang.Double.parseDouble;
import static java.util.Arrays.asList;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;

public class DoubleArgParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return asList(Double.class, double.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(ArgumentParserManager handle, Class<?> type, List<String> args) throws ParseException {
        try {
            return parseDouble(args.get(0));
        } catch (Exception e) {
            throw new ParseException(args.get(0), "number");
        }
    }
}
