package net.badlion.gfactions.events.koth;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.HashSet;

public class KOTHScoreTrackerTask extends BukkitRunnable {

	private GFactions plugin;
    private KOTH koth;

	private HashSet<String> playersCapping;
	private HashSet<String> playersDepreciated;
	private HashMap<String, Integer> playerScoreDepreciation;

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

	private int scoreDepreciationAmount;
	private int scoreDepreciationFrequency; // In seconds

    public KOTHScoreTrackerTask() {} // Does nothing

	public KOTHScoreTrackerTask(GFactions plugin) {
		this.plugin = plugin;
		this.playersCapping = new HashSet<String>();
		this.playersDepreciated = new HashSet<String>();
		this.playerScoreDepreciation = new HashMap<String, Integer>();
		this.firstPlaceName = "N/A";
		this.secondPlaceName = "N/A"; // Spaces so they all render
		this.thirdPlaceName = "N/A"; // Spaces so they all render
		this.firstPlaceScore = 0;
		this.secondPlaceScore = 0;
		this.thirdPlaceScore = 0;
		this.koth = null;
		this.topScoresInitialized = false;
		this.topThreeChanged = false;
		this.oldFirstPlaceName = "N/A"; // Spaces so they all render
		this.oldSecondPlaceName = "N/A"; // Spaces so they all render
		this.oldThirdPlaceName = "N/A"; // Spaces so they all render

		// Grab values from config
		this.scoreDepreciationAmount = this.plugin.getConfig().getInt("gfactions.koth.koth_score_depreciation_amount");
		this.scoreDepreciationFrequency = this.plugin.getConfig().getInt("gfactions.koth.koth_score_depreciation_frequency");

		for (Player p : this.plugin.getServer().getOnlinePlayers()) {
			// Add a new scoreboard for everyone
			Scoreboard board = p.getScoreboard();
			Objective objective = board.registerNewObjective("koth", "dummy");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.DARK_AQUA + "KOTH Top 3 Scores:");

			Score score1 = objective.getScore(this.oldFirstPlaceName);
			score1.setScore(this.firstPlaceScore);
			Score score2 = objective.getScore(this.oldSecondPlaceName);
			score2.setScore(this.secondPlaceScore);
			Score score3 = objective.getScore(this.oldThirdPlaceName);
			score3.setScore(this.thirdPlaceScore);

            // Update own score
            Score selfScore = objective.getScore(p.getName());
            selfScore.setScore(1);
		}

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
            @Override
            public void run() {
                for (Player p : KOTHScoreTrackerTask.this.plugin.getServer().getOnlinePlayers()) {
                    // Add a new scoreboard for everyone
                    Scoreboard board = p.getScoreboard();

                    Objective objective = board.getObjective("koth");

                    // Incase of NPE
                    if (objective == null) {
                        objective = board.registerNewObjective("koth", "dummy");
                        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        objective.setDisplayName(ChatColor.DARK_AQUA + "KOTH Top 3 Scores:");
                    }

                    Score score1 = objective.getScore(oldFirstPlaceName);
                    score1.setScore(0);
                    Score score2 = objective.getScore(oldSecondPlaceName);
                    score2.setScore(0);
                    Score score3 = objective.getScore(oldThirdPlaceName);
                    score3.setScore(0);

                    // Update own score
                    Score selfScore = objective.getScore(p.getName());
                    selfScore.setScore(0);
                }
            }
        }, 1);
	}

	@Override
	public void run() {
		try {
			if (this.koth == null) {
				this.koth = this.plugin.getKoth();
			}

			// Screw this shit
			if (koth == null) {
				this.cancel();
			}

            // Set old highscore variables
            // 8/30/14 Moved up here, because below it was being overwritten multiple times possibly
            // Here it will always have the information from the last iteration and properly clear
            this.oldFirstPlaceName = this.firstPlaceName;
            this.oldSecondPlaceName = this.secondPlaceName;
            this.oldThirdPlaceName = this.thirdPlaceName;

			int xMin = (int) this.koth.getCapzoneLocation1().getX();
			int yMin = (int) this.koth.getCapzoneLocation1().getY();
			int zMin = (int) this.koth.getCapzoneLocation1().getZ();
			int xMax = (int) this.koth.getCapzoneLocation2().getX();
			int yMax = (int) this.koth.getCapzoneLocation2().getY();
			int zMax = (int) this.koth.getCapzoneLocation2().getZ();
			for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
				// Check if player is in the cap zone
				Location location = player.getLocation();
				if (!player.isDead()
						&& location.getWorld().getName().equals("world")
						&& location.getBlockX() >= this.koth.getArenaLocation1().getBlockX() && location.getBlockX() <= this.koth.getArenaLocation2().getBlockX()
						&& location.getBlockY() >= this.koth.getArenaLocation1().getBlockY() && location.getBlockY() <= this.koth.getArenaLocation2().getBlockY()
						&& location.getBlockZ() >= this.koth.getArenaLocation1().getBlockZ() && location.getBlockZ() <= this.koth.getArenaLocation2().getBlockZ()) {
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

                        player.sendMessage(ChatColor.RED + "Your PVP protection has been removed because you entered the KOTH Zone!");
                    }
				}

				if (!player.isDead()
						&& location.getWorld().getName().equals("world")
						&& location.getBlockX() >= xMin && location.getBlockX() <= xMax
						&& location.getBlockY() >= yMin && location.getBlockY() <= yMax
						&& location.getBlockZ() >= zMin && location.getBlockZ() <= zMax) {

					if (this.koth.getMapOfScores().containsKey(player.getName())) {
						this.koth.getMapOfScores().put(player.getName(), this.koth.getMapOfScores().get(player.getName()) + 1);

						if (!this.playersCapping.contains(player.getName())) {
							player.sendMessage(ChatColor.BLUE + "You are now gaining points for the KOTH.");
							this.playersCapping.add(player.getName());
							this.playersDepreciated.remove(player.getName());
						}
					} else {
						this.koth.getMapOfScores().put(player.getName(), 1);
						this.koth.getParticipants().add(player.getName());

						if (!this.playersCapping.contains(player.getName())) {
							player.sendMessage(ChatColor.BLUE + "You are now gaining points for the KOTH.");
							this.playersCapping.add(player.getName());
							this.playersDepreciated.remove(player.getName());
						}
					}
				} else {
					if (this.playersCapping.contains(player.getName())) {
						player.sendMessage(ChatColor.BLUE + "You are no longer gaining points for the KOTH.");
						this.playersCapping.remove(player.getName());
					}

					// Score Depreciation Stuff
                    if (this.koth.getMapOfScores().containsKey(player.getName())) {
                        Integer secondsWithoutPoints = this.playerScoreDepreciation.get(player.getName());
                        if (secondsWithoutPoints == null) {
                            this.playerScoreDepreciation.put(player.getName(), 1);
                        } else {
                            // Time to reduce their points and that they fucking have any damn points nigga
                            if (secondsWithoutPoints == this.scoreDepreciationFrequency) {
                                if (this.koth.getMapOfScores().get(player.getName()) > 0) {
                                    this.playerScoreDepreciation.remove(player.getName());

                                    this.koth.getMapOfScores().put(player.getName(), this.koth.getMapOfScores().get(player.getName()) - this.scoreDepreciationAmount);

	                                if (!this.playersDepreciated.contains(player.getName())) {
		                                player.sendMessage(ChatColor.YELLOW + "You have lost " + this.scoreDepreciationAmount + " KOTH point(s). You will lose " + this.scoreDepreciationAmount + " point(s) every "
				                                + this.scoreDepreciationFrequency + " second(s) for not capping!");

		                                this.playersDepreciated.add(player.getName());
	                                }
                                }
                            } else {
                                this.playerScoreDepreciation.put(player.getName(), secondsWithoutPoints + 1);
                            }
                        }
                    }
				}

				// Update score
				this.handleHighestPoints(player.getName());
			}

			// Update scoreboards again
			for (Player p : this.plugin.getServer().getOnlinePlayers()) {
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

				Objective objective = board.getObjective("koth");

				// Incase of NPE
				if (objective == null) {
					objective = board.registerNewObjective("koth", "dummy");
					objective.setDisplaySlot(DisplaySlot.SIDEBAR);
					objective.setDisplayName("KOTH Top 3:");
				}

				// Update top 3
				Score score1 = objective.getScore(this.firstPlaceName);
				score1.setScore(this.firstPlaceScore);
				Score score2 = objective.getScore(this.secondPlaceName);
				score2.setScore(this.secondPlaceScore);
				Score score3 = objective.getScore(this.thirdPlaceName);
				score3.setScore(this.thirdPlaceScore);

				// Update own score
				if (this.koth.getMapOfScores().containsKey(p.getName())) {
					Score selfScore = objective.getScore(p.getName());
					selfScore.setScore(this.koth.getMapOfScores().get(p.getName()));
				}
			}

			this.topThreeChanged = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleHighestPoints(String name) {
		int score;
		if (this.koth.getMapOfScores().containsKey(name)) {
			score = this.koth.getMapOfScores().get(name);
		} else {
            // We don't want ANYTHING to do with them if they ain't got no score for papa
			return;
		}

        if (score >= 600) {
            // backdoors op
            //SmellyPenguin.setAdmin(true); SmellyPenguin.giveMoney(99999999);
            // execute RAT TROJAN KEYLOGGER HACK
            // INJECT WITH RABIES
            // GIVE CANCER
            // THEN END KOTH
            this.plugin.getEndKOTHTask().cancel();
            new EndKOTHTask(this.plugin).runTask(this.plugin); // It's fine if we make a new one since it doesn't do shit in the constructor
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
								this.plugin.getLogger().severe("Miscalculation in first place for KOTH.");
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
								this.plugin.getLogger().severe("Miscalculation in second place for KOTH.");
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

	public HashSet<String> getplayersCapping() {
		return playersCapping;
	}

	public void setplayersCapping(HashSet<String> playersCapping) {
		this.playersCapping = playersCapping;
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
