package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.itemstack.UItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class USkullItem extends UItem {
    private PlaceholderValue<String> skullOwner;

    @ConfigConstructor
    public USkullItem(
            @ConfigProperty("type") Material type,
            @ConfigProperty(value = "data", optional = true) PlaceholderValue<Short> data,
            @ConfigProperty(value = "amount", optional = true) PlaceholderValue<Integer> amount,
            @ConfigProperty(value = "name", optional = true) String rawName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "owner", optional = true) PlaceholderValue<String> owner
    ) {
        super(type, data, amount, rawName, lore, flags, enchantments);
        this.skullOwner = owner;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        SkullMeta meta = (SkullMeta) m;
        if (meta.hasOwner() && skullOwner != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(skullOwner.resolve(player, getPlaceholders())));
        }
    }
}
