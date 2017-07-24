package xyz.upperlevel.uppercore.gui.config.itemstack;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class SpawnEggCustomItem extends CustomItem {

    private EntityType type;

    public SpawnEggCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                              PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                              List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                              PlaceholderRegistry placeholders,
                              EntityType type) {
        super(material, data, amount, displayName, lore, flags, enchantments, placeholders);
        this.type = type;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {//Add support for older versions
        super.processMeta(player, m);
        SpawnEggMeta meta = (SpawnEggMeta) m;
        meta.setSpawnedType(type);
    }

    public static SpawnEggCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                          PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                          List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                          PlaceholderRegistry placeholders, Config config) {
        return new SpawnEggCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments, placeholders,
                config.getEnum("egg-type", EntityType.class)
        );
    }
}
