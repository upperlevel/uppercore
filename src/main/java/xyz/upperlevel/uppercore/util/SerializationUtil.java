package xyz.upperlevel.uppercore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public final class SerializationUtil {

    private SerializationUtil() {
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

    public static Location deserializeLocation(Config section) {
        return new Location(
                Bukkit.getWorld(section.getString("world")),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                section.getFloat("yaw"),
                section.getFloat("pitch")
        );
    }
}
