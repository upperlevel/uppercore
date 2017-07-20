package xyz.upperlevel.uppercore.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.gui.GuiSystem;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.config.action.Parser;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;

public class GuiOpenAction extends Action<GuiOpenAction> {

    public static final GuiOpenActionType TYPE = new GuiOpenActionType();

    @Getter
    private final PlaceholderValue<String> guiId;

    @Getter
    private final boolean clearStack;

    public GuiOpenAction(Plugin plugin, PlaceholderValue<String> guiId, boolean clearStack) {
        super(plugin, TYPE);
        this.guiId = guiId;
        this.clearStack = clearStack;
    }

    @Override
    public void run(Player player) {
        String guiId = this.guiId.resolve(player);
        Gui gui = GuiSystem.get(getPlugin(), guiId);
        if (gui == null) {
            Uppercore.logger().severe("Cannot find gui \"" + guiId + "\"");
            return;
        }
        GuiSystem.open(player, gui, clearStack);
    }


    public static class GuiOpenActionType extends BaseActionType<GuiOpenAction> {

        public GuiOpenActionType() {
            super("open-gui");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true),
                    Parameter.of("remove-history", Parser.boolValue(), false, false)
            );
        }

        @Override
        public GuiOpenAction create(Plugin plugin, Map<String, Object> pars) {
            return new GuiOpenAction(
                    plugin,
                    PlaceholderUtil.process((String) pars.get("id")),
                    (Boolean) pars.get("remove-history")
            );
        }

        @Override
        public Map<String, Object> read(GuiOpenAction action) {
            return ImmutableMap.of(
                    "id", action.guiId.toString(),
                    "remove-history", action.clearStack
            );
        }
    }
}
