package xyz.upperlevel.uppercore.game;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaEvent extends Event {
    @Getter
    private final Arena arena;

    public ArenaEvent(Arena arena) {
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    public static class Create extends ArenaEvent {
        public Create(Arena arena) {
            super(arena);
        }
    }

    public static class Destroy extends ArenaEvent {
        public Destroy(Arena arena) {
            super(arena);
        }
    }

    public static class AddSign extends ArenaEvent {
        @Getter
        private final Block block;

        @Getter
        private final Sign sign;

        public AddSign(Arena arena, Block block, Sign sign) {
            super(arena);
            this.block = block;
            this.sign = sign;
        }
    }

    public static class RemoveSign extends ArenaEvent {
        @Getter
        private final Block block;

        @Getter
        private final Sign sign;

        public RemoveSign(Arena arena, Block block, Sign sign) {
            super(arena);
            this.block = block;
            this.sign = sign;
        }
    }

    public static class PlayerJoin extends ArenaEvent {
        @Getter
        private final Player player;

        public PlayerJoin(Arena arena, Player player) {
            super(arena);
            this.player = player;
        }
    }

    public static class PlayerQuit extends ArenaEvent {
        @Getter
        private final Player player;

        public PlayerQuit(Arena arena, Player player) {
            super(arena);
            this.player = player;
        }
    }
}
