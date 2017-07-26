package xyz.upperlevel.uppercore.gui;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import xyz.upperlevel.uppercore.sound.CompatibleSound;

import java.util.Arrays;

import static org.bukkit.ChatColor.RED;

public final class GuiUtil {
    private static Sound DEF_ERROR_SOUND;//Yeah, it's a lazy initialization with a constant field

    public static ItemStack wool(DyeColor color, String name, String... lores) {
        return setNameAndLores(new Wool(color).toItemStack(1), name, lores);
    }

    @SuppressWarnings("deprecation")
    public static ItemStack stainedClay(DyeColor color, String name, String... lores) {
        return setNameAndLores(new ItemStack(Material.STAINED_CLAY, 1, color.getDyeData()), name, lores);
    }

    @SuppressWarnings("deprecation")
    public static ItemStack stainedGlassPane(DyeColor color, String name, String... lores) {
        return setNameAndLores(new ItemStack(Material.STAINED_GLASS_PANE, 1, color.getDyeData()), name, lores);
    }

    @SuppressWarnings("deprecation")
    public static ItemStack stainedGlass(DyeColor color, String name, String... lores) {
        return setNameAndLores(new ItemStack(Material.STAINED_GLASS, 1, color.getDyeData()), name, lores);
    }

    @SuppressWarnings("deprecation")
    public static ItemStack wood(TreeSpecies type, String name, String... lores) {
        return setNameAndLores(new ItemStack(Material.WOOD, 1, type.getData()), name, lores);
    }

    public static ItemStack itemStack(Material display, String name, String... lores) {
        return setNameAndLores(new ItemStack(display, 1), name, lores);
    }

    public static ItemStack potion(PotionType type, boolean extended, boolean upgraded, String name, String... lores) {
        ItemStack potion = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(type, extended, upgraded));
        potion.setItemMeta(meta);
        return potion;
    }

    public static ItemStack splashPotion(PotionType type, boolean extended, boolean upgraded, String name, String... lores) {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(type, extended, upgraded));
        potion.setItemMeta(meta);
        return potion;
    }

    public static ItemStack setNameAndLores(ItemStack item, String name, String... lores) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lores));
        item.setItemMeta(meta);
        return item;
    }

    public static void sendErrorMessage(Player player, String message) {
        if(DEF_ERROR_SOUND == null)
            DEF_ERROR_SOUND = CompatibleSound.getRaw("BLOCK_ANVIL_USE");
        sendErrorMessage(player, message, DEF_ERROR_SOUND);
    }

    public static void sendErrorMessage(Player player, String message, Sound sound) {
        player.sendMessage(RED + message);
        player.playSound(player.getLocation(), sound, 1, 1);
    }

    private GuiUtil() {}
}
