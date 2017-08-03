package xyz.upperlevel.uppercore.util;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.ChatColor.RESET;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material type) {
        this(type, (byte) 0);
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder(Material type, byte data) {
        this(new ItemStack(type, 1, (short) 0, data));
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore.stream().map(line -> RESET + line).collect(Collectors.toList()));
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        item.setDurability(durability);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Serialize the item in a more user-friendly way than bukkit ones.
     */
    @SuppressWarnings("deprecation")
    public static Map<String, Object> serialize(ItemStack item) {
        Map<String, Object> result = new HashMap<>();

        result.put("type", item.getType().name());
        result.put("data", item.getData().getData());
        result.put("amount", item.getAmount());
        result.put("durability", item.getDurability());

        ItemMeta meta = item.getItemMeta();
        result.put("displayName", meta.getDisplayName());
        result.put("lore", meta.getLore());

        // serializes enchants - <enchantment,level>
        List<String> enchantments = new ArrayList<>();
        item.getEnchantments().forEach((enc, lev) -> enchantments.add(enc.getName() + "," + lev));

        result.put("enchantments", enchantments);

        return result;
    }

    /**
     * Deserialize items.
     */
    public static ItemStack deserialize(ConfigurationSection section) {
        Material type = Material.getMaterial(section.getString("type"));
        byte data = (byte) section.getInt("data");

        ItemBuilder item = new ItemBuilder(type, data);

        item.setAmount(section.getInt("amount"));
        item.setDurability((short) section.getInt("durability"));

        item.setDisplayName(section.getString("displayName"));
        item.setLore(section.getString("lore"));

        List<String> enchants = section.getStringList("enchantments");
        for (String enchant : enchants) {
            String tmp[] = enchant.split(",");

            Enchantment enc = Enchantment.getByName(tmp[0]);
            int lev = Integer.parseInt(tmp[1]);

            item.addEnchantment(enc, lev);
        }

        return item.build();
    }
}
