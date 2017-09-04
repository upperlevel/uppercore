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
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.GuiId;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiChangeAction extends Action<GuiChangeAction> {

    public static final GuiChangeActionType TYPE = new GuiChangeActionType();

    @Getter
    private final PlaceholderValue<String> guiId;

    public GuiChangeAction(Plugin plugin, PlaceholderValue<String> guiId) {
        super(plugin, TYPE);
        this.guiId = guiId;
    }

    @Override
    public void run(Player player) {
        String guiId = this.guiId.resolve(player);
        GuiId gui = guis().get(guiId);
        if (gui == null) {
            Uppercore.logger().severe("Cannot find gui \"" + guiId + "\"");
            return;
        }
        guis().change(player, gui.get());
    }


    public static class GuiChangeActionType extends BaseActionType<GuiChangeAction> {

        public GuiChangeActionType() {
            super("change-gui");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true)
            );
        }

        @Override
        public GuiChangeAction create(Plugin plugin, Map<String, Object> pars) {
            return new GuiChangeAction(
                    plugin,
                    PlaceholderUtil.process((String) pars.get("id"))
            );
        }

        @Override
        public Map<String, Object> read(GuiChangeAction action) {
            return ImmutableMap.of(
                    "id", action.guiId.toString()
            );
        }
    }
}