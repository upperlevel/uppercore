package xyz.upperlevel.uppercore.command.args;

import lombok.NonNull;
import xyz.upperlevel.uppercore.command.args.exceptions.ParseException;
import xyz.upperlevel.uppercore.command.args.exceptions.UnparsableTypeException;
import xyz.upperlevel.uppercore.command.args.impl.*;

import java.util.*;

public class ArgumentParserManager {

    private final Map<Class<?>, ArgumentParser> parsersByParsable = new HashMap<>();

    public void registerDefaults() {
        register(new BooleanArgParser());
        register(new ByteArgParser());
        register(new CharArgParser());
        register(new FloatArgParser());
        register(new DoubleArgParser());
        register(new IntArgParser());
        register(new LongArgParser());
        register(new StringArgParser());
        register(new Array1dArgParser());
    }

    public void register(@NonNull ArgumentParser parser) {
        for (Class<?> type : parser.getParsable())
            parsersByParsable.put(type, parser);
    }

    public void register(@NonNull ArgumentParser... parsers) {
        for (ArgumentParser parser : parsers)
            register(parser);
    }

    public void register(@NonNull Collection<ArgumentParser> parsers) {
        parsers.forEach(this::register);
    }

    public boolean isParsable(Class<?> type) {
        return parsersByParsable.containsKey(type);
    }

    public boolean isParsable(List<Class<?>> types) {
        return types.stream().allMatch(this::isParsable);
    }

    public int getArgumentsCount(Class<?> type) {
        ArgumentParser parser = parsersByParsable.get(type);
        if (parser == null)
            throw new UnparsableTypeException(type);
        return parser.getArgumentsCount();
    }

    public Object parse(@NonNull Class<?> type, @NonNull List<String> args) throws ParseException {
        ArgumentParser parser = parsersByParsable.get(type);
        if (parser == null)
            throw new UnparsableTypeException(type.getName());
        return parser.parse(this, type, args);
    }
}
