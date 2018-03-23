package xyz.upperlevel.uppercore.game.arena;

import java.util.HashMap;
import java.util.Map;

public class ArenaManager {
    private Map<String, Arena> byId = new HashMap<>();

    public ArenaManager() {
    }

    public void register(Arena arena) {
        byId.put(arena.getId(), arena);
    }

    public Arena get(String id) {
        return byId.get(id);
    }
}
