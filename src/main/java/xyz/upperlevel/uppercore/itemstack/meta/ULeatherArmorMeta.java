package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class ULeatherArmorMeta extends UItemMeta<ULeatherArmorMeta, LeatherArmorMeta> {
    private PlaceholderValue<Color> color;

    @ConfigConstructor
    public ULeatherArmorMeta(
            @ConfigProperty(value = "display-name", optional = true) String displayName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "color", optional = true) PlaceholderValue<Color> color
    ) {
        super(displayName, lore, flags, enchantments);
        this.color = color;
    }

    @Override
    public void resolve(LeatherArmorMeta meta, Player player) {
        super.resolve(meta, player);
        if (color != null) {
            meta.setColor(color.resolve(player));
        }
    }
}
