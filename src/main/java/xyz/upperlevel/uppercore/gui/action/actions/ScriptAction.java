package xyz.upperlevel.uppercore.gui.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.CurrentPlugin;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.script.Script;

import javax.script.ScriptException;
import java.util.Map;
import java.util.logging.Level;


public class ScriptAction extends Action<ScriptAction> {
    public static final ScriptActionType TYPE = new ScriptActionType();

    @Getter
    private final String path;
    @Getter
    private Script script;

    @ConfigConstructor(inlineable = true)
    public ScriptAction(
            @CurrentPlugin Plugin plugin,
            @ConfigProperty("id") String path
    ) {
        super(plugin, TYPE);
        this.path = path;
    }

    @Override
    public void run(Player player) {
        if (script == null) {
            Script script = (Script) getRegistry().find(path);
            if (script == null) {
                Uppercore.logger().severe("Cannot find script \"" + path + "\"");
                this.script = Script.EMPTY;
                return;
            }
            this.script = script;
        }
        try {
            script.execute(player);
        } catch (ScriptException e) {
            Uppercore.logger().log(Level.SEVERE, "Error while executing script \"" + path + "\"", e);
        }
    }


    public static class ScriptActionType extends BaseActionType<ScriptAction> {
        public ScriptActionType() {
            super(ScriptAction.class, "script");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true)
            );
        }

        @Override
        public ScriptAction create(Plugin plugin, Map<String, Object> pars) {
            return new ScriptAction(
                    plugin,
                    (String) pars.get("id")
            );
        }

        @Override
        public Map<String, Object> read(ScriptAction action) {
            return ImmutableMap.of(
                    "id", action.path
            );
        }
    }
}
