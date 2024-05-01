package xyz.upperlevel.uppercore.gui.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;
import java.util.function.Predicate;

import static xyz.upperlevel.uppercore.util.TextUtil.translateCustom;

public class BroadcastAction extends Action<BroadcastAction> {
    public static final BroadcastActionType TYPE = new BroadcastActionType();

    @Getter
    private final PlaceholderValue<String> message;
    @Getter
    private final String permission;
    @Getter
    private final boolean raw;

    @ConfigConstructor(inlineable = true)
    private BroadcastAction(
            @ConfigProperty("message") PlaceholderValue<String> message,
            @ConfigProperty(value = "permission", optional = true) String permission,
            @ConfigProperty(value = "raw", optional = true) Boolean raw
    ) {
        super(TYPE);
        this.message = message;
        this.permission = permission;
        this.raw = raw != null ? raw : false;
    }

    @Override
    public void run(Player player) {
        if (!raw) {
            if (permission != null)
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission(permission))
                        p.sendMessage(translateCustom(message.resolve(p)));
                }
            else
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(translateCustom(message.resolve(p)));
                }
        } else {
            if (message.hasPlaceholders()) {
                sendJson(message, permission != null ? p -> p.hasPermission(permission) : p -> true);
            } else {
                sendMessage(message.resolve(null), permission != null ? p -> p.hasPermission(permission) : p -> true);
            }
        }
    }

    private void sendMessage(String json, Predicate<Player> selector) {
        String translated = translateCustom(json);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (selector.test(p)) {
                p.sendMessage(translated);
            }
        }
    }

    private void sendJson(PlaceholderValue<String> json, Predicate<Player> selector) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (selector.test(p)) {
                p.sendMessage(translateCustom(json.resolve(p)));
            }
        }
    }


    public static class BroadcastActionType extends BaseActionType<BroadcastAction> {

        public BroadcastActionType() {
            super(BroadcastAction.class, "broadcast");
            setParameters(
                    Parameter.of("message", Parser.strValue(), true),
                    Parameter.of("permission", Parser.strValue(), false),
                    Parameter.of("raw", Parser.boolValue(), false, false)
            );
        }

        @Override
        public BroadcastAction create(Map<String, Object> pars) {
            return new BroadcastAction(
                    PlaceholderUtil.process((String) pars.get("message")),
                    (String) pars.get("permission"),
                    (boolean) pars.get("raw")
            );
        }

        @Override
        public Map<String, Object> read(BroadcastAction action) {
            return ImmutableMap.of(
                    "message", action.message.toString(),
                    "permission", action.permission,
                    "raw", action.raw
            );
        }
    }
}
