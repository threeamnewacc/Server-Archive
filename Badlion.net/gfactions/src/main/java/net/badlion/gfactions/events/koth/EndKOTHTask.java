package net.badlion.gfactions.events.koth;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.gfactions.managers.FactionManager;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.events.koth.KOTH;
import net.badlion.gfactions.SLoot;
import net.badlion.gfactions.bukkitevents.EventStateChangeEvent;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EndKOTHTask extends BukkitRunnable {

	private GFactions plugin;

	public EndKOTHTask(GFactions plugin) {
		this.plugin = plugin;
    }

	@Override
	public void run() {
		// Stop score tracker
		this.plugin.getKoth().getScoreTrackerTask().cancel();

        // List used to protect the loot for only members of the faction
        List<Player> winners = new ArrayList<>();

        // Clear scoreboards
        for (Player p : this.plugin.getServer().getOnlinePlayers()) {
            Scoreboard board = p.getScoreboard();
            board.getObjective(DisplaySlot.SIDEBAR).unregister();
        }

        // Figure out who won
        ArrayList<String> players = this.plugin.getKoth().getParticipants();
        if (!players.isEmpty()) {
            Map<String, Integer> scores = this.plugin.getKoth().getMapOfScores();
            String maxPlayer = players.get(0);
            int maxTime = scores.get(maxPlayer);
            for (int i = 1; i < players.size(); ++i) {
                if (scores.get(players.get(i)) > maxTime) {
                    maxTime = scores.get(players.get(i));
                    maxPlayer = players.get(i);
                }
            }

            final String maxPlayer2 = maxPlayer;

            // We have a winner!
			Gberry.broadcastMessage(ChatColor.GREEN + "KOTH winner is " + maxPlayer + " who capped the hill for " + (int) ((double) Math.round((maxTime / 60) * 100) / 100) + " minutes and " + (int) ((double) Math.round((maxTime % 60) * 100) / 100) + " seconds");

			Player p = Bukkit.getPlayer(maxPlayer);
            winners.add(p);
			if (p != null) {
				Faction faction = FPlayers.i.get(p).getFaction();
                Bukkit.getLogger().info("~Faction " + faction.getId() + faction.getTag() + " won KOTH!");
                if (faction.getTag().equalsIgnoreCase("wilderness")) {
                    winners = faction.getOnlinePlayers();
                }
			} else {
				Bukkit.getLogger().info("~Faction (player) " + maxPlayer + " won KOTH!");
			}

            final FPlayer fPlayer = FPlayers.lastKnownNameToFPlayerMap.get(maxPlayer);
            if (fPlayer != null && !fPlayer.getId().equals("0")) {
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        FactionManager.addStatToFaction("koths", fPlayer.getFaction());
                    }
                });
            }

            // Give items to winner
            //Player player = this.plugin.getServer().getPlayer(maxPlayer);
            //if (player != null) {
            final List<ItemStack> items = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                int rand = this.plugin.generateRandomInt(1, 100);
                if (1 <= rand && rand < 15) {
                    items.addAll(this.plugin.getItemGenerator().generateRandomSuperRareItem(1));
                } else if (15 <= rand && rand < 50) {
                    items.addAll(this.plugin.getItemGenerator().generateRandomRareItem(1));
                } else if (50 <= rand && rand <= 100) {
                    items.addAll(this.plugin.getItemGenerator().generateRandomCommonItem(1));
                }
            }

            KOTH koth = this.plugin.getKoth();
            Location midpoint = koth.getCapzoneLocation1(); // to shut it up
            midpoint.setX((koth.getCapzoneLocation1().getX() + koth.getCapzoneLocation2().getX()) / 2);
            midpoint.setY((koth.getCapzoneLocation1().getY() + koth.getCapzoneLocation2().getY()) / 2);

            SLoot.dropLoot(midpoint, items, winners, 120, true);

				// this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
				// 	@Override
				// 	public void run() {
				// 		// Insert items in player's auction storage
				// 		UUID uuid = Gberry.getOfflineUUID(maxPlayer2);
				// 		if (uuid == null) {
				// 			return;
				// 		}
//
				// 		plugin.getAuction().insertHeldAuctionItems(uuid.toString(), items);
				// 		plugin.getArchMoney().changeBalance(uuid.toString(), 1000, "KOTH cash reward");
				// 	}
				// });

                    /*// Drop at their feet cuz inventory full SMELLY: LOL NOO THIS IS BAD DONT DO THIS BOYS N GURLS
                    if (player.getInventory().firstEmpty() == -1) {
                        player.getLocation().getWorld().dropItem(player.getLocation(), items.get(0));
                    } else {
                        player.getInventory().addItem(items.get(0));
                    }*/
                //player.updateInventory();
            //}

            Player player = this.plugin.getServer().getPlayer(maxPlayer);
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "Use \"/claim\" to claim your KOTH rewards!");
            }
        } else {
            Gberry.broadcastMessage(ChatColor.GREEN + "There was no winner for the KOTH, the KOTH has ended.");
        }

		// Clean up memory
		this.plugin.setKoth(null);

        // Call TabList event
        EventStateChangeEvent event = new EventStateChangeEvent("KOTH", false);
        this.plugin.getServer().getPluginManager().callEvent(event);
	}

}
