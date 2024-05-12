package xyz.upperlevel.uppercore.util;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class EnchantUtil {
    private EnchantUtil() {
    }

    public static ItemStack glow(ItemStack item) {// else: https://bukkit.org/threads/how-to-make-an-item-glow-with-no-enchant.374594/
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        }
        if (meta != null) {
            item.setItemMeta(glow(meta));
        }
        return item;
    }

    public static ItemMeta glow(ItemMeta meta) {
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return meta;
    }
}