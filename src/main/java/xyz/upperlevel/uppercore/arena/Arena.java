package xyz.upperlevel.uppercore.arena;

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
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;
import xyz.upperlevel.uppercore.placeholder.message.Message;
import xyz.upperlevel.uppercore.util.LocUtil;
import xyz.upperlevel.uppercore.util.PlayerData;

import java.util.*;
import java.util.stream.Collectors;

public class Arena {
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
    private final Map<Player, PlayerData> playersData = new HashMap<>(); // holds player data before join

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
        playersData.put(player, PlayerData.extract(player));
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
        playersData.remove(player).apply(player);
        Bukkit.getPluginManager().callEvent(new ArenaQuitEvent(this, player)); // non cancelable
        return true;
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player);
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
        return name.matches("^[a-zA-Z_]+[0-9]*");
    }
}
