package xyz.upperlevel.uppercore.test.command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.PermissionCompleter;

import java.util.Collections;
import java.util.List;

public class TestCommand extends Command {
    public TestCommand() {
        super("simplecommand");

        setDescription("A command used to test");

        setPermissionPortion(new Permission("simplecommand", PermissionDefault.OP));
        setPermissionCompleter(PermissionCompleter.INHERIT);
    }

    @Override
    public String getUsage(CommandSender sender, boolean colored) {
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
