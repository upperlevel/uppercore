package xyz.upperlevel.uppercore.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.config.action.Parser;
import xyz.upperlevel.uppercore.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.uppercore.gui.config.placeholders.PlaceholderValue;

import java.util.Map;

public class GuiOpenAction extends Action<GuiOpenAction> {
    public static final GuiOpenActionType TYPE = new GuiOpenActionType();
    @Getter
    private final PlaceholderValue<String> guiId;

    @Getter
    private final boolean clearStack;

    public GuiOpenAction(PlaceholderValue<String> guiId, boolean clearStack) {
        super(TYPE);
        this.guiId = guiId;
        this.clearStack = clearStack;
    }

    @Override
    public void run(Player player) {
        Gui gui =  GuiManager.get(guiId.get(player));
        if(gui == null) {
            Uppercore.logger().severe("Cannot find gui \"" + guiId + "\"");
            return;
        }
        GuiManager.open(player, gui, clearStack);
    }


    public static class GuiOpenActionType extends BaseActionType<GuiOpenAction> {

        public GuiOpenActionType() {
            super("gui-open");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true),
                    Parameter.of("remove-history", Parser.boolValue(), false, false)
            );
        }

        @Override
        public GuiOpenAction create(Map<String, Object> pars) {
            return new GuiOpenAction(
                    PlaceHolderUtil.process((String) pars.get("id")),
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
