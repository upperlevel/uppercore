package xyz.upperlevel.uppercore.arena;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.util.WorldUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {
    public static final File ARENAS_FOLDER = new File(Uppercore.getPlugin().getDataFolder(), "arenas");
    public static ArenaManager instance = new ArenaManager();

    private final Map<String, ArenaContainer> byId = new HashMap<>();

    //================================================================================
    // Loading

    private void load(Phase lobbyPhase) {
        File[] files = ARENAS_FOLDER.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String id = file.getName().substring(0, file.getName().lastIndexOf('.'));

            // First loads the arena world.
            String world = ArenaContainer.getSignature(id);
            WorldUtil.createEmptyWorld(world);

            // Then loads arena's data.
            ConfigurationSection section = YamlConfiguration.loadConfiguration(file).getConfigurationSection(id);
            ArenaContainer arena = ArenaContainer.deserialize(id, section);
            register(arena);

            // If the arena is ready, sets to enabled.
            if (arena.isReady()) {
                arena.setEnabled(true, lobbyPhase);
            }
        }
    }

    //================================================================================
    // Registering

    public void register(ArenaContainer arena) {
        byId.put(arena.getId(), arena);
    }

    public ArenaContainer get(String id) {
        return byId.get(id);
    }

    public ArenaContainer get(World world) {
        for (ArenaContainer arena : byId.values()) {
            if (world.equals(arena.getWorld())) {
                return arena;
            }
        }
        return null;
    }

    public ArenaContainer get(Player player) {
        for (ArenaContainer arena : byId.values()) {
            if (arena.getPlayers().contains(player)) {
                return arena;
            }
        }
        return null;
    }

    public void destroy(ArenaContainer arena) {
        byId.remove(arena.getId());
        if (arena.getWorld() != null) {
            arena.destroy();
        }
    }

    public Collection<ArenaContainer> getArenas() {
        return byId.values();
    }

    public void unload() {
        for (ArenaContainer arena : byId.values()) {
            arena.unload();
        }
    }

    public static ArenaManager get() {
        return instance;
    }
}
