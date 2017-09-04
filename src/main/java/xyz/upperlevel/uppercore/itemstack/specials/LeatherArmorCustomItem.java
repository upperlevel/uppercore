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
import org.bukkit.inventory.meta.LeatherArmorMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

public class LeatherArmorCustomItem extends CustomItem {

    private PlaceholderValue<Color> color;

    public LeatherArmorCustomItem(Material material, Config config, PlaceholderRegistry placeholders) {
        super(material, config, placeholders);
        this.color = PlaceholderValue.colorValue(config.getString("color"));
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        LeatherArmorMeta meta = (LeatherArmorMeta) m;
        if(color != null)
            meta.setColor(color.resolve(player));
    }
}
