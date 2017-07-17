package xyz.upperlevel.uppercore.script.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.Optional;
import xyz.upperlevel.uppercore.script.ScriptRegistry;
import xyz.upperlevel.uppercore.script.ScriptSystem;

import java.util.Collection;
import java.util.StringJoiner;

import static org.bukkit.ChatColor.*;

public class ScriptListCommand extends Command {

    public ScriptListCommand() {
        super("list");
        setDescription("Shows scripts.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("plugin") @Optional Plugin plugin) {
        Collection<String> scriptNames;
        if (plugin != null) {
            ScriptRegistry reg = ScriptSystem.instance().getRegistry(plugin);
            scriptNames = reg.getScripts().keySet();
        } else
            scriptNames = ScriptSystem.instance().get().keySet();
        StringJoiner str = new StringJoiner(GRAY + ", ");
        for (String name : scriptNames)
            str.add(AQUA + name);
        if (scriptNames.size() > 0)
            sender.sendMessage(GOLD + "Showing " + scriptNames.size() + " scripts: " + str.toString());
        else
            sender.sendMessage(RED + "No script found.");
    }
}
