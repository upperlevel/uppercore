package xyz.upperlevel.uppercore.gui.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;
import xyz.upperlevel.uppercore.nms.impl.MessageNms;

import java.util.Map;

import static xyz.upperlevel.uppercore.util.TextUtil.translateCustom;

public class MessageAction extends Action<MessageAction> {
    public static final MessageActionType TYPE = new MessageActionType();

    @Getter
    private final PlaceholderValue<String> message;
    @Getter
    private final boolean raw;

    @ConfigConstructor
    public MessageAction(
            @ConfigProperty("message") PlaceholderValue<String> message,
            @ConfigProperty(value = "raw", optional = true) Boolean raw
    ) {
        super(TYPE);
        this.message = message;
        this.raw = raw != null ? raw : false;
    }

    @Override
    public void run(Player player) {
        if (!raw) {
            player.sendMessage(translateCustom(message.resolve(player)));
        } else {
            MessageNms.sendJson(player, message.resolve(player));
        }
    }


    public static class MessageActionType extends BaseActionType<MessageAction> {

        public MessageActionType() {
            super(MessageAction.class, "message");
            setParameters(
                    Parameter.of("message", Parser.strValue(), true),
                    Parameter.of("raw", Parser.boolValue(), false, false)
            );
        }

        @Override
        public MessageAction create(Map<String, Object> pars) {
            return new MessageAction(
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