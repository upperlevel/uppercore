package xyz.upperlevel.uppercore.util;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class EnchantUtil {
    private EnchantUtil() {
    }

    public static ItemStack glow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        }
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }
}