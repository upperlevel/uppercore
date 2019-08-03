package xyz.upperlevel.uppercore.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.material.Bed;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class WorldUtil {

    private WorldUtil() {
    }

    public static World createEmptyWorld(String name) {
        WorldCreator creator = new WorldCreator(name);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.generator(new ChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                return createChunkData(world);
            }
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
        world.setTicksPerAnimalSpawns(1);
        world.setTicksPerMonsterSpawns(1);

        world.setGameRuleValue("keepInventory", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("showDeathMessages", "false");

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
            File files[] = path.listFiles();
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
                    String files[] = source.list();
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

    public static Block getBedHead(Block block) {
        BlockState bs = block.getState();
        if (!(bs instanceof Bed))
            throw new IllegalArgumentException("The parameter 'block' must has a BedHelper State");
        Bed bed = (Bed) bs;
        return !bed.isHeadOfBed() ? block.getRelative(bed.getFacing().getOppositeFace()) : block;
    }
}
