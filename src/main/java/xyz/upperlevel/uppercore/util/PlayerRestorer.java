package xyz.upperlevel.uppercore.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class PlayerRestorer {
    private final Map<Player, Image> bubbleByPlayer = new HashMap<>();

    public Image screen(Player player) {
        return new Image(player);
    }

    public void remember(Image image) {
        bubbleByPlayer.put(image.player, image);
    }

    public void restore(Player player) {
        Image image = bubbleByPlayer.get(player);
        if (image != null) {
            image.apply(player);
        }
    }

    public static class Image {
        private final Player player;

        private final double health;

        private final int food;
        private final float saturation;

        private final int level;
        private final float exp;

        private final ItemStack[] armor;
        private final ItemStack[] inventory;

        private final Scoreboard scoreboard;

        private final Location compassTarget;

        private Image(Player player) {
            this.player = player;

            health = player.getHealth();

            food = player.getFoodLevel();
            saturation = player.getSaturation();

            level = player.getLevel();
            exp = player.getExp();

            scoreboard = player.getScoreboard();

            compassTarget = player.getCompassTarget();

            armor = player.getInventory().getArmorContents();
            inventory = player.getInventory().getContents();
        }

        public void apply(Player p) {
            if (!player.equals(p)) {
                throw new IllegalStateException("Applying backup to a different player");
            }
            p.setHealth(health);

            p.setFoodLevel(food);
            p.setSaturation(saturation);

            p.setLevel(level);
            p.setExp(exp);

            player.setScoreboard(scoreboard);

            player.setCompassTarget(compassTarget);

            PlayerInventory inv = p.getInventory();
            inv.setArmorContents(armor);
            inv.setContents(inventory);
            p.updateInventory();
        }
    }
}
