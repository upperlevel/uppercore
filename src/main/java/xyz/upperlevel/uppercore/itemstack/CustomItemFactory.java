package xyz.upperlevel.uppercore.itemstack;

import org.bukkit.Material;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

public interface CustomItemFactory {
    UItem create(Material material, Config config, PlaceholderRegistry placeholders);
}
