package net.badlion.auction.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.badlion.auction.Auction;

public class AuctionListener implements Listener {
	
	private Auction plugin;
	
	public AuctionListener(Auction plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			
			@Override
			public void run() {
				// Check to see if they have any auction items that need to be claimed
				if (plugin.getHeldAuctionItems(player.getUniqueId().toString()) != null) {
					player.sendMessage(ChatColor.YELLOW + "You have items waiting for claim in the auction house. Use \"/auction claim\" when you have space in your inventory.");
				}
				
				// Check to see if they have alerts enabled for auctions
				if (!plugin.arePlayerAlertsOn(player.getUniqueId().toString())) {
					
				} else {
					// Add by default
					plugin.getAuctionCommand().getPlayersWhoWantAuctionMessages().add(player);
				}
			}
			
		});
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		this.plugin.getAuctionCommand().getPlayersWhoWantAuctionMessages().remove(player);
	}
}
