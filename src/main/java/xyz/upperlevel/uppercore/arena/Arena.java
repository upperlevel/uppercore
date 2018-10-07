package xyz.upperlevel.uppercore.arena;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.arena.events.ArenaAddSignEvent;
import xyz.upperlevel.uppercore.arena.events.ArenaJoinEvent;
import xyz.upperlevel.uppercore.arena.events.ArenaQuitEvent;
import xyz.upperlevel.uppercore.arena.events.ArenaRemoveSignEvent;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.message.Message;
import xyz.upperlevel.uppercore.util.LocUtil;
import xyz.upperlevel.uppercore.util.PlayerData;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * An arena manages the physical Arena, and everything related to the logical side of it (signs, players, states, ecc.).
 * A player can have multiple arenas, managed by {@link ArenaManager}.
 * They can be joined either trough signs or trough the join command.
 *
 * Note: There is a bug where the player entering the arena causes bukkit to load the arena chunks,
 * the lag caused by this launches (in some bukkit-internal unknown way) another click action, that might make
 * the player leave the arena if he's holding the leave item. Because of this a cooldown between a player's join
 * and leave has been implemented so that if the player tries to leave just after the join the operation won't succeed.
 * This behaviour isn't enforced by the {@link Arena#quit(Player)} code himself but by the leave command so that other
 * programs can choose whether to respect this behaviour or not.
 */
public class Arena {
    public static Pattern ARENA_NAME_VALIDATOR = Pattern.compile("^[a-zA-Z_]+[0-9]*");
    private static long JOIN_LEAVE_COOLDOWN = 1000;

    @Getter
    private final String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Location lobby;

    private final Set<Block> signs = new HashSet<>();

    private final Set<Player> players = new HashSet<>();
    private final Map<Player, PlayerMeta> playersData = new HashMap<>();

    @Getter
    private final PhaseManager phaseManager;

    @Getter
    private final PlaceholderRegistry placeholderRegistry;

    public Arena(String id) {
        this.id = id.toLowerCase(Locale.ENGLISH);
        this.name = this.id;

        placeholderRegistry = PlaceholderRegistry.create()
                .set("arena_id", id)
                .set("arena_name", name)
                .set("arena_players", players::size);

        phaseManager = new PhaseManager();
    }

    public boolean isReady() {
        return lobby != null;
    }

    public void addSign(Block block) {
        if (!(block.getState() instanceof Sign)) {
            throw new IllegalArgumentException("The given block is not a sign.");
        }
        signs.add(block);
        updateSign((Sign) block.getState());
        Bukkit.getPluginManager().callEvent(new ArenaAddSignEvent(this, block));
    }

    public void updateSign(Sign sign) {
        sign.setLine(0, id);
        sign.setLine(1, name);
        sign.setLine(2, players.size() + "");
    }

    public boolean removeSign(Block block) {
        if (signs.remove(block)) {
            block.breakNaturally(null);
            Bukkit.getPluginManager().callEvent(new ArenaRemoveSignEvent(this, block));
            return true;
        }
        return false;
    }

    public Set<Block> getSigns() {
        return Collections.unmodifiableSet(signs);
    }

    public void start() {
        if (!isReady()) {
            throw new IllegalStateException("Arena not ready to be started: " + id);
        }
        // This method should be overridden and should be set the first Phase
    }

    public boolean isPlaying() {
        return phaseManager.getPhase() != null;
    }

    public void stop() {
        phaseManager.setPhase(null);
    }

    public boolean join(Player player) {
        if (!isPlaying()) {
            throw new IllegalStateException("Trying to add a player while arena isn't ready.");
        }
        if (!players.add(player)) {
            return false;
        }
        Bukkit.getPluginManager().callEvent(new ArenaJoinEvent(this, player));
        playersData.put(player, new PlayerMeta(System.currentTimeMillis(), PlayerData.extract(player)));
        player.teleport(lobby);
        return true;
    }

    public boolean quit(Player player) {
        if (!isPlaying()) {
            throw new IllegalStateException("Trying to remove a player while arena isn't ready.");
        }
        if (!players.remove(player)) {
            return false;
        }
        playersData.remove(player).preJoinData.apply(player);
        Bukkit.getPluginManager().callEvent(new ArenaQuitEvent(this, player)); // non cancelable
        return true;
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public boolean isJoinLeaveCooldownActive(Player player) {
        PlayerMeta data = playersData.get(player);
        if (data == null) return false;
        return data.joinTime + JOIN_LEAVE_COOLDOWN < System.currentTimeMillis();
    }

    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public void broadcast(String message) {
        players.forEach(p -> p.sendMessage(message));
    }

    public void broadcast(Message message) {
        players.forEach(message::send);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("signs", signs.stream().map(sign -> LocUtil.serialize(sign.getLocation())).collect(Collectors.toList()));
        return data;
    }

    public void deserialize(Map<String, Object> data) {
        Config cfg = Config.from(data);
        name = cfg.getString("name", id);
        for (Config signCfg : cfg.getConfigList("signs", new ArrayList<>())) {
            Block sign = LocUtil.deserialize(signCfg).getBlock();
            if (sign.getState() instanceof Sign) {
                signs.add(sign);
            }
        }
    }

    public static boolean isValidName(String name) {
        return ARENA_NAME_VALIDATOR.matcher(name).matches();
    }

    public static void configure(Config cfg) {
        JOIN_LEAVE_COOLDOWN = cfg.getLongRequired("join-leave-cooldown");
    }

    @Data
    @AllArgsConstructor
    private static class PlayerMeta {
        long joinTime;// Used in cooldown calculations
        PlayerData preJoinData;// Before-join data (inventory, exp and everything)
    }
}
