package xyz.upperlevel.uppercore.script.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.script.ScriptRegistry;

import java.util.Collection;
import java.util.StringJoiner;

import static org.bukkit.ChatColor.*;
import static xyz.upperlevel.uppercore.Uppercore.scripts;

@WithPermission(value = "list", desc = "Allows you to list all registered scripts")
public class ScriptListCommand extends Command {

    public ScriptListCommand() {
        super("list");
        setDescription("Shows scripts.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("plugin") @Optional Plugin plugin) {
        Collection<String> scriptNames;
        if (plugin != null) {
            ScriptRegistry reg = (ScriptRegistry) scripts().get(plugin);
            scriptNames = reg.getEntries().keySet();
        } else
            scriptNames = scripts().getEntries().keySet();
        StringJoiner str = new StringJoiner(GRAY + ", ");
        for (String name : scriptNames)
            str.add(AQUA + name);
        if (scriptNames.size() > 0)
            sender.sendMessage(GOLD + "Showing " + scriptNames.size() + " scripts: " + str.toString());
        else
            sender.sendMessage(RED + "No script found.");
    }
}
