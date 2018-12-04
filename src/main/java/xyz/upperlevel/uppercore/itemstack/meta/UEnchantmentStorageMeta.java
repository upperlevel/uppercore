package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UEnchantmentStorageMeta extends UItemMeta<UEnchantmentStorageMeta, EnchantmentStorageMeta> {
    private Map<Enchantment, PlaceholderValue<Integer>> storedEnchantments = new HashMap<>();

    public UEnchantmentStorageMeta() {
    }

    @ConfigConstructor
    public UEnchantmentStorageMeta(
            @ConfigProperty(value = "display-name", optional = true) String displayName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "stored-enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> storedEnchantments
    ) {
        super(displayName, lore, flags, enchantments);
        this.storedEnchantments = storedEnchantments != null ? storedEnchantments : Collections.emptyMap();
    }

    public UEnchantmentStorageMeta addStoredEnchantment(Enchantment enchantment, int level) {
        storedEnchantments.put(enchantment, PlaceholderValue.fake(level));
        return this;
    }

    @Override
    public void resolve(EnchantmentStorageMeta meta, Player player) {
        super.resolve(meta, player);
        storedEnchantments.forEach((enchantment, level) -> meta.addStoredEnchant(enchantment, level.resolve(player), true));
    }
}
