package xyz.upperlevel.uppercore.gui.action.actions;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.CurrentPlugin;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;

import java.util.Map;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiCloseAction extends Action<GuiCloseAction> {
    public static final GuiCloseActionType TYPE = new GuiCloseActionType();

    @ConfigConstructor
    public GuiCloseAction(
            @CurrentPlugin Plugin plugin
    ) {
        super(plugin, TYPE);
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
        public GuiCloseAction create(Plugin plugin, Map<String, Object> parameters) {
            return new GuiCloseAction(plugin);
        }

        @Override
        public Map<String, Object> read(GuiCloseAction action) {
            return null;
        }
    }
}
