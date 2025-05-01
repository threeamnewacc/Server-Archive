package com.tinywebteam.badlion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Track {
	
	private String trackName;
	private ArrayList<CheckPoint> checkPoints;
	private Map<CheckPoint, CheckPoint> checkPointToNextCheckPoint;
	private ArrayList<ItemBlock> itemBlocks;
	private int numOfLaps;
	private int index;
	private HashSet<Block> finishLineBlocks;
	private ArrayList<Location> spawnLocations;
	private HashSet<Integer> allowedBlocks;
	private HashSet<Integer> slowedBlocks;
	
	public Track(String trackName, int numOfLaps, HashSet<Integer> allowedBlocks, HashSet<Integer> slowedBlocks) {
		this.trackName = trackName;
		this.itemBlocks = new ArrayList<ItemBlock>();
		this.checkPointToNextCheckPoint = new HashMap<CheckPoint, CheckPoint>();
		this.numOfLaps = numOfLaps;
		this.allowedBlocks = allowedBlocks;
		this.slowedBlocks = slowedBlocks;
	}

	public ArrayList<CheckPoint> getCheckPoints() {
		return checkPoints;
	}

	public void setCheckPoints(ArrayList<CheckPoint> checkPoints) {
		this.checkPoints = checkPoints;
	}

	public Map<CheckPoint, CheckPoint> getCheckPointToNextCheckPoint() {
		return checkPointToNextCheckPoint;
	}

	public void setCheckPointToNextCheckPoint(
			Map<CheckPoint, CheckPoint> checkPointToNextCheckPoint) {
		this.checkPointToNextCheckPoint = checkPointToNextCheckPoint;
	}

	public ArrayList<ItemBlock> getItemBlocks() {
		return itemBlocks;
	}

	public void setItemBlocks(ArrayList<ItemBlock> itemBlocks) {
		this.itemBlocks = itemBlocks;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public int getNumOfLaps() {
		return numOfLaps;
	}

	public void setNumOfLaps(int numOfLaps) {
		this.numOfLaps = numOfLaps;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public HashSet<Block> getFinishLineBlocks() {
		return finishLineBlocks;
	}

	public void setFinishLineBlocks(HashSet<Block> finishLineBlocks) {
		this.finishLineBlocks = finishLineBlocks;
	}
	
	public ArrayList<Location> getSpawnLocations() {
		return spawnLocations;
	}

	public void setSpawnLocations(ArrayList<Location> spawnLocations) {
		this.spawnLocations = spawnLocations;
	}


	public HashSet<Integer> getAllowedBlocks() {
		return allowedBlocks;
	}

	public void setAllowedBlocks(HashSet<Integer> allowedBlocks) {
		this.allowedBlocks = allowedBlocks;
	}

	public HashSet<Integer> getSlowedBlocks() {
		return slowedBlocks;
	}

	public void setSlowedBlocks(HashSet<Integer> slowedBlocks) {
		this.slowedBlocks = slowedBlocks;
	}
	

}
