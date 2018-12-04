package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.*;
import java.util.stream.Collectors;

public class UItemMeta<I extends UItemMeta, T extends ItemMeta> {
    private PlaceholderValue<String> displayName = null;
    private List<PlaceholderValue<String>> lore = new ArrayList<>();
    private List<ItemFlag> flags = new ArrayList<>();
    private Map<Enchantment, PlaceholderValue<Integer>> enchantments = new HashMap<>();

    public UItemMeta() {
    }

    @ConfigConstructor
    public UItemMeta(
            @ConfigProperty(value = "display-name", optional = true) String displayName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments
    ) {
        this.displayName = displayName == null ? null : PlaceholderValue.stringValue(ChatColor.RESET.toString() + displayName);
        this.lore = lore != null ? lore : Collections.emptyList();
        this.flags = flags != null ? flags : Collections.emptyList();
        if (enchantments != null) {
            this.enchantments.putAll(enchantments);
        }
    }

    @SuppressWarnings("unchecked")
    private I self() {
        return (I) this;
    }

    public I setDisplayName(String displayName) {
        this.displayName = PlaceholderValue.fake(displayName);
        return self();
    }

    public I addLore(String line) {
        this.lore.add(PlaceholderValue.fake(line));
        return self();
    }

    public I setLore(List<String> lore) {
        this.lore = lore.stream().map(PlaceholderValue::fake).collect(Collectors.toList());
        return self();
    }

    public I addFlag(ItemFlag flag) {
        this.flags.add(flag);
        return self();
    }

    public I setFlags(List<ItemFlag> flags) {
        this.flags = flags;
        return self();
    }

    public I addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, PlaceholderValue.fake(level));
        return self();
    }

    public I setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> PlaceholderValue.fake(entry.getValue())
        ));
        return self();
    }

    public void resolve(T meta, Player player) {
        if (displayName != null) {
            meta.setDisplayName(displayName.resolve(player));
        }
        if (lore != null) {
            meta.setLore(lore.stream().map(line -> line.resolve(player)).collect(Collectors.toList()));
        }
        if (flags != null) {
            meta.addItemFlags(flags.toArray(new ItemFlag[0]));
        }
        if (enchantments != null) {
            for (Map.Entry<Enchantment, PlaceholderValue<Integer>> enchant : enchantments.entrySet()) {
                meta.addEnchant(enchant.getKey(), enchant.getValue().resolve(player), true);
            }
        }
    }
}
