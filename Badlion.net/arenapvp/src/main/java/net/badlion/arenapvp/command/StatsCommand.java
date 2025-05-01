package net.badlion.arenapvp.command;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.manager.RatingManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatsCommand implements CommandExecutor {

	private List<UUID> fetchingStats = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Player only command");
			return false;
		}
		Player player = (Player) sender;
		if(this.fetchingStats.contains(player.getUniqueId())){
			sender.sendMessage(ChatColor.RED + "You can not use this command right now.");
			return false;
		}

		SmellyInventory smellyInventory;

		Player lookup;

		if (args.length == 0 || args[0].equalsIgnoreCase(sender.getName())) {
			// Create smelly inventory
			smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(),
					45, ChatColor.AQUA + ChatColor.BOLD.toString() + "Your Stats");
			lookup = player;
		} else {
			lookup = ArenaPvP.getInstance().getServer().getPlayer(args[0]);
			if (lookup != null) {
				// Create smelly inventory
				smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(),
						45, ChatColor.AQUA + ChatColor.BOLD.toString() + lookup.getDisguisedName() + "'s Stats");
			} else {
				sender.sendMessage(ChatColor.RED + "That player is not online!");
				return false;
			}
		}


		final SmellyInventory smellyInventory2 = smellyInventory;
		final Player finalLookup = lookup;

		if (RatingManager.getRatings(lookup) == null) {
			this.fetchingStats.add(player.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						RatingManager.getAllDBUserRatings(finalLookup.getUniqueId());
					} catch (Exception e) {
						e.printStackTrace();
						StatsCommand.this.fetchingStats.remove(player.getUniqueId());
						player.sendFormattedMessage("{0}Unable to load stats, try again later.", ChatColor.RED);
						return;
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							if (player != null && player.isOnline()) {
								for (KitRuleSet kitRuleSet : RatingManager.rankedKitLadders) {
									smellyInventory2.getMainInventory().addItem(StatsCommand.this.getItemForLadder(kitRuleSet, lookup));
								}
								smellyInventory2.getMainInventory().setItem(18, StatsCommand.this.getItemForGlobalLadder(lookup));
								BukkitUtil.openInventory(player, smellyInventory2.getMainInventory());
								StatsCommand.this.fetchingStats.remove(player.getUniqueId());
							}
						}
					}.runTask(ArenaPvP.getInstance());
				}
			}.runTaskAsynchronously(ArenaPvP.getInstance());
		}else{
			for (KitRuleSet kitRuleSet : RatingManager.rankedKitLadders) {
				smellyInventory2.getMainInventory().addItem(StatsCommand.this.getItemForLadder(kitRuleSet, lookup));
			}
			smellyInventory2.getMainInventory().setItem(18, StatsCommand.this.getItemForGlobalLadder(lookup));
			BukkitUtil.openInventory(player, smellyInventory2.getMainInventory());
		}

		return false;
	}

	private ItemStack getItemForGlobalLadder(Player player) {
		ItemStack item = new ItemStack(Material.DIAMOND_BLOCK);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "Global 1v1");

		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.LIGHT_PURPLE.toString() + RatingUtil.Rank.getRankByElo(RatingManager.getGlobalRating(player)).getName());
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);

		return item;
	}

	private ItemStack getItemForLadder(KitRuleSet kitRuleSet, Player player) {
		ItemStack item = new ItemStack(kitRuleSet.getKitItem());
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + kitRuleSet.getName() + " 1v1");

		List<String> lore = new ArrayList<>();
		try {
			lore.add(ChatColor.LIGHT_PURPLE.toString() + RatingUtil.Rank.getRankByElo(RatingManager.getRatings(player).get(kitRuleSet.getId())).getName());
		} catch (NullPointerException e) {
			lore.add(ChatColor.LIGHT_PURPLE.toString() + RatingUtil.Rank.NONE.getName());
		}
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);

		return item;
	}

}
