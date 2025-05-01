package net.badlion.uhc.listeners;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.badlion.uhc.BadlionUHC;
import net.badlion.worldborder.WorldBorderFillMessageEvent;
import net.badlion.worldborder.WorldFillerTaskCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class WorldGenerationListener implements Listener {

    public static boolean isGenerating = false;
    private static Set<Material> passThroughMaterials = new HashSet<>();

    public WorldGenerationListener() {
        WorldGenerationListener.passThroughMaterials.add(Material.LOG);
        WorldGenerationListener.passThroughMaterials.add(Material.LOG_2);
        WorldGenerationListener.passThroughMaterials.add(Material.LEAVES);
        WorldGenerationListener.passThroughMaterials.add(Material.LOG_2);
        WorldGenerationListener.passThroughMaterials.add(Material.AIR);
    }

    @EventHandler
    public void onWorldCompleteGeneration(WorldFillerTaskCompleteEvent event) {
        // Runs server OOM too easy
        //BadlionUHC.getInstance().setWorldGenerated(true, event.getWorldName(), event.getBorderData());
        int radius = BadlionUHC.getInstance().getWorldBorder().GetWorldBorder(BadlionUHC.UHCWORLD_NAME).getRadiusX();
        WorldGenerationListener.isGenerating = true;
        WorldGenerationListener.addBedrockBorder(radius);
    }

    public static void addRedGlassBorder(final int radius) {
        EditSession es = new EditSession(new BukkitWorld(Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME)), 2147483647);

        try {
            com.sk89q.worldedit.Vector v1 = new com.sk89q.worldedit.Vector(-1 * radius, 0, -1 * radius);
            com.sk89q.worldedit.Vector v2 = new com.sk89q.worldedit.Vector(radius, 100, radius);
            es.makeCuboidWalls(new CuboidRegion(v1, v2), new SingleBlockPattern(new BaseBlock(95, 14)));
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    public static void addBedrockBorder(final int radius, int blocksHigh) {
        for (int i = 0; i < blocksHigh; i++) {
            new BukkitRunnable() {
                public void run() {
                    WorldGenerationListener.addBedrockBorder(radius);
                }
            }.runTaskLater(BadlionUHC.getInstance(), i);
        }
    }

    private static void figureOutBlockToMakeBedrock(int x, int z) {
        Block block = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NAME).getHighestBlockAt(x, z);
        Block below = block.getRelative(BlockFace.DOWN);
        while (WorldGenerationListener.passThroughMaterials.contains(below.getType()) && below.getY() > 1) {
            below = below.getRelative(BlockFace.DOWN);
        }

        below.getRelative(BlockFace.UP).setType(Material.BEDROCK);
    }

    public static void addBedrockBorder(final int radius) {
        new BukkitRunnable() {

            private int counter = -radius - 1;
            private boolean phase1 = false;
            private boolean phase2 = false;
            private boolean phase3 = false;

            @Override
            public void run() {
                if (!phase1) {
                    int maxCounter = counter + 500;
                    int x = -radius - 1;
                    for (int z = counter; z <= radius && counter <= maxCounter; z++, counter++) {
                        WorldGenerationListener.figureOutBlockToMakeBedrock(x, z);
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase1 = true;
                    }

                    return;
                }

                if (!phase2) {
                    int maxCounter = counter + 500;
                    int x = radius;
                    for (int z = counter; z <= radius && counter <= maxCounter; z++, counter++) {
                        WorldGenerationListener.figureOutBlockToMakeBedrock(x, z);
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase2 = true;
                    }

                    return;
                }

                if (!phase3) {
                    int maxCounter = counter + 500;
                    int z = -radius - 1;
                    for (int x = counter; x <= radius && counter <= maxCounter; x++, counter++) {
                        if (x == radius || x == -radius - 1) {
                            continue;
                        }

                        WorldGenerationListener.figureOutBlockToMakeBedrock(x, z);
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase3 = true;
                    }

                    return;
                }


                int maxCounter = counter + 500;
                int z = radius;
                for (int x = counter; x <= radius && counter <= maxCounter; x++, counter++) {
                    if (x == radius || x == -radius - 1) {
                        continue;
                    }

                    WorldGenerationListener.figureOutBlockToMakeBedrock(x, z);
                }

                if (counter >= radius) {
                    this.cancel();

                    if (WorldGenerationListener.isGenerating) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                    }
                }
            }
        }.runTaskTimer(BadlionUHC.getInstance(), 0, 5);
    }

    @EventHandler
    public void onWorldProgressGeneration(WorldBorderFillMessageEvent event) {
        if (BadlionUHC.getInstance().getHost() == null) {
            return;
        }

        Player host = BadlionUHC.getInstance().getServer().getPlayer(BadlionUHC.getInstance().getHost().getUUID());
        if (host != null) {
            host.sendMessage(event.getMessage());
        }
    }

}
