package xyz.upperlevel.uppercore.arena;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import xyz.upperlevel.uppercore.command.CommandContext;
import xyz.upperlevel.uppercore.command.PermissionUser;
import xyz.upperlevel.uppercore.command.SenderType;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.WithPermission;

import java.util.Collection;
import java.util.HashSet;

import static org.bukkit.ChatColor.*;

public class ArenaCommands {
    @Getter
    private final ArenaManager arenaManager;

    @Getter
    private final ArenaFactory arenaFactory;

    public ArenaCommands(ArenaManager arenaManager, ArenaFactory arenaFactory) {
        this.arenaManager = arenaManager;
        this.arenaFactory = arenaFactory;
    }

    @AsCommand(
            description = "Create an arena."
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void create(CommandContext context, String id) {
        if (!Arena.isValidName(id)) {
            context.send(RED + "Only alphabetic characters!");
            return;
        }
        Arena arena = arenaFactory.create(id);
        if (!arenaManager.register(arena)) {
            context.send(RED + "Arena with same name already found: " + arena);
            return;
        }
        context.send(GREEN + "Arena '" + id + "' created!");
    }

    @AsCommand(
            description = "Remove an arena."
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void remove(CommandContext context, String id) {
        if (!arenaManager.unregister(id)) {
            context.send(RED + "Arena with that name not found: " + id);
        }
        context.send(GREEN + "Arena '" + id + "' removed.");
    }

    @AsCommand(
            description = "List created arenas."
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void list(CommandSender sender) {
        Collection<Arena> arenas = arenaManager.getArenas();
        if (arenas.size() > 0) {
            sender.sendMessage(GREEN + "" + arenas.size() + " arenas found:");
            for (Arena arena : arenas) {
                sender.sendMessage(GREEN + arena.getId());
            }
        } else {
            sender.sendMessage(GREEN + "No arena created.");
        }
    }

    @AsCommand(
            description = "Set arena lobby to player's position.",
            sender = SenderType.PLAYER
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void setlobby(CommandSender sender, Arena arena) {
        arena.setLobby(((Player) sender).getLocation());
        sender.sendMessage(GREEN + "Arena '" + arena.getId() + "' lobby changed.");
    }

    @AsCommand(
            description = "Change arena name."
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void setname(CommandSender sender, Arena arena, String name) {
        String result = String.join(" ", name);
        arena.setName(result);
        sender.sendMessage(GREEN + "Arena '" + arena.getId() + "' name changed to: '" + name + "'.");
    }

    @AsCommand(
            description = "Give info about an arena."
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void info(CommandContext context, String id) {
        Arena arena = arenaManager.getArena(id);
        if (arena == null) {
            context.send(RED + "Arena not found");
            return;
        }
        context.send("Info for arena '" + arena.getId() + "':");
        context.send(GREEN + new JSONObject(arena.serialize()).toJSONString());
    }

    @SuppressWarnings("deprecation")
    @AsCommand(
            description = "Add an arena join-sign.",
            sender = SenderType.PLAYER
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void addjoinsign(CommandContext context, String arenaId) {
        Arena arena = arenaManager.getArena(arenaId);
        if (arena == null) {
            context.send(RED + "Arena not found: " + LIGHT_PURPLE + arenaId + RED + ".");
            return;
        }
        Block block = ((Player) context.sender()).getTargetBlock((HashSet<Byte>) null, 100);
        if (block == null || !(block.getState() instanceof Sign)) {
            context.send(RED + "Point a sign block, the current one is not a sign!");
            return;
        }
        arena.addSign(block);
        context.send(RED + "Point a sign block, the current one is not a sign!");
    }

    @SuppressWarnings("deprecation")
    @AsCommand(
            description = "Remove an arena join-sign.",
            sender = SenderType.PLAYER
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void removejoinsign(CommandContext context, String arenaId) {
        Arena arena = arenaManager.getArena(arenaId);
        if (arena == null) {
            context.send(RED + "Arena not found: " + LIGHT_PURPLE + arenaId + RED + ".");
            return;
        }
        Block block = ((Player) context.sender()).getTargetBlock((HashSet<Byte>) null, 100);
        if (block == null || !(block.getState() instanceof Sign)) {
            context.send(RED + "Point a sign block, the current one is not a sign!");
            return;
        }
        arena.removeSign(block);
        context.send(GREEN + "Sign removed.");
    }

    @AsCommand(
            description = "Enable an arena."
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void enable(CommandContext context, String arenaId) {
        Arena arena = arenaManager.getArena(arenaId);
        if (arena == null) {
            context.send(RED + "Arena not found: " + LIGHT_PURPLE + arenaId + RED + ".");
            return;
        }
        if (!arena.isReady()) {
            context.send(RED + "Arena not ready. Finish to setup it!");
            return;
        }
        arena.start();
        context.send(GREEN + "Arena '" + arena.getId() + "' started.");
    }

    @AsCommand(
            description = "Disable an arena."
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void disable(CommandContext context, String arenaId) {
        Arena arena = arenaManager.getArena(arenaId);
        if (arena == null) {
            context.send(RED + "Arena not found: " + LIGHT_PURPLE + arenaId + RED + ".");
            return;
        }
        if (!arena.isPlaying()) {
            context.send(RED + "Arena isn't started.");
            return;
        }
        arena.stop();
        context.send(GREEN + "Arena '" + arenaId + "' disabled.");
    }

    @AsCommand(
            description = "Join a started arena.",
            sender = SenderType.PLAYER,
            aliases = {"in", "enter"}
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void join(CommandContext context, String arenaId) {
        Arena arena = arenaManager.getArena(arenaId);
        if (arena == null) {
            context.send(RED + "Arena not found: " + LIGHT_PURPLE + arenaId + RED + ".");
            return;
        }
        arena.join((Player) context.sender());
        context.send("Arena '" + arena.getId() + "' joined.");
    }

    @AsCommand(
            description = "Leave an arena.",
            sender = SenderType.PLAYER,
            aliases = {"leave", "exit"}
    )
    @WithPermission(
            user = PermissionUser.OP
    )
    protected void quit(CommandContext context, String arenaId) {
        Arena arena = arenaManager.getArena(arenaId);
        if (arena == null) {
            context.send(RED + "Arena not found: " + LIGHT_PURPLE + arenaId + RED + ".");
            return;
        }
        arena.quit((Player) context.sender());
        context.send("Arena '" + arena.getId() + "' left.");
    }
}
