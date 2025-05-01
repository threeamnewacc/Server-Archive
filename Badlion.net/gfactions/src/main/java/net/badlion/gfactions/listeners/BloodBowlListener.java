package net.badlion.gfactions.listeners;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.gfactions.managers.BloodBowlManager;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.tasks.bloodbowl.BloodBowlScoreTrackerTask;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class BloodBowlListener implements Listener {
	
	private GFactions plugin;
	
	public BloodBowlListener(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (this.plugin.getBloodBowlManager().isRunning()) {
			BloodBowlManager bb = this.plugin.getBloodBowlManager();
			Player player = event.getPlayer();
			FPlayer fPlayer = FPlayers.i.get(player);
			Faction faction = fPlayer.getFaction();

			Scoreboard board = event.getPlayer().getScoreboard();
			Objective objective = board.registerNewObjective("bloodbowl", "dummy");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			objective.setDisplayName(ChatColor.DARK_AQUA + "BloodBowl Top 3 Scores:");
			
			BloodBowlScoreTrackerTask task = bb.getBloodBowlScoreTracker();

			// TODO: Cleanup
			if (task.getFirstPlaceName().equals("N/A")) {
				final Score score1 = objective.getScore(task.getFirstPlaceName());
				score1.setScore(1);

				// TODO: Race condition with scores changing?
				this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
					@Override
					public void run() {
					 	score1.setScore(0);
					}
				}, 1);
			} else {
				Score score1 = objective.getScore(task.getFirstPlaceName());
				score1.setScore(task.getFirstPlaceScore());
			}

			if (task.getSecondPlaceName().equals("N/A ")) {
				final Score score2 = objective.getScore(task.getSecondPlaceName());
				score2.setScore(1);

				// TODO: Race condition with scores changing?
				this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
					@Override
					public void run() {
						score2.setScore(0);
					}
				}, 1);
			} else {
				Score score2 = objective.getScore(task.getSecondPlaceName());
				score2.setScore(task.getSecondPlaceScore());
			}

			if (task.getThirdPlaceName().equals("N/A  ")) {
				final Score score3 = objective.getScore(task.getThirdPlaceName());
				score3.setScore(1);

				// TODO: Race condition with scores changing?
				this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
					@Override
					public void run() {
						score3.setScore(0);
					}
				}, 1);
			} else {
				Score score3 = objective.getScore(task.getFirstPlaceName());
				score3.setScore(task.getThirdPlaceScore());
			}

			if (!faction.getId().equals("0")) {
				// Update own score
				Score selfScore = objective.getScore(faction.getTag());

				// What if they had a score?
				if (bb.getMapOfCaptures().containsKey(faction.getTag())) {
					selfScore.setScore(bb.getMapOfCaptures().get(faction.getId()));
				} else {
					selfScore.setScore(0);
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerEnterBloodBowl(final PlayerPortalEvent event) {
		final Player player = event.getPlayer();
		Location from = event.getFrom();
		boolean isABloodBowlPortal = false;
		BloodBowlManager bbm = this.plugin.getBloodBowlManager();

		if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && from.getWorld().getName().equals("world") &&
				  Gberry.isLocationInBetween(bbm.getLowerBoundEntry1(), bbm.getUpperBoundEntry1(), from)) {
			isABloodBowlPortal = true;
		} else if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && from.getWorld().getName().equals("world") &&
				  Gberry.isLocationInBetween(bbm.getLowerBoundEntry2(), bbm.getUpperBoundEntry2(), from)) {
			isABloodBowlPortal = true;
		} else if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && from.getWorld().getName().equals("world") &&
			      Gberry.isLocationInBetween(bbm.getLowerBoundEntry3(), bbm.getUpperBoundEntry3(), from)) {
			isABloodBowlPortal = true;
		} else if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && from.getWorld().getName().equals("world") &&
			      Gberry.isLocationInBetween(bbm.getLowerBoundEntry4(), bbm.getUpperBoundEntry4(), from)) {
			isABloodBowlPortal = true;
		}

		if (isABloodBowlPortal) {
			if (bbm.isRunning()) {
				// Do they have their PVP prot enabled?
                if (this.plugin.getMapNameToJoinTime().containsKey(player.getUniqueId().toString())) {
                    this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
                    this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                        @Override
                        public void run() {
                            // Purge from DB
                            plugin.removeProtection(player);
                        }
                    });

                    player.sendMessage(ChatColor.RED + "PVP Protection disabled from entering BloodBowl.");
                }

				FPlayer fplayer = FPlayers.i.get(player);
				final Faction faction = fplayer.getFaction();

				if (faction.getId().equals("0")) {
					player.sendMessage(ChatColor.RED + "You must be in a faction to enter BloodBowl");
					event.setCancelled(true);
					return;
				}

				// one tick later
				this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {

					@Override
					public void run() {
						// Is this player's faction already being used?
						if (BloodBowlListener.this.plugin.getBloodBowlManager().getFactionToSpawnLocationMap().containsKey(faction)) {
							// TP them
							Location location = BloodBowlListener.this.plugin.getBloodBowlManager().getFactionToSpawnLocationMap().get(faction);
							player.teleport(location);

							// Shift this location to back of line for removing
							BloodBowlListener.this.plugin.getBloodBowlManager().getLocationQueue().remove(location);
							BloodBowlListener.this.plugin.getBloodBowlManager().getLocationQueue().add(location);
						} else {
							// Shift this location to back of line for removing
							Location location = BloodBowlListener.this.plugin.getBloodBowlManager().getLocationQueue().remove();
							BloodBowlListener.this.plugin.getBloodBowlManager().getLocationQueue().add(location);
							BloodBowlListener.this.plugin.getBloodBowlManager().getFactionToSpawnLocationMap().put(faction, location);

							// TP them
							player.teleport(location);
						}

						BloodBowlListener.this.plugin.getBloodBowlManager().getParticipants().add(player.getUniqueId().toString());
						player.sendMessage(ChatColor.GREEN + "Joined BloodBowl.");
					}

				}, 1);
			} else {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "There is no BloodBowl going on right now.");
			}
		}

//		// Is a BB even running?
//		if (!this.plugin.getBloodBowlManager().isRunning()) {
//			player.sendMessage(ChatColor.RED + "No BloodBowl is currently running.");
//			return;
//		}

		// No re-entry
//		if (this.plugin.getBloodBowlManager().getParticipants().contains(player.getUniqueId().toString())) {
//			player.sendMessage(ChatColor.RED + "Cannot rejoin BloodBowl or join once already in it.");
//			return;
//		}

//		// No joining if combat tagged
//		if (this.plugin.getCombatTagApi().isInCombat(player)) {
//			player.sendMessage(ChatColor.RED + "Cannot join BloodBowl when in combat.");
//			return;
//		}

//		// Only allow them to TP if they are in spawn or in their own base
//		Faction currentLocationFaction = Board.getFactionAt(player.getLocation());
//		if (!faction.getId().equals(currentLocationFaction.getId()) && !this.plugin.getgGuardPlugin().getProtectedRegionName(player.getLocation(),
//																																	this.plugin.getgGuardPlugin().getProtectedRegions()).equals("spawn")) {
//			player.sendMessage(ChatColor.RED + "Cannot join BloodBowl when not in base or in spawn.");
//			return;
//		}
	}

}
