package xyz.upperlevel.uppercore.gui.config.itemstack;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LeatherArmorCustomItem extends CustomItem {

    private PlaceholderValue<Color> color;

    public LeatherArmorCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                  PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                                  List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                  PlaceholderRegistry placeholders,
                                  PlaceholderValue<Color> color) {
        super(material, data, amount, displayName, lore, flags, enchantments, placeholders);
        this.color = color;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        LeatherArmorMeta meta = (LeatherArmorMeta) m;
        if(color != null)
            meta.setColor(color.resolve(player));
    }

    public static LeatherArmorCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                              PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                              List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                              PlaceholderRegistry placeholders, Config config) {
        PlaceholderValue<Color> color = PlaceholderValue.colorValue(config.getString("color"));
        return new LeatherArmorCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments, placeholders,
                color
        );
    }
}
