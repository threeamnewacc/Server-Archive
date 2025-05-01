package net.badlion.smellylobby.tasks;

import net.badlion.smellylobby.SmellyLobby;
import org.bukkit.scheduler.BukkitRunnable;

public class RestartServerTask extends BukkitRunnable {

	public RestartServerTask() {
		this.runTaskLater(SmellyLobby.getInstance(), 560000L);
	}

	@Override
	public void run() {
		SmellyLobby.getInstance().getServer().dispatchCommand(SmellyLobby.getInstance().getServer().getConsoleSender(), "stop");
	}

}
