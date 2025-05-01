package com.tinywebteam.badlion;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class CheckPoint{
	
	private String checkPointName;
	private String nextPointName;
	private Block block;
	private Vector vector;
	
	public CheckPoint(String checkPointName, String nextPointName, Block block) {
		this.checkPointName = checkPointName;
		this.nextPointName = nextPointName;
		this.block = block;
		this.vector = new Vector(this.block.getX(), this.block.getY(), this.block.getZ());
	}

	public String getCheckPointName() {
		return checkPointName;
	}

	public void setCheckPointName(String checkPointName) {
		this.checkPointName = checkPointName;
	}

	public String getNextPointName() {
		return nextPointName;
	}

	public void setNextPointName(String nextPointName) {
		this.nextPointName = nextPointName;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public Vector getVector() {
		return vector;
	}

	public void setVector(Vector vector) {
		this.vector = vector;
	}
}
