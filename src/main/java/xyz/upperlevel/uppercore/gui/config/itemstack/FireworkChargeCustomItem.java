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
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static xyz.upperlevel.uppercore.config.ConfigUtils.parseFireworkEffectType;


public class FireworkChargeCustomItem extends CustomItem {
    private FireworkEffect effect;

    public FireworkChargeCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                    PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                                    List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                    FireworkEffect effect) {
        super(material, data, amount, displayName, lore, flags, enchantments);
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
                                              Config config) {
        FireworkEffect effect = parse(config.getConfigRequired("effect"));
        return new FireworkChargeCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments,
                effect
        );
    }

    @SuppressWarnings("unchecked")
    public static FireworkEffect parse(Config config) {
        boolean flicker = config.getBool("flicker", false);
        boolean trail = config.getBool("trail", false);
        List<Color> colors = ((Collection<String>)config.getCollectionRequired("colors"))
                .stream()
                .map(ConfigUtils::parseColor)
                .collect(Collectors.toList());
        List<Color> fadeColors = ((Collection<String>)config.getCollectionRequired("fade-colors"))
                .stream()
                .map(ConfigUtils::parseColor)
                .collect(Collectors.toList());
        FireworkEffect.Type type = parseFireworkEffectType(config.getStringRequired("type"));
        return FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .withColor(colors)
                .withFade(fadeColors)
                .with(type)
                .build();
    }
}
