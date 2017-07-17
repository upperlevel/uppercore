package xyz.upperlevel.uppercore.gui.config.action.actions;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.GuiSystem;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;

import java.util.Map;

public class GuiCloseAction extends Action<GuiCloseAction> {

    public static final GuiCloseActionType TYPE = new GuiCloseActionType();

    public GuiCloseAction(Plugin plugin) {
        super(plugin, TYPE);
    }

    @Override
    public void run(Player player) {
        GuiSystem.close(player);
    }

    public static class GuiCloseActionType extends BaseActionType<GuiCloseAction> {

        public GuiCloseActionType() {
            super("close-gui");
            setParameters();
        }

        @Override
        public GuiCloseAction create(Plugin plugin, Map<String, Object> parameters) {
            return new GuiCloseAction(plugin);
        }

        @Override
        public Map<String, Object> read(GuiCloseAction action) {
            return null;
        }
    }
}
