package xyz.upperlevel.uppercore.itemstack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.Banner;
import org.bukkit.material.MaterialData;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.PolymorphicSelector;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;
import xyz.upperlevel.uppercore.itemstack.specials.*;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class CustomItem implements ItemResolver {
    /**
     * Based on the ItemMeta's class, gets the correct CustomItem implementation.
     */
    private static final Map<Class<? extends ItemMeta>, Class<? extends CustomItem>> deserializers = new HashMap<>();

    static {
        deserializers.put(BannerMeta.class, BannerCustomItem.class);
        deserializers.put(EnchantmentStorageMeta.class, EnchantedBookCustomItem.class);
        deserializers.put(FireworkEffectMeta.class, FireworkChargeCustomItem.class);
        deserializers.put(FireworkMeta.class, FireworkCustomItem.class);
        deserializers.put(LeatherArmorMeta.class, LeatherArmorCustomItem.class);
        deserializers.put(MapMeta.class, MapCustomItem.class);
        deserializers.put(PotionMeta.class, PotionCustomItem.class);
        deserializers.put(SkullMeta.class, SkullCustomItem.class);
        deserializers.put(SpawnEggMeta.class, SpawnEggCustomItem.class);
    }

    public static final CustomItem AIR = new CustomItem(new ItemStack(Material.AIR));

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

    @SuppressWarnings("unchecked")
    public CustomItem(Material type, Config config, PlaceholderRegistry placeholders) {
        this.type = type;
        this.placeholders = placeholders;
        data = PlaceholderValue.shortValue(config.getString("data", "0"));//TODO: better api
        amount = PlaceholderUtil.parseInt(config.getString("amount", "1"));

        String rawName = config.getString("name");
        displayName = rawName == null ? null : PlaceholderValue.stringValue(ChatColor.RESET.toString() + rawName);
        if (config.has("lore")) {
            lore = ((Collection<String>) config.getCollection("lore"))
                    .stream()
                    .map(message -> PlaceholderUtil.process(ChatColor.RESET.toString() + message))
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
            Map<String, Object> stEnch = config.getMap("enchantments");
            for(Map.Entry<String, Object> e : stEnch.entrySet()) {
                Enchantment ench = Enchantment.getByName(e.getKey().replace(' ', '_').toUpperCase(Locale.ENGLISH));
                if (ench == null)
                    throw new InvalidConfigException("Cannot find enchantment: " + e.getKey());
                else
                    enchantments.put(ench, PlaceholderValue.intValue(e.getValue().toString()));
            }
        }
    }

    @ConfigConstructor
    public CustomItem(
            @ConfigProperty("type") Material type,
            @ConfigProperty(value = "data", optional = true) PlaceholderValue<Short> data,
            @ConfigProperty(value = "amount", optional = true) PlaceholderValue<Integer> amount,
            @ConfigProperty(value = "name", optional = true) String rawName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments
    ) {
        this.type = type;
        this.data = data != null ? data : PlaceholderValue.fake((short)0);
        this.amount = amount != null ? amount : PlaceholderValue.fake(1);
        this.displayName = rawName == null ? null : PlaceholderValue.stringValue(ChatColor.RESET.toString() + rawName);
        this.lore = lore != null ? lore : Collections.emptyList();
        this.flags = flags != null ? flags : Collections.emptyList();
        if (enchantments != null) {
            this.enchantments.putAll(enchantments);
        }
    }

    public CustomItem(ItemStack item) {
        type = item.getType();
        data = PlaceholderValue.shortValue(String.valueOf(item.getData().getData()));
        amount = PlaceholderValue.intValue(String.valueOf(item.getAmount()));

        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            displayName = meta.hasDisplayName() ? PlaceholderValue.stringValue(meta.getDisplayName()) : null;
            lore = meta.hasLore() ? meta.getLore().stream().map(PlaceholderValue::stringValue).collect(Collectors.toList()) : new ArrayList<>();
            flags = new ArrayList<>(meta.getItemFlags());
            for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet())
                enchantments.put(entry.getKey(), PlaceholderValue.intValue(String.valueOf(entry.getValue())));
        } else {
            displayName = PlaceholderValue.fake("");
            lore = Collections.emptyList();
        }
    }

    @Override
    public ItemStack resolve(Player player) {
        ItemStack item = new ItemStack(type, amount.resolve(player), data.resolve(player));
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            processMeta(player, meta);

            item.setItemMeta(meta);
        }
        return item;
    }

    public void processMeta(Player player, ItemMeta meta) {
        if (displayName != null) {
            meta.setDisplayName(displayName.resolve(player, placeholders));
        }
        meta.setLore(lore.stream().map(m -> m.resolve(player, placeholders)).collect(Collectors.toList()));
        meta.addItemFlags(flags.toArray(new ItemFlag[0]));
        for (Map.Entry<Enchantment, PlaceholderValue<Integer>> ench : enchantments.entrySet()) {
            meta.addEnchant(ench.getKey(), ench.getValue().resolve(player), true);
        }
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

    /*
    public static void registerCustomDeserializer(Class<? extends CustomItem> deserializer, Material... aliases) {
        for (Material m : aliases) {
            if (m != null) {
                deserializers.put(m, deserializer);
            }
        }
    }*/

    private static Material mat(String material) {
        return Material.getMaterial(material);
    }

    public static CustomItem deserialize(Config config, PlaceholderRegistry placeholders) {
        Material type = config.getMaterialRequired("type");

        Class<? extends ItemMeta> meta = Bukkit.getItemFactory().getItemMeta(type).getClass();
        Class<? extends CustomItem> deserializer = deserializers.getOrDefault(meta, CustomItem.class);

        try {
            return deserializer
                    .getConstructor(Material.class, Config.class, PlaceholderRegistry.class)
                    .newInstance(type, config, placeholders);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static CustomItem deserialize(Config config) {
        return deserialize(config, PlaceholderRegistry.def());
    }

    @PolymorphicSelector
    private static Class<?> selectChild(@ConfigProperty("type") Material type) {
        Class<? extends ItemMeta> target = Bukkit.getItemFactory().getItemMeta(type).getClass();
        return deserializers.getOrDefault(target, CustomItem.class);
    }
}
