package xyz.upperlevel.uppercore.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.uppercore.nms.impl.entity.BoundingBoxNms;
import xyz.upperlevel.uppercore.nms.impl.entity.EntityNms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xyz.upperlevel.uppercore.nms.NmsVersion.MINOR;
import static xyz.upperlevel.uppercore.nms.NmsVersion.RELEASE;

public final class PlayerUtil {
    private static final boolean USE_NEW_PLAYER_FINDER = MINOR > 8 || (MINOR == 8 && RELEASE >= 2);

    private PlayerUtil() {
    }

    public static void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.saveData();
    }

    public static void restore(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
    }

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 0f, 100f);
    }

    public static Collection<Entity> getEntitiesAround(Location loc, double radius) {
        if (USE_NEW_PLAYER_FINDER) {
            return getEntitiesAroundNew(loc, radius);
        } else {
            return getEntitiesAroundOld(loc, radius);
        }
    }

    private static Collection<Entity> getEntitiesAroundNew(Location loc, double radius) {
        return loc.getWorld().getNearbyEntities(loc, radius, radius, radius);
    }

    private static Collection<Entity> getEntitiesAroundOld(Location loc, double radius) {
        List<Entity> r = new ArrayList<>();

        World world = loc.getWorld();
        double minX = loc.getX() - radius;
        double minY = loc.getY() - radius;
        double minZ = loc.getZ() - radius;
        double maxX = loc.getX() + radius;
        double maxY = loc.getY() + radius;
        double maxZ = loc.getZ() + radius;

        int chMinX = (int) Math.floor(minX - 2.0) >> 4;
        int chMaxX = (int) Math.floor(maxX + 2.0) >> 4;
        int chMinZ = (int) Math.floor(minZ - 2.0) >> 4;
        int chMaxZ = (int) Math.floor(maxZ + 2.0) >> 4;

        Object bb = BoundingBoxNms.toNms(minX, minY, minZ, maxX, maxY, maxZ);

        for (int chX = chMinX; chX <= chMaxX; chX++) {
            for (int chZ = chMinZ; chZ <= chMaxZ; chZ++) {
                if (world.isChunkLoaded(chX, chZ)) {
                    for (Entity e : world.getChunkAt(chX, chZ).getEntities()) {
                        if (BoundingBoxNms.intersect(bb, EntityNms.getBoundingBox(e))) {
                            r.add(e);
                        }
                    }
                }
            }
        }

        return r;
    }

    public static Collection<Player> getPlayersAround(Location loc, double radius) {
        return getEntitiesAround(loc, radius)
                .stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .collect(Collectors.toList());
    }
}
