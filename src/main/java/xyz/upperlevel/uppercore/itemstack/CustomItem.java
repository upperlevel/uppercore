package xyz.upperlevel.uppercore.itemstack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;
import xyz.upperlevel.uppercore.itemstack.specials.*;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class CustomItem implements ItemResolver {
    private static Map<Material, CustomItemFactory> customDeserializers = new HashMap<>();

    private Material type;
    private PlaceholderValue<Short> data;
    private PlaceholderValue<Integer> amount;

    // meta
    private PlaceholderValue<String> displayName;
    private List<PlaceholderValue<String>> lore;
    private List<ItemFlag> flags;
    private Map<Enchantment, PlaceholderValue<Integer>> enchantments = new HashMap<>();

    //Local placeholders
    private PlaceholderRegistry placeholders;

    static {
        registerDefCustomDeserializers();
    }

    public CustomItem(CustomItem item) {
        this.type = item.type;
        this.data = item.data;
        this.amount = item.amount;
        this.displayName = item.displayName;
        this.lore = item.lore;
        this.flags = item.flags;
        this.enchantments = item.enchantments;
        this.placeholders = item.placeholders;
    }

    public CustomItem(Material type, Config config, PlaceholderRegistry placeholders) {
        this.type = type;
        this.placeholders = placeholders;
        data = PlaceholderValue.shortValue(config.getString("data", "0"));//TODO: better api
        amount = PlaceholderUtil.parseInt(config.getString("amount", "1"));

        displayName = config.getMessage("name");
        if (config.has("lore")) {
            lore = ((Collection<String>) config.getCollection("lore"))
                    .stream()
                    .map(PlaceholderUtil::process)
                    .collect(Collectors.toList());
        } else lore = Collections.emptyList();
        if (config.has("flags")) {
            flags = ((Collection<String>) config.getCollection("flags"))
                    .stream()
                    .map(s -> s.replace(' ', '_').toUpperCase(Locale.ENGLISH))
                    .map(ItemFlag::valueOf)
                    .collect(Collectors.toList());
        } else
            flags = Collections.emptyList();

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
    }

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

    @Override
    public ItemStack resolve(Player player) {
        ItemStack item = new ItemStack(type, amount.resolve(player), data.resolve(player));
        ItemMeta meta = item.getItemMeta();

        processMeta(player, meta);

        item.setItemMeta(meta);
        return item;
    }

    public void processMeta(Player player, ItemMeta meta) {
        if (displayName != null)
            meta.setDisplayName(displayName.resolve(player, placeholders));
        meta.setLore(lore.stream().map(m -> m.resolve(player, placeholders)).collect(Collectors.toList()));
        meta.addItemFlags(flags.toArray(new ItemFlag[0]));
        for (Map.Entry<Enchantment, PlaceholderValue<Integer>> ench : enchantments.entrySet())
            meta.addEnchant(ench.getKey(), ench.getValue().resolve(player), true);
    }


    public static void registerDefCustomDeserializers() {
        registerCustomDeserializer(BannerCustomItem::new, Material.BANNER);
        registerCustomDeserializer(SkullCustomItem::new, Material.SKULL_ITEM);
        registerCustomDeserializer(LeatherArmorCustomItem::new, Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_LEGGINGS);
        registerCustomDeserializer(MapCustomItem::new, Material.MAP);
        registerCustomDeserializer(PotionCustomItem::new, Material.POTION, Material.LINGERING_POTION, Material.SPLASH_POTION, Material.TIPPED_ARROW);
        registerCustomDeserializer(SpawnEggCustomItem::new, Material.MONSTER_EGG);
        registerCustomDeserializer(EnchantedBookCustomItem::new, Material.ENCHANTED_BOOK);
        registerCustomDeserializer(FireworkCustomItem::new, Material.FIREWORK);
        registerCustomDeserializer(FireworkChargeCustomItem::new, Material.FIREWORK_CHARGE);
    }

    public static void registerCustomDeserializer(CustomItemFactory des, Material... aliases) {
        for(Material m : aliases)
            customDeserializers.put(m, des);
    }

    @SuppressWarnings("unchecked")
    public static CustomItem deserialize(Config config, PlaceholderRegistry placeholders) {
        Material mat = config.getMaterialRequired("type");

        CustomItemFactory deserializer = customDeserializers.getOrDefault(mat, CustomItem::new);

        return deserializer.create(mat, config, placeholders);
    }

    public static CustomItem deserialize(Config config) {
        return deserialize(config, PlaceholderRegistry.def());
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        addStringDetails(joiner);
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

    public CustomItem copy() {
        return new CustomItem(this);
    }
}
