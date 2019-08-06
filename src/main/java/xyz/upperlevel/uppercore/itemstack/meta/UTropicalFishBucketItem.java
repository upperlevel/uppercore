package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.itemstack.UItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class UTropicalFishBucketItem extends UItem {
    private PlaceholderValue<DyeColor> bodyColor;
    private PlaceholderValue<TropicalFish.Pattern> pattern;
    private PlaceholderValue<DyeColor> patternColor;

    @ConfigConstructor
    public UTropicalFishBucketItem(
            @ConfigProperty("type") Material type,
            @ConfigProperty(value = "data", optional = true) PlaceholderValue<Short> data,
            @ConfigProperty(value = "amount", optional = true) PlaceholderValue<Integer> amount,
            @ConfigProperty(value = "name", optional = true) String rawName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "body-color", optional = true) PlaceholderValue<DyeColor> bodyColor,
            @ConfigProperty(value = "pattern", optional = true) PlaceholderValue<TropicalFish.Pattern> pattern,
            @ConfigProperty(value = "pattern-color", optional = true) PlaceholderValue<DyeColor> patternColor
    ) {
        super(type, data, amount, rawName, lore, flags, enchantments);
        this.bodyColor = bodyColor;
        this.pattern = pattern;
        this.patternColor = patternColor;
    }


    @Override
    public void processMeta(Player player, ItemMeta meta) {
        super.processMeta(player, meta);
        TropicalFishBucketMeta bucket = (TropicalFishBucketMeta) meta;
        bucket.setBodyColor(bodyColor.resolve(player, getPlaceholders()));
        bucket.setPattern(pattern.resolve(player, getPlaceholders()));
        bucket.setPatternColor(patternColor.resolve(player, getPlaceholders()));
    }
}
