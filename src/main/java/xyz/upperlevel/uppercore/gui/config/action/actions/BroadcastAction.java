package xyz.upperlevel.uppercore.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Bukkit;
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
                Bukkit.broadcast(message.get(player), permission);
            else
                Bukkit.broadcastMessage(message.get(player));
        } else {
            Object packet = Nms.jsonPacket(message.get(player));
            if(permission != null) {
                for(Player p : Bukkit.getOnlinePlayers())
                    if(p.hasPermission(permission))
                        Nms.sendPacket(p, packet);
            } else {
                for(Player p : Bukkit.getOnlinePlayers())
                        Nms.sendPacket(p, packet);
            }
        }
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
