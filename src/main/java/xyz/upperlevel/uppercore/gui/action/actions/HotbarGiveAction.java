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
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;
import xyz.upperlevel.uppercore.registry.Registry;

import java.util.Collections;
import java.util.List;
import java.util.Map;

// TODO
public class HotbarGiveAction extends Action<HotbarGiveAction> {
    public static final HotbarGiveActionType TYPE = new HotbarGiveActionType();

    @Getter
    private final PlaceholderValue<String> hotbarPath;
    private final List<Action> actions;
    @Getter
    private final List<Action> fail;

    @ConfigConstructor
    public HotbarGiveAction(
            @CurrentPlugin Plugin plugin,
            @ConfigProperty("id") PlaceholderValue<String> hotbarPath,
            @ConfigProperty(value = "actions", optional = true) List<Action> actions,
            @ConfigProperty(value = "fail", optional = true) List<Action> fail
    ) {
        super(plugin, TYPE);
        this.hotbarPath = hotbarPath;
        this.actions = actions;
        this.fail = fail;
    }

    @Override
    public void run(Player player) {
        String hotbarPath = this.hotbarPath.resolve(player);

        Hotbar hotbar = (Hotbar) getRegistry().get(hotbarPath);
        if (hotbar == null) {
            Uppercore.logger().severe("Cannot find hotbar \"" + hotbarPath + "\"");
            return;
        }
        hotbar.give(player);
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


    public static class HotbarGiveActionType extends BaseActionType<HotbarGiveAction> {

        public HotbarGiveActionType() {
            super(HotbarGiveAction.class, "hotbar-give");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true),
                    Parameter.of("actions", Parser.actionsValue(), Collections.emptyList(), false),
                    Parameter.of("fail", Parser.actionsValue(), Collections.emptyList(), false)
            );
        }

        @Override
        @SuppressWarnings("unchecked")
        public HotbarGiveAction create(Plugin plugin, Map<String, Object> pars) {
            return new HotbarGiveAction(
                    plugin,
                    PlaceholderValue.stringValue((String) pars.get("id")),
                    (List<Action>) pars.get("actions"),
                    (List<Action>) pars.get("fail")
            );
        }

        @Override
        public Map<String, Object> read(HotbarGiveAction action) {
            return ImmutableMap.of(
                    "id", action.hotbarPath.toString(),
                    "action", action.actions,
                    "fail", action.fail
            );
        }
    }
}
