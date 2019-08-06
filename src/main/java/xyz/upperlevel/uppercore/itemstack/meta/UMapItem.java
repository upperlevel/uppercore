package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.itemstack.UItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class UMapItem extends UItem {
    private boolean scaling;
    private PlaceholderValue<String> displayLocName;
    private PlaceholderValue<Color> displayMapColor;

    @ConfigConstructor
    public UMapItem(
            @ConfigProperty("type") Material type,
            @ConfigProperty(value = "data", optional = true) PlaceholderValue<Short> data,
            @ConfigProperty(value = "amount", optional = true) PlaceholderValue<Integer> amount,
            @ConfigProperty(value = "name", optional = true) String rawName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "scaling", optional = true) Boolean scaling,
            @ConfigProperty(value = "location-name") PlaceholderValue<String> displayLocName,
            @ConfigProperty(value = "map-color") PlaceholderValue<Color> displayMapColor
    ) {
        super(type, data, amount, rawName, lore, flags, enchantments);
        this.scaling = scaling != null ? scaling : false;
        this.displayLocName = displayLocName;
        this.displayMapColor = displayMapColor;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        MapMeta meta = (MapMeta) m;
        meta.setScaling(scaling);
        if(displayLocName != null)
            meta.setLocationName(displayLocName.resolve(player));
        if(displayMapColor != null)
            meta.setColor(displayMapColor.resolve(player));
    }
}
