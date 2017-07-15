package xyz.upperlevel.uppercore.gui.config.action.actions;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;

import java.util.Map;

public class GuiReloadAction extends Action<GuiReloadAction> {
    public static final GuiReloadActionType TYPE = new GuiReloadActionType();

    public GuiReloadAction() {
        super(TYPE);
    }

    @Override
    public void run(Player player) {
        GuiManager.reprint(player);
    }

    public static class GuiReloadActionType extends BaseActionType<GuiReloadAction> {

        public GuiReloadActionType() {
            super("gui-reload");
            setParameters();
        }

        @Override
        public GuiReloadAction create(Map<String, Object> parameters) {
            return new GuiReloadAction();
        }

        @Override
        public Map<String, Object> read(GuiReloadAction action) {
            return null;
        }
    }
}