package xyz.upperlevel.uppercore.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.Nms;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.config.action.Parser;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;

@Getter
public class MessageAction extends Action<MessageAction> {

    public static final MessageActionType TYPE = new MessageActionType();

    private final PlaceholderValue<String> message;
    private final boolean raw;

    public MessageAction(Plugin plugin, PlaceholderValue<String> message, boolean raw) {
        super(plugin, TYPE);
        this.message = message;
        this.raw = raw;
    }

    @Override
    public void run(Player player) {
        if (!raw)
            player.sendMessage(message.get(player));
        else
            Nms.sendJson(player, message.get(player));
    }


    public static class MessageActionType extends BaseActionType<MessageAction> {

        public MessageActionType() {
            super("message");
            setParameters(
                    Parameter.of("message", Parser.strValue(), true),
                    Parameter.of("raw", Parser.boolValue(), false, false)
            );
        }

        @Override
        public MessageAction create(Plugin plugin, Map<String, Object> pars) {
            return new MessageAction(
                    plugin,
                    PlaceholderUtil.process((String) pars.get("message")),
                    (Boolean) pars.get("raw")
            );
        }

        @Override
        public Map<String, Object> read(MessageAction action) {
            return ImmutableMap.of(
                    "message", action.message.toString(),
                    "raw", action.raw
            );
        }
    }
}