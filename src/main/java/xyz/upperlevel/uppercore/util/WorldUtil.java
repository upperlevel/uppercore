package xyz.upperlevel.uppercore.util;

import org.bukkit.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.generator.ChunkGenerator;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public final class WorldUtil {

    private WorldUtil() {
    }

    public static World createEmptyWorld(String name) {
        WorldCreator creator = new WorldCreator(name);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.generator(new ChunkGenerator() {
        });

        World world = creator.createWorld();
        world.setDifficulty(Difficulty.NORMAL);
        world.setSpawnFlags(true, true);
        world.setPVP(true);
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(Integer.MAX_VALUE);
        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);
        world.setSpawnFlags(false, false);

        world.setGameRule(GameRule.KEEP_INVENTORY, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);

        return world;
    }

    public static void kickAll(World world, Location to) {
        // kicks the players
        if (to == null || to.getWorld().equals(world))
            world.getPlayers().forEach(p -> p.kickPlayer("This world is under unloading."));
            // teleports the players to the 'to' location
        else world.getPlayers().forEach(p -> p.teleport(to, PlayerTeleportEvent.TeleportCause.PLUGIN));
    }

    public static void unloadWorld(World world) {
        Bukkit.unloadWorld(world, false);
    }

    public static void unloadWorldForced(World world, Location to) {
        kickAll(world, to);
        unloadWorld(world);
    }

    public static void deleteWorld(World world) {
        deleteFolder(world.getWorldFolder());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean deleteFolder(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return path.delete();
    }

    private static final List<String> toIgnore = Arrays.asList("uid.dat", "session.dat");

    public static void copyFolderRecursive(File source, File target) {
        try {
            if (!toIgnore.contains(source.getName())) {
                if (source.isDirectory()) {
                    if (!target.exists())
                        target.mkdirs();
                    String[] files = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFolderRecursive(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException ignored) {
        }
    }
}
