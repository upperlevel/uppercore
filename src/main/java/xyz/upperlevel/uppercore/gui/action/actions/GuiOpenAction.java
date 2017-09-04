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

public class GuiOpenAction extends Action<GuiOpenAction> {

    public static final GuiOpenActionType TYPE = new GuiOpenActionType();

    @Getter
    private final PlaceholderValue<String> guiId;

    @Getter
    private final boolean clearStack;

    public GuiOpenAction(Plugin plugin, PlaceholderValue<String> guiId, boolean clearStack) {
        super(plugin, TYPE);
        this.guiId = guiId;
        this.clearStack = clearStack;
    }

    @Override
    public void run(Player player) {
        String guiId = this.guiId.resolve(player);
        GuiId gui = guis().get(getPlugin(), guiId);
        if (gui == null) {
            Uppercore.logger().severe("Cannot find gui \"" + guiId + "\"");
            return;
        }
        guis().open(player, gui.get(), clearStack);
    }


    public static class GuiOpenActionType extends BaseActionType<GuiOpenAction> {

        public GuiOpenActionType() {
            super("open-gui");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true),
                    Parameter.of("remove-history", Parser.boolValue(), false, false)
            );
        }

        @Override
        public GuiOpenAction create(Plugin plugin, Map<String, Object> pars) {
            return new GuiOpenAction(
                    plugin,
                    PlaceholderUtil.process((String) pars.get("id")),
                    (Boolean) pars.get("remove-history")
            );
        }

        @Override
        public Map<String, Object> read(GuiOpenAction action) {
            return ImmutableMap.of(
                    "id", action.guiId.toString(),
                    "remove-history", action.clearStack
            );
        }
    }
}
