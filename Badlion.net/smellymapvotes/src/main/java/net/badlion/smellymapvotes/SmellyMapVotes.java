package net.badlion.smellymapvotes;

import net.badlion.smellymapvotes.commands.MapVoteCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class SmellyMapVotes extends JavaPlugin {

	private static SmellyMapVotes plugin;

	private VoteManager.ServerType serverType;

	@Override
	public void onEnable() {
		SmellyMapVotes.plugin = this;

		// Create command executors
		this.getCommand("mapvote").setExecutor(new MapVoteCommand());
	}

	@Override
	public void onDisable() {

	}

	public static SmellyMapVotes getInstance() {
		return SmellyMapVotes.plugin;
	}

	public VoteManager.ServerType getServerType() {
		return serverType;
	}

	public void setServerType(VoteManager.ServerType serverType) {
		this.serverType = serverType;
	}

}
