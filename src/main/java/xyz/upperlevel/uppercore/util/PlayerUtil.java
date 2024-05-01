package xyz.upperlevel.uppercore.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.stream.Collectors;

public final class PlayerUtil {

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
        return loc.getWorld().getNearbyEntities(loc, radius, radius, radius);
    }

    public static Collection<Player> getPlayersAround(Location loc, double radius) {
        return getEntitiesAround(loc, radius)
                .stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .collect(Collectors.toList());
    }
}
