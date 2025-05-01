package net.badlion.gfactions.listeners;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import net.badlion.gfactions.GFactions;

public class KillStreakListener implements Listener {
	
	private GFactions plugin;
	
	public KillStreakListener(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		// add them to our cache if they aren't already there
		if (!this.plugin.getKillStreakManager().getKillStreakMap().containsKey(player.getUniqueId().toString())) {
			this.plugin.getKillStreakManager().addKillNoobPointsForPlayer(player.getUniqueId().toString());
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerKill(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (player.getKiller() != null) {
			Player killer = player.getKiller();
			
			// Ok was this a "kill" or a "noob kill"
			ItemStack [] armor = player.getInventory().getArmorContents();
			boolean isNoob = true;
			for (ItemStack item : armor) {
				// TODO: Fix
				if (item != null && item.getTypeId() != 0) {
					isNoob = false;
					break;
				}
			}
			
			if (isNoob) {
				// Add to their noob kill streak and remove 2 kills from their current kill streak
				this.plugin.getKillStreakManager().addNoobKillToPlayer(killer.getUniqueId().toString());
				killer.sendMessage(ChatColor.RED + "Noob killing takes 2 points away from your kill streak and eventually has negative effects on yourself.");
				player.sendMessage(ChatColor.RED + "Killed by " + killer.getName());
			} else {
				// Ok were they in full diamond?
				if (player.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS
						&& player.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE
						&& player.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET
						&& player.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS) {
					// Add one to their "kill streak"
					this.plugin.getKillStreakManager().addKillToPlayer(killer.getUniqueId().toString());
					
					// Nice chat message system
					if (this.plugin.getKillStreakManager().getKillStreakMap().get(killer.getUniqueId().toString()) % 5 == 0 && this.plugin.getKillStreakManager().getKillStreakMap().get(killer.getUniqueId().toString()) <= 0) {
						Gberry.broadcastMessage(ChatColor.GREEN + killer.getName() + " killed " + player.getName() + " and now has a "
														+ this.plugin.getKillStreakManager().getKillStreakMap().get(killer.getUniqueId().toString()) + " killstreak.");
                        event.setDeathMessage(null);
					} else {
						Gberry.broadcastMessage(ChatColor.GREEN + killer.getName() + " killed " + player.getName());
                        event.setDeathMessage(null);
					}
				} else {
					player.sendMessage(ChatColor.RED + "Killed by " + killer.getName());
					player.sendMessage(ChatColor.GREEN + "Killed " + player.getName());
				}
			}
		} 
		
		// What if this person were on a kill streak and died to something else besides a player
		if (this.plugin.getKillStreakManager().getKillStreakMap().get(player.getUniqueId().toString()) > 0) {
			this.plugin.getKillStreakManager().resetKillStreakForPlayer(player.getUniqueId().toString());
			
			// TODO: MAKE THIS NICER
			Gberry.broadcastMessage(ChatColor.RED + player.getName() + "'s kill streak was ended by " + player.getLastDamageCause().getCause().name().replace('_', ' '));
            event.setDeathMessage(null);
        }
	}

}
