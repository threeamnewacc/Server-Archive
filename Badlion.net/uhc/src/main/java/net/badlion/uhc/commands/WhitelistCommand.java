package net.badlion.uhc.commands;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class WhitelistCommand implements CommandExecutor {

	private Set<UUID> whitelisting = new HashSet<>();
	private Map<UUID, Set<String>> donatorWhitelists = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
		if (BadlionUHC.getInstance().hasHostPermissions(sender)) {
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("all")) {
					WhitelistCommand.whitelistAllPlayers(sender);
				} else if (args[0].equalsIgnoreCase("add")) {
					if (args.length == 2) {
						if (!BadlionUHC.getInstance().getWhitelist().contains(args[1].toLowerCase())) {
							if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.MAXPLAYERS.name()).getValue() != null) {
								BadlionUHC.getInstance().getWhitelist().add(args[1].toLowerCase());
								sender.sendMessage(ChatColor.GREEN + "Player added to whitelist");
							} else {
								sender.sendMessage(ChatColor.RED + "Set max players in config first.");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "That player is already whitelisted");
						}
					} else {
						this.hostHelpMessage(sender);
					}
				} else if (args[0].equalsIgnoreCase("rm") || args[0].equalsIgnoreCase("remove")) {
					if (args.length == 2) {
						if (BadlionUHC.getInstance().getWhitelist().remove(args[1].toLowerCase())) {
							sender.sendMessage(ChatColor.GREEN + "Player removed from whitelist");
						} else {
							sender.sendMessage(ChatColor.RED + "That player is not whitelisted");
						}
					} else {
						this.hostHelpMessage(sender);
					}
				} else if (args[0].equalsIgnoreCase("on")) {
					if (!BadlionUHC.getInstance().isWhitelistBoolean()) {
						BadlionUHC.getInstance().setWhitelistBoolean(true);
						sender.sendMessage(ChatColor.YELLOW + "You have enabled the whitelist");
					} else {
						sender.sendMessage(ChatColor.YELLOW + "The whitelist is already enabled");
					}
				} else if (args[0].equalsIgnoreCase("off")) {
					if (BadlionUHC.getInstance().isWhitelistBoolean()) {
						BadlionUHC.getInstance().setWhitelistBoolean(false);
						sender.sendMessage(ChatColor.YELLOW + "You have disabled the whitelist");
					} else {
						sender.sendMessage(ChatColor.YELLOW + "The whitelist is already disabled");
					}
				} else {
					this.hostHelpMessage(sender);
				}
			} else {
				this.hostHelpMessage(sender);
			}
		} else if (sender instanceof Player) {
			final Player player = (Player) sender;
			// For special games don't allow whitelisting
			if (this.isHighEnoughRank(player) && BadlionUHC.getInstance().isAllowDonators()) {
				if (args.length > 1) {
					if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.PRE_START) {
						if (args[0].equalsIgnoreCase("add")) {
							int number = this.getNumberCanWhitelist(player);
							if (number == 0) {
								player.sendMessage(ChatColor.RED + "You can not whitelist anymore players!");
								return true;
							}

							// Is this player already online?
							if (BadlionUHC.getInstance().getServer().getPlayerExact(args[1]) != null) {
								player.sendMessage(ChatColor.RED + args[1] + " is already on the server!");
								return true;
							}

							// Is this player already whitelisted?
							if (BadlionUHC.getInstance().getWhitelist().contains(args[1].toLowerCase())) {
								player.sendMessage(ChatColor.RED + args[1] + " is already whitelisted!");
								return true;
							}

							// Is this player already whitelisting someone?
							if (this.whitelisting.contains(player.getUniqueId())) {
								player.sendMessage(ChatColor.RED + "You are already attempting to whitelist someone!");
								return true;
							}

							this.whitelisting.add(player.getUniqueId());

							// Find UUID of player
							BadlionUHC.getInstance().getServer().getScheduler().runTaskAsynchronously(BadlionUHC.getInstance(), new Runnable() {
								@Override
								public void run() {
									final UUID uuid = Gberry.getOfflineUUID(args[1]);
									if (uuid == null) {
										player.sendMessage(ChatColor.RED + "Invalid player name.");
										WhitelistCommand.this.whitelisting.remove(player.getUniqueId());
										return;
									}

									BadlionUHC.getInstance().getServer().getScheduler().runTask(BadlionUHC.getInstance(), new Runnable() {
										@Override
										public void run() {
											// Is this player a donator?
											if (BadlionUHC.getInstance().getgPermissions().userHasPermission(uuid.toString(), "badlion.donator")) {
												player.sendMessage(ChatColor.RED + "Donators already have pre-whitelist access!");
												WhitelistCommand.this.whitelisting.remove(player.getUniqueId());
												return;
											}

											// Check & update cache at same time
											if (WhitelistCommand.this.donatorWhitelists.get(player.getUniqueId()).add(args[1].toLowerCase())) {
												BadlionUHC.getInstance().getWhitelist().add(args[1].toLowerCase());

												player.sendMessage(ChatColor.GREEN + "You have whitelisted " + args[1] + "!");
											} else {
												player.sendMessage(ChatColor.RED + args[1] + " is already on your whitelist!");
											}

											WhitelistCommand.this.whitelisting.remove(player.getUniqueId());
										}
									});
								}
							});
						} else if (args[0].equalsIgnoreCase("rm") || args[0].equalsIgnoreCase("remove")) {
							// Check & update cache at same time
							if (!this.donatorWhitelists.get(player.getUniqueId()).remove(args[1].toLowerCase())) {
								player.sendMessage(ChatColor.RED + "You haven't whitelisted " + args[1] + "!");
								return true;
							}
							BadlionUHC.getInstance().getWhitelist().remove(args[1].toLowerCase());

							// Kick that player
							Player removed = BadlionUHC.getInstance().getServer().getPlayerExact(args[1]);
							if (removed != null) {
								removed.kickPlayer(player.getDisguisedName() + " has unwhitelisted you!");
							}

							player.sendMessage(ChatColor.GREEN + "You have unwhitelisted " + args[1] + "!");
						} else {
							this.donatorHelpMessage(player);
						}
					} else {
						player.sendMessage(ChatColor.RED + "You can only whitelist/unwhitelist players before the countdown starts!");
					}
				} else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
					Set<String> whitelistedPlayers = this.donatorWhitelists.get(player.getUniqueId());
					if (whitelistedPlayers != null && whitelistedPlayers.size() > 0) {
						String str = ChatColor.GREEN + "Your whitelisted players: ";

						for (String playerName : whitelistedPlayers) {
							str += playerName + ", ";
						}

						str = str.substring(0, str.length() - 2);

						player.sendMessage(str);
					} else {
						player.sendMessage(ChatColor.RED + "You haven't whitelisted anybody!");
					}
				} else {
					this.donatorHelpMessage(player);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
		}
		return true;
	}

	public static void whitelistAllPlayers(CommandSender sender) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			String name = p.getName();
			BadlionUHC.getInstance().getWhitelist().add(name.toLowerCase());
		}

		sender.sendMessage(ChatColor.GREEN + "All players added to whitelist");
	}

	public boolean isHighEnoughRank(Player player) {
		return player.hasPermission("badlion.lion");
	}

	public int getNumberCanWhitelist(Player player) {
		boolean lionPlus = player.hasPermission("badlion.lionplus");
		Set<String> whitelistedPlayers = this.donatorWhitelists.get(player.getUniqueId());
		if (whitelistedPlayers != null) {
			return lionPlus ? 2 - whitelistedPlayers.size() : 1 - whitelistedPlayers.size();
		}

		// Add them to the cache
		this.donatorWhitelists.put(player.getUniqueId(), new HashSet<String>());

		return lionPlus ? 2 : 1;
	}

	public void donatorHelpMessage(Player player) {
		player.sendMessage(ChatColor.GOLD + "===Whitelist Commands===");
		player.sendMessage(ChatColor.GOLD + "You can whitelist " + this.getNumberCanWhitelist(player) + " more player(s)");
		player.sendMessage(ChatColor.GOLD + "/wl list - List the players you've whitelisted");
		player.sendMessage(ChatColor.GOLD + "/wl add <player> - Adds a player to whitelist");
		player.sendMessage(ChatColor.GOLD + "/wl rm <player> - Removes a player from whitelist");
		player.sendMessage(ChatColor.GOLD + "Warning: You can only whitelist/unwhitelist players before the countdown starts!");
	}

	public void hostHelpMessage(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "===Whitelist Commands===");
		sender.sendMessage(ChatColor.GOLD + "Amount currently whitelisted: " + BadlionUHC.getInstance().getWhitelist().size());
		sender.sendMessage(ChatColor.GOLD + "/wl on:off - Enable/Disable the whitelist");
		sender.sendMessage(ChatColor.GOLD + "/wl all - Whitelists all online players");
		sender.sendMessage(ChatColor.GOLD + "/wl add <player> - Adds a player to whitelist");
		sender.sendMessage(ChatColor.GOLD + "/wl rm <player> - Removes a player from whitelist");
		sender.sendMessage(ChatColor.GOLD + "Warning: You will not be able to manually whitelist players " +
				"if it goes over the UHC player limit you set");
	}

}
