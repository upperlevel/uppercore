package xyz.upperlevel.uppercore.command;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.StringUtil;
import xyz.upperlevel.uppercore.command.function.*;
import xyz.upperlevel.uppercore.util.TextUtil;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.bukkit.ChatColor.*;

public abstract class NodeCommand extends Command {
    private final Map<String, Command> commands = new HashMap<>();

    @Getter
    private Permission everyPermission; // the * permission

    public NodeCommand(String name) {
        super(name);
    }

    public void addCommand(Command command) {
        if (command.getParent() != null) {
            throw new IllegalArgumentException("The same instance of " + command.getClass().getSimpleName() + " is registered in more than one NodeCommand");
        }
        commands.put(command.getName(), command);
        for (String alias : command.getAliases()) {
            commands.put(alias, command);
        }
    }

    public void addCommand(List<Command> commands) {
        commands.forEach(this::addCommand);
    }

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public void completePermission() {
        super.completePermission(); // completes the default permission
        if (getPermission() != null) {
            WithEveryPermission annotation = getClass().getAnnotation(WithEveryPermission.class);
            String path = getPermission().getName() + ".*";
            if (annotation != null) {
                everyPermission = new Permission(path, annotation.description(), annotation.defaultUser().get(this));
            } else {
                everyPermission = new Permission(path, DefaultPermissionUser.INHERIT.get(this));
            }
            if (getParent() != null) {
                everyPermission.addParent(getParent().everyPermission, true);
            }
        }
        for (Command command : commands.values()) { // completes all sub commands permissions
            command.completePermission();
        }
    }

    @Override
    public void registerPermission(PluginManager pluginManager) {
        super.registerPermission(pluginManager);
        for (Command command : commands.values()) { // registers all sub commands permission
            command.registerPermission(pluginManager);
        }
    }

    @Override
    protected boolean onCall(CommandSender sender, List<String> arguments) {
        if (arguments.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments. You must specify the command name.");
            return false;
        }
        Command cmd = getCommand(arguments.get(0));
        if (cmd == null || !sender.hasPermission(cmd.getPermission())) {
            sender.sendMessage(ChatColor.RED + "No command found for: " + arguments.get(0));
            return false;
        }
        cmd.call(sender, arguments.subList(1, arguments.size()));
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, List<String> arguments) {
        if (arguments.isEmpty()) { // if there is no argument we list all runnable commands
            return commands.values()
                    .stream()
                    .filter(command -> sender.hasPermission(command.getPermission()))
                    .map(Command::getName)
                    .collect(Collectors.toList());
        } else if (arguments.size() > 1) { // if there are more than one argument we get sub command and ask a suggestion
            Command command = getCommand(arguments.get(0));
            if (command != null) {
                return command.suggest(sender, arguments.subList(1, arguments.size()));
            }
            return Collections.emptyList();
        } else { // if there is just one argument we need to get the commands that starts with it
            String argument = arguments.get(0);
            return commands.values()
                    .stream()
                    .filter(command -> sender.hasPermission(command.getPermission()))
                    .map(Command::getName)
                    .filter(name -> StringUtil.startsWithIgnoreCase(name, argument))
                    .collect(Collectors.toList());
        }
    }

    /*
    @WithPermission("help")
    public class HelpCommand extends Command {

        public HelpCommand() {
            super("help");

            setDescription("Gives you info about commands!");
            addAliases(asList("?", "h"));
        }

        protected String getPath() {
            List<String> path = new ArrayList<>();
            Command command = this;
            while (command != null) {
                path.add(command.getName());
                command = command.getParent();
            }
            StringJoiner joiner = new StringJoiner(" ", "/", "");
            ListIterator<String> i = path.listIterator(path.size());
            while (i.hasPrevious())
                joiner.add(i.previous());
            return joiner.toString();
        }

        @AsCommand
        public void run(CommandSender sender, @WithName("page") @WithOptional(value = "1") int page) {
            List<BaseComponent[]> entries = new ArrayList<>();
            for (Command cmd : NodeCommand.this.getCommands()) {
                if (cmd.canExecute(sender)) {
                    entries.add(TextComponent.fromLegacyText(cmd.getHelpline(sender, true)));
                }
            }

            int pages = TextUtil.getPages(1, entries.size(), 0);


            if (page <= 0) {
                sender.sendMessage(RED + "Hey, the max pages number is " + pages + "!");
                return;
            }

            String path = getPath();
            TextComponent header;
            {
                header = new TextComponent(GOLD + "Help for commands \"" + NodeCommand.this.getName() + "\" ");

                TextComponent leftArrow = new TextComponent("[<]");
                if (page <= 1) {
                    leftArrow.setColor(ChatColor.RED);
                } else {
                    leftArrow.setColor(ChatColor.GREEN);
                    leftArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Previous page").create()));
                    leftArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, path + " " + (page - 1)));
                }
                header.addExtra(leftArrow);

                TextComponent middle = new TextComponent(" " + page + "/" + pages + " ");
                middle.setColor(ChatColor.GOLD);
                header.addExtra(middle);

                TextComponent rightArrow = new TextComponent("[>]");
                if (page >= pages) {
                    rightArrow.setColor(ChatColor.RED);
                } else {
                    rightArrow.setColor(ChatColor.GREEN);
                    rightArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Next page").create()));
                    rightArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, path + " " + (page + 1)));
                }
                header.addExtra(rightArrow);

                TextComponent f = new TextComponent(":");
                f.setColor(ChatColor.GOLD);
                header.addExtra(f);
            }

            TextUtil.sendComponentMessages(
                    sender,
                    TextUtil.getComponentPage(singletonList(new BaseComponent[]{header}), entries, emptyList(), page - 1)
            );
        }
    }*/
}
