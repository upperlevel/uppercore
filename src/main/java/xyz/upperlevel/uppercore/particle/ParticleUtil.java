package xyz.upperlevel.uppercore.particle;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class ParticleUtil {

    public static Builder builder() {
        return new Builder();
    }

    @Accessors(chain = true, fluent = true)
    @Setter
    public static class Builder {
        private Particle type;
        private Location center;

        private float offsetX, offsetY, offsetZ;
        private float speed = 1.0f;
        private int amount = 1;

        private float size = 1;

        private Color color = null;

        private Material material = null;

        private double range = -1;

        private List<Player> targetPlayers;

        public Builder offset(float x, float y, float z) {
            offsetX = x;
            offsetY = y;
            offsetZ = z;
            return this;
        }

        public Builder material(Material type) {
            this.material = type;
            return this;
        }

        private void validate() {
            Objects.requireNonNull(type);
            Objects.requireNonNull(center);

            if (range != -1) throw new RuntimeException("Range not implemented yet");
        }

        public void display() {
            validate();

            // Generate data and adjust parameters
            // Credits to effectslib
            Object data = null;
            switch (type) {
                case SPELL_MOB:
                case SPELL_MOB_AMBIENT:
                    if (color == null) break;

                    // Colored particles can't have a speed of 0.
                    if (speed == 0) speed = 1;

                    amount = 0;
                    offsetX = color.getRed() / 255f;
                    offsetY = color.getGreen() / 255f;
                    offsetZ = color.getBlue() / 255f;

                    // The redstone particle reverts to red if R is 0!
                    if (offsetX < Float.MIN_NORMAL) {
                        offsetX = Float.MIN_NORMAL;
                    }

                    break;
                case ITEM_CRACK:
                    if (material == null || material == Material.AIR) return;
                    data = new ItemStack(material);

                    break;
                case BLOCK_CRACK:
                case BLOCK_DUST:
                case FALLING_DUST:
                    if (material == null || material == Material.AIR) return;
                    data = material.createBlockData();
                    if (data == null) return;

                    break;
                case REDSTONE:
                    if (color == null) {
                        color = Color.RED;
                    }
                    data = new Particle.DustOptions(color, size);
                    break;
            }

            for (Player player : targetPlayers) {
                player.spawnParticle(type, center, amount, offsetX, offsetY, offsetZ, speed, data);
            }
        }

        public void display(List<Player> target) {
            targetPlayers(target).display();
        }

        public void display(int range) {
            range(range).display();
        }
    }
}
