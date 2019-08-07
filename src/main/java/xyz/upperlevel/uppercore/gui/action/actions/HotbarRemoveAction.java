package xyz.upperlevel.uppercore.gui.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.hotbar.Hotbar;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;

public class HotbarRemoveAction extends Action<HotbarRemoveAction> {
    public static final HotbarTakeActionType TYPE = new HotbarTakeActionType();

    @Getter
    private final PlaceholderValue<String> id;

    @ConfigConstructor(inlineable = true)
    public HotbarRemoveAction(
            @ConfigProperty("id") PlaceholderValue<String> id
    ) {
        super(TYPE);
        this.id = id;
    }

    @Override
    public void run(Player player) {
        String barId = id.resolve(player);

        Hotbar hotbar = getRegistry().get(barId);
        if (hotbar == null) {
            Uppercore.logger().severe("Cannot find hotbar \"" + barId + "\"");
            return;
        }
        hotbar.remove(player);
    }


    public static class HotbarTakeActionType extends BaseActionType<HotbarRemoveAction> {

        public HotbarTakeActionType() {
            super(HotbarRemoveAction.class, "hotbar-remove");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true)//TODO: better Placeholder support
            );
        }

        @Override
        public HotbarRemoveAction create(Map<String, Object> pars) {
            return new HotbarRemoveAction(
                    PlaceholderValue.stringValue((String) pars.get("id"))
            );
        }

        @Override
        public Map<String, Object> read(HotbarRemoveAction action) {
            return ImmutableMap.of(
                    "id", action.id.toString()
            );
        }
    }
}