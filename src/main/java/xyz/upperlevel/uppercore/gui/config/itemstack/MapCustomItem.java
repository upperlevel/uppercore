package xyz.upperlevel.uppercore.gui.config.itemstack;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class MapCustomItem extends CustomItem {

    private boolean scaling;
    private PlaceholderValue<String> displayLocName;
    private PlaceholderValue<Color> displayMapColor;

    public MapCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                         PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                         List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                         boolean scaling, PlaceholderValue<String> displayLocName,
                         PlaceholderValue<Color> displayMapColor) {
        super(material, data, amount, displayName, lore, flags, enchantments);
        this.scaling = scaling;
        this.displayLocName = displayLocName;
        this.displayMapColor = displayMapColor;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        MapMeta meta = (MapMeta) m;
        meta.setScaling(scaling);
        if(displayLocName != null)
            meta.setLocationName(displayLocName.get(player));
        if(displayMapColor != null)
            meta.setColor(displayMapColor.get(player));
    }

    public static MapCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                              PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                              List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                              Config config) {
        boolean scaling = config.getBool("scaling", false);
        PlaceholderValue<String> displayLocName = PlaceholderValue.stringValue(config.getString("location-name"));
        PlaceholderValue<Color> displayMapColor = PlaceholderValue.colorValue(config.getString("map-color"));
        return new MapCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments,
                scaling, displayLocName, displayMapColor
        );
    }
}
