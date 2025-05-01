package net.badlion.messenger;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class MessengerTask extends BukkitRunnable {

	private Messenger plugin;
	
	public MessengerTask(Messenger plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		Gberry.broadcastMessage(ChatColor.GOLD + this.plugin.getMessages().get(Math.abs(this.plugin.getGenerator().nextInt() % this.plugin.getMessages().size())));
	}
}
