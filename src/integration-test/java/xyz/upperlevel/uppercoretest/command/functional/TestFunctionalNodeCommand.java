package xyz.upperlevel.uppercoretest.command.functional;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.command.functional.parameter.ArgumentParserManager;

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
