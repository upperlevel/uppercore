package xyz.upperlevel.uppercore.arena.events;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import xyz.upperlevel.uppercore.arena.Arena;

public class ArenaRemoveSignEvent extends ArenaEvent {
    @Getter
    private final Block block;

    @Getter
    private final Sign sign;

    public ArenaRemoveSignEvent(Arena arena, Block block) {
        super(arena);
        this.block = block;
        this.sign = (Sign) block.getState();
    }
}
