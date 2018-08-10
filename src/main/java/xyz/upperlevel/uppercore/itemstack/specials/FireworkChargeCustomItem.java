package xyz.upperlevel.uppercore.itemstack.specials;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.ConfigUtil;
import xyz.upperlevel.uppercore.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static xyz.upperlevel.uppercore.config.ConfigUtil.parseFireworkEffectType;


public class FireworkChargeCustomItem extends CustomItem {
    private FireworkEffect effect;

    public FireworkChargeCustomItem(Material material, Config config, PlaceholderRegistry placeholders) {
        super(material, config, placeholders);
        effect = parse(config.getConfigRequired("effect"));
    }

    @ConfigConstructor
    public FireworkChargeCustomItem(
            @ConfigProperty("type") Material type,
            @ConfigProperty(value = "data", optional = true) PlaceholderValue<Short> data,
            @ConfigProperty(value = "amount", optional = true) PlaceholderValue<Integer> amount,
            @ConfigProperty(value = "value", optional = true) String rawName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "effect", optional = true) FireworkEffect effect
    ) {
        super(type, data, amount, rawName, lore, flags, enchantments);
        this.effect = effect;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        FireworkEffectMeta meta = (FireworkEffectMeta) m;
        meta.setEffect(effect);
    }

    @SuppressWarnings("unchecked")
    public static FireworkEffect parse(Config config) {
        boolean flicker = config.getBool("flicker", false);
        boolean trail = config.getBool("trail", false);
        List<Color> colors = ((Collection<String>) config.getCollection("colors", Collections.emptyList()))
                .stream()
                .map(ConfigUtil::parseColor)
                .collect(Collectors.toList());
        List<Color> fadeColors = ((Collection<String>) config.getCollection("fade-colors", Collections.emptyList()))
                .stream()
                .map(ConfigUtil::parseColor)
                .collect(Collectors.toList());
        FireworkEffect.Type type = parseFireworkEffectType(config.getString("type", FireworkEffect.Type.BALL.name()));
        return FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .withColor(colors)
                .withFade(fadeColors)
                .with(type)
                .build();
    }
}
