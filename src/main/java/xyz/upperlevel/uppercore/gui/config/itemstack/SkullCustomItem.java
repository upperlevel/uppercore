package xyz.upperlevel.uppercore.gui.config.itemstack;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkullCustomItem extends CustomItem {

    private PlaceholderValue<String> skullOwner;

    public SkullCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                           PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                           List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                           Map<String, Placeholder> local,
                           PlaceholderValue<String> skullOwner) {
        super(material, data, amount, displayName, lore, flags, enchantments, local);
        this.skullOwner = skullOwner;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        SkullMeta meta = (SkullMeta) m;
        if(skullOwner != null)
            meta.setOwner(skullOwner.resolve(player, getLocalPlaceholders()));
    }

    public static SkullCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                       PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                       List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                       Map<String, Placeholder> local, Config config) {
        PlaceholderValue<String> skullOwner = PlaceholderValue.stringValue(config.getString("owner"), local);
        return new SkullCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments, local,
                skullOwner
        );
    }
}
