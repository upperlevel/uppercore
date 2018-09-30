package xyz.upperlevel.uppercoretest.command.functional;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.SenderType;
import xyz.upperlevel.uppercore.command.functional.*;

import java.util.Arrays;

import static xyz.upperlevel.uppercore.command.PermissionUser.OP;

public class TestFunctionalParameters extends NodeCommand {
    public TestFunctionalParameters() {
        super("par");
        append(FunctionalCommand.load(this));
    }

    @AsCommand
    public void simple(CommandSender sender, int arg1) {
        sender.sendMessage("Your number: " + arg1 + ".");
    }

    @AsCommand
    public void array(CommandSender sender, String arg1, String[] arg2) {
        sender.sendMessage("Your first arg was: " + arg1 + ". Your second string was: " + Arrays.toString(arg2) + ".");
    }

    /**
     * This command tests @WithPermission on one parameter.
     * The command will run only if the sender has the permission on the parameter.
     */
    @AsCommand
    @WithPermission("permission")
    public void permission(CommandSender sender, @WithPermission(value = "arg", user = OP) String arg) {
        sender.sendMessage("You may be OP since you successfully ran this command with arg: " + arg + ".");
    }

    /**
     * This command tests many @WithPermission on different parameters.
     * The command will run only if the sender has them all.
     */
    @AsCommand
    @WithPermission("permissions")
    public void permissions(CommandSender sender, @WithPermission("arg1") String arg1, @WithPermission("arg2") String arg2) {
        sender.sendMessage("Sender: " + sender.getName());
        sender.sendMessage("Arg1: " + arg1);
        sender.sendMessage("Arg2: " + arg2);
    }

    /**
     * This command tests a @WithOptional parameter.
     * If the parameter is optional, if not given, will be set to null.
     */
    @AsCommand
    public void optional(CommandSender sender, @WithOptional("-1") int arg) {
        sender.sendMessage("Sender: " + sender.getName());
        sender.sendMessage("Arg: " + arg);
    }

    /**
     * This command tests many @WithOptional parameters.
     * Only the lasts arguments can be optional, so a sender can't specify 'arg2' without specifying 'arg1'.
     */
    @AsCommand
    public void optionals(CommandSender sender, @WithOptional String arg1, @WithOptional String arg2) {
        sender.sendMessage("Sender: " + sender.getName());
        sender.sendMessage("Arg1: " + arg1);
        sender.sendMessage("Arg2: " + arg2);
    }

    /**
     * This command tests a @WithOptional @WithPermission parameter.
     * In this case, if the sender has not the permission on an optional parameter the command will be executed anyway.
     */
    @AsCommand
    public void permissionAndOptional(CommandSender sender, @WithOptional @WithPermission("arg1") String arg1) {
        sender.sendMessage("Sender: " + sender.getName());
        sender.sendMessage("Arg1: " + arg1);
    }

    /**
     * This command tests a @WithOptional @WithPermission parameter, followed by a @WithPermission parameter.
     * The sender must have permission for 'arg2', if 'arg1' is not specified, the first argument will be
     * interpreted as 'arg2' and 'arg1' is filled with optional's default value or null.
     */
    @AsCommand
    public void optionalPermissionAndPermission(CommandSender sender, @WithOptional @WithPermission("arg1") String arg1, @WithPermission("arg2") String arg2) {
        sender.sendMessage("Sender: " + sender.getName());
        sender.sendMessage("Arg1: " + arg1);
        sender.sendMessage("Arg2: " + arg2);
    }

    /**
     * This command tests a @WithSender parameter.
     * If the sender matches the SenderType, then he can type 'arg1'.
     */
    @AsCommand
    public void sender(CommandSender sender, @WithSender(SenderType.CONSOLE) String arg1) {
        sender.sendMessage("Sender: " + sender.getName());
        sender.sendMessage("Arg1: " + arg1);
    }
}
