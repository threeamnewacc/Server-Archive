package net.badlion.gfactions.tasks.bloodbowl;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.gfactions.managers.BloodBowlManager;
import net.badlion.gfactions.managers.FactionManager;
import net.badlion.gfactions.GFactions;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashSet;

public class BloodBowlScoreTrackerTask extends BukkitRunnable {

	private GFactions plugin;
	private BloodBowlManager bloodBowlManager;

	private HashSet<String> factionsCapping;

	private String firstPlaceName;
	private String secondPlaceName;
	private String thirdPlaceName;
	private int firstPlaceScore;
	private int secondPlaceScore;
	private int thirdPlaceScore;

	private String oldFirstPlaceName;
	private String oldSecondPlaceName;
	private String oldThirdPlaceName;

	private boolean topScoresInitialized;
	private boolean topThreeChanged;
	private World world;

	public static int SECONDS_PER_CAPTURE_POINT = 180;
	public static String BLOODBOWL_ANNOUNCEMENT_PREFIX = ChatColor.RED + "[" + ChatColor.BLUE + "BloodBowl" + ChatColor.RED + "]" + ChatColor.BLUE + ": " + ChatColor.GREEN;

	public BloodBowlScoreTrackerTask(GFactions plugin) {
		this.plugin = plugin;
		this.factionsCapping = new HashSet<String>();
		this.firstPlaceName = "N/A";
		this.secondPlaceName = "N/A "; // Spaces so they all render
		this.thirdPlaceName = "N/A  "; // Spaces so they all render
		this.firstPlaceScore = 0;
		this.secondPlaceScore = 0;
		this.thirdPlaceScore = 0;
		this.bloodBowlManager = this.plugin.getBloodBowlManager();
		this.topScoresInitialized = false;
		this.topThreeChanged = false;
		this.oldFirstPlaceName = "N/A"; // Spaces so they all render
		this.oldSecondPlaceName = "N/A "; // Spaces so they all render
		this.oldThirdPlaceName = "N/A  "; // Spaces so they all render

		for (Player p : this.plugin.getServer().getOnlinePlayers()) {
			// Add a new scoreboard for everyone
			Scoreboard board = p.getScoreboard();
			Objective objective = board.registerNewObjective("bloodbowl", "dummy");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			objective.setDisplayName(ChatColor.DARK_AQUA + "BloodBowl Top 3 Scores:");

			Score score1 = objective.getScore(this.oldFirstPlaceName);
			score1.setScore(1);
			Score score2 = objective.getScore(this.oldSecondPlaceName);
			score2.setScore(1);
			Score score3 = objective.getScore(this.oldThirdPlaceName);
			score3.setScore(1);

			FPlayer fPlayer = FPlayers.i.get(p);
			Faction faction = fPlayer.getFaction();

			// Update own score
			Score selfScore = objective.getScore(faction.getTag());
			selfScore.setScore(1);
		}

		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : BloodBowlScoreTrackerTask.this.plugin.getServer().getOnlinePlayers()) {
					// Add a new scoreboard for everyone
					Scoreboard board = p.getScoreboard();

					Objective objective = board.getObjective("bloodbowl");

					// Incase of NPE
					if (objective == null) {
						objective = board.registerNewObjective("bloodbowl", "dummy");
						objective.setDisplaySlot(DisplaySlot.SIDEBAR);
						objective.setDisplayName(ChatColor.DARK_AQUA + "BloodBowl Top 3 Scores:");
					}

					Score score1 = objective.getScore(oldFirstPlaceName);
					score1.setScore(0);
					Score score2 = objective.getScore(oldSecondPlaceName);
					score2.setScore(0);
					Score score3 = objective.getScore(oldThirdPlaceName);
					score3.setScore(0);

					FPlayer fPlayer = FPlayers.i.get(p);
					Faction faction = fPlayer.getFaction();

					// Update own score
					Score selfScore = objective.getScore(faction.getTag());
					selfScore.setScore(0);
				}
			}
		}, 1);

		this.world = Bukkit.getWorld("bloodbowl");
	}

	@Override
	public void run() {
		try {
			if (this.bloodBowlManager == null) {
				this.bloodBowlManager = this.plugin.getBloodBowlManager();
			}

			// Screw this shit
			if (this.bloodBowlManager == null) {
				this.cancel();
			}

            // Set old highscore variables
            // 8/30/14 Moved up here, because below it was being overwritten multiple times possibly
            // Here it will always have the information from the last iteration and properly clear
            this.oldFirstPlaceName = this.firstPlaceName;
            this.oldSecondPlaceName = this.secondPlaceName;
            this.oldThirdPlaceName = this.thirdPlaceName;

			HashSet<Faction> factionsInThisIteration = new HashSet<>();

			boolean factionCapturedAPoint = false;

			for (final Player player : this.world.getPlayers()) {
				// Get Faction Information
				FPlayer fPlayer = FPlayers.i.get(player);
				final Faction faction = fPlayer.getFaction();

				// Wilderness does not work
				if (faction.getId().equals("0")) {
					continue;
				}

				// Don't need to process same faction a second time
				if (factionsInThisIteration.contains(faction)) {
					continue;
				}

				// Check if player is in the cap zone
				if (!player.isDead()
							&& Gberry.isLocationInBetween(this.bloodBowlManager.getCapzoneLocation1(), this.bloodBowlManager.getCapzoneLocation2(), player.getLocation())) {
					// Check for PVP protection
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

						player.sendMessage(ChatColor.RED + "Your PVP protection has been removed because you entered the Bloodbowl!");
					}
				}

				if (!player.isDead()
							&& Gberry.isLocationInBetween(this.bloodBowlManager.getCapzoneLocation1(), this.bloodBowlManager.getCapzoneLocation2(), player.getLocation())) {

					if (this.bloodBowlManager.getMapOfScores().containsKey(faction.getId())) {
						this.bloodBowlManager.getMapOfScores().put(faction.getId(), this.bloodBowlManager.getMapOfScores().get(faction.getId()) + 1);
						factionsInThisIteration.add(faction);

						// They earned a capture point
						if (this.bloodBowlManager.getMapOfScores().get(faction.getId()) == SECONDS_PER_CAPTURE_POINT) {
							Bukkit.getLogger().info(faction.getId() + "(" + faction.getTag() + ") gained a point for BloodBowl");
						 	Integer currentCapturePoints = this.bloodBowlManager.getMapOfCaptures().get(faction.getId());
							// FUCK NPE's
							if (currentCapturePoints == null) {
								currentCapturePoints = 0;
							}

							this.bloodBowlManager.getMapOfCaptures().put(faction.getId(), currentCapturePoints + 1);
							factionCapturedAPoint = true;
							this.handleHighestPoints(faction, faction.getTag());

							// They won
							if (currentCapturePoints == 9) {
								Gberry.broadcastMessage(BLOODBOWL_ANNOUNCEMENT_PREFIX + faction.getTag() + " has won the BloodBowl!");
								this.bloodBowlManager.endBloodBowl();
                                if (!faction.getId().equals("0")) {
                                    final ArrayList<ItemStack> items = new ArrayList<>();
                                    items.addAll(plugin.getItemGenerator().generateRandomSuperRareItem(3));
                                    items.addAll(plugin.getItemGenerator().generateRandomRareItem(4));
                                    items.addAll(plugin.getItemGenerator().generateRandomCommonItem(3));

                                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            FactionManager.addStatToFaction("bloodbowls", faction);
                                            //plugin.getAuction().insertHeldAuctionItems(faction.getFPlayerLeader().getId(), items); TODO
                                        }
                                    });
                                }
								return;
							} else {
								this.bloodBowlManager.getMapOfScores().put(faction.getId(), 0);
								Gberry.broadcastMessage(BLOODBOWL_ANNOUNCEMENT_PREFIX + faction.getTag() + " has controlled the hill for 3 straight minutes! They have earned a point!");
								continue;
							}
						}

						// Been in control for 15 seconds
						if (this.bloodBowlManager.getMapOfScores().get(faction.getId()) % 15 == 0) {
							Bukkit.getLogger().info(faction.getId() + " (" + faction.getTag() + ") has had control of hill for " + this.bloodBowlManager.getMapOfScores().get(faction.getId()) + " seconds");
							Gberry.broadcastMessage(BLOODBOWL_ANNOUNCEMENT_PREFIX + faction.getTag() + " has had hill control for " + ChatColor.GOLD +
															this.bloodBowlManager.getMapOfScores().get(faction.getId()) + ChatColor.GREEN + " seconds.");
						}

						if (!this.factionsCapping.contains(faction.getId())) {
							Gberry.broadcastMessage(BLOODBOWL_ANNOUNCEMENT_PREFIX + faction.getTag() + " has control of the hill!");
							this.factionsCapping.add(faction.getId());
						}
					} else {
						this.bloodBowlManager.getMapOfScores().put(faction.getId(), 1);
						this.bloodBowlManager.getParticipants().add(faction.getId());

						Gberry.broadcastMessage(BLOODBOWL_ANNOUNCEMENT_PREFIX + faction.getTag() + " has control of the hill!");

						factionsInThisIteration.add(faction);
						this.factionsCapping.add(faction.getId());
					}
				} else {
					// Slightly more complex logic to make sure we don't have someone else capping
					ArrayList<Player> factionMembersOnline = faction.getOnlinePlayers();
					boolean stillSomeoneInsideCapZone = false;
					for (Player p : factionMembersOnline) {
						if (!p.isDead()
									&& Gberry.isLocationInBetween(this.bloodBowlManager.getCapzoneLocation1(), this.bloodBowlManager.getCapzoneLocation2(), p.getLocation())) {
							stillSomeoneInsideCapZone = true;
							break;
						}
					}

					// Ok, no one is inside the cap zone anymore
					if (!stillSomeoneInsideCapZone) {
						if (this.factionsCapping.contains(faction.getId())) {
							Gberry.broadcastMessage(BLOODBOWL_ANNOUNCEMENT_PREFIX + faction.getTag() + " has lost control of the hill!");

							// They aren't capping anymore, they get their score reset
							this.bloodBowlManager.getMapOfScores().put(faction.getId(), 0);
							this.factionsCapping.remove(faction.getId());

							factionsInThisIteration.remove(faction);
						}
					}
				}

				// Handled above now in the continue section
//				// Update score
//				if (factionCapturedAPoint) {
//					this.handleHighestPoints(faction.getTag());
//				}
			}

			if (factionCapturedAPoint) {
				// Update scoreboards again
				for (Player p : this.plugin.getServer().getOnlinePlayers()) {
					// Get Faction Information
					FPlayer fPlayer = FPlayers.i.get(p);
					Faction faction = fPlayer.getFaction();
					Scoreboard board = p.getScoreboard();

					if (this.topThreeChanged) {
						// If one of these changed remove from the list, they will get re-added
						if (!this.oldFirstPlaceName.equals(this.firstPlaceName)) {
							board.resetScores(this.oldFirstPlaceName);
						}
						if (!this.oldSecondPlaceName.equals(this.secondPlaceName)) {
							board.resetScores(this.oldSecondPlaceName);
						}
						if (!this.oldThirdPlaceName.equals(this.thirdPlaceName)) {
							board.resetScores(this.oldThirdPlaceName);
						}
					}

					Objective objective = board.getObjective("bloodbowl");

					// Incase of NPE
					if (objective == null) {
						objective = board.registerNewObjective("bloodbowl", "dummy");
						objective.setDisplaySlot(DisplaySlot.SIDEBAR);
						objective.setDisplayName("BloodBowl Top 3 Scores:");
					}

					// Update top 3
					Score score1 = objective.getScore(this.firstPlaceName);
					score1.setScore(this.firstPlaceScore);
					Score score2 = objective.getScore(this.secondPlaceName);
					score2.setScore(this.secondPlaceScore);
					Score score3 = objective.getScore(this.thirdPlaceName);
					score3.setScore(this.thirdPlaceScore);

					// Update own score
					if (this.bloodBowlManager.getMapOfCaptures().containsKey(faction.getId())) {
						Score selfScore = objective.getScore(faction.getTag());
						Integer points = this.bloodBowlManager.getMapOfCaptures().get(faction.getId());
						if (points == null) {
							points = 0;
						}

						selfScore.setScore(points);
					}
				}

				this.topThreeChanged = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleHighestPoints(Faction faction, String name) {
		int score;
		if (this.bloodBowlManager.getMapOfCaptures().containsKey(faction.getId())) {
			score = this.bloodBowlManager.getMapOfCaptures().get(faction.getId());
		} else {
			// Initialize their score with 0
			score = 1;
		}

		// Initializing crap
		// THEORY: If these values are all 0 its impossible for someone to have more points than, 1st, 2nd, or 3rd because they will ally have 1 or 0 points.
		// So I don't have to worry about checking if they are more points than first at this point
		if (!this.topScoresInitialized) {
			if (this.firstPlaceScore == 0) {
				this.firstPlaceName = name;
				this.firstPlaceScore = score;
				this.topThreeChanged = true;
				return;
			}

			if (this.secondPlaceScore == 0) {
				if (this.firstPlaceName.equals(name)) {
					this.firstPlaceScore = score;
					this.topThreeChanged = true; // TODO: ADDED - TEST IF ACTUALLY NEEDED
					return;
				}
				this.secondPlaceName = name;
				this.secondPlaceScore = score;
				this.topThreeChanged = true;
				return;
			}

			if (this.thirdPlaceScore == 0) {
				if (this.firstPlaceName.equals(name) || this.secondPlaceName.equals(name)) {
					if (this.firstPlaceName.equals(name)) {
						this.firstPlaceScore = score;
					} else if (score > this.firstPlaceScore) {
						// Swap them
						this.secondPlaceName = this.firstPlaceName;
						this.secondPlaceScore = this.firstPlaceScore;
						this.firstPlaceName = name;
						this.firstPlaceScore = score;
					} else {
						this.secondPlaceScore = score;
					}
					this.topThreeChanged = true;
					return;
				}
				this.thirdPlaceName = name;
				this.thirdPlaceScore = score;

				this.topThreeChanged = true;
				this.topScoresInitialized = true;
				return;
			}
		}

		// Top three initialized, handle who has more points
		if (score >= this.thirdPlaceScore) {
			if (score >= this.secondPlaceScore) {
				if (score >= this.firstPlaceScore) {
					// Shift everyone down and shove the new guy up top
					if (!this.thirdPlaceName.equals(name) && !this.secondPlaceName.equals(name) && !this.firstPlaceName.equals(name)) {
						this.thirdPlaceName = this.secondPlaceName;
						this.thirdPlaceScore = this.secondPlaceScore;
						this.secondPlaceName = this.firstPlaceName;
						this.secondPlaceScore = this.firstPlaceScore;
						this.firstPlaceName = name;
						this.firstPlaceScore = score;
						this.topThreeChanged = true;
						return;
					} else {
						// Oh boy...third to first or 2nd to first...gotta fix this up
						if (this.thirdPlaceName.equals(name)) {
							this.thirdPlaceName = this.secondPlaceName;
							this.thirdPlaceScore = this.secondPlaceScore;
							this.secondPlaceName = this.firstPlaceName;
							this.secondPlaceScore = this.firstPlaceScore;
							this.firstPlaceName = name;
							this.firstPlaceScore = score;
						} else if (this.secondPlaceName.equals(name)) {
							// Second place moved up to first place

							this.secondPlaceName = this.firstPlaceName;
							this.secondPlaceScore = this.firstPlaceScore;
							this.firstPlaceName = name;
							this.firstPlaceScore = score;
						} else {                          // TODO: THIS CODE SHOULD RUN WHEN TOP 3 ARE ALL TIED - TEST
							// At this point should just be updating the first place score
							if (!this.firstPlaceName.equals(name)) {
								this.plugin.getLogger().severe("Miscalculation in first place for BloodBowl.");
							}
							this.firstPlaceScore = score;
						}
						this.topThreeChanged = true;
						return;
					}
				} else {
					if (!this.thirdPlaceName.equals(name) && !this.secondPlaceName.equals(name)) {
						this.thirdPlaceName = this.secondPlaceName;
						this.thirdPlaceScore = this.secondPlaceScore;
						this.secondPlaceName = name;
						this.secondPlaceScore = score;
					} else {
						if (this.thirdPlaceName.equals(name)) {
							this.thirdPlaceName = this.secondPlaceName;
							this.thirdPlaceScore = this.secondPlaceScore;
							this.secondPlaceName = name;
							this.secondPlaceScore = score;
						} else {                // TODO: THIS CODE SHOULD RUN WHEN 2ND 3RD ARE ALL TIED - TEST
							// At this point should just be updating second place
							if (!this.secondPlaceName.equals(name)) {
								this.plugin.getLogger().severe("Miscalculation in second place for BloodBowl.");
							}
							this.secondPlaceScore = score;
						}
					}
					this.topThreeChanged = true;
					return;
				}
			} else {
				if (this.secondPlaceName.equals(name)) {
					this.secondPlaceScore = score;
					this.topThreeChanged = true;
					return;
				}

				// Just throw them in 3rd place
				this.thirdPlaceName = name;
				this.thirdPlaceScore = score;
				this.topThreeChanged = true;
				return;
			}
		}
	}

	public GFactions getPlugin() {
		return plugin;
	}

	public void setPlugin(GFactions plugin) {
		this.plugin = plugin;
	}

	public String getFirstPlaceName() {
		return firstPlaceName;
	}

	public void setFirstPlaceName(String firstPlaceName) {
		this.firstPlaceName = firstPlaceName;
	}

	public String getSecondPlaceName() {
		return secondPlaceName;
	}

	public void setSecondPlaceName(String secondPlaceName) {
		this.secondPlaceName = secondPlaceName;
	}

	public String getThirdPlaceName() {
		return thirdPlaceName;
	}

	public void setThirdPlaceName(String thirdPlaceName) {
		this.thirdPlaceName = thirdPlaceName;
	}

	public int getFirstPlaceScore() {
		return firstPlaceScore;
	}

	public void setFirstPlaceScore(int firstPlaceScore) {
		this.firstPlaceScore = firstPlaceScore;
	}

	public int getSecondPlaceScore() {
		return secondPlaceScore;
	}

	public void setSecondPlaceScore(int secondPlaceScore) {
		this.secondPlaceScore = secondPlaceScore;
	}

	public int getThirdPlaceScore() {
		return thirdPlaceScore;
	}

	public void setThirdPlaceScore(int thirdPlaceScore) {
		this.thirdPlaceScore = thirdPlaceScore;
	}
}
