package xyz.upperlevel.uppercore.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.config.action.Parser;
import xyz.upperlevel.uppercore.gui.hotbar.Hotbar;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarSystem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class HotbarGiveAction extends Action<HotbarGiveAction> {

    public static final HotbarGiveActionType TYPE = new HotbarGiveActionType();

    private final PlaceholderValue<String> id;
    private final List<Action> actions;
    private final List<Action> fail;

    public HotbarGiveAction(Plugin plugin, PlaceholderValue<String> id, List<Action> actions, List<Action> fail) {
        super(plugin, TYPE);
        this.id = id;
        this.actions = actions;
        this.fail = fail;
    }

    @Override
    public void run(Player player) {
        String hotbarId = id.get(player);
        Hotbar hotbar = HotbarSystem.getHotbar(getPlugin(), hotbarId);
        if (hotbar == null) {
            Uppercore.logger().severe("Cannot find hotbar \"" + hotbarId + "\"");
            return;
        }
        hotbar.give(player);
    }


    public static class HotbarGiveActionType extends BaseActionType<HotbarGiveAction> {

        public HotbarGiveActionType() {
            super("hotbar-give");
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
                    "id", action.id.toString(),
                    "action", action.actions,
                    "fail", action.fail
            );
        }
    }
}
