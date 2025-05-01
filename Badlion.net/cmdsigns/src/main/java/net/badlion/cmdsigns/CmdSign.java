package net.badlion.cmdsigns;

import org.bukkit.block.Block;


public class CmdSign {
	
	private Block block;
	private boolean premium;
	private String commands;
	
	public CmdSign(Block block, boolean premium, String commands) {
		this.block = block;
		this.premium = premium;
		this.commands = commands;
	}

	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
	}


	public Block getBlock() {
		return block;
	}


	public void setBlock(Block block) {
		this.block = block;
	}

	public String getCommands() {
		return commands;
	}

	public void setCommands(String commands) {
		this.commands = commands;
	}

}
