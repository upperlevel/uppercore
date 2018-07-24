package xyz.upperlevel.uppercoretest.command;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.PermissionCompleter;

import java.util.Collections;
import java.util.List;

public class TestCommand extends Command {
    public TestCommand() {
        super("simplecommand");

        setDescription("A command used to test");

        setPermissionCompleter(PermissionCompleter.NONE);
    }

    @Override
    public String getUsage(CommandSender sender) {
        return "<arg1> <arg2> <arg3>";
    }

    @Override
    protected boolean onCall(CommandSender sender, List<String> args) {
        sender.sendMessage("called name: simplecommand, with args: " + args.toString());
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, List<String> args) {
        return Collections.emptyList();
    }
}
