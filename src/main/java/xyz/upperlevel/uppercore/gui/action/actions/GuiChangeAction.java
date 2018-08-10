package xyz.upperlevel.uppercore.gui.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.CurrentPlugin;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiChangeAction extends Action<GuiChangeAction> {
    public static final GuiChangeActionType TYPE = new GuiChangeActionType();

    @Getter
    private final PlaceholderValue<String> guiId;

    @ConfigConstructor(inlineable = true)
    public GuiChangeAction(
            @CurrentPlugin Plugin plugin,
            @ConfigProperty("id") PlaceholderValue<String> guiId
    ) {
        super(plugin, TYPE);
        this.guiId = guiId;
    }

    @Override
    public void run(Player player) {
        String guiId = this.guiId.resolve(player);
        Gui gui = (Gui) getRegistry().find(guiId);
        if (gui == null) {
            Uppercore.logger().severe("Cannot find gui \"" + guiId + "\"");
            return;
        }
        guis().change(player, gui);
    }


    public static class GuiChangeActionType extends BaseActionType<GuiChangeAction> {

        public GuiChangeActionType() {
            super(GuiChangeAction.class, "change-gui");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true)
            );
        }

        @Override
        public GuiChangeAction create(Plugin plugin, Map<String, Object> pars) {
            return new GuiChangeAction(
                    plugin,
                    PlaceholderUtil.process((String) pars.get("id"))
            );
        }

        @Override
        public Map<String, Object> read(GuiChangeAction action) {
            return ImmutableMap.of(
                    "id", action.guiId.toString()
            );
        }
    }
}