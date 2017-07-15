package xyz.upperlevel.uppercore.command;

import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.args.ArgumentParserManager;
import xyz.upperlevel.uppercore.command.args.exceptions.ParseException;
import xyz.upperlevel.uppercore.command.args.exceptions.UnparsableTypeException;
import xyz.upperlevel.uppercore.command.exceptions.CommandSyntaxException;
import xyz.upperlevel.uppercore.command.exceptions.InternalCommandException;
import xyz.upperlevel.uppercore.command.exceptions.NoCommandFoundException;
import xyz.upperlevel.uppercore.util.TextUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static java.util.Arrays.asList;
import static lombok.AccessLevel.NONE;
import static org.bukkit.ChatColor.*;

@Data
public abstract class Command {

    private NodeCommand parent;

    private final String name;
    private String description;
    private List<String> aliases = new ArrayList<>();
    private String permission;

    private Sender sender = Sender.ALL; // non null

    @Getter(NONE)
    private Method executor;

    public Command(String name) {
        this.name = name.toLowerCase();
        addAlias(this.name);
        setup();
    }

    protected void setParent(NodeCommand parent) {
        this.parent = parent;
    }

    /**
     * Adds the given alias to this command.
     *
     * @param alias the alias to add
     */
    public void addAlias(String alias) {
        aliases.add(alias.toLowerCase());
        aliases.sort(String::compareTo);
    }

    /**
     * Adds aliases to this command.
     *
     * @param aliases the aliases to add
     */
    public void addAliases(String... aliases) {
        for (String alias : aliases)
            addAlias(alias);
    }

    /**
     * Adds the given aliases to this command.
     *
     * @param aliases the alaises to add
     */
    public void addAliases(List<String> aliases) {
        for (String alias : aliases)
            addAlias(alias);
    }

    public void setup() {
        for (Method method : getClass().getDeclaredMethods()) {
            Executor e = method.getAnnotation(Executor.class);
            if (e != null) {
                if (executor == null) {
                    method.setAccessible(true);
                    executor = method;
                    sender = e.sender();
                }
            }
        }
    }

    private Map<Class<? extends CommandSender>, String> helplines = new HashMap<>();

    public String getHelpline(CommandSender sender, boolean format) {
        return helplines.computeIfAbsent(sender.getClass(), type -> getUsage(sender, format) + " " + (format ? GRAY : "") + description);
    }

    /**
     * Gets the command complete usage depending from the given sender.
     *
     * @param sender the sender to build the usage for
     * @param format the usage have to be colored?
     * @return the complete usage
     */
    public String getUsage(CommandSender sender, boolean format) {
        StringJoiner usage = new StringJoiner(" ");
        List<String> relatives = new ArrayList<>();
        NodeCommand high = parent;
        while (high != null) {
            relatives.add(0, high.getName());
            high = high.getParent();
        }
        if (!relatives.isEmpty())
            usage.add(StringUtils.join(relatives, " "));
        usage.add(getHelplineName(sender, format));

        String args = getHelplineArgs(sender, format);
        if (args != null)
            usage.add(args);

        return (format ? AQUA : "") + "/" + usage.toString();
    }

    public String getHelplineName(CommandSender sender, boolean format) {
        StringJoiner usage = new StringJoiner(",");
        for (String alias : aliases)
            usage.add(alias);
        return (format && this.sender.isCorrect(sender) ? AQUA : RED) + usage.toString();
    }

    public String getHelplineArgs(CommandSender sender, boolean format) {
        if (executor == null)
            return null;
        StringJoiner usage = new StringJoiner(" ");
        for (int i = 1; i < executor.getParameters().length; i++) {
            Parameter parameter = executor.getParameters()[i];
            Optional opt = parameter.getDeclaredAnnotation(Optional.class);

            StringJoiner value;
            if (opt != null && opt.sender().isCorrect(sender))
                value = new StringJoiner(" ", "[", "]");
            else
                value = new StringJoiner(" ", "<", ">");
            Argument arg = parameter.getDeclaredAnnotation(Argument.class);
            value.add((arg != null ? arg.value() : parameter.getName()) + (opt != null && !opt.value().isEmpty() ? "=" + opt.value() : ""));

            usage.add(value.toString());
        }
        if (usage.length() == 0)
            return null;
        else
            return (format ? DARK_AQUA : "") + usage.toString();
    }

    private boolean isOptional(CommandSender sender, Parameter parameter) {
        Optional opt = parameter.getDeclaredAnnotation(Optional.class);
        return opt != null && opt.sender().isCorrect(sender);
    }

    private Object processOptional(ArgumentParserManager parser, CommandSender sender, Parameter parameter) {
        Optional opt = parameter.getDeclaredAnnotation(Optional.class);
        if (parser.getArgumentsCount(parameter.getType()) > 1)
            return null;
        try {
            return parser.parse(parameter.getType(), Collections.singletonList(opt.value()));
        } catch (ParseException ignored) {
            return null;
        }
    }

    public void execute(ArgumentParserManager parser, CommandSender sender, List<String> args)
            throws CommandSyntaxException, NoCommandFoundException, InternalCommandException {
        if (executor == null)
            return;
        List<Object> result = new ArrayList<>();
        result.add(sender);
        int arg = 0;
        for (int i = 1; i < executor.getParameterCount(); i++) {
            Parameter parameter = executor.getParameters()[i];
            Class<?> type = parameter.getType();
            if (arg >= args.size()) {
                if (isOptional(sender, parameter))
                    result.add(processOptional(parser, sender, parameter));
                else {
                    TextUtil.sendMessages(sender, asList(
                            RED + "Command syntax exception. " + GOLD + "You may use the command like this:",
                            getUsage(sender, true)
                    ));
                    return;
                }
            } else {
                if (parser.isParsable(type)) {
                    int used = parser.getArgumentsCount(type);
                    if (used < 0)
                        used = args.size() - arg;
                    try {
                        result.add(parser.parse(type, args.subList(arg, arg + used)));
                    } catch (ParseException e) {
                        if (isOptional(sender, parameter)) {
                            // puts default optional value
                            result.add(processOptional(parser, sender, parameter));
                        } else {
                            sender.sendMessage(e.getMessageFormatted());
                            return;
                        }
                    }
                    arg += used;
                } else
                    throw new UnparsableTypeException("Unparsable type \"" + type.getName() + "\" in \"" + getClass().getName() + "\"");
            }
        }
        try {
            executor.invoke(this, result.toArray());
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException e) {
            throw new InternalCommandException();
        }
    }
}
