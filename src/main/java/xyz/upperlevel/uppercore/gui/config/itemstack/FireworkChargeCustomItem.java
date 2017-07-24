package xyz.upperlevel.uppercore.gui.config.itemstack;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigUtils;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.*;
import java.util.stream.Collectors;

import static xyz.upperlevel.uppercore.config.ConfigUtils.parseFireworkEffectType;


public class FireworkChargeCustomItem extends CustomItem {
    private FireworkEffect effect;

    public FireworkChargeCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                    PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                                    List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                    PlaceholderRegistry placeholders,
                                    FireworkEffect effect) {
        super(material, data, amount, displayName, lore, flags, enchantments, placeholders);
        this.effect = effect;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        FireworkEffectMeta meta = (FireworkEffectMeta) m;
        meta.setEffect(effect);
    }

    public static FireworkChargeCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                                PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                                List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                                PlaceholderRegistry placeholders, Config config) {
        FireworkEffect effect = parse(config.getConfigRequired("effect"));
        return new FireworkChargeCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments, placeholders,
                effect
        );
    }

    @SuppressWarnings("unchecked")
    public static FireworkEffect parse(Config config) {
        boolean flicker = config.getBool("flicker", false);
        boolean trail = config.getBool("trail", false);
        List<Color> colors = ((Collection<String>)config.getCollection("colors", Collections.emptyList()))
                .stream()
                .map(ConfigUtils::parseColor)
                .collect(Collectors.toList());
        List<Color> fadeColors = ((Collection<String>)config.getCollection("fade-colors", Collections.emptyList()))
                .stream()
                .map(ConfigUtils::parseColor)
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
