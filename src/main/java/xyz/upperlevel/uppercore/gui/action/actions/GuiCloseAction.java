package xyz.upperlevel.uppercore.gui.action.actions;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;

import java.util.Map;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiCloseAction extends Action<GuiCloseAction> {
    public static final GuiCloseActionType TYPE = new GuiCloseActionType();

    @ConfigConstructor(inlineable = true)
    public GuiCloseAction() {
        super(TYPE);
    }

    @Override
    public void run(Player player) {
        guis().close(player);
    }

    public static class GuiCloseActionType extends BaseActionType<GuiCloseAction> {

        public GuiCloseActionType() {
            super(GuiCloseAction.class, "close-gui");
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
