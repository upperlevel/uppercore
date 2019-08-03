package xyz.upperlevel.uppercore.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.util.LocUtil;

import java.util.HashMap;
import java.util.Map;

public class Arena {
    @Getter
    private ArenaContainer container;

    @Getter
    @Setter
    private Location lobby;

    @ConfigConstructor
    public Arena(
            @ConfigProperty("lobby") Location lobby
    ) {
        this.lobby = lobby;
    }

    void setContainer(ArenaContainer container) {
        this.container = container;
    }

    /**
     * If the arena contains entities (like shop villagers or staff),
     * this method must be used to spawn them and will be called
     * when the arena is enabled.
     */
    public void decorate() {
    }

    /**
     * If the arena contains entities (like shop villagers or stuff),
     * this method must be used to de-spawn them and will be called
     * before saving it.
     */
    public void vacate() {
    }

    /**
     * Checks whether the arena is ready to be set enabled.
     *
     * @return true if all data are set.
     */
    public boolean isReady() {
        return lobby != null;
    }

    /**
     * Translates all arena data to a map.
     *
     * @return a Map containing the serialized data.
     */
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("location", LocUtil.serialize(lobby));
        }};
    }
}
