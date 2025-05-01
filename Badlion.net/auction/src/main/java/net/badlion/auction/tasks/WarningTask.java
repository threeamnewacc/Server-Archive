package net.badlion.auction.tasks;

import io.github.andrepl.chatlib.Text;
import net.badlion.auction.Auction;
import net.badlion.auction.ItemForSale;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WarningTask extends BukkitRunnable {

	private Auction plugin;
	private int timeRemaining;

	public WarningTask(Auction plugin, int timeRemaining) {
		this.plugin = plugin;
		this.timeRemaining = timeRemaining;
	}

	@Override
	public void run() {
		ItemForSale item = this.plugin.getItemUpForSale();

		String itemString = this.plugin.getAuctionCommand().getPrefix() + item.getPlayer().getName() + ChatColor.BLUE + " is auctioning " + ChatColor.GOLD + item.getItem().getAmount() + ChatColor.AQUA + " [";
		String totalPriceString = "";
        if (!item.getBids().isEmpty()) {
            totalPriceString = this.plugin.getAuctionCommand().getPrefix() + "Current bid is " + ChatColor.YELLOW
                    + (item.getCurrentBid() == 0 ? item.getPrice() : item.getCurrentBid()) + ChatColor.BLUE
                    + " and minimum bid increment is " + ChatColor.YELLOW + item.getIncrement();
        } else {
            totalPriceString = this.plugin.getAuctionCommand().getPrefix() + "Bidding starts at " + ChatColor.YELLOW
                    + item.getPrice() + ChatColor.BLUE
                    + " and minimum bid increment is " + ChatColor.YELLOW + item.getIncrement();
        }
		// New way, use ChatLib library and fukkit im done and going home
		Text text = new Text(itemString);
		text.appendItem(item.getItem());
        text.append("§b]"); // Hardcoded ChatColor.AQUA to work
		String warningMessage = this.plugin.getAuctionCommand().getPrefix() + timeRemaining + " seconds remaining on current auction.";
		for (Player p :  this.plugin.getAuctionCommand().getPlayersWhoWantAuctionMessages()) {
			text.send(p);
			p.sendMessage(totalPriceString);
			p.sendMessage(warningMessage);
		}
	}

}
