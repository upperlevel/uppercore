package xyz.upperlevel.uppercore.gui.action.actions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.itemstack.CustomItem;

import java.util.Map;

public class GiveItemAction extends Action<GiveItemAction> {

    public static final GiveItemActionType TYPE = new GiveItemActionType();

    @Getter
    private CustomItem item;

    public GiveItemAction(Plugin plugin, CustomItem item) {
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
            super("give-item");
            setParameters(
                    Parameter.of("item", Parser.itemValue(), true)
            );
        }

        @Override
        public GiveItemAction create(Plugin plugin, Map<String, Object> pars) {
            return new GiveItemAction(
                    plugin,
                    (CustomItem) pars.get("item")
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
