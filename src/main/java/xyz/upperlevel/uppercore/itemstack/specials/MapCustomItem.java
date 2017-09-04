package xyz.upperlevel.uppercore.itemstack.specials;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

public class MapCustomItem extends CustomItem {

    private boolean scaling;
    private PlaceholderValue<String> displayLocName;
    private PlaceholderValue<Color> displayMapColor;

    public MapCustomItem(Material material, Config config, PlaceholderRegistry placeholders) {
        super(material, config, placeholders);
        scaling = config.getBool("scaling", false);
        displayLocName = PlaceholderValue.stringValue(config.getString("location-name"));
        displayMapColor = PlaceholderValue.colorValue(config.getString("map-color"));
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
