package xyz.upperlevel.uppercore.gui.config.itemstack;

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
import xyz.upperlevel.uppercore.gui.config.util.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BannerCustomItem extends CustomItem {
    private DyeColor baseColor;
    private List<Pattern> patterns;

    public BannerCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                            PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                            List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                            DyeColor baseColor, List<Pattern> patterns) {
        super(material, data, amount, displayName, lore, flags, enchantments);
        this.baseColor = baseColor;
        this.patterns = patterns;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        BannerMeta meta = (BannerMeta) m;
        meta.setBaseColor(baseColor);
        meta.setPatterns(patterns);
    }

    @SuppressWarnings("unchecked")
    public static BannerCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                  PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                  List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                  Config config) {
        DyeColor baseColor = config.getDye("color");
        Collection<Map<String, Object>> rawPatterns = (Collection<Map<String, Object>>) config.getCollection("patterns");
        List<Pattern> patterns = new ArrayList<>();
        if(rawPatterns != null) {
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
        return new BannerCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments,
                baseColor, patterns
        );
    }
}
