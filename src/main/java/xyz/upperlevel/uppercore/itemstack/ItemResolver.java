package xyz.upperlevel.uppercore.itemstack;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemResolver {

    ItemStack resolve(Player player);
}
