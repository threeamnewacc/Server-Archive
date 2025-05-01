package net.badlion.survivalgames.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.worldrotator.GWorld;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ChestFinderTask extends BukkitRunnable {

    private Chunk[] chunks = null;
    private GWorld gWorld = null;
    private int numberOfChestsFound = 0;
    private int tier1ChestsFound = 0;
    private int tier2ChestsFound = 0;

    public ChestFinderTask(Chunk[] chunks, GWorld gWorld) {
        this.chunks = chunks;
        this.gWorld = gWorld;
    }

    public void run() {
        List<String> tier1ChestLocations = new ArrayList<>();
        List<String> tier2ChestLocations = new ArrayList<>();

        for (Chunk chunk : this.chunks) {
            BlockState[] tileEntities = chunk.getTileEntities();
            for (BlockState i : tileEntities) {
                if (i instanceof Chest) {
                    Location chestLocation = i.getLocation();
                    String locString = "" + (int) chestLocation.getX() + " " + (int) chestLocation.getY() + " " + (int) chestLocation.getZ();
                    if (i.getBlock().getType() == Material.CHEST) {
                        tier1ChestLocations.add(locString);
                    }

                    ((Chest) i).getBlockInventory().clear();
                    ((Chest) i).getInventory().clear();

                    this.numberOfChestsFound++;
                    this.tier1ChestsFound++;
                }
            }
        }

	    for (Chunk chunk : this.chunks) {
		    BlockState[] tileEntities = chunk.getTileEntities();
		    for (BlockState i : tileEntities) {
			    if (i instanceof Chest) {
				    Location chestLocation = i.getLocation();
				    String locString = "" + (int) chestLocation.getX() + " " + (int) chestLocation.getY() + " " + (int) chestLocation.getZ();
				    if (i.getBlock().getType() == Material.ENDER_CHEST) {
					    tier2ChestLocations.add(locString);
					    this.numberOfChestsFound++;
					    this.tier2ChestsFound++;
				    }
			    }
		    }
	    }

	    /*for (TileEntity entity : ((WorldTileEntityList) ((CraftWorld) this.gWorld.getBukkitWorld()).getHandle().tileEntityList).fullList) {
            if (entity instanceof TileEntityEnderChest) {
                String locString = "" + entity.x + " " + entity.y + " " + entity.z;
                tier2ChestLocations.add(locString);
                this.numberOfChestsFound++;
                this.tier2ChestsFound++;
            }
        }*/

        this.gWorld.getYml().set("tier_1_chests", tier1ChestLocations);
        this.gWorld.getYml().set("tier_2_chests", tier2ChestLocations);

        Gberry.broadcastMessageNoBalance(ChatColor.GREEN + "" + this.tier1ChestsFound + " tier 1 chests found");
        Gberry.broadcastMessageNoBalance(ChatColor.GREEN + "" + this.tier2ChestsFound + " tier 2 chests found");
        Gberry.broadcastMessageNoBalance(ChatColor.GREEN + "" + this.numberOfChestsFound + " chests have all been found");
    }

}
