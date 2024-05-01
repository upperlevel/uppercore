package xyz.upperlevel.uppercore.gui;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import xyz.upperlevel.uppercore.sound.SoundUtil;

import java.util.Arrays;

import static org.bukkit.ChatColor.RED;

public final class GuiUtil {
    private static Sound DEF_ERROR_SOUND;//Yeah, it's a lazy initialization with a constant field

    @SuppressWarnings("deprecation")
    public static ItemStack stainedClay(DyeColor color, String name, String... lores) {
        return setNameAndLores(new ItemStack(Material.GLASS_PANE, 1, color.getWoolData()), name, lores);
    }

    public static ItemStack itemStack(Material display, String name, String... lores) {
        return setNameAndLores(new ItemStack(display, 1), name, lores);
    }

    public static ItemStack potion(PotionType type, boolean extended, boolean upgraded, String name, String... lores) {
        ItemStack potion = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        applyPotionEffects(meta, type, extended, upgraded, name, lores);
        potion.setItemMeta(meta);
        return potion;
    }

    private static void applyPotionEffects(PotionMeta meta, PotionType type, boolean extended, boolean upgraded, String name, String... lores) {
        meta.setBasePotionType(type);
        if (extended || upgraded) {
            PotionEffect orig = type.getPotionEffects().get(0);
            int duration = orig.getDuration() * (extended ? 2 : 1);
            int amplifier = orig.getAmplifier() * (upgraded ? 2 : 1);
            meta.addCustomEffect(new PotionEffect(orig.getType(), duration, amplifier), true);
        }
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lores));
    }

    public static ItemStack splashPotion(PotionType type, boolean extended, boolean upgraded, String name, String... lores) {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        applyPotionEffects(meta, type, extended, upgraded, name, lores);
        potion.setItemMeta(meta);
        return potion;
    }
    
    @SuppressWarnings("deprecation")
    public static ItemStack head(String playerName, String displayName, String... lore) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(playerName);
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setNameAndLores(ItemStack item, String name, String... lores) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lores));
        item.setItemMeta(meta);
        return item;
    }

    public static void sendErrorMessage(Player player, String message) {
        if (DEF_ERROR_SOUND == null)
            DEF_ERROR_SOUND = Sound.BLOCK_ANVIL_USE;
        sendErrorMessage(player, message, DEF_ERROR_SOUND);
    }

    public static void sendErrorMessage(Player player, String message, Sound sound) {
        player.sendMessage(RED + message);
        player.playSound(player.getLocation(), sound, 1, 1);
    }

    private GuiUtil() {}
}
