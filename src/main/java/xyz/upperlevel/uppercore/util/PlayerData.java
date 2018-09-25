package xyz.upperlevel.uppercore.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerData {
    private ItemStack[] inventoryContent;
    private Scoreboard scoreboard;
    private int totalExperience;
    private double health;
    private int foodLevel;

    public PlayerData(
            ItemStack[] inventoryContent,
            Scoreboard scoreboard,
            int totalExperience,
            double health,
            int foodLevel
    ) {
        this.inventoryContent = inventoryContent;
        this.scoreboard = scoreboard;
        this.totalExperience = totalExperience;
        this.health = health;
        this.foodLevel = foodLevel;
    }

    public void apply(Player player) {
        Inventory inv = player.getInventory();
        inv.clear();
        inv.setContents(inventoryContent);

        player.setScoreboard(scoreboard);
        player.setTotalExperience(totalExperience);
        player.setHealth(health);
        player.setFoodLevel(foodLevel);
    }

    public static PlayerData extract(Player player) {
        return new PlayerData(
                player.getInventory().getContents(),
                player.getScoreboard(),
                player.getTotalExperience(),
                player.getHealth(),
                player.getFoodLevel()
        );
    }
}
