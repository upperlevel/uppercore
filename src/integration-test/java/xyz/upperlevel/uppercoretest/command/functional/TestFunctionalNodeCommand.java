package xyz.upperlevel.uppercoretest.command.functional;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.NodeCommand;

import java.util.List;

public class TestFunctionalNodeCommand extends NodeCommand {
    public TestFunctionalNodeCommand() {
        super("testfunc");
        append(new TestFunctionalCommands());
        append(new TestFunctionalParameters());
    }

    @Override
    public boolean onCall(CommandSender sender, List<String> args) {
        sender.sendMessage("Sender: " + sender.getName());
        sender.sendMessage("Args: " + args);
        return true;
    }
}
