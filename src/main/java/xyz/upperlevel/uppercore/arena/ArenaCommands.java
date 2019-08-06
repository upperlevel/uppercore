package xyz.upperlevel.uppercore.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.WithOptional;

import java.io.IOException;
import java.util.Collection;

import static org.bukkit.ChatColor.*;

public class ArenaCommands {
    private final Class<? extends Arena> arenaClass;

    public ArenaCommands(Class<? extends Arena> arenaClass) {
        this.arenaClass = arenaClass;
    }

    // ================================================================================
    // arena create
    // ================================================================================

    @AsCommand(description = "Creates a new arena.")
    public void create(Player player, String id) {
        if (ArenaManager.get().get(id) != null) {
            player.sendMessage(RED + "Arena already exists for: " + id);
        }

        Arena arena = Arena.create(arenaClass, id);
        ArenaManager.get().register(arena);

        Location to = new Location(arena.getWorld(), 0, 74, 0);
        to.clone().subtract(0, 4, 0).getBlock().setType(Material.STONE);
        player.teleport(to);
        player.setFlying(false);

        player.sendMessage(GREEN + "Arena created: " + id);
        player.sendMessage(GRAY + "Paste your map's schematic here, or build it from scratch!");
        player.sendMessage(GRAY + "Ensure you've set all arena parameters with: " + YELLOW + "/bw arena check");
        player.sendMessage(GRAY + "When you're done type: " + YELLOW + "/bw arena save");
    }

    // ================================================================================
    // arena delete
    // ================================================================================

    @AsCommand(description = "Deletes an arena.")
    public void delete(Player player, @WithOptional Arena arena) {
        if (arena == null) {
            World world = player.getWorld();
            arena = ArenaManager.get().get(world);
            if (arena == null) {
                player.sendMessage(RED + "The world " + world.getName() + " doesn't hold any arena.");
                return;
            }
        }
        ArenaManager.get().destroy(arena);
        player.sendMessage(GREEN + "Arena removed: " + arena.getId());
    }

    // ================================================================================
    // arena edit
    // ================================================================================

    @AsCommand(description = "Edits an arena.")
    public void edit(Player player, Arena arena) {
        player.teleport(arena.getLobby() != null ? arena.getLobby() : new Location(arena.getWorld(), 0, 64, 0));
    }

    // ================================================================================
    // arena toggle
    // ================================================================================

    @AsCommand(description = "Toggles the state of an arena.")
    public void toggle(Player player) {
        Arena arena = ArenaManager.get().get(player.getWorld());
        if (arena == null) {
            player.sendMessage(RED + "This world does not hold any arena.");
            return;
        }

        if (!arena.isReady()) {
            player.sendMessage(RED + "This arena isn't ready yet.");
            return;
        }

        arena.setEnabled(!arena.isEnabled());
        player.sendMessage(GREEN + "Arena state toggled to: " + (arena.isEnabled() ? GREEN + "enabled" : RED + "disabled") + GREEN + ".");
    }

    // ================================================================================
    // arena save
    // ================================================================================

    @AsCommand(description = "Saves an arena.")
    public void save(Player player) {
        World world = player.getWorld();

        Arena arena = ArenaManager.get().get(world);
        if (arena == null) {
            player.sendMessage(RED + "The world " + world.getName() + " doesn't hold any arena.");
            return;
        }

        if (!arena.isReady()) {
            player.sendMessage(RED + "This arena isn't ready yet.");
            return;
        }

        if (arena.isEnabled()) {
            player.sendMessage(RED + "You can't save an arena while it's enabled.");
            return;
        }

        try {
            arena.save();
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(RED + "I/O error during arena transferring, read console for more info.");
            return;
        }

        player.sendMessage(GREEN + "Arena saved: " + arena.getId());
    }

    // ================================================================================
    // arena list
    // ================================================================================

    @AsCommand(description = "Lists all arenas.")
    public void list(CommandSender sender) {
        Collection<Arena> arenas = ArenaManager.get().getArenas();
        if (arenas.isEmpty()) {
            sender.sendMessage(RED + "No arena created.");
        } else {
            sender.sendMessage(GREEN + "You have " + arenas.size() + " arena(s):");
            arenas.forEach(arena -> sender.sendMessage((arena.isReady() ? GREEN : RED) + "- " + arena.getId()));
        }
    }

    // ================================================================================
    // arena lobby
    // ================================================================================

    @AsCommand(description = "Sets the arena lobby.")
    public void setLobby(Player player) {
        Arena arena = ArenaManager.get().get(player.getWorld());
        if (arena == null) {
            player.sendMessage(RED + "This world doesn't hold any arena.");
            return;
        }
        arena.setLobby(player.getLocation());
        player.sendMessage(GREEN + "Arena lobby set.");
    }
}
