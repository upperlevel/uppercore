package xyz.upperlevel.uppercore.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInventoryBackup {
    private ItemStack[] items;
    private ItemStack[] armorItems;

    public PlayerInventoryBackup(ItemStack[] items, ItemStack[] armorItems) {
        this.items = items;
        this.armorItems = armorItems;
    }

    public PlayerInventoryBackup(PlayerInventory inv) {
        this.items = inv.getContents();
        this.armorItems = inv.getArmorContents();
    }

    public PlayerInventoryBackup(Player player) {
        this(player.getInventory());
    }

    public void restore(Player p) {
        PlayerInventory inv = p.getInventory();
        inv.setContents(items);
        inv.setArmorContents(armorItems);
        p.updateInventory();
    }

    public static PlayerInventoryBackup create(Player player) {
        return new PlayerInventoryBackup(player);
    }
}
