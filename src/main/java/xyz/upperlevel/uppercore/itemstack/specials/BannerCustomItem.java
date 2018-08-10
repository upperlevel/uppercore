package xyz.upperlevel.uppercore.itemstack.specials;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.*;

public class BannerCustomItem extends CustomItem {
    private DyeColor baseColor;
    private List<Pattern> patterns;

    @SuppressWarnings("unchecked")
    public BannerCustomItem(Material material, Config config, PlaceholderRegistry placeholders) {
        super(material, config, placeholders);
        this.baseColor = config.getDye("baseColor");
        Collection<Map<String, Object>> rawPatterns = (Collection<Map<String, Object>>) config.getCollection("patterns");
        patterns = new ArrayList<>();
        if (rawPatterns != null) {
            for (Map<String, Object> p : rawPatterns) {
                Config sub = Config.wrap(p);
                DyeColor color = sub.getDyeRequired("color");
                PatternType type = PatternType.getByIdentifier(sub.getStringRequired("pattern"));
                if (type == null) {
                    Uppercore.logger().severe("Cannot find pattern identifier \"" + p.get("pattern") + "\"");
                    type = PatternType.BASE;
                }
                patterns.add(new Pattern(color, type));
            }
        }
    }

    @ConfigConstructor
    public BannerCustomItem(
            @ConfigProperty("type") Material type,
            @ConfigProperty(value = "data", optional = true) PlaceholderValue<Short> data,
            @ConfigProperty(value = "amount", optional = true) PlaceholderValue<Integer> amount,
            @ConfigProperty(value = "value", optional = true) String rawName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "baseColor", optional = true) DyeColor baseColor,
            @ConfigProperty(value = "patterns", optional = true) List<Pattern> patters
    ) {
        super(type, data, amount, rawName, lore, flags, enchantments);
        this.baseColor = baseColor;
        this.patterns = patters != null ? patters : Collections.emptyList();
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        BannerMeta meta = (BannerMeta) m;
        meta.setBaseColor(baseColor);
        meta.setPatterns(patterns);
    }
}
