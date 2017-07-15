package xyz.upperlevel.uppercore.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.config.action.Parser;
import xyz.upperlevel.uppercore.gui.config.placeholders.PlaceholderValue;
import xyz.upperlevel.uppercore.gui.hotbar.Hotbar;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarManager;

import java.util.Map;

public class HotbarRemoveAction extends Action<HotbarRemoveAction> {
    public static final HotbarTakeActionType TYPE = new HotbarTakeActionType();
    @Getter
    private final PlaceholderValue<String> id;

    public HotbarRemoveAction(PlaceholderValue<String> id) {
        super(TYPE);
        this.id = id;
    }

    @Override
    public void run(Player player) {
        String pid = id.get(player);
        Hotbar hotbar = HotbarManager.get(pid);
        if (hotbar == null) {
            Uppercore.logger().severe("Cannot find hotbar \"" + pid + "\"");
            return;
        }
        hotbar.remove(player);
    }


    public static class HotbarTakeActionType extends BaseActionType<HotbarRemoveAction> {

        public HotbarTakeActionType() {
            super("hotbar-remove");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true)//TODO: better Placeholder support
            );
        }

        @Override
        @SuppressWarnings("unchecked")//:(
        public HotbarRemoveAction create(Map<String, Object> pars) {
            return new HotbarRemoveAction(
                    PlaceholderValue.strValue((String) pars.get("id"))
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