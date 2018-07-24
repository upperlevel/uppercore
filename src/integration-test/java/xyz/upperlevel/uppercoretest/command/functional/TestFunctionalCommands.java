package xyz.upperlevel.uppercoretest.command.functional;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.SenderType;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.command.functional.WithPermission;
import xyz.upperlevel.uppercore.command.functional.WithSender;

public class TestFunctionalCommands {
    public TestFunctionalCommands() {
    }

    /**
     * This command tests the FunctionalCommand, with no args.
     */
    @AsCommand
    public void base(CommandSender sender) {
        sender.sendMessage("Command called");
    }

    /**
     * This command tests the command @SenderType.
     * If the sender is not of the given type the command cannot be executed.
     */
    @AsCommand
    @WithSender(SenderType.CONSOLE)
    public void sender(CommandSender sender) {
        sender.sendMessage("Command called");
    }

    /**
     * This command tests the command @WithPermission.
     * If the sender has not the given permission the command cannot be executed.
     */
    @AsCommand
    @WithPermission
    public void permission(CommandSender sender) {
        sender.sendMessage("Command called");
    }
}
