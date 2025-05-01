package net.badlion.mpg.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.worldrotator.GWorld;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MapChestFinderTask extends BukkitRunnable {

    private Chunk[] chunks = null;
    private GWorld gWorld = null;
    private int numberOfChestsFound = 0;
    private int tier1ChestsFound = 0;
    private int tier2ChestsFound = 0;

    public MapChestFinderTask(Chunk[] chunks, GWorld gWorld) {
        this.chunks = chunks;
        this.gWorld = gWorld;
    }

    public void run() {
        List<String> tier1ChestLocations = new ArrayList<>();
        List<String> tier2ChestLocations = new ArrayList<>();

        for (Chunk chunk : chunks) {
            BlockState[] tileEntities = chunk.getTileEntities();
            for (BlockState i : tileEntities) {
	            if (i.getType() == Material.CHEST) {
		            Location chestLocation = i.getLocation();
		            String locString = Gberry.getLocationString(chestLocation);

		            tier1ChestLocations.add(locString);

		            this.numberOfChestsFound++;
		            this.tier1ChestsFound++;
	            } else if (i.getType() == Material.ENDER_CHEST) {
		            Location chestLocation = i.getLocation();
		            String locString = Gberry.getLocationString(chestLocation);

		            tier2ChestLocations.add(locString);

		            this.numberOfChestsFound++;
		            this.tier2ChestsFound++;
	            }
            }
        }

        /*for (TileEntity entity : ((WorldTileEntityList) ((CraftWorld) Bukkit.getWorld(this.gWorld.getInternalName())).getHandle().tileEntityList).fullList) {
            if (entity instanceof TileEntityEnderChest) {
                String locString = Gberry.getLocationString(Bukkit.getWorld(this.gWorld.getInternalName()).getBlockAt(entity.x, entity.y, entity.z).getLocation());
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
