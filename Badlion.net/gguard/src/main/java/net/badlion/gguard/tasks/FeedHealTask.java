package net.badlion.gguard.tasks;

import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class FeedHealTask extends BukkitRunnable {
	
	private GGuard plugin;
	
	public FeedHealTask(GGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		// Copied code from Gberry since we don't want GGuard to ever fail to load because of Gberry
		final List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		int size = players.size();
		int diff = (int) Math.ceil((double) players.size() / 20);

		for (int i = 0, j = 0; i < size; i += diff) {
			// Overshot
			if (i >= size) {
				return;
			}

			// Some shit for the task
			final int start = i;
			final int end = i + diff;
			Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

				@Override
				public void run() {
					for (int i = start; i < end; ++i) {
						// Overshot
						if (i >= players.size()) {
							return;
						}

						Player player = players.get(i);

						ProtectedRegion region = FeedHealTask.this.plugin.getProtectedRegion(player.getLocation(), FeedHealTask.this.plugin.getProtectedRegions());
						if (region != null && region.isFeedPlayers()) {
							player.setFoodLevel(20);
							player.setSaturation(20);
							player.setExhaustion(0);
						}

						if (region != null && region.isHealPlayers()) {
							player.setHealth(player.getMaxHealth());
						}
					}
				}

			}, ++j);
		}
	}

}
