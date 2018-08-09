package xyz.upperlevel.uppercore.command;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.WithOptional;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.message.Message;

import java.util.Collections;
import java.util.List;

public class HelpCommand {
    /* Configuration */
    private static int commandsPerPage;

    private static Message headerMessage;
    private static Message footerMessage;
    private static Message lineMessage;

    @AsCommand
    public void help(CommandContext context, @WithOptional("1") int page) {
        CommandSender sender = context.sender();
        NodeCommand parent = context.command().getParent();
        if (parent == null) {
            throw new IllegalStateException("HelpCommand without parent");
        }
        int commandsSize = parent.getCommands().size();
        int maxPages = (int) Math.ceil(((double) commandsSize) / (double) commandsPerPage);
        PlaceholderRegistry frame = PlaceholderRegistry.create()
                .set("node_cmd", parent.getPath())
                .set("page", page)
                .set("max_page", maxPages);
        headerMessage.send(sender, frame);
        for (int i = (page - 1) * commandsPerPage; i < Math.min(commandsSize, page * commandsPerPage); i++) {
            Command cmd = parent.getCommands().get(i);
            lineMessage.send(sender, PlaceholderRegistry.create()
                    .set("node_cmd", cmd.getPath())
                    .set("cmd", cmd.getName())
                    .set("cmd_usage", cmd.getUsage(sender, false))
                    .set("cmd_desc", cmd.getDescription()));
        }
        footerMessage.send(sender, frame);
    }

    public static void configure(Config cfg) {
        commandsPerPage = cfg.getInt("commands-per-page");

        headerMessage = cfg.getMessage("help-header");
        footerMessage = cfg.getMessage("help-footer");
        lineMessage = cfg.getMessage("help-line");
    }
}
