package xyz.upperlevel.uppercore.gui;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.List;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public abstract class SimpleGui implements Gui {
    protected Inventory buffer;
    protected List<Icon> icons;
    protected List<Icon> footer;

    @Override
    public void onClick(InventoryClickEvent event) {
        if (buffer == null)
            throw new IllegalStateException("Cannot click a non-existant inventory");
        int slot = event.getSlot();
        if (slot < 0)
            return;
        int footerStart = buffer.getSize() - footer.size() - 1;
        Icon clicked;
        if(slot > footerStart) {
            clicked = footer.get(slot - footerStart - 1);
        } else if(slot < buffer.getSize()) {
            clicked = icons.get(slot);
        } else return;

        if (clicked != null)
            clicked.getClick().run((Player) event.getWhoClicked());
    }

    @Override
    public void show(Player player) {
        if (buffer == null)
            buffer = build();
        player.openInventory(buffer);
    }

    @Override
    public void onOpen(Player player) {
    }

    @Override
    public void onClose(Player player) {
    }

    @SuppressWarnings("unchecked")
    public void refreshAll() {
        if (buffer != null) {
            List<Player> viewers = (List)buffer.getViewers();
            if (!viewers.isEmpty()) {
                buffer = build();
                for(int i = viewers.size() - 1; i >= 0; i--) {
                    guis().reprint(viewers.get(i));
                }
            } else buffer = null;
        }
    }

    protected Inventory build() {
        icons = buildBody();
        footer = buildFooter();
        Inventory inv = Bukkit.createInventory(null, GuiSize.min(icons.size() + footer.size()), buildTitle());

        int i = 0;
        for (Icon icon : icons)
            inv.setItem(i++, icon == null ? null : icon.getDisplay());
        i = inv.getSize() - footer.size();
        for(Icon icon : footer) {
            inv.setItem(i++, icon == null ? null : icon.getDisplay());
        }
        return inv;
    }

    public abstract String buildTitle();

    public abstract List<Icon> buildBody();

    public List<Icon> buildFooter() {
        return Collections.emptyList();
    }
}
