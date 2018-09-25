package xyz.upperlevel.uppercore.arena.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Cancellable;
import xyz.upperlevel.uppercore.arena.Arena;

public class ArenaAddSignEvent extends ArenaEvent implements Cancellable {
    @Getter
    private final Block block;

    @Getter
    private final Sign sign;

    @Getter
    @Setter
    private boolean cancelled;

    public ArenaAddSignEvent(Arena arena, Block block) {
        super(arena);
        this.block = block;
        this.sign = (Sign) block.getState();
    }
}
