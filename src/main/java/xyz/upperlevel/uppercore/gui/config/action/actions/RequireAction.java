package xyz.upperlevel.uppercore.gui.config.action.actions;


import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.config.action.Parser;
import xyz.upperlevel.uppercore.gui.config.placeholders.PlaceholderValue;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarManager;
import xyz.upperlevel.uppercore.gui.script.Script;

import javax.script.ScriptException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class RequireAction extends Action<RequireAction> {
    public static final RequireActionType TYPE = new RequireActionType();
    @Getter
    private final String permission;
    @Getter
    private final PlaceholderValue<String> hotbar;
    @Getter
    private final String script;
    @Getter
    private final List<Action> actions;
    @Getter
    private final List<Action> fail;

    public RequireAction(String permission, PlaceholderValue<String> hotbar, String script, List<Action> actions, List<Action> fail) {
        super(TYPE);
        this.permission = permission;
        this.hotbar = hotbar;
        this.script = script;

        this.actions = actions;
        this.fail = fail;
    }

    @Override
    public void run(Player player) {
        if(test(player))
            for(Action a : actions)
                a.run(player);
        else
            for(Action a : fail)
                a.run(player);
    }

    public boolean test(Player player) {
        return  (permission == null || player.hasPermission(permission)) &&
                (hotbar == null || hasHotbar(player, hotbar)) &&
                (script == null || testScript(player, script));
    }

    private boolean hasHotbar(Player player, PlaceholderValue<String> hotbar) {
        final String id = hotbar.get(player);
        return HotbarManager.isHolding(player, HotbarManager.get(id));
    }

    private boolean testScript(Player player, String id) {
        Script script = Uppercore.get().getScriptSystem().get(id);
        if(script == null) {
            Uppercore.logger().severe("Cannot find script '" + id + "'");
            return true;
        }
        Object res;
        try {
            res = script.execute(player);
        } catch (ScriptException e) {
            Uppercore.logger().log(Level.SEVERE, "Error while executing script '" + id + "'", e);
            return true;
        }
        if(res instanceof Boolean)
            return (Boolean) res;
        else {
            Uppercore.logger().severe("Bad return type in script '" + id + "', must be boolean for a require action!");
            return true;
        }
    }


    public static class RequireActionType extends BaseActionType<RequireAction> {

        public RequireActionType() {
            super("require");
            setParameters(
                    Parameter.of("permission", Parser.strValue(), false),
                    Parameter.of("hotbar", Parser.strValue(), false),
                    Parameter.of("script", Parser.strValue(), false),

                    Parameter.of("actions", Parser.actionsValue(), Collections.emptyList(),false),
                    Parameter.of("else", Parser.actionsValue(), Collections.emptyList(),false)
            );
        }

        @Override
        @SuppressWarnings("unchecked")//Come oooon
        public RequireAction create(Map<String, Object> pars) {
            return new RequireAction(
                    (String) pars.get("permission"),
                    PlaceholderValue.strValue((String) pars.get("hotbar")),
                    (String) pars.get("script"),

                    (List<Action>)pars.get("actions"),
                    (List<Action>)pars.get("else")
            );
        }

        @Override
        public Map<String, Object> read(RequireAction action) {
            return ImmutableMap.of(
                    "permission", action.permission,
                    "hotbar", action.hotbar,
                    "script", action.script,

                    "action", action.actions,
                    "else", action.fail
            );
        }
    }
}

