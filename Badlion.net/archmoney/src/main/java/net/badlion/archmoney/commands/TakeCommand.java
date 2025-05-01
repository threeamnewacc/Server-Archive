package net.badlion.archmoney.commands;

import net.badlion.archmoney.ArchMoney;
import net.badlion.gberry.Gberry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TakeCommand implements CommandExecutor {
	
	private ArchMoney plugin;
	
	public TakeCommand(ArchMoney plugin){
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		final int amount;
		final String issuer_uuid;
		Player player = null;
		
		if (sender instanceof Player){
			player = (Player) sender;
		
			if (!player.hasPermission("ArchMoney.take")){
				player.sendMessage("You do not have permission to use this command");
				return true;
			}
			
			if (args.length < 3){
				player.sendMessage("Correct usage is /moneytake <name/uuid> <amount> <reason>");
				return true;
			}
			
			try{
				amount = Integer.parseInt(args[1]);
			} catch(Exception e) {
				player.sendMessage(ChatColor.RED + "Please enter a valid number");
				return true;
			}
			
			if (amount <= 0){
				player.sendMessage(ChatColor.RED + "Enter a number greater then zero, YOU SHOULD KNOW BETTER!");
				return true;
			}
			
			issuer_uuid = player.getUniqueId().toString();
		} else {
			if (args.length < 3){
				Bukkit.getLogger().info("Correct usage is /moneytake <name/uuid> <amount> <reason>");
				return true;
			}
			
			try{
				amount = Integer.parseInt(args[1]);
			} catch(Exception e) {
				Bukkit.getLogger().info(ChatColor.RED + "Please enter a valid number");
				return true;
			}
			if (amount <= 0){
				Bukkit.getLogger().info(ChatColor.RED + "Enter a number greater then zero, YOU SHOULD KNOW BETTER!");
				return true;
			}
			
			issuer_uuid = "~Console";
		}

		
    	StringBuilder reason_builder = new StringBuilder();
    	for (int i = 2; i < args.length; i++){
    		reason_builder.append(args[i]);
    		reason_builder.append(" ");
    	}
    	
    	final String reason = "TAKEMONEY: " + reason_builder.toString().substring(0, reason_builder.toString().length() - 1);
    	final Player playerFinal = player;
    	
    	if (args[0].startsWith("~") || args[0].length() > 16){
    		Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
    			public void run(){
		    		String change_uuid = args[0];
		    		if (plugin.checkBalanceSQL(change_uuid) == -1){
		    			if (playerFinal != null){
		    				playerFinal.sendMessage(ChatColor.RED + "Invalid UUID");
		    			} else {
		    				Bukkit.getLogger().info(ChatColor.RED + "Invalid UUID");
		    			}
		    		} else {
			    		plugin.changeBalance(change_uuid, amount * -1);
	    				plugin.logTransaction(issuer_uuid, change_uuid, amount, reason);
		    		}
    			}
    		});
    	} else {
    		Player change_player = Bukkit.getPlayerExact(args[0]);
    		if (change_player == null){
    			Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {	
    	    		public void run(){
    	    			String change_uuid = Gberry.getOfflineUUID(args[0]).toString();
    		    		if (plugin.checkBalanceSQL(change_uuid) == -1){
    		    			if (playerFinal != null){
    		    				playerFinal.sendMessage(ChatColor.RED + "Invalid UUID");
    		    			} else {
    		    				Bukkit.getLogger().info(ChatColor.RED + "Invalid UUID");
    		    			}
    		    		} else {
	    	    			plugin.changeBalance(change_uuid, amount * -1);
	    	    			plugin.logTransaction(issuer_uuid, change_uuid, amount, reason);
    		    		}
    	    		}
    			});
    		} else {
    			final String change_uuid = change_player.getUniqueId().toString();
    			plugin.changeBalance(change_uuid, amount * -1);
    			change_player.sendMessage(ChatColor.BLUE + "You have had " + ChatColor.GOLD + amount + ChatColor.BLUE + " taken by an admin");
        		Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
        			public void run(){
        				plugin.logTransaction(issuer_uuid, change_uuid, amount, reason);
        			}
        		});
    		}
    	}
    	
		return true;
	}

}