package xyz.upperlevel.uppercore.game.arena;

import java.util.HashMap;
import java.util.Map;

public class ArenaManager {
    private Map<String, Arena> arenas = new HashMap<>();

    public ArenaManager() {
    }

    public void register(Arena arena) {
        arenas.put(arena.getId(), arena);
    }

    public Arena getArena(String id) {
        return arenas.get(id);
    }
}
