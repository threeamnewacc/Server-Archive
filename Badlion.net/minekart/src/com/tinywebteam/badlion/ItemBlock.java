package com.tinywebteam.badlion;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;

public class ItemBlock {
	
	private EnderCrystal enderCrystal;
	private Location spawnLocation;
	private Block block;
	private boolean itemAvailable;
	
	public ItemBlock(Location spawnLocation, EnderCrystal enderCrystal, Block block) {
		this.enderCrystal = enderCrystal;
		this.spawnLocation = spawnLocation;
		this.block = block;
		this.itemAvailable = true;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public EnderCrystal getEnderCrystal() {
		return enderCrystal;
	}

	public void setEnderCrystal(EnderCrystal enderCrystal) {
		this.enderCrystal = enderCrystal;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public void setSpawnLocation(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}

	public boolean isItemAvailable() {
		return itemAvailable;
	}

	public void setItemAvailable(boolean itemAvailable) {
		this.itemAvailable = itemAvailable;
	}

}
