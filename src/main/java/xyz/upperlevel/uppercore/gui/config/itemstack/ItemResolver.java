package xyz.upperlevel.uppercore.gui.config.itemstack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemResolver {

    ItemStack resolve(Player player);
}
