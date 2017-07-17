package xyz.upperlevel.uppercore.gui.config.action.actions;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;

import java.util.Map;

public class GuiBackAction extends Action<GuiBackAction> {

    public static final GuiBackActionType TYPE = new GuiBackActionType();

    public GuiBackAction(Plugin plugin) {
        super(plugin, TYPE);
    }

    @Override
    public void run(Player player) {
        GuiManager.backGui(player);
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

