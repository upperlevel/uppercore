package xyz.upperlevel.uppercore.hotbar;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.ConfigIcon;
import xyz.upperlevel.uppercore.task.UpdaterTask;
import xyz.upperlevel.uppercore.util.Dbg;

import java.util.*;


public class HotbarView {
    @Getter
    private final Player player;

    @Getter
    private final ConfigIcon[] icons = new ConfigIcon[9];
    @Getter
    private final ItemStack[] items = new ItemStack[9];

    @Getter
    private final Set<Hotbar> hotbars = new HashSet<>();
    @Getter
    private final Map<ConfigIcon, UpdaterTask> updaters = new HashMap<>(); // one updater per hotbar

    // takes care of slots held by hotbars
    @Getter
    private final Map<Hotbar, Set<Integer>> slotsByHotbar = new HashMap<>();
    @Getter
    private final Hotbar[] hotbarsBySlot = new Hotbar[9];

    public HotbarView(Player player) {
        this.player = player;
    }

    public void printSlot(int slot) {
        ConfigIcon icon = icons[slot];
        ItemStack item;
        if (icon != null)
            item = icon.getDisplay().resolve(player);
        else
            item = null;
        items[slot] = item;
        player.getInventory().setItem(slot, item);
    }

    private void wipeSlot(int slot) {
        player.getInventory().setItem(slot, null);
        items[slot] = null;
    }

    public void printIcon(ConfigIcon icon) {
        for (int slot = 0; slot < icons.length; slot++)
            if (icons[slot] != null && icons[slot] == icon)
                printSlot(slot);
    }

    private void wipeIcon(ConfigIcon icon) {
        for (int slot = 0; slot < icons.length; slot++) {
            if (icons[slot] != null && icons[slot].equals(icon))
                wipeSlot(slot);
        }
    }

    public boolean tryToSaveSlot(int slot) {
        Inventory inv = player.getInventory();
        ItemStack item = inv.getItem(slot);
        if (item == null)
            return true;
        ListIterator<ItemStack> i = inv.iterator();
        while (i.hasNext()) {
            int ind = i.nextIndex();
            ItemStack itm = i.next();
            if (ind == slot || (ind < 9 && hotbarsBySlot[ind] != null))
                continue;
            if(itm == null) {
                i.set(item);
                return true;
            } else if (itm.isSimilar(item)) {
                int free = itm.getAmount() - itm.getMaxStackSize();
                if(item.getAmount() > free) {
                    itm.setAmount(itm.getMaxStackSize());
                } else {
                    itm.setAmount(itm.getAmount() + free);
                    return true;
                }
            }
        }
        return false;
    }

    public void printHotbar(Hotbar hotbar) {
        for (int slot : getSlots(hotbar))
            printSlot(slot);
    }

    public void print() {
        for (Hotbar hotbar : hotbars)
            printHotbar(hotbar);
    }

    public void clear() {
        hotbars.clear();
        for (int slot = 0; slot < 9; slot++) {
            if(!isIconSlot(slot))
                continue;
            player.getInventory().setItem(slot, null);
            icons[slot] = null;
            items[slot] = null;
            hotbarsBySlot[slot] = null;
        }
        slotsByHotbar.clear();
        updaters.values().forEach(UpdaterTask::stop);
        updaters.clear();
    }

    public ConfigIcon getIcon(int slot) {
        return icons[slot];
    }

    public boolean isIcon(ItemStack item) {
        for (ItemStack cache : items)
            if (cache != null) {
                if (cache.equals(item))
                    return true;
            } else if (item == null)
                return true;
        return false;
    }

    public boolean isIconSlot(int slot) {
        return icons[slot] != null;
    }

    public boolean isHotbarSlot(int slot) {
        return hotbars.stream().anyMatch(hotbar -> hotbar.isIconSlot(slot));
    }

    public boolean addIcon(ConfigIcon icon) {
        int slot = getNextSlot();
        if (slot >= 0) {
            icons[slot] = icon;
            return true;
        }
        return false;
    }

    public int getIconsCount(ConfigIcon icon) {
        int count = 0;
        for (ConfigIcon other : icons) {
            if (icon != null) {
                if (icon.equals(other))
                    count++;

            } else if (other == null)
                count++;
        }
        return count;
    }

    public void setIcon(int slot, ConfigIcon icon) {
        setIcon(slot, icon, null);
    }

    private void setIcon(int slot, ConfigIcon icon, Hotbar hotbar) {
        if(icon == null) {
            removeIcon(slot, true);
            return;
        }

        if(!removeIcon(slot, false)) {
            tryToSaveSlot(slot);
        }
        icons[slot] = icon;
        if (hotbar != null)
            slotsByHotbar.computeIfAbsent(hotbar, bar -> new HashSet<>()).add(slot);
        hotbarsBySlot[slot] = hotbar;
        printSlot(slot);
        if (icon.needUpdate() && getIconsCount(icon) == 1) {
            UpdaterTask task = new UpdaterTask(icon.getUpdateInterval(), () -> printIcon(icon));
            updaters.put(icon, task);
            task.start();
        }
    }

    public void removeIcon(int slot) {
        removeIcon(slot, true);
    }

    private boolean removeIcon(int slot, boolean update) {
        if(hotbarsBySlot[slot] == null)
            return false;
        for (Set<Integer> slots : slotsByHotbar.values())
            slots.remove(slot);
        hotbarsBySlot[slot] = null;
        ConfigIcon icon = icons[slot];
        if (icon != null && getIconsCount(icon) == 1) {
            UpdaterTask task = updaters.remove(icon);
            if (task != null)
                task.stop();
        }
        icons[slot] = null;
        if (update)
            wipeSlot(slot);
        return true;
    }

    public int getFreeSlots() {
        int count = 0;
        for (ConfigIcon icon : icons) {
            if (icon == null)
                count++;
        }
        return count;
    }

    public int getNextSlot() {
        for (int slot = 0; slot < icons.length; slot++) {
            if (icons[slot] == null)
                return slot;
        }
        return -1;
    }

    public Hotbar getHotbar(int slot) {
        return hotbarsBySlot[slot];
    }

    public boolean addHotbar(Hotbar hotbar) {
        if (isHolding(hotbar)) {
            Dbg.pf("%s is already holding a hotbar", player.getName());
            return false;
        }
        if (isOverlaying(hotbar)) {
            Dbg.pf("The given hotbar would overlap the current %s hotbar-view", player.getName());
            return false;
        }
        for (int slot = 0; slot < hotbar.getIcons().length; slot++) {
            ConfigIcon icon = hotbar.getIcon(slot);
            if (icon != null)
                setIcon(slot, icon, hotbar);
        }
        for (ConfigIcon icon : hotbar.getNoSlotIcons())
            addIcon(icon);
        hotbars.add(hotbar);
        Dbg.pf("Hotbar added to %s", player.getName());
        return true;
    }

    public boolean removeHotbar(Hotbar hotbar) {
        if (!isHolding(hotbar)) {
            Dbg.pf("Trying to remove an hotbar that %s isn't holding", player.getName());
            return false;
        }
        Set<Integer> slots = new HashSet<>(slotsByHotbar.getOrDefault(hotbar, Collections.emptySet()));
        for (int slot : slots)
            removeIcon(slot);
        hotbars.remove(hotbar);
        Dbg.pf("Hotbar removed from %s", player.getName());
        return true;
    }

    public boolean hasSlot(Hotbar hotbar, int slot) {
        Set<Integer> slots = slotsByHotbar.get(hotbar);
        return slots != null && slots.contains(slot);
    }

    public Set<Integer> getSlots(Hotbar hotbar) {
        return slotsByHotbar.get(hotbar);
    }

    /**
     * Checks if the given hotbar can be placed within the player's view.
     * Basically checks if at least one item of the hotbar would overlap in the player's view.
     */
    public boolean isOverlaying(Hotbar hotbar) {
        for (int slot = 0; slot < hotbar.getIcons().length; slot++) {
            ConfigIcon icon = hotbar.getIcon(slot);
            if (icon != null && icons[slot] != null)
                return true;
        }
        return hotbar.getNoSlotIcons().size() > getFreeSlots();
    }

    public boolean isHolding(Hotbar hotbar) {
        return hotbars.contains(hotbar);
    }
}
