package net.badlion.gfactions.events;

import java.util.ArrayList;

public class Dungeon {

	private String dungeonName;
	private int spawnX;
	private int spawnY;
	private int spawnZ;
	private int numOfChests;
	private ArrayList<DungeonChest> chests;
	private int zombiePercentage;
	private int skeletonPercentage;
	private int creeperPercentage;
	private int spiderPercentage;
	private int blazePercentage;
	private int ghastPercentage;
	private int giantPercentage;
	private int maxMobLevelY;
	private int maxPlayerLevelY;
	
	public Dungeon(String dungeonName, int spawnX, int spawnY, int spawnZ, int numOfChests, ArrayList<DungeonChest> chests, 
			int zombiePercentage, int skeletonPercentage, int creeperPercentage, int spiderPercentage, int blazePercentage, 
			int ghastPercentage, int giantPercentage, int maxMobLevelY, int maxPlayerLevelY) {
		this.dungeonName = dungeonName;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.spawnZ = spawnZ;
		this.numOfChests = numOfChests;
		this.chests = chests;
		this.zombiePercentage = zombiePercentage;
		this.skeletonPercentage = skeletonPercentage;
		this.creeperPercentage = creeperPercentage;
		this.spiderPercentage = spiderPercentage;
		this.blazePercentage = blazePercentage;
		this.ghastPercentage = ghastPercentage;
		this.giantPercentage = giantPercentage;
		this.maxMobLevelY = maxMobLevelY;
		this.maxPlayerLevelY = maxPlayerLevelY;
	}

	public String getDungeonName() {
		return dungeonName;
	}

	public void setDungeonName(String dungeonName) {
		this.dungeonName = dungeonName;
	}

	public int getSpawnX() {
		return spawnX;
	}

	public void setSpawnX(int spawnX) {
		this.spawnX = spawnX;
	}

	public int getSpawnY() {
		return spawnY;
	}

	public void setSpawnY(int spawnY) {
		this.spawnY = spawnY;
	}

	public int getSpawnZ() {
		return spawnZ;
	}

	public void setSpawnZ(int spawnZ) {
		this.spawnZ = spawnZ;
	}

	public int getNumOfChests() {
		return numOfChests;
	}

	public void setNumOfChests(int numOfChests) {
		this.numOfChests = numOfChests;
	}

	public ArrayList<DungeonChest> getChests() {
		return chests;
	}

	public void setChests(ArrayList<DungeonChest> chests) {
		this.chests = chests;
	}

	public int getZombiePercentage() {
		return zombiePercentage;
	}

	public void setZombiePercentage(int zombiePercentage) {
		this.zombiePercentage = zombiePercentage;
	}

	public int getSkeletonPercentage() {
		return skeletonPercentage;
	}

	public void setSkeletonPercentage(int skeletonPercentage) {
		this.skeletonPercentage = skeletonPercentage;
	}

	public int getCreeperPercentage() {
		return creeperPercentage;
	}

	public void setCreeperPercentage(int creeperPercentage) {
		this.creeperPercentage = creeperPercentage;
	}

	public int getSpiderPercentage() {
		return spiderPercentage;
	}

	public void setSpiderPercentage(int spiderPercentage) {
		this.spiderPercentage = spiderPercentage;
	}

	public int getBlazePercentage() {
		return blazePercentage;
	}

	public void setBlazePercentage(int blazePercentage) {
		this.blazePercentage = blazePercentage;
	}

	public int getGhastPercentage() {
		return ghastPercentage;
	}

	public void setGhastPercentage(int ghastPercentage) {
		this.ghastPercentage = ghastPercentage;
	}

	public int getGiantPercentage() {
		return giantPercentage;
	}

	public void setGiantPercentage(int giantPercentage) {
		this.giantPercentage = giantPercentage;
	}

	public int getMaxMobLevelY() {
		return maxMobLevelY;
	}

	public void setMaxMobLevelY(int maxMobLevelY) {
		this.maxMobLevelY = maxMobLevelY;
	}

	public int getMaxPlayerLevelY() {
		return maxPlayerLevelY;
	}

	public void setMaxPlayerLevelY(int maxPlayerLevelY) {
		this.maxPlayerLevelY = maxPlayerLevelY;
	}
}
