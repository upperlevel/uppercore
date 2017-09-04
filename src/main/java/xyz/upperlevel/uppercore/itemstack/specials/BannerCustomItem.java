package xyz.upperlevel.uppercore.itemstack.specials;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BannerCustomItem extends CustomItem {
    private DyeColor baseColor;
    private List<Pattern> patterns;

    @SuppressWarnings("unchecked")
    public BannerCustomItem(Material material, Config config, PlaceholderRegistry placeholders) {
        super(material, config, placeholders);
        this.baseColor = config.getDye("baseColor");
        Collection<Map<String, Object>> rawPatterns = (Collection<Map<String, Object>>) config.getCollection("patterns");
        patterns = new ArrayList<>();
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
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        BannerMeta meta = (BannerMeta) m;
        meta.setBaseColor(baseColor);
        meta.setPatterns(patterns);
    }
}
