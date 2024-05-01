package xyz.upperlevel.uppercore.test.command.functional;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.NodeCommand;

import java.util.Queue;

public class TestFunctionalNodeCommand extends NodeCommand {
    public TestFunctionalNodeCommand() {
        super("testfunc");
        append(new TestFunctionalCommands());
        append(new TestFunctionalParameters());
    }

    @Override
    public boolean call(CommandSender sender, Queue<String> args) {
        sender.sendMessage("Sender: " + sender.getName());
        sender.sendMessage("Args: " + args);
        return true;
    }
}
