package xyz.upperlevel.uppercore.itemstack;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Material;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

public interface CustomItemFactory {
    CustomItem create(Material material, Config config, PlaceholderRegistry placeholders);
}
