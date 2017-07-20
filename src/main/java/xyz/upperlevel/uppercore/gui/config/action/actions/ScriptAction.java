package xyz.upperlevel.uppercore.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.config.action.Parser;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.script.Script;
import xyz.upperlevel.uppercore.script.ScriptSystem;

import javax.script.Bindings;
import javax.script.ScriptException;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;


public class ScriptAction extends Action<ScriptAction> {

    public static final ScriptActionType TYPE = new ScriptActionType();

    @Getter
    private final String id;
    private Script script;

    public ScriptAction(Plugin plugin, String id) {
        super(plugin, TYPE);
        this.id = id;
    }

    @Override
    public void run(Player player) {
        if (script == null) {
            script = ScriptSystem.get(id);
            if (script == null) {
                Uppercore.logger().severe("Cannot find script \"" + id + "\"");
                script = Script.EMPTY;
                return;
            }
        }
        if (script == Script.EMPTY) return;
        Bindings b = script.createBindings();
        b.put("player", player);
        b.put("placeholder", (Function<String, String>) str -> PlaceholderUtil.resolvePlaceholders(player, str));
        try {
            script.run(b);
        } catch (ScriptException e) {
            Uppercore.logger().log(Level.SEVERE, "Error while executing script \"" + id + "\"", e);
        }
    }


    public static class ScriptActionType extends BaseActionType<ScriptAction> {
        public ScriptActionType() {
            super("script");
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
                    "id", action.id
            );
        }
    }
}
