package net.badlion.survivalgames.tasks;

import net.badlion.survivalgames.SGGame;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class LoadMapTask extends BukkitRunnable {

    //private int x = -16;
    private int x = 0;
    private SGGame sgGame;
    private boolean done = false;

    public LoadMapTask(SGGame sgGame) {
        this.sgGame = sgGame;
    }

    @Override
    public void run() {
        if (this.done) {
	        this.cleanMap();
	        this.sgGame.fillAllChests();
	        this.cancel();
            return;
        }

        // Load all the chunks
        //org.bukkit.Chunk chunk = this.sgGame.getSpawnLocation(0).getChunk();
        //for (int z = -16; z < 16; z++) {
            //Bukkit.getWorld(this.sgGame.getSgWorld().getgWorld().getInternalName()).getChunkAt(chunk.getX() + x, chunk.getZ() + z).load();
        //}

        x++;

        if (x == 14) {
            for (Location location : this.sgGame.getSgWorld().getTier1ChestLocations()) {
                try {
                    if (location.getBlock().getType() != org.bukkit.Material.CHEST) {
                        throw new RuntimeException("Invalid chests in config " + this.sgGame.getGWorld().getInternalName());
                    }

                    Chest chest = (Chest) location.getBlock().getState();
                    chest.getInventory().clear();

                    // Handle double chests
                    if (chest.getInventory().getHolder() instanceof DoubleChest) {
                        DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
                        DoubleChestInventory doubleChestInventory = (DoubleChestInventory) doubleChest.getInventory();
                        this.sgGame.getTier1Chests().add(doubleChestInventory.getLeftSide());
                        this.sgGame.getTier1Chests().add(doubleChestInventory.getRightSide());
                    } else {
                        this.sgGame.getTier1Chests().add(chest.getInventory());
                    }
                } catch (RuntimeException e) {
                    Bukkit.getLogger().info("Unable to find tier 1 chest at " + location.toString());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                    return;
                }
            }

            for (Location location : this.sgGame.getSgWorld().getTier2ChestLocations()) {
                try {
                    if (location.getBlock().getType() != org.bukkit.Material.CHEST) {
                        throw new RuntimeException("Invalid chests in config " + this.sgGame.getGWorld().getInternalName());
                    }

                    Chest chest = (Chest) location.getBlock().getState();
                    chest.getInventory().clear();

                    // Handle double chests
                    if (chest.getInventory().getHolder() instanceof DoubleChest) {
                        DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
                        DoubleChestInventory doubleChestInventory = (DoubleChestInventory) doubleChest.getInventory();
                        this.sgGame.getTier2Chests().add(doubleChestInventory.getLeftSide());
                        this.sgGame.getTier2Chests().add(doubleChestInventory.getRightSide());
                    } else {
                        this.sgGame.getTier2Chests().add(chest.getInventory());
                    }
                } catch (RuntimeException e) {
                    Bukkit.getLogger().info("Unable to find tier 2 chest at " + location.toString());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                    return;
                }
            }

            this.done = true;
        }
    }

    public void cleanMap() {
	    Chunk[] chunks = this.sgGame.getSgWorld().getgWorld().getBukkitWorld().getLoadedChunks();
	    for (Chunk chunk : chunks) {
		    BlockState[] tileEntities = chunk.getTileEntities();
		    for (BlockState i : tileEntities) {
			    if (i instanceof Beacon) {
				    Beacon blockState = ((Beacon) i);
				    blockState.getInventory().clear();
			    } else if (i instanceof BrewingStand) {
				    BrewingStand blockState = ((BrewingStand) i);
				    blockState.getInventory().clear();
			    } else if (i instanceof Chest) {
				    Chest blockState = ((Chest) i);
				    blockState.getInventory().clear();
			    } else if (i instanceof Dispenser) {
				    Dispenser blockState = ((Dispenser) i);
				    blockState.getInventory().clear();
			    } else if (i instanceof Furnace) {
				    Furnace blockState = ((Furnace) i);
				    blockState.getInventory().clear();
			    } else if (i instanceof Hopper) {
				    Hopper blockState = ((Hopper) i);
				    blockState.getInventory().clear();
			    } else if (i instanceof Jukebox) {
				    Jukebox blockState = ((Jukebox) i);
				    blockState.eject();
			    }
		    }
	    }

	    for (Entity entity : this.sgGame.getSgWorld().getgWorld().getBukkitWorld().getEntities()) {
		    if (entity.getType() == EntityType.DROPPED_ITEM) {
			    entity.remove();
		    }
	    }
    }

}
