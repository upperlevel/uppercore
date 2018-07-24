package xyz.upperlevel.uppercoretest.command;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.NodeCommand;

import java.util.List;

public class TestNodeCommand extends NodeCommand {
    public TestNodeCommand() {
        super("simplenodecommand");
    }

    @Override
    public String getUsage(CommandSender sender) {
        return "<subcommand>";
    }

    @Override
    public boolean onCall(CommandSender sender, List<String> args) {
        sender.sendMessage("called simplenodecommand");
        return true;
    }
}
