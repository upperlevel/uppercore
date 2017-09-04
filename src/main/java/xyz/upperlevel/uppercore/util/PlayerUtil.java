package xyz.upperlevel.uppercore.util;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.uppercore.util.nms.impl.entity.BoundingBoxNms;
import xyz.upperlevel.uppercore.util.nms.impl.entity.EntityNms;

import java.util.Collection;
import java.util.function.Consumer;

import static xyz.upperlevel.uppercore.util.nms.NmsVersion.MINOR;
import static xyz.upperlevel.uppercore.util.nms.NmsVersion.RELEASE;

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

    public static void forEveryPlayerAround(Player viewer, Location loc, double radius, Consumer<Player> callback) {
        if(USE_NEW_PLAYER_FINDER)
            forEveryPlayerAroundNew(viewer, loc, radius, callback);
        else
            forEveryPlayerAroundManual(viewer, loc, radius, callback);
    }

    private static void forEveryPlayerAroundNew(Player viewer, Location loc, double radius, Consumer<Player> callback) {
        Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, radius, radius, radius);
        for (Entity e : entities)
            if (viewer != e && e instanceof Player)
                callback.accept((Player) e);
    }

    private static void forEveryPlayerAroundManual(Player viewer, Location loc, double radius, Consumer<Player> callback) {
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
                    for (Entity t : world.getChunkAt(chX, chZ).getEntities()) {
                        if (t instanceof Player && viewer != t) {
                            if (BoundingBoxNms.intersect(bb, EntityNms.getBoundingBox(t))) {
                                callback.accept((Player) t);
                            }
                        }
                    }
                }
            }
        }
    }
}
