package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import net.badlion.gpermissions.Group;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RanksCommand implements CommandExecutor {

    private GFactions plugin;

    public HashMap<String, String> donationNames = new HashMap<String, String>();

    public RanksCommand(GFactions plugin) {
        this.plugin = plugin;

        this.donationNames.put("default", "Peasant");
        this.donationNames.put("member", "Villager");
        this.donationNames.put("squire", "Squire");
        this.donationNames.put("stone", "Knight");
        this.donationNames.put("coal", "Musketeer");
        this.donationNames.put("gold", "Duke");
        this.donationNames.put("iron", "Archduke");
        this.donationNames.put("princess", "Princess");
        this.donationNames.put("diamond", "Prince");
        this.donationNames.put("emerald", "King");
        this.donationNames.put("queen", "Queen");
        this.donationNames.put("mod", "Chat Mod");
        this.donationNames.put("admin", "Admin");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            // Get the player's rank
			Player player = (Player) sender;
            Group group = this.plugin.getGperms().getUserGroup(((Player) sender).getUniqueId());

            String color = group.getPrefix().substring(0, 2).replaceAll("&","§");
            String name = group.getName();
            name = this.donationNames.get(name);

			/*if (args.length == 2) {
				if (args[0].equalsIgnoreCase("buy")) {
					if (args[1].equalsIgnoreCase("knight")) {
						if (sender.hasPermission("GFactions.stone")) {
							sender.sendMessage(ChatColor.RED + "You already have this rank.");
						} else {
							if (this.plugin.getArchMoney().checkBalance(player.getUniqueId().toString()) > 500000) {
								this.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), -500000, "Purchase Knight Rank");
								Bukkit.dispatchCommand(this.plugin.getServer().getConsoleSender(), "user " + player.getUniqueId().toString() + " setgroup stone");
							} else {
								sender.sendMessage(ChatColor.RED + "You do not have enough funds to purchase this rank.");
							}
						}
					} else if (args[1].equalsIgnoreCase("musketeer")) {
						if (sender.hasPermission("GFactions.coal")) {
							sender.sendMessage(ChatColor.RED + "You already have this rank.");
						} else {
                            if (!sender.hasPermission("GFactions.stone")) {
                                sender.sendMessage(ChatColor.RED + "You must purchase Knight first before buying this rank.");
                            } else if (this.plugin.getArchMoney().checkBalance(player.getUniqueId().toString()) > 1000000) {
								this.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), -1000000, "Purchase Musketeer Rank");
								Bukkit.dispatchCommand(this.plugin.getServer().getConsoleSender(), "user " + player.getUniqueId().toString() + " setgroup coal");
							} else {
								sender.sendMessage(ChatColor.RED + "You do not have enough funds to purchase this rank.");
							}
						}
					} else if (args[1].equalsIgnoreCase("duke")) {
						if (sender.hasPermission("GFactions.gold")) {
							sender.sendMessage(ChatColor.RED + "You already have this rank.");
						} else {
                            if (!sender.hasPermission("GFactions.coal")) {
                                sender.sendMessage(ChatColor.RED + "You must purchase Musketeer first before buying this rank.");
                            } else if (this.plugin.getArchMoney().checkBalance(player.getUniqueId().toString()) > 2000000) {
								this.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), -2000000, "Purchase Duke Rank");
								Bukkit.dispatchCommand(this.plugin.getServer().getConsoleSender(), "user " + player.getUniqueId().toString() + " setgroup gold");
							} else {
								sender.sendMessage(ChatColor.RED + "You do not have enough funds to purchase this rank.");
							}
						}
					} else if (args[1].equalsIgnoreCase("archduke")) {
						if (sender.hasPermission("GFactions.iron")) {
							sender.sendMessage(ChatColor.RED + "You already have this rank.");
						} else {
                            if (!sender.hasPermission("GFactions.gold")) {
                                sender.sendMessage(ChatColor.RED + "You must purchase Duke first before buying this rank.");
                            } else if (this.plugin.getArchMoney().checkBalance(player.getUniqueId().toString()) > 4000000) {
								this.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), -4000000, "Purchase Archduke Rank");
								Bukkit.dispatchCommand(this.plugin.getServer().getConsoleSender(), "user " + player.getUniqueId().toString() + " setgroup iron");
							} else {
								sender.sendMessage(ChatColor.RED + "You do not have enough funds to purchase this rank.");
							}
						}
					} else if (args[1].equalsIgnoreCase("prince")) {
						if (sender.hasPermission("GFactions.diamond")) {
							sender.sendMessage(ChatColor.RED + "You already have this rank.");
						} else {
                            if (!sender.hasPermission("GFactions.iron")) {
                                sender.sendMessage(ChatColor.RED + "You must purchase ArchDuke first before buying this rank.");
                            } else if (this.plugin.getArchMoney().checkBalance(player.getUniqueId().toString()) > 8000000) {
								this.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), -8000000, "Purchase Prince Rank");
								Bukkit.dispatchCommand(this.plugin.getServer().getConsoleSender(), "user " + player.getUniqueId().toString() + " setgroup diamond");
							} else {
								sender.sendMessage(ChatColor.RED + "You do not have enough funds to purchase this rank.");
							}
						}
					} else if (args[1].equalsIgnoreCase("princess")) {
						if (sender.hasPermission("GFactions.diamond")) {
							sender.sendMessage(ChatColor.RED + "You already have this rank.");
						} else {
                            if (!sender.hasPermission("GFactions.iron")) {
                                sender.sendMessage(ChatColor.RED + "You must purchase ArchDuke first before buying this rank.");
                            } else if (this.plugin.getArchMoney().checkBalance(player.getUniqueId().toString()) > 8000000) {
								this.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), -8000000, "Purchase Princess Rank");
								Bukkit.dispatchCommand(this.plugin.getServer().getConsoleSender(), "user " + player.getUniqueId().toString() + " setgroup princess");
							} else {
								sender.sendMessage(ChatColor.RED + "You do not have enough funds to purchase this rank.");
							}
						}
					} else if (args[1].equalsIgnoreCase("king")) {
						if (sender.hasPermission("GFactions.emerald")) {
							sender.sendMessage(ChatColor.RED + "You already have this rank.");
						} else {
                            if (!sender.hasPermission("GFactions.diamond")) {
                                sender.sendMessage(ChatColor.RED + "You must purchase Prince first before buying this rank.");
                            } else if (this.plugin.getArchMoney().checkBalance(player.getUniqueId().toString()) > 16000000) {
								this.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), -16000000, "Purchase King Rank");
								Bukkit.dispatchCommand(this.plugin.getServer().getConsoleSender(), "user " + player.getUniqueId().toString() + " setgroup emerald");
							} else {
								sender.sendMessage(ChatColor.RED + "You do not have enough funds to purchase this rank.");
							}
						}
					} else if (args[1].equalsIgnoreCase("queen")) {
						if (sender.hasPermission("GFactions.emerald")) {
							sender.sendMessage(ChatColor.RED + "You already have this rank.");
						} else {
                            if (!sender.hasPermission("GFactions.diamond")) {
                                sender.sendMessage(ChatColor.RED + "You must purchase Princess first before buying this rank.");
                            } else if (this.plugin.getArchMoney().checkBalance(player.getUniqueId().toString()) > 16000000) {
								this.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), -16000000, "Purchase Queen Rank");
								Bukkit.dispatchCommand(this.plugin.getServer().getConsoleSender(), "user " + player.getUniqueId().toString() + " setgroup queen");
							} else {
								sender.sendMessage(ChatColor.RED + "You do not have enough funds to purchase this rank.");
							}
						}
					}

					return true;
				}
			}*/

            sender.sendMessage(ChatColor.DARK_AQUA + "=====Donator Ranks=====");
            sender.sendMessage(" - Peasant..............................Default");
            sender.sendMessage(" - Villager..............................Registered (/register)");
            //sender.sendMessage(" - §2Squire..............................$5");
            sender.sendMessage(" - §5Knight..............................$15 USD");
            sender.sendMessage(" - §eMusketeer..............................$35 USD");
            sender.sendMessage(" - §dDuke..............................$50 USD");
            sender.sendMessage(" - §aArchduke..............................$80 USD");
            sender.sendMessage(" - §bPrince/Princess..............................$110 USD");
            sender.sendMessage( " - §6King/Queen..............................$150 USD");
            sender.sendMessage("Your rank: " + color
                    + name.substring(0, 1).toUpperCase() + name.substring(1));
            sender.sendMessage(ChatColor.GOLD + "To buy these ranks go to http://store.badlion.net/");
            sender.sendMessage(ChatColor.GOLD + "Be on the look out for sales and promotions throughout the year!");

        }
        return true;
    }

}
