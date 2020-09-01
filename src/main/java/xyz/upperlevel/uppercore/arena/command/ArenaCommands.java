package xyz.upperlevel.uppercore.arena.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.arena.Arena;
import xyz.upperlevel.uppercore.arena.ArenaManager;
import xyz.upperlevel.uppercore.arena.OnQuitHandler;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.WithOptional;
import xyz.upperlevel.uppercore.command.functional.WithPermission;
import xyz.upperlevel.uppercore.util.LocUtil;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
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

    @AsCommand(
            description = "Creates a new arena."
    )
    @WithPermission
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

        player.sendMessage(GREEN + "Arena created successfully: " + id);
        player.sendMessage(GRAY + "The arena world generated is called: " + arena.getWorld().getName());
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
    // teleport
    // ================================================================================

    @AsCommand(description = "Teleports the player to the specified arena.")
    public void teleport(Player player, Arena arena) {
        player.teleport(arena.getLobby() != null ? arena.getLobby() : new Location(arena.getWorld(), 0, 64, 0));
    }

    // ================================================================================
    // toggle
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
    // save
    // ================================================================================

    @AsCommand(description = "Saves an arena.")
    public void save(Player player, Arena arena) {
        if (!arena.isReady()) {
            player.sendMessage(RED + "This arena isn't ready yet.");
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
    // list
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
    // lobby
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

    // ================================================================================
    // addjoinsign
    // ================================================================================

    @AsCommand(description = "Adds an arena join sign.")
    public void addJoinSign(Player player, Arena arena) {
        Block block = player.getTargetBlock(null, 10);
        if (!(block.getState() instanceof Sign)) {
            player.sendMessage(RED + "You're not targeting a sign.");
            return;
        }
        arena.addJoinSign((Sign) block.getState());
        player.sendMessage(GREEN + "Join sign added for arena: " + YELLOW + arena.getId() + GREEN + ".");
    }

    // ================================================================================
    // rmjoinsign
    // ================================================================================

    @AsCommand(description = "Removes an arena join sign.")
    public void rmJoinSign(Player player, Arena arena) {
        Block block = player.getTargetBlock(null, 10);
        if (!(block.getState() instanceof Sign)) {
            player.sendMessage(RED + "You're not targeting a sign.");
            return;
        }
        arena.removeJoinSign((Sign) block.getState());
        player.sendMessage(GREEN + "Join sign removed for arena: " + YELLOW + arena.getId() + GREEN + ".");
    }

    // ================================================================================
    // info
    // ================================================================================

    @AsCommand(description = "Shows information about the specified arena.")
    public void info(CommandSender sender, Arena arena) {
        StringWriter writer = new StringWriter();
        new Yaml().dump(arena.serialize(), writer);
        sender.sendMessage(GREEN + writer.toString());
    }

    // ================================================================================
    // sethub
    // ================================================================================

    @AsCommand(description = "Shows information about the specified arena.")
    public void setHub(Player player) {
        OnQuitHandler.Local.setHub(player.getLocation());
        player.sendMessage(GREEN + "Hub set to your current position.");
    }

    // ================================================================================
    // joingui
    // ================================================================================

    @AsCommand(description = "Shows a list of arenas that can be join if clicked.")
    public void joinGui(Player player) {
        Uppercore.guis().open(player, ArenaManager.get().getJoinGui());
    }
}
