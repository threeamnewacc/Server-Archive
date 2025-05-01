package net.badlion.arenavotes;

import net.badlion.arenavotes.listener.VoteListener;
import net.badlion.gberry.Gberry;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaVotes extends JavaPlugin {

	@Override
	public void onEnable() {
		Gberry.loggingTags.add("VOTE");

		this.getServer().getPluginManager().registerEvents(new VoteListener(), this);
	}


}
