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
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.PolymorphicSelector;
import xyz.upperlevel.uppercore.itemstack.meta.*;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class UItem implements ItemResolver {
    private static final Map<Class<? extends ItemMeta>, Class<? extends UItem>> items = new HashMap<>();

    static {
        items.put(BannerMeta.class, UBannerItem.class);
        items.put(EnchantmentStorageMeta.class, UEnchantmentStorageItem.class);
        items.put(FireworkEffectMeta.class, UFireworkEffectItem.class);
        items.put(FireworkMeta.class, UFireworkItem.class);
        items.put(LeatherArmorMeta.class, ULeatherArmorItem.class);
        items.put(MapMeta.class, UMapItem.class);
        items.put(PotionMeta.class, UPotionItem.class);
        items.put(SkullMeta.class, USkullItem.class);
        items.put(KnowledgeBookMeta.class, UKnowledgeBookItem.class);
        items.put(TropicalFishBucketMeta.class, UTropicalFishBucketItem.class);
    }

    public static final UItem AIR = new UItem(new ItemStack(Material.AIR));

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

    public UItem(UItem item) {
        this.type = item.type;
        this.data = item.data;
        this.amount = item.amount;
        this.displayName = item.displayName;
        this.lore = item.lore;
        this.flags = item.flags;
        this.enchantments = item.enchantments;
        this.placeholders = item.placeholders;
    }

    @ConfigConstructor
    public UItem(
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

    public UItem(ItemStack item) {
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

    public UItem copy() {
        return new UItem(this);
    }

    /*
    public static void registerCustomDeserializer(Class<? extends UItem> deserializer, Material... aliases) {
        for (Material m : aliases) {
            if (m != null) {
                deserializers.put(m, deserializer);
            }
        }
    }*/

    private static Material mat(String material) {
        return Material.getMaterial(material);
    }

    @PolymorphicSelector
    private static Class<?> selectChild(@ConfigProperty("type") Material type) {
        Class<? extends ItemMeta> sourceClass = Bukkit.getItemFactory().getItemMeta(type).getClass();

        // The meta interfaces are implemented by craftbukkit classes
        for (Map.Entry<Class<? extends ItemMeta>, Class<? extends UItem>> entry : items.entrySet()) {
            if (entry.getKey().isAssignableFrom(sourceClass)) {
                return entry.getValue();
            }
        }

        return UItem.class;
    }
}
