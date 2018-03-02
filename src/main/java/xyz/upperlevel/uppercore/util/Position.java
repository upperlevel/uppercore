package xyz.upperlevel.uppercore.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import xyz.upperlevel.uppercore.config.Config;

import java.util.HashMap;
import java.util.Map;

public class Position implements ConfigurationSerializable {
    @Getter
    @Setter
    private double x, y, z;

    @Getter
    @Setter
    private float yaw, pitch;

    public Position() {
    }

    public Position(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Position(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public Block toBlock(World world) {
        return toLocation(world).getBlock();
    }

    public Position copy() {
        return new Position(x, y, z, yaw, pitch);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Position) {
            Position position = (Position) object;
            return position.x == x &&
                    position.y == y &&
                    position.z == z &&
                    position.yaw == yaw &&
                    position.pitch == pitch;
        }
        return super.equals(object);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("x", x);
        data.put("y", y);
        data.put("z", z);
        data.put("yaw", yaw);
        data.put("pitch", pitch);
        return data;
    }

    public static Position deserialize(Map<String, Object> data) {
        Config cfg = Config.wrap(data);
        Position r = new Position();
        r.x = cfg.getDouble("x");
        r.y = cfg.getDouble("y");
        r.z = cfg.getDouble("z");
        r.yaw = cfg.getFloat("yaw");
        r.pitch = cfg.getFloat("pitch");
        return r;
    }
}
