package xyz.upperlevel.uppercore.itemstack.specials;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.config.Config;
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
        effects = ((Collection<Map<String, Object>>)config.getCollection("effects", Collections.emptyList()))
                .stream()
                .map(c -> FireworkChargeCustomItem.parse(Config.wrap(c)))
                .collect(Collectors.toList());
        String rawPower = config.getString("power");
        power = rawPower == null ? null : PlaceholderUtil.parseInt(rawPower);
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        FireworkMeta meta = (FireworkMeta) m;
        meta.clearEffects();
        meta.addEffects(effects);
        if(power != null)
            meta.setPower(power.resolve(player));
    }
}
