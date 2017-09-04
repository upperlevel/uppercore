package xyz.upperlevel.uppercore.script;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifier;

public class ScriptId extends Identifier<Script> {
    public ScriptId(Plugin plugin, String id, Script handle) {
        super(plugin, id, handle);
    }

    public static ScriptId of(Plugin plugin, String id, Script handle) {
        return new ScriptId(plugin, id, handle);
    }
}
