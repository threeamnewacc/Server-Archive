package net.badlion.enderpearlcd;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class EnderPearlCDListener implements Listener {

	public static int COOLDOWN = 15 * 1000;

	private static Map<UUID, Long> lastThrow = new HashMap<>();

	public static void removeEnderPearlCD(Player player) {
		EnderPearlCDListener.lastThrow.remove(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerUseEP(PlayerInteractEvent event) {
		if ((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.LEFT_CLICK_BLOCK) ||
				(event.getItem() == null) || (event.getItem().getType() != Material.ENDER_PEARL)) {
			return;
		}

		Player player = event.getPlayer();

		Long now = System.currentTimeMillis();
		Long lastPearl = EnderPearlCDListener.lastThrow.get(player.getUniqueId());

		if ((lastPearl == null) || (now - lastPearl >= EnderPearlCDListener.COOLDOWN)) {
			EnderPearlCDListener.lastThrow.put(player.getUniqueId(), now);
		} else {
			event.setCancelled(true);

			Long timeLeft = (EnderPearlCDListener.COOLDOWN - (now - lastPearl)) / 1000L;
			player.sendFormattedMessage("{0}Enderpearl cooldown remaining: {1} seconds.", ChatColor.RED, timeLeft);

			// Update inventory to show
			player.updateInventory();
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		// Don't leak memory
		EnderPearlCDListener.lastThrow.remove(event.getPlayer().getUniqueId());
	}

}
