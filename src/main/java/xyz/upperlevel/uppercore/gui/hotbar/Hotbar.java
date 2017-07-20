package xyz.upperlevel.uppercore.gui.hotbar;


import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.Icon;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.gui.link.Link;

import java.util.*;
import java.util.function.Predicate;

@Data
public class Hotbar {

    private Plugin plugin;
    private String id;

    private Icon[] icons = new Icon[9];
    private List<Icon> noSlotIcons = new ArrayList<>();

    private int nextFree = 0;
    private int size = 0;

    private String permission;
    private boolean onJoin;

    public Hotbar() {
    }

    public Hotbar(Plugin plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    public boolean isIdentified() {
        return plugin != null && id != null;
    }

    public String getGlobalId() {
        if (!isIdentified()) return null;
        return (plugin.getName() + ":" + id).toLowerCase(Locale.ENGLISH);
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

    public Icon getIcon(int slot) {
        return icons[slot];
    }

    public boolean setIcon(int slot, Icon icon) {
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
    public boolean remove(Predicate<Icon> predicate) {
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
    public void remove(Collection<Icon> links) {
        remove(links::contains);
    }

    /**
     * Removes any link contained in the array.
     *
     * @param links the array containing the links to removeHotbar
     */
    public void remove(Icon[] links) {
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
        addIcon(new Icon(item));
    }

    public void addIcons(Collection<Icon> icons) {
        if (icons.size() > (9 - size))
            throw new HotbarOutOfSpaceException(this, icons.size());

        for (Icon icon : icons) {
            this.icons[nextFree] = icon;
            size++;
            findNextFree();
        }
    }

    public void addIcons(Icon[] icons) {
        addIcons(Arrays.asList(icons));
    }

    public boolean addIcon(ItemStack item, Link link) {
        return addIcon(new Icon(item, link));
    }

    public boolean addIcon(Icon icon) {
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
        if (permission != null && !player.hasPermission(permission))
            return false;
        HotbarSystem.getView(player).addHotbar(this);
        return true;
    }

    public boolean remove(Player player) {
        return HotbarSystem.getView(player).removeHotbar(this);
    }

    /**
     * Deserializes the hotbar by the given id and the given config.
     *
     * @param config the config where load the hotbar
     * @return the hotbar created
     */
    public static Hotbar deserialize(Plugin plugin, String id, Config config) {
        Hotbar hotbar = new Hotbar(plugin, id);
        hotbar.permission = (String) config.get("permission");
        for (Config section : config.getConfigList("icons")) {
            Icon icon = Icon.deserialize(plugin, section);
            int slot = section.getInt("slot", -1);
            if (slot == -1)
                hotbar.noSlotIcons.add(icon);
            else
                hotbar.icons[slot] = icon;
        }
        hotbar.onJoin = config.getBool("on-join", false);
        return hotbar;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Hotbar hotbar;

        public Builder() {
            hotbar = new Hotbar();
        }

        public Builder(Hotbar hotbar) {
            this.hotbar = hotbar;
        }

        public Builder permission(String permission) {
            hotbar.setPermission(permission);
            return this;
        }

        public Builder onJoin(boolean onJoin) {
            hotbar.setOnJoin(onJoin);
            return this;
        }

        public Builder add(Icon link) {
            hotbar.addIcon(link);
            return this;
        }

        public Builder add(ItemStack item, Link link) {
            hotbar.addIcon(item, link);
            return this;
        }

        public Builder add(Icon... links) {
            hotbar.addIcons(links);
            return this;
        }

        public Builder add(Collection<Icon> links) {
            hotbar.addIcons(links);
            return this;
        }

        public Builder set(int slot, Icon icon) {
            hotbar.setIcon(slot, icon);
            return this;
        }

        public Hotbar build() {
            return hotbar;
        }
    }
}
