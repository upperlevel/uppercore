package xyz.upperlevel.uppercore.script.commands;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.Argument;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.WithPermission;
import xyz.upperlevel.uppercore.script.PrecompiledScript;
import xyz.upperlevel.uppercore.script.Script;

import javax.script.ScriptEngine;

import static org.bukkit.ChatColor.*;
import static xyz.upperlevel.uppercore.script.ScriptManager.getEngineName;

@WithPermission(value = "info", desc = "Allows you to see a script's info")
public class ScriptInfoCommand extends Command {
    public ScriptInfoCommand() {
        super("info");
        setDescription("Shows info about a script.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("script") Script script) {
        ScriptEngine engine = script.getEngine();
        //TODO: add script execution time logging
        sender.sendMessage(AQUA + "engine: " + GOLD + getEngineName(engine));
        sender.sendMessage(AQUA + "compiled: " + GOLD + (script instanceof PrecompiledScript));
    }
}
