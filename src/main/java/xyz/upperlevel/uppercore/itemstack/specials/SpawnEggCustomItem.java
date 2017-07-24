package xyz.upperlevel.uppercore.itemstack.specials;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

public class SpawnEggCustomItem extends CustomItem {

    private EntityType type;

    public SpawnEggCustomItem(Material material, Config config, PlaceholderRegistry placeholders) {
        super(material, config, placeholders);
        this.type = config.getEnum("egg-type", EntityType.class);
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {//Add support for older versions
        super.processMeta(player, m);
        SpawnEggMeta meta = (SpawnEggMeta) m;
        meta.setSpawnedType(type);
    }
}
