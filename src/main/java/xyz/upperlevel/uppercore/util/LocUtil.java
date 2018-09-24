package xyz.upperlevel.uppercore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.upperlevel.uppercore.config.Config;

import java.util.HashMap;
import java.util.Map;

public final class LocUtil {

    public static String format(Location loc, boolean world) {
        return (world ? (loc.getWorld().getName() + ":") : "") + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    public static Map<String, Object> serialize(Location location) {
        Map<String, Object> data = new HashMap<>();
        data.put("world", location.getWorld().getName());
        data.put("x", location.getX());
        data.put("y", location.getY());
        data.put("z", location.getZ());
        data.put("yaw", location.getYaw());
        data.put("pitch", location.getPitch());
        return data;
    }

    public static Location deserialize(Config section) {
        return new Location(
                Bukkit.getWorld(section.getString("world")),// TODO: check World not found exception and write it in the Javadoc
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                section.getFloat("yaw"),
                section.getFloat("pitch")
        );
    }

    private LocUtil() {}
}
