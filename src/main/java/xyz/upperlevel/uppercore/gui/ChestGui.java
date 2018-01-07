package xyz.upperlevel.uppercore.gui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;
import xyz.upperlevel.uppercore.gui.link.Link;
import xyz.upperlevel.uppercore.itemstack.ItemResolver;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;
import xyz.upperlevel.uppercore.task.UpdaterTask;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class ChestGui implements Gui {
    @Getter
    private PlaceholderValue<String> title;
    @Getter
    private int size;
    @Getter
    private InventoryType type;
    private ConfigIcon[] icons;
    @Getter
    private int updateInterval = -1;
    private final Map<Player, UpdaterTask> updaters = new HashMap<>();

    /**
     * Initializes the gui by copying another gui
     *
     * @param gui the gui to copy
     */
    public ChestGui(ChestGui gui) {
        this.title = gui.title;
        this.size = gui.size;
        this.type = gui.type;
        this.icons = Arrays.copyOf(icons, icons.length);
        setUpdateInterval(gui.updateInterval);
    }

    /**
     * Initializes the gui by its size and title.
     *
     * @param size  the size of the gui
     * @param title the title of the gui
     */
    public ChestGui(int size, PlaceholderValue<String> title) {
        this.title = title;
        this.size = size;
        this.icons = new ConfigIcon[size];
        onSetup();
    }

    /**
     * Initializes the gui by its type and title.
     *
     * @param type  the type of the gui
     * @param title the title of the gui
     */
    public ChestGui(InventoryType type, PlaceholderValue<String> title) {
        this.type = type;
        this.title = title;
        this.icons = new ConfigIcon[type.getDefaultSize()];
        onSetup();
    }

    /**
     * Initializes the gui by its size or type and title.
     *
     * @param size  the size of the gui
     * @param title the title of the gui
     */
    public ChestGui(int size, InventoryType type, PlaceholderValue<String> title) {
        this.title = title;
        this.size = size;
        this.type = type;
        if(size >= 0) {
            this.icons = new ConfigIcon[size];
            if(type != null)
                throw new IllegalArgumentException("Cannot have both size and type present!");
        } else
            this.icons = new ConfigIcon[type.getDefaultSize()];
        onSetup();
    }

    @SuppressWarnings("unchecked")
    protected ChestGui(Plugin plugin, Config config) {
        if (config.has("type")) {
            type = config.getEnum("type", InventoryType.class);
            icons = new ConfigIcon[type.getDefaultSize()];
        } else if (config.has("size")) {
            size = config.getInt("size");
            if (size % 9 != 0) {
                plugin.getLogger().warning("In a gui: size must be a multiple of 9");
                size = GuiSize.min(size);
            }
            icons = new ConfigIcon[size];
        } else
            throw new InvalidConfigException("Both 'type' and 'size' are empty!");
        updateInterval = config.getInt("update-interval", -1);
        title = config.getMessageStrRequired("title");
        Collection<Map<String, Object>> iconsData = (Collection<Map<String, Object>>) config.getCollection("icons");
        if (iconsData != null) {
            for (Map<String, Object> data : iconsData) {
                ConfigIcon item = ConfigIcon.deserialize(plugin, Config.wrap(data));
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

    public String solveTitle(Player player) {
        return title.resolve(player);
    }

    /**
     * Gets the item at the given slot.
     *
     * @param slot the slot to getHistories the item in
     */
    public ConfigIcon getIcon(int slot) {
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
        return addIcon(new ConfigIcon(item, link));
    }

    /**
     * Adds an item in the first slot empty.
     *
     * @param icon the item to addIcons
     */
    public boolean addItem(ItemStack icon) {
        return addIcon(new ConfigIcon(icon));
    }

    public boolean addIcon(ConfigIcon icon) {
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

    public boolean addIcons(ConfigIcon... icons) {
        for (ConfigIcon icon : icons)
            if (!addIcon(icon))
                return false;
        return true;
    }

    private void setItem(int slot, ItemStack item, Link link) {
        setIcon(slot, new ConfigIcon(item, link));
    }

    public void setItem(int slot, ItemStack item) {
        setIcon(slot, new ConfigIcon(item));
    }

    /**
     * Sets the given item at the given slot.
     *
     * @param slot the slot where to give the item
     * @param icon the item to give
     */
    public void setIcon(int slot, ConfigIcon icon) {
        icons[slot] = icon;
    }

    public void setItem(int[] slots, ItemStack item) {
        setIcon(slots, new ConfigIcon(item));
    }

    public void setIcon(int[] slots, ConfigIcon icon) {
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
    public List<ConfigIcon> getIcons() {
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
        ConfigIcon icon = icons[event.getSlot()];
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
            inv = Bukkit.createInventory(null, type, solveTitle(player));
        else
            inv = Bukkit.createInventory(null, size, solveTitle(player));
        for (int slot = 0; slot < icons.length; slot++)
            if (icons[slot] != null)
                inv.setItem(slot, icons[slot].getDisplay().resolve(player));
        return inv;
    }

    public ChestGui copy() {
        return new ChestGui(this);
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
        } catch (InvalidConfigException e) {
            e.addLocation("in gui " + id);
            throw e;
        }
    }

    public static Builder builder(int size) {
        return new Builder(size);
    }

    public static class Builder {
        private final ChestGui gui;

        public Builder(int size) {
            gui = new ChestGui(size, PlaceholderValue.fake(""));
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
            gui.addIcon(ConfigIcon.of(item, link));
            return this;
        }

        public Builder add(Supplier<ItemStack> item, Link link) {
            gui.addIcon(ConfigIcon.of(item, link));
            return this;
        }

        public Builder add(ConfigIcon icon) {
            gui.addIcon(icon);
            return this;
        }

        public Builder addAll(ItemStack... items) {
            gui.addItems(items);
            return this;
        }

        public Builder addAll(ConfigIcon... icons) {
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

        public Builder set(int slot, ConfigIcon icon) {
            gui.setIcon(slot, icon);
            return this;
        }

        public Builder set(int[] slots, ItemStack item) {
            gui.setItem(slots, item);
            return this;
        }

        public Builder set(int[] slots, ConfigIcon icon) {
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
