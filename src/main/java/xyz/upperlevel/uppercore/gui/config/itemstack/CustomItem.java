package xyz.upperlevel.uppercore.gui.config.itemstack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class CustomItem {

    private Material type;
    private PlaceholderValue<Short> data;
    private PlaceholderValue<Integer> amount;

    // meta
    private PlaceholderValue<String> displayName;
    private List<PlaceholderValue<String>> lore;
    private List<ItemFlag> flags;
    private Map<Enchantment, PlaceholderValue<Integer>> enchantments = new HashMap<>();

    public CustomItem(ItemStack item) {
        type = item.getType();
        data = PlaceholderValue.shortValue(String.valueOf(item.getData().getData()));
        amount = PlaceholderValue.intValue(String.valueOf(item.getAmount()));

        ItemMeta meta = item.getItemMeta();
        displayName = meta.hasDisplayName() ? PlaceholderValue.stringValue(meta.getDisplayName()) : null;
        lore = meta.hasLore() ? meta.getLore().stream().map(PlaceholderValue::stringValue).collect(Collectors.toList()) : new ArrayList<>();
        flags = new ArrayList<>(meta.getItemFlags());
        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet())
            enchantments.put(entry.getKey(), PlaceholderValue.intValue(String.valueOf(entry.getValue())));
    }

    public ItemStack toItemStack(Player player) {
        ItemStack item = new ItemStack(type, amount.resolve(player), data.resolve(player));
        ItemMeta meta = item.getItemMeta();

        processMeta(player, meta);

        item.setItemMeta(meta);
        return item;
    }

    public void processMeta(Player player, ItemMeta meta) {
        if (displayName != null)
            meta.setDisplayName(displayName.resolve(player));
        meta.setLore(lore.stream().map(m -> m.resolve(player)).collect(Collectors.toList()));
        meta.addItemFlags(flags.toArray(new ItemFlag[0]));
        for (Map.Entry<Enchantment, PlaceholderValue<Integer>> ench : enchantments.entrySet())
            meta.addEnchant(ench.getKey(), ench.getValue().resolve(player), true);
    }

    @SuppressWarnings("unchecked")
    public static CustomItem deserialize(Config config) {
        Material mat = config.getMaterialRequired("type");
        PlaceholderValue<Short> data = PlaceholderValue.shortValue(config.getString("data", "0"));//TODO: better api
        PlaceholderValue<Integer> amount = PlaceholderUtil.parseInt(config.getString("amount", "1"));

        PlaceholderValue<String> displayName = config.getMessage("name");
        List<PlaceholderValue<String>> lores;
        if (config.has("lore")) {
            lores = ((Collection<String>) config.getCollection("lore"))
                    .stream()
                    .map(PlaceholderUtil::process)
                    .collect(Collectors.toList());
        } else lores = Collections.emptyList();
        List<ItemFlag> flags;
        if (config.has("flags")) {
            flags = ((Collection<String>) config.getCollection("flags"))
                    .stream()
                    .map(s -> s.replace(' ', '_').toUpperCase(Locale.ENGLISH))
                    .map(ItemFlag::valueOf)
                    .collect(Collectors.toList());
        } else
            flags = Collections.emptyList();

        Map<Enchantment, PlaceholderValue<Integer>> enchantments;

        enchantments = new HashMap<>();
        if (config.has("enchantments")) {
            Map<String, Object> stEnch = config.getSection("enchantments");
            for(Map.Entry<String, Object> e : stEnch.entrySet()) {
                Enchantment ench = Enchantment.getByName(e.getKey().replace(' ', '_').toUpperCase(Locale.ENGLISH));
                if (ench == null)
                    throw new InvalidConfigurationException("Cannot find enchantment: " + e.getKey());
                else
                    enchantments.put(ench, PlaceholderValue.intValue(e.getValue().toString()));
            }
        }

        switch (mat) {
            case BANNER:
                return BannerCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case SKULL:
                mat = Material.SKULL_ITEM;
            case SKULL_ITEM:
                return SkullCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case LEATHER_BOOTS:
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
            case LEATHER_LEGGINGS:
                return LeatherArmorCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case MAP:
                return MapCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case POTION:
            case LINGERING_POTION:
            case SPLASH_POTION:
            case TIPPED_ARROW:
                return PotionCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case MONSTER_EGG:
                return SpawnEggCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case ENCHANTED_BOOK:
                return EnchantedBookCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case FIREWORK:
                return FireworkCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case FIREWORK_CHARGE:
                return FireworkChargeCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            default:
                return new CustomItem(mat, data, amount, displayName, lores, flags, enchantments);
        }
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        return '{' + joiner.toString() + '}';
    }

    protected void addStringDetails(StringJoiner joiner) {
        joiner.add("type: " + type);
        joiner.add("data: " + data);
        joiner.add("amount: " + amount);
        joiner.add("displayName: " + displayName);
        joiner.add("lore: " + lore);
        joiner.add("flags: " + flags);
        joiner.add("enchantments: " + enchantments);
    }


}
