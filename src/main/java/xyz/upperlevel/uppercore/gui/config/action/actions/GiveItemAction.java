package xyz.upperlevel.uppercore.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType.Parameter;
import xyz.upperlevel.uppercore.gui.config.action.Parser;
import xyz.upperlevel.uppercore.gui.config.itemstack.CustomItem;

import java.util.Map;

public class GiveItemAction extends Action<GiveItemAction> {
    public static final GiveItemActionType TYPE = new GiveItemActionType();

    @Getter
    private CustomItem item;

    public GiveItemAction(CustomItem item) {
        super(TYPE);
        this.item = item;
    }

    @Override
    public void run(Player player) {
        player.getInventory().addItem(item.toItemStack(player));
        player.updateInventory();
    }

    public static class GiveItemActionType extends BaseActionType<GiveItemAction> {

        public GiveItemActionType() {
            super("give-item");
            setParameters(
                    Parameter.of("item", Parser.itemValue(), true)
            );
        }

        @Override
        public GiveItemAction create(Map<String, Object> pars) {
            return new GiveItemAction(
                    (CustomItem)pars.get("item")
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
