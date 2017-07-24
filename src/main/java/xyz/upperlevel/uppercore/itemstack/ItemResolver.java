package xyz.upperlevel.uppercore.itemstack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemResolver {

    ItemStack resolve(Player player);
}
