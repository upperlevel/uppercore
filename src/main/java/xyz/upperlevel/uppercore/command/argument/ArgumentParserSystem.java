package xyz.upperlevel.uppercore.command.argument;

import lombok.NonNull;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;
import xyz.upperlevel.uppercore.command.argument.exceptions.UnparsableTypeException;
import xyz.upperlevel.uppercore.command.arguments.*;
import xyz.upperlevel.uppercore.gui.arguments.GuiArgumentParser;
import xyz.upperlevel.uppercore.gui.hotbar.arguments.HotbarArgumentParser;
import xyz.upperlevel.uppercore.scoreboard.arguments.ScoreboardArgumentParser;

import java.util.*;

public final class ArgumentParserSystem {

    private static final Map<Class<?>, ArgumentParser> parsersByParsable = new HashMap<>();

    private ArgumentParserSystem() {
    }

    public static void initialize() {
        register(new BooleanArgumentParser());
        register(new ByteArgumentParser());
        register(new CharArgumentParser());
        register(new FloatArgumentParser());
        register(new DoubleArgumentParser());
        register(new IntArgumentParser());
        register(new LongArgumentParser());
        register(new StringArgumentParser());
        register(new Array1dArgumentParser());

        register(new PluginArgumentParser());
        register(new PlayerArgumentParser());

        register(new GuiArgumentParser());
        register(new HotbarArgumentParser());
        register(new ScoreboardArgumentParser());
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
        parsers.forEach(ArgumentParserSystem::register);
    }

    public static boolean isParsable(Class<?> type) {
        return parsersByParsable.containsKey(type);
    }

    public static boolean isParsable(List<Class<?>> types) {
        return types.stream().allMatch(ArgumentParserSystem::isParsable);
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
