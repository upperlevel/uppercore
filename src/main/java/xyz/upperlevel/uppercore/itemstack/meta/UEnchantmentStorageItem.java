package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.itemstack.UItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.*;

public class UEnchantmentStorageItem extends UItem {
    private Map<Enchantment, PlaceholderValue<Integer>> storedEnchantments;

    @ConfigConstructor
    public UEnchantmentStorageItem(
            @ConfigProperty("type") Material type,
            @ConfigProperty(value = "data", optional = true) PlaceholderValue<Short> data,
            @ConfigProperty(value = "amount", optional = true) PlaceholderValue<Integer> amount,
            @ConfigProperty(value = "name", optional = true) String rawName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "stored-enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> storedEnchantments
    ) {
        super(type, data, amount, rawName, lore, flags, enchantments);
        this.storedEnchantments = storedEnchantments != null ? storedEnchantments : Collections.emptyMap();
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) m;
        for (Map.Entry<Enchantment, PlaceholderValue<Integer>> ench : storedEnchantments.entrySet()) {
            meta.addStoredEnchant(ench.getKey(), ench.getValue().resolve(player), true);
        }
    }
}
