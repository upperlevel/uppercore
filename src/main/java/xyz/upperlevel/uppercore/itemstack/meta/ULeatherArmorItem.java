package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.itemstack.UItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class ULeatherArmorItem extends UItem {
    private PlaceholderValue<Color> color;

    @ConfigConstructor
    public ULeatherArmorItem(
            @ConfigProperty("type") Material type,
            @ConfigProperty(value = "data", optional = true) PlaceholderValue<Short> data,
            @ConfigProperty(value = "amount", optional = true) PlaceholderValue<Integer> amount,
            @ConfigProperty(value = "name", optional = true) String rawName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "color", optional = true) PlaceholderValue<Color> color
    ) {
        super(type, data, amount, rawName, lore, flags, enchantments);
        this.color = color;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        LeatherArmorMeta meta = (LeatherArmorMeta) m;
        if (color != null) {
            meta.setColor(color.resolve(player));
        }
    }
}
