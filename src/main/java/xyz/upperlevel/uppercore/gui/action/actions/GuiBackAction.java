package xyz.upperlevel.uppercore.gui.action.actions;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;

import java.util.Map;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiBackAction extends Action<GuiBackAction> {
    public static final GuiBackActionType TYPE = new GuiBackActionType();

    @ConfigConstructor(inlineable = true)
    public GuiBackAction() {
        super(TYPE);
    }

    @Override
    public void run(Player player) {
        guis().back(player);
    }

    public static class GuiBackActionType extends BaseActionType<GuiBackAction> {

        public GuiBackActionType() {
            super(GuiBackAction.class, "gui-back");
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

