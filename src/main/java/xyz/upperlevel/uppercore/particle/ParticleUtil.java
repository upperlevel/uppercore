package xyz.upperlevel.uppercore.particle;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;

import java.util.Arrays;
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
        private byte materialData = 0;

        private double range = -1;

        private List<Player> targetPlayers;

        public Builder offset(float x, float y, float z) {
            offsetX = x;
            offsetY = y;
            offsetZ = z;
            return this;
        }

        public Builder material(Material type, byte data) {
            this.material = type;
            this.materialData = data;
            return this;
        }

        private void validate() {
            Objects.requireNonNull(type);
            Objects.requireNonNull(center);
        }

        public void display() {
            validate();
            Uppercore.effects().display(type, center, offsetX, offsetY, offsetZ, speed, amount, size, color, material, materialData, range, targetPlayers);
        }

        public void display(List<Player> target) {
            targetPlayers(target).display();
        }

        public void display(int range) {
            range(range).display();
        }
    }
}
