package xyz.upperlevel.uppercore.gui.config.action.actions;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;

import java.util.Map;

public class GuiBackAction extends Action<GuiBackAction> {
    public static final GuiBackActionType TYPE = new GuiBackActionType();

    public GuiBackAction() {
        super(TYPE);
    }

    @Override
    public void run(Player player) {
        GuiManager.back(player);
    }

    public static class GuiBackActionType extends BaseActionType<GuiBackAction> {

        public GuiBackActionType() {
            super("gui-back");
            setParameters();
        }

        @Override
        public GuiBackAction create(Map<String, Object> parameters) {
            return new GuiBackAction();
        }

        @Override
        public Map<String, Object> read(GuiBackAction action) {
            return null;
        }
    }
}

