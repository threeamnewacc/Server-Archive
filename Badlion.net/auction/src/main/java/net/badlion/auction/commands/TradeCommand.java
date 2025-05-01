package net.badlion.auction.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.badlion.auction.Auction;

public class TradeCommand implements CommandExecutor {
	
	private Auction plugin;
	
	public TradeCommand(Auction plugin) {
		this.plugin = plugin;
	}
	
	private void helpTrade(Player player) {
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] tmpArgs) {
	
		return true;
	}
	
	private void startTrade(Player player) {
		
	}
	
	private void cancelTrade(Player player) {
		
	}
	
	private void offerTrade(Player player) {
		
	}
	
	private void acceptTrade(Player player) {
		
	}
	
	private void denyTrade(Player player) {
		
	}

}
