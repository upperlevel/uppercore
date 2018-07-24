package xyz.upperlevel.uppercoretest.command.functional;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.command.functional.parameter.ArgumentParserManager;

import java.util.List;

public class TestFunctionalNodeCommand extends NodeCommand {
    public TestFunctionalNodeCommand() {
        super("testfunctional");

        // Register ALL functions annotated with @AsCommand in both classes
        addCommands(FunctionalCommand.load(new TestFunctionalCommands(), new ArgumentParserManager()));
        addCommands(FunctionalCommand.load(new TestFunctionalParameters(), new ArgumentParserManager()));
    }

    @Override
    public String getUsage(CommandSender sender) {
        return ""; // TODO
    }

    @Override
    public boolean onCall(CommandSender sender, List<String> args) {
        sender.sendMessage("Sender: " + sender.getName());
        sender.sendMessage("Args: " + args);
        return true;
    }
}
