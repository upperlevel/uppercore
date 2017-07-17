package xyz.upperlevel.uppercore.command.arguments;

import lombok.NonNull;
import xyz.upperlevel.uppercore.command.arguments.exceptions.ParseException;
import xyz.upperlevel.uppercore.command.arguments.exceptions.UnparsableTypeException;
import xyz.upperlevel.uppercore.command.arguments.impl.*;

import java.util.*;

public final class ArgumentParserManager {

    private static final Map<Class<?>, ArgumentParser> parsersByParsable = new HashMap<>();

    private ArgumentParserManager() {
    }

    static {
        register(new BooleanArgumentParser());
        register(new ByteArgumentParser());
        register(new CharArgumentParser());
        register(new FloatArgumentParser());
        register(new DoubleArgumentParser());
        register(new IntArgumentParser());
        register(new LongArgumentParser());
        register(new StringArgumentParser());
        register(new Array1dArgumentParser());

        register(new GuiArgumentParser());
        register(new HotbarArgumentParser());
        register(new PluginArgumentParser());
    }

    public static void register(@NonNull ArgumentParser parser) {
        for (Class<?> type : parser.getParsable())
            parsersByParsable.put(type, parser);
    }

    public static void register(@NonNull ArgumentParser... parsers) {
        for (ArgumentParser parser : parsers)
            register(parser);
    }

    public static void register(@NonNull Collection<ArgumentParser> parsers) {
        parsers.forEach(ArgumentParserManager::register);
    }

    public static boolean isParsable(Class<?> type) {
        return parsersByParsable.containsKey(type);
    }

    public static boolean isParsable(List<Class<?>> types) {
        return types.stream().allMatch(ArgumentParserManager::isParsable);
    }

    public static int getArgumentsCount(Class<?> type) {
        ArgumentParser parser = parsersByParsable.get(type);
        if (parser == null)
            throw new UnparsableTypeException(type);
        return parser.getArgumentsCount();
    }

    public static Object parse(@NonNull Class<?> type, @NonNull List<String> args) throws ParseException {
        ArgumentParser parser = parsersByParsable.get(type);
        if (parser == null)
            throw new UnparsableTypeException(type.getName());
        return parser.parse(type, args);
    }
}
