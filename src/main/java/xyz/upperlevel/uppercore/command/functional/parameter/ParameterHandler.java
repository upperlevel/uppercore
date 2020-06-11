package xyz.upperlevel.uppercore.command.functional.parameter;

import xyz.upperlevel.uppercore.util.Dbg;

import java.util.*;
import java.util.function.Function;

public final class ParameterHandler {
    public static class ArgsTracker {
        private final Queue<String> args;

        private final Class<?> type;
        private final List<String> trace = new ArrayList<>();

        private final Function<ArgsTracker, RuntimeException> wrongPool;

        public ArgsTracker(Queue<String> args, Class<?> type, Function<ArgsTracker, RuntimeException> wrongPool) {
            this.args = args;
            this.type = type;
            this.wrongPool = wrongPool;
        }

        public int remaining() {
            return args.size();
        }

        public String take() {
            String arg = args.poll();
            if (arg == null) {
                throw areWrong();
            } else {
                trace.add(arg);
                return arg;
            }
        }

        public List<String> takeAll() {
            trace.addAll(args);
            args.clear();
            return new ArrayList<>(trace);
        }

        public RuntimeException areWrong() {
            return wrongPool.apply(this);
        }
    }

    public interface Parser {
        Object parse(ArgsTracker args);
    }

    public interface Prompter {
        List<String> suggest(ArgsTracker args);
    }

    private static final Map<Class<?>, Parser> parserByType = new HashMap<>();
    private static final Map<Class<?>, Prompter> prompterByType = new HashMap<>();

    private ParameterHandler() {
    }

    public static void register(List<Class<?>> types, Parser parser, Prompter prompter) {
        types.forEach(type -> {
            parserByType.put(type, parser);
            prompterByType.put(type, prompter);
        });
    }

    public static void register(List<Class<?>> types, Parser parser) {
        register(types, parser, args -> Collections.emptyList());
    }

    public static Object parse(Class<?> type, Queue<String> args) {
        Parser parser = parserByType.get(type);
        if (parser == null)
            throw new IllegalStateException(String.format("Parser not found for type: %s", type.getSimpleName()));
        return parser.parse(new ArgsTracker(
                args,
                type,
                tracker -> new ParameterParseException(type, tracker.trace)
        ));
    }

    public static void skip(Class<?> type, Queue<String> args) {
        try {
            parse(type, args);
        } catch (ParameterParseException e) {
            // Ignore possible parsing issues: this function has been done just to
            // consume args according to the given type, not actually to use them.
        }
    }

    public static List<String> suggest(Class<?> type, Queue<String> args) {
        Prompter prompter = prompterByType.get(type);
        if (prompter == null) {
            throw new IllegalStateException(String.format("Prompter not found for type: %s", type.getSimpleName()));
        }
        return prompter.suggest(new ArgsTracker(
                args,
                type,
                tracker -> new IllegalStateException(
                        "Shouldn't be reaching this state, the args list are wrong for suggestion." +
                        "Are you explicitly calling areWrong() or an Uppercore issues is making you take() from an empty list."
                )
        ));
    }
}
