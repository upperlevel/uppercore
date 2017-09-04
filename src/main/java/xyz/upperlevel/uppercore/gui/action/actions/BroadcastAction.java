package xyz.upperlevel.uppercore.gui.action.actions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;
import xyz.upperlevel.uppercore.util.nms.impl.MessageNms;
import xyz.upperlevel.uppercore.util.nms.impl.entity.PlayerNms;

import java.util.Map;
import java.util.function.Predicate;

import static xyz.upperlevel.uppercore.util.TextUtil.translateCustom;

@Getter
public class BroadcastAction extends Action<BroadcastAction> {

    public static final BroadcastActionType TYPE = new BroadcastActionType();

    private final PlaceholderValue<String> message;
    private final String permission;
    private final boolean raw;

    public BroadcastAction(Plugin plugin, PlaceholderValue<String> message, String permission, boolean raw) {
        super(plugin, TYPE);
        this.message = message;
        this.permission = permission;
        this.raw = raw;
    }

    @Override
    public void run(Player player) {
        if(!raw) {
            if (permission != null)
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(p.hasPermission(permission))
                        p.sendMessage(translateCustom(message.resolve(p)));
                }
            else
                for(Player p : Bukkit.getOnlinePlayers())
                    p.sendMessage(translateCustom(message.resolve(p)));
        } else {
            if(message.hasPlaceholders()) {
                sendJson(message, permission != null ? p -> p.hasPermission(permission) : p -> true);
            } else {
                Object packet = MessageNms.jsonPacket(message.resolve(null));
                sendPacket(packet, permission != null ? p -> p.hasPermission(permission) : p -> true);
            }
        }
    }

    private void sendPacket(Object packet, Predicate<Player> selector) {
        for(Player p : Bukkit.getOnlinePlayers())
            if(selector.test(p))
                PlayerNms.sendPacket(p, packet);
    }

    private void sendJson(PlaceholderValue<String> json, Predicate<Player> selector) {
        for(Player p : Bukkit.getOnlinePlayers())
            if(selector.test(p))
                p.sendMessage(translateCustom(json.resolve(p)));
    }


    public static class BroadcastActionType extends BaseActionType<BroadcastAction> {

        public BroadcastActionType() {
            super("broadcast");
            setParameters(
                    Parameter.of("message", Parser.strValue(), true),
                    Parameter.of("permission", Parser.strValue(), false),
                    Parameter.of("raw", Parser.boolValue(), false, false)
            );
        }

        @Override
        public BroadcastAction create(Plugin plugin, Map<String, Object> pars) {
            return new BroadcastAction(
                    plugin,
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
