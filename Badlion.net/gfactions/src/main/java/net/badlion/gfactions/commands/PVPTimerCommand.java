package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PVPTimerCommand implements CommandExecutor {
	
	private GFactions plugin;
	
	public PVPTimerCommand(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof Player) {
			/*final Player player = (Player) sender;
            if (this.plugin.getMapNameToPvPTimeRemaining().containsKey(player.getUniqueId().toString())) {
                long currentTime = System.currentTimeMillis();
                long joinTime = this.plugin.getMapNameToJoinTime().get(player.getUniqueId().toString());
                long timeRemaining = plugin.getMapNameToPvPTimeRemaining().get(player.getUniqueId().toString());
                timeRemaining -= currentTime - joinTime;

                if (timeRemaining <= 0) {
                    player.sendMessage(ChatColor.RED + "No PVP Protection Time Remaining.");

                    // Their PVP protection is over, time to remove from the system
                    this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
                    this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                        @Override
                        public void run() {
                            // Purge from DB
                            plugin.removeProtection(player);
                        }
                    });
                } else {
                    player.sendMessage(ChatColor.RED + "PVP Protection Remaining: " + timeRemaining / 60000 + " minutes and " + timeRemaining / 1000 % 60 + " seconds.");
                }

            } else {
                player.sendMessage(ChatColor.RED + "No PVP Protection Time Remaining.");
            }*/

			final Player player = (Player) sender;
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("check")) {
                    if (this.plugin.getMapNameToPvPTimeRemaining().containsKey(player.getUniqueId().toString())) {
                        long currentTime = System.currentTimeMillis();
                        long joinTime = this.plugin.getMapNameToJoinTime().get(player.getUniqueId().toString());
                        long timeRemaining = plugin.getMapNameToPvPTimeRemaining().get(player.getUniqueId().toString());
                        timeRemaining -= currentTime - joinTime;

                        if (timeRemaining <= 0) {
                            player.sendMessage(ChatColor.RED + "No PVP Protection Time Remaining.");

                            // Their PVP protection is over, time to remove from the system
                            this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
                            this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

                            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                                @Override
                                public void run() {
                                    // Purge from DB
                                    plugin.removeProtection(player);
                                }
                            });
                        } else {
                            player.sendMessage(ChatColor.RED + "PVP Protection Remaining: " + timeRemaining / 60000 + " minutes and " + timeRemaining / 1000 % 60 + " seconds.");
                        }

                    } else {
                        player.sendMessage(ChatColor.RED + "No PVP Protection Time Remaining.");
                    }
				} else if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("remove")) {
					// They don't want their PVP Protection
					if (this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString()) != null) {
						this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

						this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

							@Override
							public void run() {
								player.sendMessage(ChatColor.GREEN + "You have enabled PVP.");

								// Purge from DB
								plugin.removeProtection(player);
							}
						});
					} else {
						player.sendMessage(ChatColor.GREEN + "PVP is already enabled.");
					}
				} else {
					player.sendMessage(ChatColor.BLUE + "===PvPTimer Help===");
					player.sendMessage(ChatColor.GRAY + "/pvptimer remove" + ChatColor.WHITE + " - Remove your protection");
					player.sendMessage(ChatColor.GRAY + "/pvptimer check" + ChatColor.WHITE + " - Check how much protection time you have left");
				}
			} else {
				player.sendMessage(ChatColor.BLUE + "===PvPTimer Help===");
				player.sendMessage(ChatColor.GRAY + "/pvptimer remove" + ChatColor.WHITE + " - Remove your protection");
				player.sendMessage(ChatColor.GRAY + "/pvptimer check" + ChatColor.WHITE + " - Check how much protection time you have left");
			}
		}
		return true;
	}

}
