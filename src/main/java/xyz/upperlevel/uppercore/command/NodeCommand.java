package xyz.upperlevel.uppercore.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.util.TextUtil;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.bukkit.ChatColor.*;

public abstract class NodeCommand extends Command {

    @Getter
    private final List<Command> commands = new ArrayList<>();

    private final Map<String, Command> commandsByName = new HashMap<>();
    private final HelpCommand helpCmd = new HelpCommand();

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
        super.execute(sender, args);
        if (args.isEmpty()) {
            helpCmd.run(sender, 1);
            return;
        }
        Command cmd = getCommand(args.get(0));
        if (cmd == null) {
            TextUtil.sendMessages(sender, asList(
                    RED + "No commands found for \"" + LIGHT_PURPLE + args.get(0) + RED + "\". " + GOLD + "To see all commands use:",
                    getUsage(sender, true)
            ));
            return;
        }
        cmd.execute(sender, args.subList(1, args.size()));
    }

    public class HelpCommand extends Command {

        public HelpCommand() {
            super("help");

            setDescription("Gives you info about commands!");
            addAliases(asList("?", "h"));
        }

        @Executor
        public void run(CommandSender sender, @Argument("page") @Optional(value = "1") int page) {
            List<String> entries = new ArrayList<>();
            for (Command cmd : NodeCommand.this.getCommands())
                entries.add(cmd.getHelpline(sender, true));

            int pages = TextUtil.getPages(1, entries.size(), 0);
            List<String> header = singletonList(
                    GOLD + "Help for commands \"" + NodeCommand.this.getName() + "\" " +
                            (page == 1 ? RED : GREEN) + "[<]" +
                            GOLD + " " + page + "/" + pages + " " +
                            (page == pages ? RED : GREEN) + "[>]" +
                            GOLD + ":");
            List<String> footer = emptyList();

            if (page <= 0) {
                sender.sendMessage(RED + "Hey, the max pages number is " + pages + "!");
                return;
            }

            TextUtil.sendMessages(
                    sender,
                    TextUtil.getPage(header, entries, footer, page - 1)
            );
        }
    }

}
