package xyz.upperlevel.uppercore.command;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.WithName;
import xyz.upperlevel.uppercore.command.functional.WithOptional;
import xyz.upperlevel.uppercore.command.functional.WithPermission;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.message.Message;
import xyz.upperlevel.uppercore.util.Dbg;

import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand {
    private static int commandsPerPage;
    private static boolean showInaccessibleCommands;

    private static Message headerMessage;
    private static Message footerMessage;
    private static Message lineMessage;

    @AsCommand(
            description = "Shows you info about the current command."
    )
    @WithPermission(
            user = PermissionUser.AVAILABLE
    )
    public void help(CommandContext context, @WithName("page") @WithOptional("1") int page) {
        CommandSender sender = context.sender();
        NodeCommand parent = context.command().getParent();
        if (parent == null) {
            throw new IllegalStateException("HelpCommand without parent");
        }
        Dbg.pf("show-inaccessible-commands: %b", showInaccessibleCommands);
        List<Command> commands = parent.getCommands()
                .stream()
                .filter(cmd -> showInaccessibleCommands || cmd.hasPermission(sender))
                .collect(Collectors.toList());
        int commandsSize = commands.size();
        int maxPages = (int) Math.ceil(((double) commandsSize) / (double) commandsPerPage);
        PlaceholderRegistry<?> frame = PlaceholderRegistry.create()
                .set("node_cmd", parent.getFullName())
                .set("page", page)
                .set("max_page", maxPages);
        headerMessage.send(sender, frame);
        for (int i = (page - 1) * commandsPerPage; i < Math.min(commandsSize, page * commandsPerPage); i++) {
            Command cmd = commands.get(i);
            lineMessage.send(sender, PlaceholderRegistry.create()
                    .set("node_cmd", cmd.getFullName())
                    .set("cmd", cmd.getName())
                    .set("cmd_usage", " " + cmd.getUsage(sender, false))
                    .set("cmd_desc", cmd.getDescription()));
        }
        footerMessage.send(sender, frame);
    }

    public static void configure(Config cfg) {
        commandsPerPage = cfg.getInt("commands-per-page");
        showInaccessibleCommands = cfg.getBool("show-inaccessible-commands");

        headerMessage = cfg.getMessage("help-header");
        footerMessage = cfg.getMessage("help-footer");
        lineMessage = cfg.getMessage("help-line");
    }
}
