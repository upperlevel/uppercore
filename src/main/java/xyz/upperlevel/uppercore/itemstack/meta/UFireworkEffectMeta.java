package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.FireworkEffect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;


public class UFireworkEffectMeta extends UItemMeta<UFireworkMeta, FireworkEffectMeta> {
    private FireworkEffect effect;

    public UFireworkEffectMeta() {
    }

    @ConfigConstructor
    public UFireworkEffectMeta(
            @ConfigProperty(value = "display-name", optional = true) String displayName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "effect", optional = true) FireworkEffect effect
    ) {
        super(displayName, lore, flags, enchantments);
        this.effect = effect;
    }

    public UFireworkEffectMeta setEffect(FireworkEffect effect) {
        this.effect = effect;
        return this;
    }

    @Override
    public void resolve(FireworkEffectMeta meta, Player player) {
        super.resolve(meta, player);
        meta.setEffect(effect);
    }
}
