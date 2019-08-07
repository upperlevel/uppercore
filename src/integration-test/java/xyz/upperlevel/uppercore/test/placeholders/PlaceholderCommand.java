package xyz.upperlevel.uppercore.test.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Collections;
import java.util.List;

public class PlaceholderCommand extends Command {
    public PlaceholderCommand() {
        super("testplaceholders");
    }

    @Override
    public String getUsage(CommandSender sender, boolean colored) {
        return "";
    }

    @Override
    protected boolean onCall(CommandSender sender, List<String> args) {
        Player player = sender instanceof Player ? (Player) sender : null;

        String text = String.join(" ", args);
        String solved;

        if (!args.isEmpty() && "papi".equals(args.get(0))) {
            // Test against native placeholder detection
            solved = PlaceholderAPI.setPlaceholders(player, text);
        } else {
            solved = PlaceholderValue.stringValue(text).resolve(player);
        }

        sender.sendMessage(solved);
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, List<String> arguments) {
        return Collections.emptyList();
    }
}
