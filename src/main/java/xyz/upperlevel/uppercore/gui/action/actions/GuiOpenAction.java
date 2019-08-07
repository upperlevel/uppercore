package xyz.upperlevel.uppercore.gui.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiOpenAction extends Action<GuiOpenAction> {
    public static final GuiOpenActionType TYPE = new GuiOpenActionType();

    @Getter
    private final PlaceholderValue<String> guiId;

    @Getter
    private final boolean clearStack;

    @ConfigConstructor(inlineable = true)
    public GuiOpenAction(
            @ConfigProperty("id") PlaceholderValue<String> guiId,
            @ConfigProperty(value = "remove-history", optional = true) Boolean clearStack
    ) {
        super(TYPE);
        this.guiId = guiId;
        this.clearStack = clearStack != null ? clearStack : false;
    }

    public GuiOpenAction(Plugin plugin, PlaceholderValue<String> guiId) {
        this(guiId, null);
    }

    @Override
    public void run(Player player) {
        String guiId = this.guiId.resolve(player);

        Gui gui = getRegistry().get(guiId);
        if (gui == null) {
            Uppercore.logger().severe("Cannot find gui \"" + guiId + "\"");
            return;
        }
        guis().open(player, gui, clearStack);
    }


    public static class GuiOpenActionType extends BaseActionType<GuiOpenAction> {
        public GuiOpenActionType() {
            super(GuiOpenAction.class, "open-gui");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true),
                    Parameter.of("remove-history", Parser.boolValue(), false, false)
            );
        }

        @Override
        public GuiOpenAction create(Map<String, Object> pars) {
            return new GuiOpenAction(
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
