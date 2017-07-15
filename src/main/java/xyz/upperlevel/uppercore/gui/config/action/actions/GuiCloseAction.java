package xyz.upperlevel.uppercore.gui.config.action.actions;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;

import java.util.Map;

public class GuiCloseAction extends Action<GuiCloseAction> {
    public static final GuiCloseActionType TYPE = new GuiCloseActionType();

    public GuiCloseAction() {
        super(TYPE);
    }

    @Override
    public void run(Player player) {
        GuiManager.back(player);
    }

    public static class GuiCloseActionType extends BaseActionType<GuiCloseAction> {

        public GuiCloseActionType() {
            super("gui-close");
            setParameters();
        }

        @Override
        public GuiCloseAction create(Map<String, Object> parameters) {
            return new GuiCloseAction();
        }

        @Override
        public Map<String, Object> read(GuiCloseAction action) {
            return null;
        }
    }
}
