package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.itemstack.UItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class UFireworkItem extends UItem {
    private List<FireworkEffect> effects;
    private PlaceholderValue<Integer> power;

    @ConfigConstructor
    public UFireworkItem(
            @ConfigProperty("type") Material type,
            @ConfigProperty(value = "data", optional = true) PlaceholderValue<Short> data,
            @ConfigProperty(value = "amount", optional = true) PlaceholderValue<Integer> amount,
            @ConfigProperty(value = "name", optional = true) String rawName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "effects", optional = true) List<FireworkEffect> effects,
            @ConfigProperty(value = "power", optional = true) PlaceholderValue<Integer> power
    ) {
        super(type, data, amount, rawName, lore, flags, enchantments);
        this.effects = effects;
        this.power = power;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        FireworkMeta meta = (FireworkMeta) m;
        meta.clearEffects();
        meta.addEffects(effects);
        if (power != null) {
            meta.setPower(power.resolve(player));
        }
    }
}
