package xyz.upperlevel.uppercore.gui.action.actions;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;

import java.util.Map;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiReloadAction extends Action<GuiReloadAction> {
    public static final GuiReloadActionType TYPE = new GuiReloadActionType();

    @ConfigConstructor
    public GuiReloadAction() {
        super(TYPE);
    }

    @Override
    public void run(Player player) {
        guis().reprint(player);
    }

    public static class GuiReloadActionType extends BaseActionType<GuiReloadAction> {

        public GuiReloadActionType() {
            super(GuiReloadAction.class, "reload-gui");
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