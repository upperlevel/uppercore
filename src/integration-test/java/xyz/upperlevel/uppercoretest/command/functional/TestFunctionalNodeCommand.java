package xyz.upperlevel.uppercoretest.command.functional;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.NodeCommand;

import java.util.List;

public class TestFunctionalNodeCommand extends NodeCommand {
    public TestFunctionalNodeCommand() {
        super("testfunc");
        addCommand(new TestFunctionalCommands());
        addCommand(new TestFunctionalParameters());
    }

    @Override
    public boolean onCall(CommandSender sender, List<String> args) {
        sender.sendMessage("Sender: " + sender.getName());
        sender.sendMessage("Args: " + args);
        return true;
    }
}
