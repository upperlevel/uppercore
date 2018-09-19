package xyz.upperlevel.uppercore.game;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.Config;

import java.util.*;

public class ArenaManager implements Listener {
    private final Map<String, Arena> arenasById = new HashMap<>();
    private final Map<Block, Arena> arenasBySign = new HashMap<>();

    public ArenaManager(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void dismiss() {
        HandlerList.unregisterAll(this);
    }

    public boolean register(@NonNull Arena arena) {
        if (!arenasById.containsKey(arena.getId())) {
            arenasById.put(arena.getId(), arena);
            Bukkit.getPluginManager().callEvent(new ArenaEvent.Create(arena));
            return true;
        }
        return false;
    }

    public boolean unregister(String id) {
        Arena arena = arenasById.remove(id);
        if (arena != null) {
            Bukkit.getPluginManager().callEvent(new ArenaEvent.Destroy(arena));
            return true;
        }
        return false;
    }

    public Arena getArena(String id) {
        return arenasById.get(id);
    }

    public Arena getArenaBySign(Block block) {
        return arenasBySign.get(block);
    }

    public Collection<Arena> getArenas() {
        return arenasById.values();
    }

    @EventHandler
    protected void onAddSign(ArenaEvent.AddSign e) {
        arenasBySign.put(e.getBlock(), e.getArena());
    }

    @EventHandler
    protected void onBlockBreak(BlockBreakEvent e) {
        Arena arena = arenasBySign.get(e.getBlock());
        if (arena != null) {
            arena.removeSign(e.getBlock());
            e.getPlayer().sendMessage(ChatColor.RED + "You just broke an arena sign!");
        }
    }

    @EventHandler
    protected void onRemoveSign(ArenaEvent.RemoveSign e) {
        arenasBySign.remove(e.getBlock(), e.getArena());
    }

    public List<Map<String, Object>> serialize() {
        List<Map<String, Object>> serialized = new ArrayList<>();
        for (Arena arena : arenasById.values()) {
            serialized.add(arena.serialize());
        }
        return serialized;
    }

    public static ArenaManager load(Plugin plugin, List<Map<String, Object>> arenas, ArenaFactory arenaFactory) {
        ArenaManager arenaManager = new ArenaManager(plugin);
        for (Map<String, Object> arena : arenas) {
            Config cfg = Config.wrap(arena);
            arenaManager.register(arenaFactory.load(cfg.getStringRequired("id"), arena));
        }
        return arenaManager;
    }
}
