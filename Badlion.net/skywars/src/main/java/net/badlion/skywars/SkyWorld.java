package net.badlion.skywars;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.blocks.CraftMassBlockUpdate;
import net.badlion.gberry.utils.blocks.MassBlockUpdate;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGWorld;
import net.badlion.mpg.exceptions.BadMapException;
import net.badlion.worldrotator.GWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SkyWorld extends MPGWorld {

    private List<Location> spawnLocations = new ArrayList<>();
    private List<Location> tier1ChestLocations = new ArrayList<>();
    private List<Location> tier2ChestLocations = new ArrayList<>();
    private List<Block> glassBlocksToRemove = new ArrayList<>();
    private List<Location> locationsToDestroy = new ArrayList<>();
    private Map<Integer, List<Location>> spawnChestLocations = new HashMap<>();
    private Location spectatorLocation;
    private int minYLevel;

    public SkyWorld(GWorld gWorld) {
        super(gWorld);
    }

    @Override
    public void load() {
	    super.load();

        this.spawnLocations = this.getLocationsFromYml("spawn_locations", true);
        this.tier1ChestLocations = this.getLocationsFromYml("tier_1_chests");
        this.tier2ChestLocations = this.getLocationsFromYml("tier_2_chests");

        // Try to ready a file from the disk and if not report it as an error
        File jsonFile = new File(this.getGWorld().getDirectory(), this.getGWorld().getInternalName() + "_blocks.json");
        if (jsonFile.exists()) {
            try {
                JSONObject jsonObject = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(Paths.get(jsonFile.getAbsolutePath()))));

                for (String locationString : (List<String>) jsonObject.get("blocks_to_destroy")) {
                    Location location = Gberry.parseLocation(locationString);
                    this.locationsToDestroy.add(location);
                }
            } catch (IOException e) {
                SkyWars.getInstance().getLogger().info("Error reading " + this.getGWorld().getInternalName());
            }
        } else {
            SkyWars.getInstance().getLogger().info("File missing for SkyWarsArena " + this.getGWorld().getInternalName());
        }

        // Get chest spawns
        for (int i = 1; i <= this.spawnLocations.size(); i++) {
            this.spawnChestLocations.put(i, this.getLocationsFromYml("spawn-" + i));
        }

        this.minYLevel = this.gWorld.getYml().getInt("minytilldeath");
    }

    public void generateGlassAroundSpawns() {
        // TODO: Diff blocks based on what they want
        for (Location spawn : this.spawnLocations) {
            // Below
            int y = spawn.getBlockY() - 2;
            for (int x = spawn.getBlockX() - 1; x <= spawn.getBlockX() + 1; x++) {
                for (int z = spawn.getBlockZ() - 1; z <= spawn.getBlockZ() + 1; z++) {
                    Block block = this.getGWorld().getBukkitWorld().getBlockAt(x, y, z);
                    block.setType(Material.GLASS);
                    this.glassBlocksToRemove.add(block);
                }
            }

            // Above
            y = spawn.getBlockY() + 3;
            for (int x = spawn.getBlockX() - 1; x <= spawn.getBlockX() + 1; x++) {
                for (int z = spawn.getBlockZ() - 1; z <= spawn.getBlockZ() + 1; z++) {
                    Block block = this.getGWorld().getBukkitWorld().getBlockAt(x, y, z);
                    block.setType(Material.GLASS);
                    this.glassBlocksToRemove.add(block);
                }
            }

            // Left
            int x = spawn.getBlockX() - 1;
            for (y = spawn.getBlockY() - 1; y <= spawn.getBlockY() + 2; y++) {
                for (int z = spawn.getBlockZ() - 1; z <= spawn.getBlockZ() + 1; z++) {
                    Block block = this.getGWorld().getBukkitWorld().getBlockAt(x, y, z);
                    block.setType(Material.GLASS);
                    this.glassBlocksToRemove.add(block);
                }
            }

            // Right
            x = spawn.getBlockX() + 1;
            for (y = spawn.getBlockY() - 1; y <= spawn.getBlockY() + 2; y++) {
                for (int z = spawn.getBlockZ() - 1; z <= spawn.getBlockZ() + 1; z++) {
                    Block block = this.getGWorld().getBukkitWorld().getBlockAt(x, y, z);
                    block.setType(Material.GLASS);
                    this.glassBlocksToRemove.add(block);
                }
            }

            // Forward
            int z = spawn.getBlockZ() - 1;
            for (y = spawn.getBlockY() - 1; y <= spawn.getBlockY() + 2; y++) {
                for (x = spawn.getBlockX() - 1; x <= spawn.getBlockX() + 1; x++) {
                    Block block = this.getGWorld().getBukkitWorld().getBlockAt(x, y, z);
                    block.setType(Material.GLASS);
                    this.glassBlocksToRemove.add(block);
                }
            }

            // Backward
            z = spawn.getBlockZ() + 1;
            for (y = spawn.getBlockY() - 1; y <= spawn.getBlockY() + 2; y++) {
                for (x = spawn.getBlockX() - 1; x <= spawn.getBlockX() + 1; x++) {
                    Block block = this.getGWorld().getBukkitWorld().getBlockAt(x, y, z);
                    block.setType(Material.GLASS);
                    this.glassBlocksToRemove.add(block);
                }
            }
        }
    }

    public void destroyIslands() {
        new DestroyArenaTask().runTaskTimer(SkyWars.getInstance(), 0, 1);
    }

    private class DestroyArenaTask extends BukkitRunnable {

        private List<Location> blocksToDestroy;
        private Iterator<Location> iterator;
        private boolean initialized = false;

        @Override
        public void run() {
            if (!this.initialized) {
                this.initialized = true;
                this.blocksToDestroy = new ArrayList<>(SkyWorld.this.locationsToDestroy);
                this.iterator = this.blocksToDestroy.iterator();

                Gberry.broadcastMessage(MPG.MPG_PREFIX + "The spawn islands have begun falling apart.");
                return;
            }

            MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(SkyWorld.this.getGWorld().getBukkitWorld());
            massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);
            massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);
            int i = 0;
            while (this.iterator.hasNext()) {
                Location location = this.iterator.next();
                massBlockUpdate.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0);
            }

            massBlockUpdate.notifyClients();

            this.cancel();
        }

    }

    public void removeGlass() {
        for (Block block : this.glassBlocksToRemove) {
            block.setType(Material.AIR);
        }
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public List<Location> getTier1ChestLocations() {
        return tier1ChestLocations;
    }

    public List<Location> getTier2ChestLocations() {
        return tier2ChestLocations;
    }

    public void addToDestroy(Block block) {
        this.locationsToDestroy.add(block.getLocation());
    }

    public Map<Integer, List<Location>> getSpawnChestLocations() {
        return spawnChestLocations;
    }

    public int getMinYLevel() {
        return minYLevel;
    }

}
