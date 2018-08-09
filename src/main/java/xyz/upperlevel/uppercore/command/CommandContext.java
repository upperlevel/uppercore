package xyz.upperlevel.uppercore.command;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.command.CommandSender;

public class CommandContext {
    @Getter
    @Accessors(fluent = true)
    private final CommandSender sender;

    @Getter
    @Accessors(fluent = true)
    private final Command command;

    public CommandContext(CommandSender sender, Command command) {
        this.sender = sender;
        this.command = command;
    }

    public void send(String message) {
        sender.sendMessage(message);
    }
}
