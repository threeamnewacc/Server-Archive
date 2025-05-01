package net.badlion.kitpvp.tasks;

import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

import com.tinywebteam.badlion.MineKart;

public class RemoveItemTask extends BukkitRunnable {
	
	private MineKart plugin;
	private Item item;
	
	public RemoveItemTask(MineKart plugin, Item item) {
		this.plugin = plugin;
		this.item = item;
	}
	
	@Override
	public void run() {
		this.item.remove();
	}

}
