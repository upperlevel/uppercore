package xyz.upperlevel.uppercore.gui;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;
import xyz.upperlevel.uppercore.gui.config.UpdaterTask;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.itemstack.ItemResolver;
import xyz.upperlevel.uppercore.gui.link.Link;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static xyz.upperlevel.uppercore.Uppercore.guis;

@Data
public class ChestGui implements Gui {
    private PlaceholderValue<String> title;
    private int size;
    private InventoryType type;
    private Icon[] icons;
    private int updateInterval = -1;
    private final Map<Player, UpdaterTask> updaters = new HashMap<>();

    /**
     * Initializes the gui by its size and title. The id can be set to null if not needed.
     *
     * @param size  the size of the gui
     * @param title the title of the gui
     */
    public ChestGui(int size, String title) {
        this.title = PlaceholderValue.stringValue(title);
        this.size = size;
        this.icons = new Icon[size];
        onSetup();
    }

    /**
     * Initializes the gui by its type and title. The id can be set to null if not needed.
     *
     * @param type  the type of the gui
     * @param title the title of the gui
     */
    public ChestGui(InventoryType type, String title) {
        this.type = type;
        this.title = PlaceholderValue.stringValue(title);
        this.icons = new Icon[type.getDefaultSize()];
        onSetup();
    }

    @SuppressWarnings("unchecked")
    protected ChestGui(Plugin plugin, Config config) {
        if (config.has("type")) {
            type = config.getEnum("type", InventoryType.class);
            icons = new Icon[type.getDefaultSize()];
        } else if (config.has("size")) {
            size = config.getInt("size");
            if (size % 9 != 0) {
                plugin.getLogger().warning("In a gui: size must be a multiple of 9");
                size = GuiSize.min(size);
            }
            icons = new Icon[size];
        } else
            throw new InvalidConfigurationException("Both 'type' and 'size' are empty!");
        updateInterval = config.getInt("update-interval", -1);
        title = config.getMessageRequired("title");
        Collection<Map<String, Object>> iconsData = (Collection<Map<String, Object>>) config.getCollection("icons");
        if (iconsData != null) {
            for (Map<String, Object> data : iconsData) {
                Icon item = Icon.deserialize(plugin, Config.wrap(data));
                icons[(int) data.get("slot")] = item;
            }
        }
    }

    public void onSetup() {
    }

    public void setTitle(String title) {
        this.title = PlaceholderValue.stringValue(title);
    }

    public void setTitle(PlaceholderValue<String> title) {
        this.title = title;
    }

    /**
     * Gets the item at the given slot.
     *
     * @param slot the slot to getHistories the item in
     */
    public Icon getIcon(int slot) {
        return icons[slot];
    }

    /**
     * Returns first slot empty.
     */
    public int firstEmpty() {
        for (int i = 0; i < size; i++)
            if (icons[i] == null)
                return i;
        return -1;
    }

    public boolean addItem(ItemStack item, Link link) {
        return addIcon(new Icon(item, link));
    }

    /**
     * Adds an item in the first slot empty.
     *
     * @param icon the item to addIcons
     */
    public boolean addItem(ItemStack icon) {
        return addIcon(new Icon(icon));
    }

    public boolean addIcon(Icon icon) {
        int i = firstEmpty();
        if (i >= 0) {
            icons[i] = icon;
            return true;
        }
        return false;
    }

    /**
     * Adds the given links.
     *
     * @param items the links to addIcons
     * @return true if all links have been added, otherwise false
     */
    public boolean addItems(ItemStack... items) {
        for (ItemStack item : items)
            if (!addItem(item))
                return false;
        return true;
    }

    public boolean addIcons(Icon... icons) {
        for (Icon icon : icons)
            if (!addIcon(icon))
                return false;
        return true;
    }

    private void setItem(int slot, ItemStack item, Link link) {
        setIcon(slot, new Icon(item, link));
    }

    public void setItem(int slot, ItemStack item) {
        setIcon(slot, new Icon(item));
    }

    /**
     * Sets the given item at the given slot.
     *
     * @param slot the slot where to give the item
     * @param icon the item to give
     */
    public void setIcon(int slot, Icon icon) {
        icons[slot] = icon;
    }

    public void setItem(int[] slots, ItemStack item) {
        setIcon(slots, new Icon(item));
    }

    public void setIcon(int[] slots, Icon icon) {
        for (int slot : slots)
            icons[slot] = icon;
    }

    public void setUpdateInterval(int updateInterval) {//TODO: Optimize for the first run
        if (this.updateInterval != updateInterval) {
            if (updateInterval > 0) {//If this interval is valid
                if (updaters.size() > 0) {//And there are some players that are updating
                    updaters.replaceAll((player, old) -> {
                        old.stop();
                        UpdaterTask task = new UpdaterTask(updateInterval, () -> onUpdate(player));
                        task.start();
                        return task;
                    });
                } else if (this.updateInterval < 0) {//If there isn't any updater because the old updateInterval wasn't valid
                    guis().getHistories().entrySet()
                            .stream()
                            .filter(e -> e.getValue().peek() == this)
                            .forEach((e) -> startUpdateTask(e.getKey()));
                }
            } else {
                updaters.forEach((p, t) -> t.stop());
                updaters.clear();
            }
        }
    }

    /**
     * Gets a list of links non null.
     *
     * @return links not null
     */
    public List<Icon> getIcons() {
        return Arrays.stream(icons)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void show(Player player) {
        player.openInventory(create(player));
    }

    @Override
    public void onOpen(Player player) {
        if (updateInterval > 0)
            startUpdateTask(player);
    }

    protected void startUpdateTask(Player player) {
        UpdaterTask task = new UpdaterTask(updateInterval, () -> onUpdate(player));
        updaters.put(player, task);
        task.start();
    }

    protected void onUpdate(Player player) {
        guis().reprint(player);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Icon icon = icons[event.getSlot()];
        if (icon != null)
            icon.onClick(event);
    }

    @Override
    public void onClose(Player player) {
        UpdaterTask task = updaters.remove(player);
        if (task != null)
            task.stop();
    }

    /**
     * Creates the inventory based on the player who's opening it.
     *
     * @param player the opener
     * @return the inventory created
     */
    public Inventory create(Player player) {
        Inventory inv;
        if (type != null)
            inv = Bukkit.createInventory(null, type, title.resolve(player));
        else
            inv = Bukkit.createInventory(null, size, title.resolve(player));
        for (int slot = 0; slot < icons.length; slot++)
            if (icons[slot] != null)
                inv.setItem(slot, icons[slot].getDisplay().resolve(player));
        return inv;
    }

    /**
     * Loads a gui from id and configuration section.
     *
     * @param id     the id of the gui
     * @param config the config where to load the gui
     * @return the gui created
     */
    @SuppressWarnings("unchecked")
    public static ChestGui deserialize(Plugin plugin, String id, Config config) {
        try {
            return new ChestGui(plugin, config);
        } catch (InvalidConfigurationException e) {
            e.addLocalizer("in gui " + id);
            throw e;
        }
    }

    public static Builder builder(int size) {
        return new Builder(size);
    }

    public static class Builder {
        private final ChestGui gui;

        public Builder(int size) {
            gui = new ChestGui(size, "");
        }

        public Builder(ChestGui gui) {
            this.gui = gui;
        }

        public Builder type(InventoryType type) {
            gui.type = type;
            return this;
        }

        public Builder title(String title) {
            gui.setTitle(title);
            return this;
        }

        public Builder add(ItemStack item) {
            gui.addItem(item);
            return this;
        }

        public Builder add(ItemStack item, Link link) {
            gui.addItem(item, link);
            return this;
        }

        public Builder add(ItemResolver item, Link link) {
            gui.addIcon(Icon.of(item, link));
            return this;
        }

        public Builder add(Supplier<ItemStack> item, Link link) {
            gui.addIcon(Icon.of(item, link));
            return this;
        }

        public Builder add(Icon icon) {
            gui.addIcon(icon);
            return this;
        }

        public Builder addAll(ItemStack... items) {
            gui.addItems(items);
            return this;
        }

        public Builder addAll(Icon... icons) {
            gui.addIcons(icons);
            return this;
        }

        public Builder set(int slot, ItemStack item, Link link) {
            gui.setItem(slot, item, link);
            return this;
        }

        public Builder set(int slot, ItemStack item) {
            gui.setItem(slot, item);
            return this;
        }

        public Builder set(int slot, Icon icon) {
            gui.setIcon(slot, icon);
            return this;
        }

        public Builder set(int[] slots, ItemStack item) {
            gui.setItem(slots, item);
            return this;
        }

        public Builder set(int[] slots, Icon icon) {
            gui.setIcon(slots, icon);
            return this;
        }

        public Builder updateInterval(int interval) {
            gui.updateInterval = interval;
            return this;
        }

        public ChestGui build() {
            return gui;
        }
    }

}
