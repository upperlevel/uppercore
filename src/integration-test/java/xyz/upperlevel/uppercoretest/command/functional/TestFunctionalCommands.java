package xyz.upperlevel.uppercoretest.command.functional;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.PermissionUser;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.SenderType;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.command.functional.WithPermission;
import xyz.upperlevel.uppercore.command.functional.WithSender;

public class TestFunctionalCommands extends NodeCommand {
    public TestFunctionalCommands() {
        super("cmd");
        append(FunctionalCommand.load(this));
    }

    /**
     * This command tests the FunctionalCommand, with no args.
     */
    @AsCommand
    public void base(CommandSender sender) {
        sender.sendMessage("Base command called.");
    }

    /**
     * This command tests the command @SenderType.
     * If the sender is not of the given type the command cannot be executed.
     */
    @AsCommand
    @WithSender(SenderType.CONSOLE)
    public void sender(CommandSender sender) {
        sender.sendMessage("Command called, in fact the sender is: " + sender.getClass().getSimpleName() + ". Matches? " + SenderType.CONSOLE.match(sender) + ".");
    }

    /**
     * This command tests the command @WithPermission.
     * If the sender has not the given permission the command cannot be executed.
     */
    @AsCommand
    @WithPermission(value="permission", user = PermissionUser.OP)
    public void permission(CommandSender sender) {
        sender.sendMessage("Command called.");
    }
}
