package net.badlion.kitpvp.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.scheduler.BukkitRunnable;

import com.tinywebteam.badlion.ItemBlock;
import com.tinywebteam.badlion.MineKart;

public class SpawnItemBlock extends BukkitRunnable {
	
	private MineKart plugin;
	private ItemBlock itemBlock;
	//private HashSet<Block> enderCrystalLocations;
	
	public SpawnItemBlock(MineKart plugin, ItemBlock itemBlock/*, HashSet<Block> enderCrystalLocations*/) {
		this.plugin = plugin;
		this.itemBlock = itemBlock;
		//this.enderCrystalLocations = enderCrystalLocations;
	}
	
	@Override
	public void run() {
		// spawn new itemblock
		Location location = this.itemBlock.getBlock().getLocation();
		location.setX(location.getX() + 0.5);
		location.setY(location.getY() - 0.7);
		location.setZ(location.getZ() + 0.5);
		
		// TODO: Insert hack here if we can't find a smarter way to fix it?
		
		this.itemBlock.setEnderCrystal(Bukkit.getWorld("world").spawn(location, EnderCrystal.class));
		this.itemBlock.setItemAvailable(true);
		//this.enderCrystalLocations.add(this.itemBlock.getBlock());
	}
}
