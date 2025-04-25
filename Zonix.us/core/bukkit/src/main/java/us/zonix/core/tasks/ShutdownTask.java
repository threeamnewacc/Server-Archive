package us.zonix.core.tasks;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.util.BungeeUtil;
import us.zonix.core.util.event.PreShutdownEvent;

@Getter
@Setter
@AllArgsConstructor
public class ShutdownTask extends BukkitRunnable {

	private final static List<Integer> BROADCAST_TIMES = Arrays
			.asList(3600, 1800, 900, 600, 300, 180, 120, 60, 45, 30, 15, 10, 5, 4, 3, 2, 1);

	private CorePlugin plugin;

	private int secondsUntilShutdown;

	@Override
	public void run() {
		if (ShutdownTask.BROADCAST_TIMES.contains(secondsUntilShutdown)) {
			this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&c&lZonix&7] &fThe server is &c&lRESTARTING &fin &a&l" + secondsUntilShutdown + " seconds."));
		}

		if (this.secondsUntilShutdown <= 5) {
			this.plugin.getServer().getOnlinePlayers().forEach(player -> BungeeUtil.sendToServer(player, "hub-01"));
		}

		if (this.secondsUntilShutdown <= 0) {
			PreShutdownEvent event = new PreShutdownEvent();
			this.plugin.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return;
			}

			this.plugin.getServer().shutdown();
		}

		this.secondsUntilShutdown--;
	}
}
