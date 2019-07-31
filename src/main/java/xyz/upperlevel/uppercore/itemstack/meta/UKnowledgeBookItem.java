package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.itemstack.UItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UKnowledgeBookItem extends UItem {
    private List<PlaceholderValue<String>> recipes;

    @ConfigConstructor
    public UKnowledgeBookItem(
            @ConfigProperty("type") Material type,
            @ConfigProperty(value = "data", optional = true) PlaceholderValue<Short> data,
            @ConfigProperty(value = "amount", optional = true) PlaceholderValue<Integer> amount,
            @ConfigProperty(value = "value", optional = true) String rawName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "recipes", optional = true) List<PlaceholderValue<String>> recipes
    ) {
        super(type, data, amount, rawName, lore, flags, enchantments);
        this.recipes = recipes;
    }

    @Override
    public void processMeta(Player player, ItemMeta meta) {
        super.processMeta(player, meta);
        ((KnowledgeBookMeta) meta).setRecipes(
                recipes.stream()
                        .map(recipe -> NamespacedKey.minecraft(recipe.resolve(player, getPlaceholders())))
                        .collect(Collectors.toList())
        );
    }
}
