package net.badlion.arenalobby.commands;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.exceptions.NoRatingFoundException;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.LadderManager;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.arenalobby.managers.RatingManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatsCommand extends GCommandExecutor {

	public StatsCommand() {
		super(0);
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		Player lookup = this.player;
		Group lookupGroup = this.group.clone();
		SmellyInventory smellyInventory;

		if (args.length == 0 || args[0].equalsIgnoreCase(this.player.getName())) {
			// Create smelly inventory
			smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(),
					45, ChatColor.AQUA + ChatColor.BOLD.toString() + "Your Stats");
		} else {
			lookup = ArenaLobby.getInstance().getServer().getPlayer(args[0]);
			if (lookup != null) {
				lookupGroup = ArenaLobby.getInstance().getPlayerGroup(lookup);

				// Create smelly inventory
				smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(),
						45, ChatColor.AQUA + ChatColor.BOLD.toString() + lookup.getName() + "'s Stats");
			} else {
				this.player.sendFormattedMessage("{0}That player is not online!", ChatColor.RED);
				return;
			}
		}

		if (!PotPvPPlayerManager.getPotPvPPlayer(lookup.getUniqueId()).isLoaded()) {
			this.player.sendFormattedMessage("{0}Data has not been loaded, try again in a few seconds!", ChatColor.RED);
			return;
		}

		final Group lookupGroup2 = lookupGroup;
		final Player lookup2 = lookup;
		final SmellyInventory smellyInventory2 = smellyInventory;
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {

				// Fill 1v1 ranked ladders
				Map<String, Ladder> oneVsOneRanked = LadderManager.getLadderMap(ArenaCommon.LadderType.RANKED_1V1);
				for (String kitRuleSet : oneVsOneRanked.keySet()) {
					smellyInventory2.getMainInventory().addItem(StatsCommand.this.getItemForLadder(kitRuleSet, oneVsOneRanked.get(kitRuleSet), lookupGroup2));
				}

				// Add global ladder item
				smellyInventory2.getMainInventory().setItem(18, StatsCommand.this.getItemForGlobalLadder(ArenaCommon.LadderType.RANKED_1V1, lookupGroup2));

				// Fill FFA info
		    /*
	        int counter = 27;
	        for (FFAWorld ffaWorld : FFAWorld.getFfaWorlds().values()) {
		        smellyInventory2.getMainInventory().setItem(counter, StatsCommand.this.getItemForFFA(ffaWorld, lookup2));

		        counter++;
	        }
			*/
				// Fill TDM info
				//smellyInventory2.getMainInventory().setItem(36, StatsCommand.this.getItemForTDM(lookup2));

				BukkitUtil.openInventory(StatsCommand.this.player, smellyInventory2.getMainInventory());
			}
		});
	}

	private ItemStack getItemForGlobalLadder(ArenaCommon.LadderType ladderType, Group group) {
		ItemStack item = new ItemStack(Material.DIAMOND_BLOCK);
		ItemMeta itemMeta = item.getItemMeta();
		if (ladderType == ArenaCommon.LadderType.RANKED_1V1) {
			itemMeta.setDisplayName(ChatColor.GREEN + "Global 1v1");
		} else if (ladderType == ArenaCommon.LadderType.RANKED_2V2) {
			itemMeta.setDisplayName(ChatColor.GREEN + "Global 2v2");
		}

		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.LIGHT_PURPLE.toString() + RatingUtil.Rank.getRankByElo(RatingManager.getDBPlayerGlobalRating(group.getLeader().getUniqueId())).getName());
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);

		return item;
	}

	private ItemStack getItemForLadder(String kitRuleSet, Ladder ladder, Group group) {
		ItemStack item = new ItemStack(ladder.getKitRuleSet().getKitItem());
		ItemMeta itemMeta = item.getItemMeta();
		if (ladder.getLadderType() == ArenaCommon.LadderType.RANKED_1V1) {
			itemMeta.setDisplayName(ChatColor.GREEN + kitRuleSet + " 1v1");
		} else if (ladder.getLadderType() == ArenaCommon.LadderType.RANKED_2V2) {
			itemMeta.setDisplayName(ChatColor.GREEN + kitRuleSet + " 2v2");
		} else if (ladder.getLadderType() == ArenaCommon.LadderType.RANKED_3V3) {
			itemMeta.setDisplayName(ChatColor.GREEN + kitRuleSet + " 3v3");
		} else {
			itemMeta.setDisplayName(ChatColor.GREEN + kitRuleSet + " 5v5");
		}

		List<String> lore = new ArrayList<>();
		try {
			lore.add(ChatColor.LIGHT_PURPLE.toString() + RatingUtil.Rank.getRankByElo(RatingManager.getGroupRating(group, ladder)).getName());
		} catch (NoRatingFoundException e) {
			lore.add(ChatColor.LIGHT_PURPLE.toString() + RatingUtil.Rank.NONE.getName());
		}
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);

		return item;
	}

	/*
	private ItemStack getItemForFFA(FFAWorld ffaWorld, Player player) {
		ItemStack item = new ItemStack(ffaWorld.getFFAItem().getType(), 1, ffaWorld.getFFAItem().getDurability());
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + ffaWorld.getFFAItem().getItemMeta().getDisplayName().substring(7));

		FFAManager.FFAStats ffaStats = FFAManager.getFFAStats(player.getUniqueId(), ffaWorld.getKitRuleSet());
		List<String> lore = new ArrayList<>();
		if (ffaStats.getDeaths() != 0) {
			lore.add(ChatColor.YELLOW + "KDR: " + String.format("%.2f", ffaStats.getTotalKills() / (double) ffaStats.getTotalDeaths()));
		} else {
			lore.add(ChatColor.YELLOW + "KDR: N/A");
		}
		lore.add(ChatColor.YELLOW + "Kills: " + ffaStats.getTotalKills());
		lore.add(ChatColor.YELLOW + "Deaths: " + ffaStats.getTotalDeaths());
		lore.add(ChatColor.YELLOW + "Max Killstreak: " + ffaStats.getMaxKillStreak());

		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);

		return item;
	}
	*/
	@Override
	public void usage(CommandSender sender) {

	}

}
