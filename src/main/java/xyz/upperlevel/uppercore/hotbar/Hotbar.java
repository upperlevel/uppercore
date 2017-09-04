package xyz.upperlevel.uppercore.hotbar;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.ConfigIcon;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.gui.link.Link;

import java.util.*;
import java.util.function.Predicate;

import static xyz.upperlevel.uppercore.Uppercore.hotbars;

@Data
public class Hotbar {
    private ConfigIcon[] icons = new ConfigIcon[9];
    private List<ConfigIcon> noSlotIcons = new ArrayList<>();

    private int nextFree = 0;
    private int size = 0;

    public Hotbar() {
    }

    public Hotbar(Plugin plugin, Config config) {
        if (config.has("icons"))
            for (Config section : config.getConfigList("icons")) {
                ConfigIcon icon = ConfigIcon.deserialize(plugin, section);
                int slot = section.getInt("slot", -1);
                if (slot == -1)
                    noSlotIcons.add(icon);
                else
                    icons[slot] = icon;
            }
    }

    /**
     * Checks if the hotbar is empty.
     *
     * @return true only if the player has one or more links
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Checks if the hotbar is full.
     *
     * @return true only if the hotbar is full
     */
    public boolean isFull() {
        return nextFree == -1;
    }

    /**
     * Returns the number of links in the hotbar.
     *
     * @return the number of links in the hotbar
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the number of slots available.
     *
     * @return the number of slots available
     */
    public int getFree() {
        return 9 - size;
    }

    private void findNextFree() {
        do {
            if (++nextFree >= 9) {
                nextFree = -1;
                break;
            }
        } while (icons[nextFree] != null);
    }

    public ConfigIcon getIcon(int slot) {
        return icons[slot];
    }

    public boolean setIcon(int slot, ConfigIcon icon) {
        if (icon == null)
            return remove(slot);
        if (icons[slot] != null)
            return false;
        icons[slot] = icon;
        size++;
        if (nextFree == slot)
            findNextFree();
        return true;
    }

    /**
     * Removes any link matching the predicate.
     *
     * @param predicate the predicate that decides which item to removeHotbar
     * @return true if the hotbar changed
     */
    public boolean remove(Predicate<ConfigIcon> predicate) {
        int initialSize = size;
        for (int i = 0; i < 9; i++) {
            if (predicate.test(icons[i])) {
                if (icons[i] == null)
                    size--;
                icons[i] = null;
            }
        }
        return size != initialSize;
    }

    /**
     * Removes any link that is contained in the collection.
     *
     * @param links the collection with the links to removeHotbar
     */
    public void remove(Collection<ConfigIcon> links) {
        remove(links::contains);
    }

    /**
     * Removes any link contained in the array.
     *
     * @param links the array containing the links to removeHotbar
     */
    public void remove(ConfigIcon[] links) {
        remove(Arrays.asList(links));
    }

    /**
     * Removes the link in that slot (if any is present).
     *
     * @param slot the slot with the link to removeHotbar
     * @return true only if any links were in that slot
     */
    public boolean remove(int slot) {
        if (icons[slot] == null)
            return false;
        icons[slot] = null;
        size--;
        return true;
    }

    /**
     * Removes all the links.
     */
    public void clearIcons() {
        for (int slot = 0; slot < icons.length; slot++)
            icons[slot] = null;
        size = 0;
    }

    public void addItem(ItemStack item) {
        addIcon(new ConfigIcon(item));
    }

    public void addIcons(Collection<ConfigIcon> icons) {
        if (icons.size() > (9 - size))
            throw new HotbarOutOfSpaceException(this, icons.size());

        for (ConfigIcon icon : icons) {
            this.icons[nextFree] = icon;
            size++;
            findNextFree();
        }
    }

    public void addIcons(ConfigIcon[] icons) {
        addIcons(Arrays.asList(icons));
    }

    public boolean addIcon(ItemStack item, Link link) {
        return addIcon(new ConfigIcon(item, link));
    }

    public boolean addIcon(ConfigIcon icon) {
        if (isFull())
            return false;
        icons[nextFree] = icon;
        findNextFree();
        return true;
    }

    public boolean isIconSlot(int slot) {
        return icons[slot] != null;
    }

    public boolean give(Player player) {
        hotbars().view(player).addHotbar(this);
        return true;
    }

    public boolean remove(Player player) {
        return hotbars().view(player).removeHotbar(this);
    }

    /**
     * Deserializes the hotbar by the given id and the given config.
     *
     * @param config the config where load the hotbar
     * @return the hotbar created
     */
    public static Hotbar deserialize(Plugin plugin, Config config) {
        return new Hotbar(plugin, config);
    }

    public static class HotbarOutOfSpaceException extends RuntimeException {
        @Getter
        private final Hotbar hotbar;
        @Getter
        private final int toAdd;

        public HotbarOutOfSpaceException(Hotbar hotbar, int toAdd) {
            super("Error adding links to hotbar: trying to add " + toAdd + " but only " + hotbar.getFree() + " empty!");
            this.hotbar = hotbar;
            this.toAdd = toAdd;
        }
    }
}
