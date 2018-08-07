package xyz.upperlevel.uppercore.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public String getUsage(CommandSender sender) {
        return "[page]";
    }

    @Override
    protected boolean onCall(CommandSender sender, List<String> arguments) {
        NodeCommand parent = getParent();
        if (parent == null) {
            sender.sendMessage(ChatColor.RED + "No parent command to read from.");
            return true;
        }
        for (Command command : parent.getCommands()) {
            String helpline = ChatColor.YELLOW + command.getName() + " " + (sender.hasPermission(command.getPermission()) ? ChatColor.GREEN : ChatColor.RED) + command.getPermission().getName();
            sender.sendMessage(helpline);
        }
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, List<String> arguments) {
        return Collections.emptyList();
    }
}
