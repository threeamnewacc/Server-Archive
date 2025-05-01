package net.badlion.gfactions.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EnderPearlCDListener implements Listener {

	public static int COOLDOWN = 15000;
    public static Map<UUID, Long> lastThrow = new HashMap<>();

    @EventHandler(priority = EventPriority.LAST)
    public void onPlayerUseEP(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK
		        || event.getItem() == null || event.getItem().getType() != Material.ENDER_PEARL
		        || event.useItemInHand() == Event.Result.DENY || event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

	    Player player = event.getPlayer();
        Long now = System.currentTimeMillis();

        if (validthrow(player, now)) {
            EnderPearlCDListener.lastThrow.put(player.getUniqueId(), now);
        } else {
            event.setCancelled(true);
        }
    }

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onIllegalEPTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			Player player = event.getPlayer();
			Block block = event.getTo().getBlock();
			if (block.getType() == Material.TRAP_DOOR || block.getType() == Material.FENCE_GATE
					|| block.getType() == Material.LADDER) {
				player.sendMessage(ChatColor.RED + "Cannot enderpearl here.");

				// Remove their CD timer
				EnderPearlCDListener.lastThrow.remove(player.getUniqueId());

				event.setCancelled(true);

				// Add their pearl back
				player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
				player.updateInventory();
			}
		}
	}

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	    // No memory leak
        EnderPearlCDListener.lastThrow.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        EnderPearlCDListener.lastThrow.remove(event.getEntity().getUniqueId());
    }

    public long remainingCooldown(Player player, long throwTime) {
        Long lastPlayerPearl = EnderPearlCDListener.lastThrow.get(player.getUniqueId());
        return (COOLDOWN - (throwTime - lastPlayerPearl)) / 1000L;
    }

    private boolean validthrow(Player player, long throwTime) {
        Long lastPlayerPearl = EnderPearlCDListener.lastThrow.get(player.getUniqueId());

        if ((lastPlayerPearl == null) || (throwTime - lastPlayerPearl >= COOLDOWN)) {
            return true;
        }

        player.sendMessage(ChatColor.RED + "Enderpearl cooldown remaining: " + remainingCooldown(player, throwTime) + " seconds.");
        return false;
    }

}
