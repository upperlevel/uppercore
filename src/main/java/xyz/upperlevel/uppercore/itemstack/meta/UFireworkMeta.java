package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.FireworkEffect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.FireworkMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.*;

public class UFireworkMeta extends UItemMeta<UFireworkMeta, FireworkMeta> {
    private List<FireworkEffect> effects;
    private PlaceholderValue<Integer> power = null;

    public UFireworkMeta() {
    }

    @ConfigConstructor
    public UFireworkMeta(
            @ConfigProperty(value = "display-name", optional = true) String displayName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "effects", optional = true) List<FireworkEffect> effects,
            @ConfigProperty(value = "power", optional = true) PlaceholderValue<Integer> power
    ) {
        super(displayName, lore, flags, enchantments);
        this.effects = effects;
        this.power = power;
    }

    public UFireworkMeta setEffects(List<FireworkEffect> effects) {
        this.effects = effects;
        return this;
    }

    public UFireworkMeta setPower(int power) {
        this.power = PlaceholderValue.fake(power);
        return this;
    }

    @Override
    public void resolve(FireworkMeta meta, Player player) {
        super.resolve(meta, player);
        if (effects != null) {
            meta.clearEffects();
            meta.addEffects(effects);
        }
        meta.setPower(power.resolve(player));
    }
}
