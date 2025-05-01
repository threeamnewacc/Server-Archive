package net.badlion.potpvp.commands;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.exceptions.NoRatingFoundException;
import net.badlion.potpvp.ffaworlds.FFAWorld;
import net.badlion.potpvp.helpers.LobbyItemHelper;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.FFAManager;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.managers.RatingManager;
import net.badlion.potpvp.managers.TDMManager;
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
		    lookup = PotPvP.getInstance().getServer().getPlayer(args[0]);
		    if (lookup != null) {
			    lookupGroup = PotPvP.getInstance().getPlayerGroup(lookup);

			    // Create smelly inventory
			    smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(),
					    45, ChatColor.AQUA + ChatColor.BOLD.toString() + lookup.getName() + "'s Stats");
		    } else {
			    this.player.sendMessage(ChatColor.RED + "That player is not online!");
			    return;
		    }
	    }

	    if (!PotPvPPlayerManager.getPotPvPPlayer(lookup.getUniqueId()).isLoaded()) {
		    this.player.sendMessage(ChatColor.RED + "Data has not been loaded, try again in a few seconds!");
		    return;
	    }

	    final Group lookupGroup2 = lookupGroup;
	    final Player lookup2 = lookup;
	    final SmellyInventory smellyInventory2 = smellyInventory;
	    BukkitUtil.runTaskAsync(new Runnable() {
		    @Override
		    public void run() {
        if (lookupGroup2.isParty()) {
	        int size = lookupGroup2.players().size();
	        if (size != 2 && size != 3 && size != 5) {
		        StatsCommand.this.player.sendMessage(ChatColor.GREEN + "Stats only available for solo's and 2's/3's/5's parties.");
		        return;
	        }

	        if (size == 2) {
		        // Fill 2v2 ranked ladders
		        Map<String, Ladder> twoVsTwoRanked = Ladder.getLadderMap(Ladder.LadderType.TwoVsTwoRanked);
		        for (String kitRuleSet : twoVsTwoRanked.keySet()) {
			        smellyInventory2.getMainInventory().addItem(StatsCommand.this.getItemForLadder(kitRuleSet, twoVsTwoRanked.get(kitRuleSet), lookupGroup2));
		        }

		        // Add global ladder item
		        smellyInventory2.getMainInventory().setItem(18, StatsCommand.this.getItemForGlobalLadder(Ladder.LadderType.TwoVsTwoRanked, lookupGroup2));
	        } else if (size == 3){
		        // Fill 3v3 ranked ladders
		        Map<String, Ladder> threeVsThreeRanked = Ladder.getLadderMap(Ladder.LadderType.ThreeVsThreeRanked);
		        for (String kitRuleSet : threeVsThreeRanked.keySet()) {
			        smellyInventory2.getMainInventory().addItem(StatsCommand.this.getItemForLadder(kitRuleSet, threeVsThreeRanked.get(kitRuleSet), lookupGroup2));
		        }
	        } else {
		        // Fill 5v5 ranked ladders
		        Map<String, Ladder> fiveVsFiveRanked = Ladder.getLadderMap(Ladder.LadderType.FiveVsFiveRanked);
		        for (String kitRuleSet : fiveVsFiveRanked.keySet()) {
			        smellyInventory2.getMainInventory().addItem(StatsCommand.this.getItemForLadder(kitRuleSet, fiveVsFiveRanked.get(kitRuleSet), lookupGroup2));
		        }
	        }
        } else {
	        // Fill 1v1 ranked ladders
	        Map<String, Ladder> oneVsOneRanked = Ladder.getLadderMap(Ladder.LadderType.OneVsOneRanked);
	        for (String kitRuleSet : oneVsOneRanked.keySet()) {
		        smellyInventory2.getMainInventory().addItem(StatsCommand.this.getItemForLadder(kitRuleSet, oneVsOneRanked.get(kitRuleSet), lookupGroup2));
	        }

	        // Add global ladder item
	        smellyInventory2.getMainInventory().setItem(18, StatsCommand.this.getItemForGlobalLadder(Ladder.LadderType.OneVsOneRanked, lookupGroup2));

	        // Fill FFA info
	        int counter = 27;
	        for (FFAWorld ffaWorld : FFAWorld.getFfaWorlds().values()) {
		        smellyInventory2.getMainInventory().setItem(counter, StatsCommand.this.getItemForFFA(ffaWorld, lookup2));

		        counter++;
	        }

	        // Fill TDM info
	        smellyInventory2.getMainInventory().setItem(36, StatsCommand.this.getItemForTDM(lookup2));
        }

	    BukkitUtil.openInventory(StatsCommand.this.player, smellyInventory2.getMainInventory());
		    }
	    });
    }

	private ItemStack getItemForGlobalLadder(Ladder.LadderType ladderType, Group group) {
		ItemStack item = new ItemStack(Material.DIAMOND_BLOCK);
		ItemMeta itemMeta = item.getItemMeta();
		if (ladderType == Ladder.LadderType.OneVsOneRanked) {
			itemMeta.setDisplayName(ChatColor.GREEN + "Global 1v1");
		} else if (ladderType == Ladder.LadderType.TwoVsTwoRanked) {
			itemMeta.setDisplayName(ChatColor.GREEN + "Global 2v2");
		}

		List<String> lore = new ArrayList<>();
		if (group.isParty()) {
			lore.add(ChatColor.LIGHT_PURPLE.toString() + RatingManager.getDBPartyGlobalRating(group));
		} else {
			lore.add(ChatColor.LIGHT_PURPLE.toString() + RatingManager.getDBPlayerGlobalRating(group.getLeader().getUniqueId()));
		}
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);

		return item;
	}

	private ItemStack getItemForLadder(String kitRuleSet, Ladder ladder, Group group) {
		ItemStack item = new ItemStack(ladder.getKitRuleSet().getKitItem());
		ItemMeta itemMeta = item.getItemMeta();
		if (ladder.getLadderType() == Ladder.LadderType.OneVsOneRanked) {
			itemMeta.setDisplayName(ChatColor.GREEN + kitRuleSet + " 1v1");
		} else if (ladder.getLadderType() == Ladder.LadderType.TwoVsTwoRanked) {
			itemMeta.setDisplayName(ChatColor.GREEN + kitRuleSet + " 2v2");
		} else if (ladder.getLadderType() == Ladder.LadderType.ThreeVsThreeRanked) {
			itemMeta.setDisplayName(ChatColor.GREEN + kitRuleSet + " 3v3");
		} else {
			itemMeta.setDisplayName(ChatColor.GREEN + kitRuleSet + " 5v5");
		}

		List<String> lore = new ArrayList<>();
		try {
			lore.add(ChatColor.LIGHT_PURPLE.toString() + RatingManager.getGroupRating(group, ladder));
		} catch (NoRatingFoundException e) {
			lore.add(ChatColor.LIGHT_PURPLE.toString() + RatingManager.DEFAULT_RATING);
		}
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);

		return item;
	}

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

	private ItemStack getItemForTDM(Player player) {
		ItemStack item = new ItemStack(LobbyItemHelper.getTDMItem());
		ItemMeta itemMeta = item.getItemMeta();

		TDMManager.TDMStats tdmStats = TDMManager.getTDMStats(player.getUniqueId());
		List<String> lore = new ArrayList<>();
		if (tdmStats.getTotalDeaths() != 0) {
			lore.add(ChatColor.YELLOW + "KDR: " + String.format("%.2f", tdmStats.getTotalKills() / (double) tdmStats.getTotalDeaths()));
		} else {
			lore.add(ChatColor.YELLOW + "KDR: N/A");
		}
		lore.add(ChatColor.YELLOW + "Kills: " + tdmStats.getTotalKills());
		lore.add(ChatColor.YELLOW + "Deaths: " + tdmStats.getTotalDeaths());
		lore.add(ChatColor.YELLOW + "Max Killstreak: " + tdmStats.getMaxKillStreak());

		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);

		return item;
	}

    @Override
    public void usage(CommandSender sender) {

    }

}
