package xyz.upperlevel.uppercore.itemstack.specials;

import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FireworkCustomItem extends CustomItem {

    private List<FireworkEffect> effects;
    private PlaceholderValue<Integer> power;

    @SuppressWarnings("unchecked")
    public FireworkCustomItem(Material material, Config config, PlaceholderRegistry placeholders) {
        super(material, config, placeholders);
        effects = ((Collection<Map<String, Object>>) config.getCollection("effects", Collections.emptyList()))
                .stream()
                .map(c -> FireworkChargeCustomItem.parse(Config.wrap(c)))
                .collect(Collectors.toList());
        String rawPower = config.getString("power");
        power = rawPower == null ? null : PlaceholderUtil.parseInt(rawPower);
    }

    @ConfigConstructor
    public FireworkCustomItem(
            @ConfigProperty("type") Material type,
            @ConfigProperty(value = "data", optional = true) PlaceholderValue<Short> data,
            @ConfigProperty(value = "amount", optional = true) PlaceholderValue<Integer> amount,
            @ConfigProperty(value = "value", optional = true) String rawName,
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
