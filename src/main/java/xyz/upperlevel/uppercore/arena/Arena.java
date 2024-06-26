package xyz.upperlevel.uppercore.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.arena.event.ArenaJoinEvent;
import xyz.upperlevel.uppercore.arena.event.ArenaQuitEvent;
import xyz.upperlevel.uppercore.arena.event.JoinSignUpdateEvent;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.message.Message;
import xyz.upperlevel.uppercore.util.Dbg;
import xyz.upperlevel.uppercore.util.LocUtil;
import xyz.upperlevel.uppercore.util.PlayerRestorer;
import xyz.upperlevel.uppercore.util.WorldUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Locale.ENGLISH;
import static xyz.upperlevel.uppercore.Uppercore.getPlugin;

public class Arena {
    // The player can manually leave only after a certain delay after joining the arena.
    // This is done in order to bypass a Spigot bug where if the player has right-clicked a sign, it could happen that right-clicks even the
    // quit-bed and make him quit the arena soon after it has joined it.
    public static final long CAN_MANUALLY_LEAVE_AFTER_JOIN_DELAY = 2 * 1000;

    public static final PlayerRestorer playerRestorer = new PlayerRestorer();

    // Config
    public static String mainArenaName;
    public static String mode;
    public static OnQuitHandler onQuitHandler;

    //================================================================================
    // Properties
    //================================================================================

    @Getter
    private final String id;

    @Getter
    @Setter
    private String name;

    @Getter
    private final String signature;

    @Getter
    private final World world;

    @Getter
    private final PlaceholderRegistry<?> placeholders;

    @Getter
    private boolean enabled;

    @Getter
    private final PhaseManager phaseManager;

    @Getter
    @Setter
    private Location lobby;

    private final Map<Block, Sign> joinSignByBlock = new HashMap<>();

    @Getter
    private final Map<Player, Long> joinedAtByPlayer = new HashMap<>();

    public Arena(String id) {
        this.id = id.toLowerCase(ENGLISH);
        this.name = id;
        this.signature = getSignature(id);
        this.world = Bukkit.getWorld(signature);

        this.phaseManager = new PhaseManager(signature);
        phaseManager.onEnable(null); // Just for log purposes.

        if (world == null) {
            throw new IllegalStateException("Arena's world isn't loaded, or doesn't exist: " + signature);
        }
        this.placeholders = createPlaceholders();
    }

    @ConfigConstructor
    public Arena(
            @ConfigProperty("id") String id,
            @ConfigProperty("name") Optional<String> name,
            @ConfigProperty("lobby") Location lobby,
            @ConfigProperty("join-signs") List<Location> joinSigns) {
        this(id);
        name.ifPresent(s -> this.name = s);
        this.lobby = lobby;
        for (Location sign : joinSigns) {
            Block block = sign.getBlock();
            if (!(block.getState() instanceof Sign)) {
                Uppercore.logger().warning("An invalid sign was saved for arena: " + id);
                continue;
            }
            addJoinSign(block);
        }
    }

    /**
     * Creates the placeholders for this arena.
     */
    protected PlaceholderRegistry<?> createPlaceholders() {
        return PlaceholderRegistry.create()
                .set("arena_id", () -> id)
                .set("arena_name", this::getName)
                .set("arena_signature", () -> signature)
                .set("arena_phase", () -> this.getPhaseManager().getPhaseName())
                .set("players", () -> Integer.toString(players.size()));
    }

    // ------------------------------------------------------------------------------------------------ Preload

    public void preload() {
        Uppercore.logger().info(String.format("Preloading the arena: %s. This will make players join it faster.", getName()));

        world.loadChunk(lobby.getChunk());
        getJoinSigns().forEach(joinSign -> world.loadChunk(joinSign.getChunk()));
    }

    // ------------------------------------------------------------------------------------------------ Join signs

    public void updateJoinSigns(Collection<Sign> joinSigns) {
        JoinSignUpdateEvent event = new JoinSignUpdateEvent(this, joinSigns);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void updateJoinSigns() {
        //Dbg.pf("[%s] Dispatching join-signs update signal...", id);
        updateJoinSigns(joinSignByBlock.values());
    }

    /**
     * Adds a join sign to the arena.
     */
    public void addJoinSign(Block block) {
        if (!(block.getState() instanceof Sign))
            throw new IllegalArgumentException("`block` must have a Sign block-state");

        joinSignByBlock.put(block, (Sign) block.getState());
        updateJoinSigns(getJoinSigns());
    }

    public Sign getJoinSign(Block block) {
        return joinSignByBlock.get(block);
    }

    /**
     * Removes a join sign from the arena.
     */
    public boolean removeJoinSign(Block block) {
        boolean result = joinSignByBlock.remove(block) != null;
        block.breakNaturally();
        return result;
    }

    /**
     * A list of all join signs present for this arena.
     */
    public Collection<Sign> getJoinSigns() {
        return joinSignByBlock.values();
    }

    // ------------------------------------------------------------------------------------------------ Serialization

    public List<String> checkProblems() {
        var problems = new ArrayList<String>();
        if (lobby == null) problems.add("Lobby not set");
        return problems;
    }

    public boolean isReady() {
        return checkProblems().isEmpty();
    }

    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", id);
            put("name", name);
            put("lobby", LocUtil.serialize(lobby));
            put("join-signs", joinSignByBlock.keySet().stream()
                    .map(sign -> LocUtil.serialize(sign.getLocation()))
                    .collect(Collectors.toList())
            );
        }};
    }

    /**
     * Saves the arena world and writes data to arena's yml file.
     */
    public void save() throws IOException {
        world.save();

        ArenaManager.ARENAS_FOLDER.mkdirs();
        File file = new File(ArenaManager.ARENAS_FOLDER, id + ".yml");
        file.createNewFile();

        new Yaml().dump(serialize(), new FileWriter(file));
        Dbg.pf("[%s] Saved to: %s", id, file.getPath());
    }

    /**
     * This method should spawn all the needed entities within this arena.
     * It's called when the arena is enabled.
     */
    public void decorate() {
    }

    /**
     * This method must remove all the entities within this arena.
     * It's called when the arena is disabled.
     */
    public void vacate() {
    }

    /**
     * Sets arena enabled or not.
     * When enabled, all needed entities are spawned and the arena is set in LobbyPhase.
     */
    public void setEnabled(boolean enabled) {
        if (!isReady()) {
            throw new IllegalStateException("Arena can't be enabled, without it being ready.");
        }
        this.enabled = enabled;
        if (enabled) {
            decorate();
            phaseManager.setPhase(getEntryPhase());
        } else {
            for (Player player : new ArrayList<>(players)) {
                quit(player, ArenaQuitEvent.ArenaQuitReason.ARENA_ABORT);
            }
            phaseManager.setPhase(null);
            vacate();
        }
    }

    /**
     * Checks whether the arena is playable.
     *
     * If an arena is playable, a newly joined player will be
     * able to play within the arena. If not playable he'll be
     * kicked out or casted to spectator.
     */
    public boolean isPlayable() {
        return true;
    }

    public Phase getEntryPhase() {
        return null;
    }

    /**
     * Removes all the entities spawned and unloads its world (without saving).
     */
    public void unload() {
        vacate();
        for (Player player : world.getPlayers()) {
            quit(player, ArenaQuitEvent.ArenaQuitReason.ARENA_ABORT);
        }
        getPhaseManager().setPhase(null);
        WorldUtil.unloadWorld(world);
    }

    /**
     * Destroys the arena from the face of earth.
     * This method shouldn't be called directly but through ArenaManager.
     */
    public void destroy() {
        unload();
        WorldUtil.deleteWorld(world);
        new File(ArenaManager.ARENAS_FOLDER, id + ".yml").delete();
    }

    public static String getSignature(String id) {
        return getPlugin().getName() + "." + id.toLowerCase(ENGLISH);
    }

    public static <A extends Arena> A create(Class<A> arenaClass, String id) {
        WorldUtil.createEmptyWorld(getSignature(id));
        try {
            return arenaClass.getConstructor(String.class).newInstance(id);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("An Arena implementation must always have a constructor like ArenaImpl(String id).");
        }
    }

    //================================================================================
    // Game
    //================================================================================

    private final List<Player> players = new ArrayList<>();

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public void broadcast(Message message, PlaceholderRegistry<?> placeholders) {
        message.broadcast(players, placeholders);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    private World getFreeWorld() {
        World free = null;
        for (World world : Bukkit.getWorlds()) {
            if (ArenaManager.get().get(world) == null) {
                // Gets the first free world, prefers normal worlds to nether/end.
                if (free == null || free.getEnvironment() != World.Environment.NORMAL) {
                    free = world;
                }
            }
        }
        return free;
    }

    public boolean join(Player player) {
        if (!enabled) {
            throw new IllegalStateException("A disabled arena can't be joined.");
        }
        if (ArenaManager.get().get(player) != null) {
            throw new IllegalStateException(String.format("The player %s is already inside another arena.", player.getName()));
        }

        // Takes an image of the player before calling the join event that usually setups the player.
        PlayerRestorer.Image image = playerRestorer.screen(player);

        // Bukkit-event
        ArenaJoinEvent event = new ArenaJoinEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        // Method-event
        if (phaseManager.onJoin(player)) {
            return false;
        }
        players.add(player);
        playerRestorer.remember(image); // If the player actually joined, we can remember the image took.

        updateJoinSigns();

        joinedAtByPlayer.put(player, System.currentTimeMillis());

        return true;
    }

    public boolean quit(Player player, ArenaQuitEvent.ArenaQuitReason reason) {
        if (!players.contains(player)) {
            return false;
        }

        // If the player is trying to quit the arena through a command but not enough time is passed then blocks it.
        if (reason == ArenaQuitEvent.ArenaQuitReason.COMMAND && joinedAtByPlayer.getOrDefault(player, 0L) + CAN_MANUALLY_LEAVE_AFTER_JOIN_DELAY > System.currentTimeMillis()) {
            Dbg.pf("%s is trying to quit the arena %s soon after having joined it", player.getName(), getName());
            return false;
        }

        // Bukkit-event
        ArenaQuitEvent event = new ArenaQuitEvent(player, this, reason);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        // Method-event
        if (phaseManager.onQuit(player)) {
            return false;
        }

        if (!players.remove(player)) {
            return false;
        }
        // Here the player has quit for sure.
        playerRestorer.restore(player);
        updateJoinSigns();
        if (onQuitHandler != null) {
            onQuitHandler.handle(player);
        }
        return true;
    }

    public static void loadConfig(Config cfg) {
        mode = cfg.getString("mode");

        if (mode.equals("bungee")) onQuitHandler = new OnQuitHandler.Bungee();
        else if (mode.equals("local")) onQuitHandler = new OnQuitHandler.Local();
        else throw new IllegalStateException(String.format("Invalid Arena mode: %s", mode));

        mainArenaName = cfg.getString("main-arena");
    }
}
