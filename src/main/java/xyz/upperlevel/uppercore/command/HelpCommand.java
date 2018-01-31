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
            sender.sendMessage(ChatColor.RED + "Help command without parent?");
            return true;

        }
        sender.sendMessage(ChatColor.GREEN + "help command for: " + parent.getName());
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, List<String> arguments) {
        return Collections.emptyList();
    }
}
