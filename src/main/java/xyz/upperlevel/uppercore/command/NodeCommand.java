package xyz.upperlevel.uppercore.command;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.StringUtil;
import xyz.upperlevel.uppercore.util.TextUtil;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.bukkit.ChatColor.*;

public abstract class NodeCommand extends Command {

    @Getter
    private final List<Command> commands = new ArrayList<>();

    private final Map<String, Command> commandsByName = new HashMap<>();
    private final HelpCommand helpCmd = new HelpCommand();
    @Getter
    private Permission anyPerm;

    public NodeCommand(String name) {
        super(name);
        register(helpCmd);
    }

    public void register(Command command) {
        commandsByName.put(command.getName().toLowerCase(), command);
        for (String alias : command.getAliases())
            commandsByName.put(alias.toLowerCase(), command);
        command.setParent(this);
        commands.add(command);
    }

    public Command getCommand(String name) {
        return commandsByName.get(name.toLowerCase());
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if(!canExecute(sender))
            return;
        super.execute(sender, args);
        if (args.isEmpty()) {
            helpCmd.run(sender, 1);
            return;
        }
        Command cmd = getCommand(args.get(0));
        if (cmd == null || !cmd.canExecute(sender)) {
            TextUtil.sendMessages(sender, asList(
                    RED + "No commands found for \"" + LIGHT_PURPLE + args.get(0) + RED + "\". " + GOLD + "To see all commands use:",
                    getUsage(sender, true)
            ));
            return;
        }
        cmd.execute(sender, args.subList(1, args.size()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> args) {
        if(args.isEmpty()) {
            return commands.stream()
                    .filter(c -> c.canExecute(sender))
                    .map(Command::getName)
                    .collect(Collectors.toList());
        } else if(args.size() > 1) {
            Command sub = getCommand(args.get(0));
            if(sub != null)
                return sub.tabComplete(sender, args.subList(1, args.size()));
            else
                return emptyList();
        } else {
            String arg = args.get(0);

            return commands.stream()
                    .filter(c -> c.canExecute(sender))
                    .map(Command::getName)
                    .filter(s -> StringUtil.startsWithIgnoreCase(s, arg))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void calcPermissions() {
        super.calcPermissions();

        if(getPermission() != null) {
            WithChildPermission perm = getClass().getAnnotation(WithChildPermission.class);
            String path = getPermission().getName() + ".*";
            if (perm != null)
                anyPerm = new Permission(path, perm.desc(), perm.def().get(this));
            else
                anyPerm = new Permission(path, DefaultPermission.INHERIT.get(this));
            if(getParent() != null)
                anyPerm.addParent(getParent().anyPerm, true);

            for(Command command : commands) {
                command.calcPermissions();
            }
        }
    }

    @Override
    public void registerPermissions(PluginManager manager) {
        super.registerPermissions(manager);
        if(anyPerm != null)
            manager.addPermission(anyPerm);
        for(Command command : commands)
            command.registerPermissions(manager);
    }

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
            while(command != null) {
                path.add(command.getName());
                command = command.getParent();
            }
            StringJoiner joiner = new StringJoiner(" ","/","");
            ListIterator<String> i = path.listIterator(path.size());
            while (i.hasPrevious())
                joiner.add(i.previous());
            return joiner.toString();
        }

        @Executor
        public void run(CommandSender sender, @Argument("page") @Optional(value = "1") int page) {
            List<BaseComponent[]> entries = new ArrayList<>();
            for (Command cmd : NodeCommand.this.getCommands())
                entries.add(TextComponent.fromLegacyText(cmd.getHelpline(sender, true)));

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
                if(page <= 1) {
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
                if(page >= pages) {
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
    }

}
