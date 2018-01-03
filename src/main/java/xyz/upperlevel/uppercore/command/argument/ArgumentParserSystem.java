package xyz.upperlevel.uppercore.command.argument;

import lombok.NonNull;
import org.bukkit.util.StringUtil;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;
import xyz.upperlevel.uppercore.command.argument.exceptions.UnparsableTypeException;
import xyz.upperlevel.uppercore.command.arguments.*;
import xyz.upperlevel.uppercore.gui.arguments.GuiArgumentParser;
import xyz.upperlevel.uppercore.hotbar.arguments.HotbarArgumentParser;
import xyz.upperlevel.uppercore.script.arguments.ScriptArgumentParser;
import xyz.upperlevel.uppercore.sound.argument.SoundArgumentParser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xyz.upperlevel.uppercore.Uppercore.hotbars;

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

        register(new SoundArgumentParser());
        register(new ScriptArgumentParser());
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

    public static ArgumentParser getParser(Class<?> type) {
        return parsersByParsable.get(type);
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

    public static List<String> tabComplete(Collection<String> in, List<String> args) {
        if (args.isEmpty())
            return new ArrayList<>(in);
        String arg = args.get(0);
        return in.stream()
                .filter(s -> StringUtil.startsWithIgnoreCase(s, arg))
                .collect(Collectors.toList());
    }

    public static List<String> tabComplete(Stream<String> in, List<String> args) {
        if (args.isEmpty())
            return in.collect(Collectors.toList());
        String arg = args.get(0);
        return in
                .filter(s -> StringUtil.startsWithIgnoreCase(s, arg))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }
}
