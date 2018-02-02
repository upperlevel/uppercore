package xyz.upperlevel.uppercore.script.commands;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.function.WithName;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.function.AsCommand;
import xyz.upperlevel.uppercore.command.function.WithPermission;
import xyz.upperlevel.uppercore.script.PrecompiledScript;
import xyz.upperlevel.uppercore.script.Script;

import javax.script.ScriptEngine;

import static org.bukkit.ChatColor.*;
import static xyz.upperlevel.uppercore.script.ScriptManager.getEngineName;

@WithPermission(value = "info", description = "Allows you to see a script's info")
public class ScriptInfoCommand extends Command {
    public ScriptInfoCommand() {
        super("info");
        setDescription("Shows info about a script.");
    }

    @AsCommand
    public void run(CommandSender sender, @WithName("script") Script script) {
        ScriptEngine engine = script.getEngine();
        //TODO: add script execution time logging
        sender.sendMessage(AQUA + "engine: " + GOLD + getEngineName(engine));
        sender.sendMessage(AQUA + "compiled: " + GOLD + (script instanceof PrecompiledScript));
    }
}
