package xyz.upperlevel.uppercore.gui.config.itemstack;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class SkullCustomItem extends CustomItem {

    private PlaceholderValue<String> skullOwner;

    public SkullCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                           PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                           List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                           PlaceholderRegistry placeholders,
                           PlaceholderValue<String> skullOwner) {
        super(material, data, amount, displayName, lore, flags, enchantments, placeholders);
        this.skullOwner = skullOwner;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        SkullMeta meta = (SkullMeta) m;
        if(skullOwner != null)
            meta.setOwner(skullOwner.resolve(player, getPlaceholders()));
    }

    public static SkullCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                       PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                       List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                       PlaceholderRegistry placeholders, Config config) {
        PlaceholderValue<String> skullOwner = PlaceholderValue.stringValue(config.getString("owner"));
        return new SkullCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments, placeholders,
                skullOwner
        );
    }
}
