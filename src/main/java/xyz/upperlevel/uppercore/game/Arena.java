package xyz.upperlevel.uppercore.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.util.LocUtil;

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

    @Getter
    private Phase currentPhase;

    @Getter
    private boolean playing;

    public Arena(String id) {
        this.id = id.toLowerCase(Locale.ENGLISH);
        this.name = this.id;
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
        Bukkit.getPluginManager().callEvent(new ArenaEvent.AddSign(this, block, (Sign) block.getState()));
    }

    public void updateSign(Sign sign) {
        sign.setLine(0, id);
        sign.setLine(1, name);
        sign.setLine(2, players.size() + "");
    }

    public boolean removeSign(Block block) {
        if (signs.remove(block)) {
            block.breakNaturally(null);
            Bukkit.getPluginManager().callEvent(new ArenaEvent.RemoveSign(this, block, (Sign) block.getState()));
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
        playing = true;
    }

    public void stop() {
        setPhase(null);
        playing = false;
    }

    public void setPhase(Phase phase) {
        Phase old = currentPhase;
        if (old != null) {
            old.onDisable(phase);
        }
        currentPhase = phase;
        if (phase != null) {
            phase.onEnable(old);
        }
    }

    public boolean join(Player player) {
        if (!playing) {
            throw new IllegalStateException("Trying to add a player while arena isn't ready.");
        }
        if (!players.add(player)) {
            return false;
        }
        Bukkit.getPluginManager().callEvent(new ArenaEvent.PlayerJoin(this, player));
        player.teleport(lobby);
        return true;
    }

    public boolean quit(Player player) {
        if (!playing) {
            throw new IllegalStateException("Trying to remove a player while arena isn't ready.");
        }
        if (players.remove(player)) {
            Bukkit.getPluginManager().callEvent(new ArenaEvent.PlayerQuit(this, player));
            return true;
        }
        return false;
    }

    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(players);
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
