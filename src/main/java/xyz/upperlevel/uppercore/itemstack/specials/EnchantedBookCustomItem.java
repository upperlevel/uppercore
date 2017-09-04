package xyz.upperlevel.uppercore.itemstack.specials;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EnchantedBookCustomItem extends CustomItem {

    private Map<Enchantment, PlaceholderValue<Integer>> storedEnchantments;

    public EnchantedBookCustomItem(Material material, Config config, PlaceholderRegistry placeholders) {
        super(material, config, placeholders);
        storedEnchantments = new HashMap<>();

        if (config.has("stored-enchantments")) {
            Map<String, Object> stEnch = config.getSection("stored-enchantments");

            for(Map.Entry<String, Object> e : stEnch.entrySet()) {
                Enchantment ench = Enchantment.getByName(e.getKey().replace(' ', '_').toUpperCase(Locale.ENGLISH));
                if (ench == null)
                    Uppercore.logger().severe("Cannot find enchantment: " + e.getKey());
                else
                    storedEnchantments.put(ench, PlaceholderValue.intValue(e.getValue().toString()));
            }
        }
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) m;
        for(Map.Entry<Enchantment, PlaceholderValue<Integer>> ench : storedEnchantments.entrySet())
            meta.addStoredEnchant(ench.getKey(), ench.getValue().resolve(player), true);
    }
}
