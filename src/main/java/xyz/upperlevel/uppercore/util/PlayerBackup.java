package xyz.upperlevel.uppercore.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerBackup {
    private ItemStack[] items;
    private ItemStack[] armorItems;
    private float exp;
    private int level;

    public PlayerBackup(ItemStack[] items, ItemStack[] armorItems, int exp) {
        this.items = items;
        this.armorItems = armorItems;
        this.exp = exp;
    }

    public PlayerBackup(PlayerInventory inv, float exp, int level) {
        this.items = inv.getContents();
        this.armorItems = inv.getArmorContents();
        this.exp = exp;
        this.level = level;
    }

    public PlayerBackup(Player player) {
        this(player.getInventory(), player.getExp(), player.getLevel());
    }

    public void restore(Player p) {
        PlayerInventory inv = p.getInventory();
        inv.setContents(items);
        inv.setArmorContents(armorItems);
        p.setLevel(level);
        p.setExp(exp);
        p.updateInventory();
    }

    public static PlayerBackup create(Player player) {
        return new PlayerBackup(player);
    }
}
