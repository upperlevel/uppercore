package xyz.upperlevel.uppercore.command.args.impl;

import org.bukkit.ChatColor;
import xyz.upperlevel.uppercore.command.args.ArgumentParser;
import xyz.upperlevel.uppercore.command.args.ArgumentParserManager;
import xyz.upperlevel.uppercore.command.args.exceptions.ParseException;

import java.util.Iterator;
import java.util.List;

import static java.lang.Byte.parseByte;
import static java.util.Arrays.asList;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;

public class ByteArgParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return asList(Byte.class, byte.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(ArgumentParserManager handle, Class<?> type, List<String> args) throws ParseException {
        try {
            return parseByte(args.get(0));
        } catch (Exception e) {
            throw new ParseException(args.get(0), "number");
        }
    }
}
