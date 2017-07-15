package xyz.upperlevel.uppercore.gui.config.itemstack;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.config.placeholders.PlaceholderValue;
import xyz.upperlevel.uppercore.gui.config.util.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EnchantedBookCustomItem extends CustomItem {
    private Map<Enchantment, PlaceholderValue<Integer>> storedEnchantments;

    public EnchantedBookCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                   PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                                   List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                   Map<Enchantment, PlaceholderValue<Integer>> storedEnchantments) {
        super(material, data, amount, displayName, lore, flags, enchantments);
        this.storedEnchantments = storedEnchantments;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) m;
        for(Map.Entry<Enchantment, PlaceholderValue<Integer>> ench : storedEnchantments.entrySet())
            meta.addStoredEnchant(ench.getKey(), ench.getValue().get(player), true);
    }

    @SuppressWarnings("unchecked")
    public static EnchantedBookCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                              PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                              List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                              Config config) {
        Map<Enchantment, PlaceholderValue<Integer>> storedEnchantments;

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
        return new EnchantedBookCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments,
                storedEnchantments
        );
    }
}
