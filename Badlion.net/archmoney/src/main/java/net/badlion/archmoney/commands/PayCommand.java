package net.badlion.archmoney.commands;

import net.badlion.archmoney.ArchMoney;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {
	
	private ArchMoney plugin;
	
	public PayCommand(ArchMoney plugin){
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)){
			Bukkit.getLogger().info(ChatColor.RED + "This command is only for players!");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args.length != 2){
			return false;
		}
		
		int amount;
		
		try{
			amount = Integer.parseInt(args[1]);
		} catch(Exception e) {
			player.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Please enter a valid number.");
			return true;
		}

		if (amount == 0){
			player.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Please enter a valid number.");
			return true;
		} else if (amount < 0){
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "kick " + player.getName() + " Do not attempt to exploit plugins!  Multiple offenses will result in you being permanently banned!");
			return true;
		}
		
		final String player_uuid = player.getUniqueId().toString();
		
		if (amount > plugin.checkBalance(player_uuid)){
			player.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "You do not have sufficient funds.");
			return true;
		}
		
		String to_uuid;
		
		Player toPlayer = Bukkit.getPlayerExact(args[0]);
		if (toPlayer == null){
			player.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Player not found.");
			return true;
		} else {
			to_uuid = toPlayer.getUniqueId().toString();
		}
				
		plugin.transfer(to_uuid, player_uuid, amount, "Player transfer using /pay");
		
		player.sendMessage(ChatColor.GREEN + "$" + amount + " has been sent to " + ChatColor.YELLOW + toPlayer.getName());
		toPlayer.sendMessage(ChatColor.GREEN + "$" + amount + " has been received from " + ChatColor.YELLOW + player.getName());
		
		return true;
	}
}
