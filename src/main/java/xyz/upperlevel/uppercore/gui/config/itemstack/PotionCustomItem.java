package xyz.upperlevel.uppercore.gui.config.itemstack;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.*;

public class PotionCustomItem extends CustomItem {

    private PotionType type;
    private PlaceholderValue<Color> customColor;
    private List<PotionEffect> customEffects;

    public PotionCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                            PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                            List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                            PlaceholderRegistry placeholders,
                            PotionType type, PlaceholderValue<Color> customColor, List<PotionEffect> customEffects) {
        super(material, data, amount, displayName, lore, flags, enchantments, placeholders);
        this.type = type;
        this.customColor = customColor;
        this.customEffects = customEffects;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        PotionMeta meta = (PotionMeta) m;
        if(type != null)
            meta.setBasePotionData(new PotionData(type));
        if(customColor != null)
            meta.setColor(customColor.resolve(player));
        meta.clearCustomEffects();
        for(PotionEffect e : customEffects)
            meta.addCustomEffect(e, true);
    }

    @SuppressWarnings("unchecked")
    public static PotionCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                        PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                        List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                        PlaceholderRegistry placeholders, Config config) {
        PotionType type = config.getEnum("potion-type", PotionType.class);
        String rawColor = config.getString("color");
        PlaceholderValue<Color> customColor = rawColor == null ? null : PlaceholderValue.colorValue(rawColor);
        List<PotionEffect> customEffects = new ArrayList<>();
        Collection<Map<String, Object>> rawEffects = (Collection<Map<String, Object>>) config.getCollection("effects");
        if(rawEffects != null) {
            for (Map<String, Object> e : rawEffects)
                customEffects.add(new PotionEffect(e));
        }
        return new PotionCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments, placeholders,
                type, customColor, customEffects
        );
    }
}
