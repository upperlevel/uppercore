package xyz.upperlevel.uppercore.gui.action.actions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;

import java.util.Map;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiBackAction extends Action<GuiBackAction> {

    public static final GuiBackActionType TYPE = new GuiBackActionType();

    public GuiBackAction(Plugin plugin) {
        super(plugin, TYPE);
    }

    @Override
    public void run(Player player) {
        guis().back(player);
    }

    public static class GuiBackActionType extends BaseActionType<GuiBackAction> {

        public GuiBackActionType() {
            super("gui-back");
            setParameters();
        }

        @Override
        public GuiBackAction create(Plugin plugin, Map<String, Object> parameters) {
            return new GuiBackAction(plugin);
        }

        @Override
        public Map<String, Object> read(GuiBackAction action) {
            return null;
        }
    }
}

