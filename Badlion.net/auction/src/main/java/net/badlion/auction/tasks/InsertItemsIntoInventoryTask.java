package net.badlion.auction.tasks;

import net.badlion.auction.Auction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class InsertItemsIntoInventoryTask extends BukkitRunnable {
	
	private Auction plugin;
	private UUID uuid;
	private ArrayList<ItemStack> items;
	private ArrayList<Long> times;
	
	public InsertItemsIntoInventoryTask(Auction plugin, UUID uuid, ArrayList<ItemStack> items, ArrayList<Long> times) {
		this.plugin = plugin;
		this.uuid = uuid;
		this.items = items;
		this.times = times;
	}
	
	@Override
	public void run() {
		final Player p = this.plugin.getServer().getPlayer(this.uuid);
		if (p == null) {
			// Screw them they logged out, do nothing
			return;
		}
		
		// Iterate through and add the items we can
		int it = 0;
		ItemStack [] pi = p.getInventory().getContents();
		boolean added = false;
		for (int i = 0; i < pi.length; i++) {
			if (pi[i] == null) {
				// Set our flag and add the item
				added = true;
				ItemStack item = items.get(it++);
				pi[i] = item;
				
				// Check to see if we are done
				if (it == items.size()) {
					break;
				}
			}
		}
		
		if (!added) {
			p.sendMessage(ChatColor.RED + "Your inventory is full right now. Claim items when you have room to put them in your inventory.");
			return;
		}
		
		// Update their inventory
		p.getInventory().setContents(pi);
		
		if (it != items.size()) {
			p.sendMessage(ChatColor.RED + "Your inventory could not fit all the items you have to claim.  We added the ones we could and the rest can be claimed later.");
			
			// Figure out which items to clear out of the database
			final ArrayList<Long> usedTimes = new ArrayList<Long>();
			for (int i = 0; i < it; i++) {
				usedTimes.add(this.times.get(i));
			}
			
			// Clear all these items out of the database
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
				
				@Override
				public void run() {
					plugin.deleteHeldAuctionItems(p.getUniqueId().toString(), usedTimes);
				}
				
			});
		} else {
			p.sendMessage(ChatColor.GREEN + "You have claimed all your items from the Auction House.");
			
			// Clear all the items out of the database
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
				
				@Override
				public void run() {
					plugin.deleteHeldAuctionItems(p.getUniqueId().toString(), times);
				}
				
			});
		}
	}

	public Auction getPlugin() {
		return plugin;
	}

	public void setPlugin(Auction plugin) {
		this.plugin = plugin;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public ArrayList<ItemStack> getItems() {
		return items;
	}

	public void setItems(ArrayList<ItemStack> items) {
		this.items = items;
	}

	public ArrayList<Long> getTimes() {
		return times;
	}

	public void setTimes(ArrayList<Long> times) {
		this.times = times;
	}

}
