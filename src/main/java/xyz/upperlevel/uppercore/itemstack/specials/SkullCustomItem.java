package xyz.upperlevel.uppercore.itemstack.specials;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

public class SkullCustomItem extends CustomItem {

    private PlaceholderValue<String> skullOwner;

    public SkullCustomItem(Material material, Config config, PlaceholderRegistry placeholders) {
        super(material, config, placeholders);
        this.skullOwner = PlaceholderValue.stringValue(config.getString("owner"));
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        SkullMeta meta = (SkullMeta) m;
        if(skullOwner != null)
            meta.setOwner(skullOwner.resolve(player, getPlaceholders()));
    }
}
