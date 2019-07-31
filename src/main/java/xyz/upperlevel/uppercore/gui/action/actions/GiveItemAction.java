package xyz.upperlevel.uppercore.gui.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.CurrentPlugin;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.itemstack.UItem;

import java.util.Map;

public class GiveItemAction extends Action<GiveItemAction> {
    public static final GiveItemActionType TYPE = new GiveItemActionType();

    @Getter
    private UItem item;

    @ConfigConstructor(inlineable = true)
    public GiveItemAction(
            @CurrentPlugin Plugin plugin,
            @ConfigProperty("item") UItem item
    ) {
        super(plugin, TYPE);
        this.item = item;
    }

    @Override
    public void run(Player player) {
        player.getInventory().addItem(item.resolve(player));
        player.updateInventory();
    }

    public static class GiveItemActionType extends BaseActionType<GiveItemAction> {

        public GiveItemActionType() {
            super(GiveItemAction.class, "give-item");
            setParameters(
                    Parameter.of("item", Parser.itemValue(), true)
            );
        }

        @Override
        public GiveItemAction create(Plugin plugin, Map<String, Object> pars) {
            return new GiveItemAction(
                    plugin,
                    (UItem) pars.get("item")
            );
        }

        @Override
        public Map<String, Object> read(GiveItemAction action) {
            return ImmutableMap.of(
                    "item", action.item
            );
        }
    }
}
