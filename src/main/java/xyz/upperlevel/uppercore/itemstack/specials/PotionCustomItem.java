package xyz.upperlevel.uppercore.itemstack.specials;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PotionCustomItem extends CustomItem {

    private PotionType type;
    private PlaceholderValue<Color> customColor;
    private List<PotionEffect> customEffects;

    public PotionCustomItem(Material material, Config config, PlaceholderRegistry placeholders) {
        super(material, config, placeholders);
        type = config.getEnum("potion-type", PotionType.class);
        customColor = PlaceholderValue.colorValue(config.getString("color"));
        customEffects = new ArrayList<>();
        Collection<Map<String, Object>> rawEffects = (Collection<Map<String, Object>>) config.getCollection("effects");
        if(rawEffects != null) {
            for (Map<String, Object> e : rawEffects)
                customEffects.add(new PotionEffect(e));
        }
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
}
