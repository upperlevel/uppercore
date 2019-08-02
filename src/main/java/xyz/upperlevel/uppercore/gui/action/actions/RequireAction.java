package xyz.upperlevel.uppercore.gui.action.actions;


import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.CurrentPlugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.hotbar.Hotbar;
import xyz.upperlevel.uppercore.hotbar.HotbarView;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;
import xyz.upperlevel.uppercore.registry.Registry;
import xyz.upperlevel.uppercore.script.Script;

import javax.script.ScriptException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static xyz.upperlevel.uppercore.Uppercore.hotbars;

// TODO
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

    @ConfigConstructor
    public RequireAction(
            @CurrentPlugin Plugin plugin,
            @ConfigProperty(value = "permission", optional = true) String permission,
            @ConfigProperty(value = "hotbar", optional = true) PlaceholderValue<String> hotbar,
            @ConfigProperty(value = "script", optional = true) String script,
            @ConfigProperty(value = "actions", optional = true) List<Action> actions,
            @ConfigProperty(value = "fail", optional = true) List<Action> fail
    ) {
        super(plugin, TYPE);
        this.permission = permission;
        this.hotbar = hotbar;
        this.script = script;

        this.actions = actions != null ? actions : Collections.emptyList();
        this.fail = fail != null ? fail : Collections.emptyList();
    }

    @Override
    public void run(Player player) {
        if (test(player)) {
            for (Action a : actions) {
                a.run(player);
            }
        } else {
            for (Action a : fail) {
                a.run(player);
            }
        }
    }

    @Override
    public void setRegistry(Registry<?> registry) {
        super.setRegistry(registry);
        for (Action<?> action : actions) {
            action.setRegistry(registry);
        }
        for (Action<?> action : fail) {
            action.setRegistry(registry);
        }
    }

    public boolean test(Player player) {
        return (permission == null || player.hasPermission(permission)) &&
                (hotbar == null || hasHotbar(player, hotbar)) &&
                (script == null || testScript(player, script));
    }

    private boolean hasHotbar(Player player, PlaceholderValue<String> hotbarPath) {
        String id = hotbarPath.resolve(player);
        Hotbar hotbar = (Hotbar) getRegistry().find(id);
        if (hotbar == null) {
            Uppercore.logger().severe("Cannot find hotbar \"" + id + "\"");
            return false;
        }
        HotbarView view = hotbars().view(player);
        return view.isHolding(hotbar);
    }

    private boolean testScript(Player player, String path) {
        Script script = (Script) getRegistry().find(path);
        if (script == null) {
            Uppercore.logger().severe("Cannot find script '" + path + "'");
            return true;
        }
        Object res;
        try {
            res = script.execute(player);
        } catch (ScriptException e) {
            Uppercore.logger().log(Level.SEVERE, "Error while executing script '" + path + "'", e);
            return true;
        }
        if (res instanceof Boolean)
            return (Boolean) res;
        else {
            Uppercore.logger().severe("Bad return type in script '" + path + "', must be boolean for a require action!");
            return true;
        }
    }


    public static class RequireActionType extends BaseActionType<RequireAction> {

        public RequireActionType() {
            super(RequireAction.class, "require");
            setParameters(
                    Parameter.of("permission", Parser.strValue(), false),
                    Parameter.of("hotbar", Parser.strValue(), false),
                    Parameter.of("script", Parser.strValue(), false),

                    Parameter.of("actions", Parser.actionsValue(), Collections.emptyList(), false),
                    Parameter.of("else", Parser.actionsValue(), Collections.emptyList(), false)
            );
        }

        @Override
        @SuppressWarnings("unchecked")
        public RequireAction create(Plugin plugin, Map<String, Object> pars) {
            return new RequireAction(
                    plugin,
                    (String) pars.get("permission"),
                    PlaceholderValue.stringValue((String) pars.get("hotbar")),
                    (String) pars.get("script"),
                    (List<Action>) pars.get("actions"),
                    (List<Action>) pars.get("else")
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

