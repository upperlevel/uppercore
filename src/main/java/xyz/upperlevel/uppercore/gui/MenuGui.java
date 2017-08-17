package xyz.upperlevel.uppercore.gui;

import com.google.common.collect.Iterators;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static xyz.upperlevel.uppercore.Uppercore.guis;

public abstract class MenuGui implements Gui {
    public static final int ITEMS_PER_PAGE = GuiSize.DOUBLE.size();
    public static final ItemStack DEF_ARROW_NEXT = GuiUtil.head("MHF_ArrowRight", "Next");
    public static final ItemStack DEF_ARROW_BACK = GuiUtil.head("MHF_ArrowLeft", "Back");

    private Map<Player, Integer> pageRegistry = new HashMap<>();
    private Map<Integer, Icon> iconTracer;
    private int viewers = 0;
    private List<Inventory> pages;
    private List<Icon> headers;
    private List<Icon> icons;
    private List<Icon> footers;


    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if(slot < 0)
            return;
        Player clicker = (Player) event.getWhoClicked();
        int page = pageRegistry.getOrDefault(clicker, 0);
        Icon clicked = getIcon(page, slot);
        if(clicked != null)
            clicked.getClick().run(clicker);
    }

    @Override
    public void show(Player player) {
        if(pages == null)
            buildPages();
        int page = pageRegistry.getOrDefault(player, 0);
        if(page >= pages.size())
            page = pages.size() - 1;
        Inventory inv = pages.get(page);
        player.openInventory(inv);
    }

    @Override
    public void onOpen(Player player) {
        viewers++;
    }

    @Override
    public void onClose(Player player) {
        viewers--;
        pageRegistry.remove(player);
    }

    public void refreshAll() {
        if (pages != null) {
            if (viewers > 0) {
                List<Player> oldViewers = getViewers().collect(Collectors.toList());
                buildPages();
                for (Player p : oldViewers)
                    guis().reprint(p);
            } else pages = null;
        }
    }

    @SuppressWarnings("unchecked")
    public Stream<Player> getViewers() {
        if(pages == null || viewers == 0)
            return Stream.empty();
        return pages.stream().flatMap(i -> (Stream<Player>)(Stream)i.getViewers().stream());
    }

    public void buildPages() {
        headers = buildHeader();
        icons = buildBody();
        footers = buildFooter();
        iconTracer = new HashMap<>();
        Icon back = Icon.of(getArrowBack(), this::back);
        Icon next = Icon.of(getArrowNext(), this::next);

        pages = new ArrayList<>();

        Iterator<Icon> i = Iterators.concat(headers.iterator(), icons.iterator());
        int remaining = headers.size() + icons.size() + footers.size();
        int pageIndex = 0;
        do {
            boolean hasPrevious = pageIndex > 0;
            boolean hasNext = remaining > ITEMS_PER_PAGE;

            int realSpace = ITEMS_PER_PAGE;
            if(hasPrevious)
                realSpace--;
            if(hasNext)
                realSpace--;


            int currSize, currChestSize;
            if(remaining > realSpace) {
                currSize = realSpace;
                currChestSize = ITEMS_PER_PAGE;
                remaining -= realSpace;
                if(remaining < footers.size()) {
                    currSize -= (footers.size() - remaining);
                    remaining = footers.size();
                }
            } else {
                currSize = remaining - footers.size();
                currChestSize = GuiSize.min(remaining);
                remaining = 0;
            }
            Inventory inv = Bukkit.createInventory(null, currChestSize, buildTitle(pageIndex));
            pages.add(inv);
            int offset = 0;
            if(hasPrevious) {
                inv.setItem(0, back.getDisplay());
                setIcon(pageIndex, 0, back);
                offset++;
            }
            if(hasNext) {
                int index = inv.getSize() - 1;
                inv.setItem(index, next.getDisplay());
                setIcon(pageIndex, index, next);
            }

            for(int j = 0; j < currSize; j++) {
                Icon icon = i.next();
                inv.setItem(offset + j, icon.getDisplay());
                setIcon(pageIndex, offset + j, icon);
            }
            pageIndex++;
        } while(remaining > 0);
        pageIndex--;
        Inventory inv = pages.get(pageIndex);
        int j = inv.getSize() - footers.size();
        for(Icon icon : footers) {
            inv.setItem(j, icon.getDisplay());
            setIcon(pageIndex, j, icon);
            j++;
        }
    }

    protected void next(Player player) {
        pageRegistry.compute(player, (p, i) -> i == null ? 1 : i + 1);
        guis().reprint(player);
    }

    public ItemStack getArrowNext() {
        return DEF_ARROW_NEXT;
    }

    protected void back(Player player) {
        pageRegistry.compute(player, (p, i) -> i - 1);
        guis().reprint(player);
    }

    public ItemStack getArrowBack() {
        return DEF_ARROW_BACK;
    }

    protected void setIcon(int page, int index, Icon icon) {
        iconTracer.put(page * ITEMS_PER_PAGE + index, icon);
    }

    public Icon getIcon(int page, int index) {
        return iconTracer.get(page * ITEMS_PER_PAGE + index);
    }

    public abstract String buildTitle(int page);

    public List<Icon> buildHeader() {
        return emptyList();
    }

    public abstract List<Icon> buildBody();

    public List<Icon> buildFooter() {
        return emptyList();
    }
}