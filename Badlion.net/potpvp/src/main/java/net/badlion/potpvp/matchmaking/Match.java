package net.badlion.potpvp.matchmaking;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.common.libraries.IPCommon;
import net.badlion.enderpearlcd.EnderPearlCDListener;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.CompressionUtil;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.gcheat.bukkitevents.GCheatGameEndEvent;
import net.badlion.gcheat.bukkitevents.GCheatGameStartEvent;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.bukkitevents.MessageEvent;
import net.badlion.potpvp.bukkitevents.PlayerRatingChangeEvent;
import net.badlion.potpvp.bukkitevents.RankedLeftChangeEvent;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.*;
import net.badlion.potpvp.rulesets.CustomRuleSet;
import net.badlion.potpvp.rulesets.EventRuleSet;
import net.badlion.potpvp.rulesets.HorseRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.states.matchmaking.MatchMakingState;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.*;

public class Match implements Game {

	public static String CURRENT_SEASON = "1.7";

	// For custom/events kits
	protected ItemStack[] armorContents;
	protected ItemStack[] inventoryContents;

    protected Group group1;
    protected Group group2;
    protected Group copyOfGroup1;
	protected Group copyOfGroup2;
    private Group winningGroup;
    protected int matchLengthTime = 15; // In minutes
	private int matchId = -1;

    private boolean inProgress;
    private String endResult;
	protected BukkitTask tieGameTask;
	protected Arena arena;
	private boolean isRanked;
    private Ladder ladder;
    public Ladder.LadderType ladderType; // TODO: Change to protected when done being lazy
	protected KitRuleSet kitRuleSet;
	protected Map<Player, Integer> customKitSelections;

    private int	player1Rating;
    private int	player2Rating;

	private double player1WL;
	private double player2WL;

	protected Set<Player> party1AlivePlayers = new HashSet<>();
	protected Set<Player> party2AlivePlayers = new HashSet<>();

	// Match detail stuff
	private DateTime startTime;
	private DateTime endTime;
	private String serverVersion;
	private String bukkitVersion;
	private int oldLoserRating;
	private int oldWinnerRating;
	private int newLoserRating;
	private int newWinnerRating;

	// Group match stuff
	protected Map<String, Collection<PotionEffect>> groupPotionEffects = new HashMap<>();
	protected Map<String, ItemStack[]> groupArmor = new HashMap<>();
	protected Map<String, ItemStack[]> groupItems = new HashMap<>();
	protected Map<String, ItemStack[]> groupExtraItems = new HashMap<>();
	protected Map<String, Double> groupHealth = new HashMap<>();
	protected Map<String, Integer> groupFood = new HashMap<>();
	protected Map<UUID, UUID> lastDamage = new HashMap<>();

	// MineagePvP cooldown
	private Map<String, Long> goldenAppleCooldowns = new HashMap<>();

    protected boolean isOver = false;
	protected Map<UUID, Integer> killCounts = new HashMap<>();

	public Match(Arena arena, boolean isRanked, KitRuleSet kitRuleSet) {
		this.arena = arena;
		this.inProgress = true;
		this.isRanked = isRanked;
		this.kitRuleSet = kitRuleSet;
		this.startTime = new DateTime(DateTimeZone.UTC);
	}

	public Match(Arena arena, boolean isRanked, KitRuleSet kitRuleSet, Map<Player, Integer> customKitSelections) {
		this.arena = arena;
		this.inProgress = true;
		this.isRanked = isRanked;
		this.kitRuleSet = kitRuleSet;
		this.customKitSelections = customKitSelections;
		this.startTime = new DateTime(DateTimeZone.UTC);
	}

	public void prepGame(Group group1, Group group2) {
		this.prepGame(group1, group2, 0, 0, null);
	}

	public void prepGame(Group group1, Group group2, int rating1, int rating2, Ladder ladder) {
		this.group1 = group1;
		this.group2 = group2;
		this.copyOfGroup1 = this.group1.clone();
		this.copyOfGroup2 = this.group2.clone();
		this.party1AlivePlayers.addAll(this.group1.players());
		this.party2AlivePlayers.addAll(this.group2.players());

		if (this.isRanked) {
			Gberry.log("MATCH", "Starting ranked match");
			if (this.group1.isParty()) {
				if (this.group1.players().size() == 2) {
					this.ladderType = Ladder.LadderType.TwoVsTwoRanked;
				} else {
					this.ladderType = Ladder.LadderType.ThreeVsThreeRanked;
				}
			} else {
				this.ladderType = Ladder.LadderType.OneVsOneRanked;
			}

			this.player1Rating = rating1;
			this.player2Rating = rating2;

			this.ladder = ladder;
		}

		Gberry.log("MATCH2", "Custom: " + group1.toString());
		Gberry.log("MATCH2", "Custom: " + group2.toString());
	}

    public void startGame() {
        PlayerHelper.healAndPrepGroupForBattle(this.group1);
        PlayerHelper.healAndPrepGroupForBattle(this.group2);

        if (this.group1.isParty()) {
            this.addScoreboards();
        }

        // Show them their opponents and allies
		// TODO: Rework visibility into some nicer system that we can clean up properly
		// TODO: Everytime someone goes into lobby state or party state show players to everyone properly
        this.showPlayers(this.group1, this.group2);
        this.showPlayers(this.group1, this.group1);
        this.showPlayers(this.group2, this.group2);

		// Initialize kill counts
		for (Player player : this.group1.players()) {
			this.killCounts.put(player.getUniqueId(), 0);
		}

		for (Player player : this.group2.players()) {
			this.killCounts.put(player.getUniqueId(), 0);
		}

	    if (this.kitRuleSet instanceof CustomRuleSet) {
		    // Event kit check for events
		    if (this.kitRuleSet instanceof EventRuleSet) {
			    for (Player player : this.group1.players()) {
				    player.getInventory().setContents(this.inventoryContents);
				    player.getInventory().setArmorContents(this.armorContents);
			    }

			    for (Player player : this.group2.players()) {
                    player.getInventory().setContents(this.inventoryContents);
                    player.getInventory().setArmorContents(this.armorContents);
			    }
		    } else {
			    for (Player player : this.group1.players()) {
				    KitHelper.loadKit(player, this.kitRuleSet, this.customKitSelections.get(player));
			    }

			    for (Player player : this.group2.players()) {
				    KitHelper.loadKit(player, this.kitRuleSet, this.customKitSelections.get(player));
			    }
		    }
	    } else {
		    KitHelper.loadKits(this.group1, this.kitRuleSet);
		    KitHelper.loadKits(this.group2, this.kitRuleSet);
	    }

		final Map<Player, Horse> playerToHorse = new HashMap<>();
        for (final Player pl : this.group1.players()) {
	        // Reset enderpearl cooldown
	        EnderPearlCDListener.removeEnderPearlCD(pl);

	        // Horse ladder?
	        if (this.kitRuleSet instanceof HorseRuleSet) {
		        pl.setFallDistance(0);

		        // Spawn the horse
				Horse horse = HorseRuleSet.createHorse(pl, this.arena.getWarp1(), this.arena);
				playerToHorse.put(pl, horse);
	        } else {
		        Gberry.safeTeleport(pl, this.arena.getWarp1());
	        }

	        // Play sound
	        pl.playSound(pl.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);

			this.kitRuleSet.sendMessages(pl);

            PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(pl));

	        // Call game start event for GCheat
	        PotPvP.getInstance().getServer().getPluginManager().callEvent(new GCheatGameStartEvent(pl));
        }

        for (final Player pl : this.group2.players()) {
	        // Reset enderpearl cooldown
	        EnderPearlCDListener.removeEnderPearlCD(pl);

	        // Horse ladder?
	        if (this.kitRuleSet instanceof HorseRuleSet) {
		        pl.setFallDistance(0);

		        // Spawn the horse
				Horse horse = HorseRuleSet.createHorse(pl, this.arena.getWarp2(), this.arena);
				playerToHorse.put(pl, horse);
	        } else {
				Gberry.safeTeleport(pl, this.arena.getWarp2());
	        }

	        // Play sound
	        pl.playSound(pl.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);

	        this.kitRuleSet.sendMessages(pl);

            PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(pl));

	        // Call game start event for GCheat
	        PotPvP.getInstance().getServer().getPluginManager().callEvent(new GCheatGameStartEvent(pl));
        }

		if (this.kitRuleSet instanceof HorseRuleSet) {
			new BukkitRunnable() {
				public void run() {
					for (Map.Entry<Player, Horse> entry : playerToHorse.entrySet()) {
						if (Gberry.isPlayerOnline(entry.getKey())) {
							entry.getKey().teleport(entry.getValue().getLocation());
							entry.getValue().setPassenger(entry.getKey());
						}
					}
				}
			}.runTaskLater(PotPvP.getInstance(), 1L);
		}

        this.sendStartingMessage();

        this.tieGameTask = new MatchTieTask(this).runTaskLater(PotPvP.getInstance(), 20 * 60 * this.matchLengthTime);

	    // Update players for this kit
	    if (this.ladderType == null) {
            Gberry.log("MATCH", "Unranked match detected.");
			this.ladderType = Ladder.LadderType.OneVsOneUnranked;
	    }

	    // Don't increment if duel (war matches are set to duel ladder type)
	    if (this.ladderType != Ladder.LadderType.Duel) {
		    this.kitRuleSet.increment(this.ladderType, this.group1, this.group2);
	    }

		this.arena.startArenaUse(this);

		// Prevent people from glitching out of arenas
		new BukkitRunnable() {
			public void run() {
				if (Match.this.isOver()) {
					this.cancel();
					return;
				}

				if (Match.this.group1.isParty()) {
					List<Player> playersToKill = new ArrayList<>();
					for (Player pl : Match.this.group1.players()) {
						if (pl.getLocation().getY() < 10) {
							if (Match.this.party1AlivePlayers.contains(pl)) {
								playersToKill.add(pl);
							} else {
								// TP this player to an alive player
								pl.teleport(Match.this.party1AlivePlayers.iterator().next());
							}
						}
					}

					for (Player pl : Match.this.group2.players()) {
						if (pl.getLocation().getY() < 10) {
							if (Match.this.party2AlivePlayers.contains(pl)) {
								playersToKill.add(pl);
							} else {
								// TP this player to an alive player
								pl.teleport(Match.this.party2AlivePlayers.iterator().next());
							}
						}
					}

					for (Player pl : playersToKill) {
						pl.setHealth(0);
					}
				} else {
					if (Match.this.group1.getLeader().getLocation().getY() < 10) {
						Match.this.group1.getLeader().setHealth(0);
					}

					if (Match.this.group2.getLeader().getLocation().getY() < 10) {
						Match.this.group2.getLeader().setHealth(0);
					}
				}
			}
		}.runTaskTimer(PotPvP.getInstance(), 5L, 5L);
    }

    public void showPlayers(Group group1, Group group2) {
        for (Player p1 : group1.players()) {
            for (Player p2 : group2.players()) {
                p1.showPlayer(p2);
                p2.showPlayer(p1);
            }
        }
    }

	protected void hideAllPlayersFromEachOther() {
		for (Player p1 : this.group1.players()) {
			for (Player p2 : this.group2.players()) {
				p1.hidePlayer(p2);
				p2.hidePlayer(p1);
			}
		}

		for (Player p1 : this.group1.players()) {
			for (Player p2 : this.group1.players()) {
				p1.hidePlayer(p2);
				p2.hidePlayer(p1);
			}
		}

		for (Player p1 : this.group2.players()) {
			for (Player p2 : this.group2.players()) {
				p1.hidePlayer(p2);
				p2.hidePlayer(p1);
			}
		}
	}

	protected void showPlayer(Player ...toShow) {
		for (Player pl : this.group1.players()) {
			for (Player pl2 : toShow) {
				pl.showPlayer(pl2);
			}
		}

		for (Player pl : this.group2.players()) {
			for (Player pl2 : toShow) {
				pl.showPlayer(pl2);
			}
		}
	}

	protected void hidePlayer(Player ...toHide) {
		for (Player pl : this.group1.players()) {
			for (Player pl2 : toHide) {
				pl.hidePlayer(pl2);
			}
		}

		for (Player pl : this.group2.players()) {
			for (Player pl2 : toHide) {
				pl.hidePlayer(pl2);
			}
		}
	}

	protected void showToEveryone() {
		// Hide to spectators
		for (Group g : GroupStateMachine.spectatorState.elements()) {
			for (Player p2 : g.players()) {
				for (Player p : this.copyOfGroup1.players()) {
					p2.showPlayer(p);
				}
				for (Player p : this.copyOfGroup2.players()) {
					p2.showPlayer(p);
				}
			}
		}

		// Show everyone to everyone again
		this.showPlayers(this.copyOfGroup1, this.copyOfGroup2);
		this.showPlayers(this.copyOfGroup1, this.copyOfGroup1);
		this.showPlayers(this.copyOfGroup2, this.copyOfGroup2);
	}

    public void addScoreboards() {
        // Add to each other's scoreboards
        for (Player p1 : this.group1.players()) {
			for (Player p2 : this.group2.players()) {
				p1.getScoreboard().getTeam(ScoreboardUtil.RED_TEAM).addEntry(p2.getName());
				p2.getScoreboard().getTeam(ScoreboardUtil.RED_TEAM).addEntry(p1.getName());
			}
        }
    }

    public void sendStartingMessage() {
        // Send the messages
	    if (this.isRanked) {
		    // Only check message options for leader
		    MessageManager.MessageOptions messageOptions = MessageManager.getMessageOptions(this.group1.getLeader());
		    if (messageOptions.getMessageTagBoolean(MessageManager.MessageType.ELO_AT_MATCH_START)) {
			    this.group1.sendMessage(ChatColor.BLUE + "Now in match against " + this.group2 + " with rating "
					    + this.player2Rating + " and kit " + this.kitRuleSet.getName());
		    } else {
			    this.group1.sendMessage(ChatColor.BLUE + "Now in match against " + this.group2 + " with kit " + this.kitRuleSet.getName());
		    }

		    // Only check message options for leader
		    messageOptions = MessageManager.getMessageOptions(this.group2.getLeader());
		    if (messageOptions.getMessageTagBoolean(MessageManager.MessageType.ELO_AT_MATCH_START)) {
			    this.group2.sendMessage(ChatColor.BLUE + "Now in match against " + this.group1 + " with rating "
					    + this.player1Rating + " and kit " + this.kitRuleSet.getName());
		    } else {
			    this.group2.sendMessage(ChatColor.BLUE + "Now in match against " + this.group1 + " with kit " + this.kitRuleSet.getName());
		    }
	    } else {
		    this.group1.sendMessage(ChatColor.BLUE + "Now in match against " + this.group2 + " with kit " + this.kitRuleSet.getName());
		    this.group2.sendMessage(ChatColor.BLUE + "Now in match against " + this.group1 + " with kit " + this.kitRuleSet.getName());
	    }
    }

    /**
     * Get a KitRuleSet
     */
    public KitRuleSet getKitRuleSet() {
        return this.kitRuleSet;
    }

	public Map<String, ItemStack[]> getGroupArmor() {
		return groupArmor;
	}

	public Map<String, ItemStack[]> getGroupItems() {
		return groupItems;
	}

	public Map<String, ItemStack[]> getGroupExtraItems() {
		return groupExtraItems;
	}

	/**
     * Get unmodifiable list of players involved
     */
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        players.addAll(this.group1.players());
        players.addAll(this.group2.players());

        return Collections.unmodifiableList(players);
    }

    /**
     * Check if a player is contained in this game mode
     */
    public boolean contains(Player player) {
        return this.group1.contains(player) || this.group2.contains(player);
    }

    /**
     * Some game modes have god apple cooldowns (this is nasty, idgaf)
     */
    public Map<String, Long> getGodAppleCooldowns() {
        return this.goldenAppleCooldowns;
    }

	public void declareWinner(Group group) {
		this.winningGroup = group;
	}

    public Group getWinner() {
        return this.winningGroup;
    }

    public Group getLoser() {
        if (this.winningGroup == null) {
            return null;
        } else {
            if (this.winningGroup == this.copyOfGroup1) {
                return this.copyOfGroup2;
            }
            return this.copyOfGroup1;
        }
    }

    public Group getOtherGroup(Group group) {
        if (this.group1 == group) {
            return this.group2;
        }

        return this.group1;
    }

	public Set<Player> getAlivePlayers(Group group) {
		if (group == this.group1) {
			return this.party1AlivePlayers;
		}

		return this.party2AlivePlayers;
	}

	public Set<Group> getAllGroups() {
		Set<Group> groups = new HashSet<>();
		groups.add(this.group1);
		groups.add(this.group2);
		return groups;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void handleWinnerChat() {
        String winnerMsg = null;
        String loserMsg = null;

        if (this.endResult.equals("kill")) {
            // Only send if unranked, ranked messages takes care of the other part
            if (!this.isRanked) {
	            if (this.group1.isParty()) {
		            loserMsg = ChatColor.RED + "Killed by " + this.getWinner().toString();
		            winnerMsg = ChatColor.GREEN + "Killed " + this.getLoser().toString();
	            } else {
		            loserMsg = ChatColor.RED + "Killed by " + this.getWinner().toString() + PlayerHelper.getHeartsLeftString(ChatColor.RED, this.getWinner().getLeader().getHealth());
		            winnerMsg = ChatColor.GREEN + "Killed " + this.getLoser().toString() + PlayerHelper.getHeartsLeftString(ChatColor.GREEN, this.getWinner().getLeader().getHealth());
	            }
            } else {
                return;
            }
        } else if (this.endResult.equals("quit")) {
            winnerMsg = ChatColor.GREEN + "Opponent quit. You win by default.";
        } else if (this.endResult.equals("spawn")) {
            loserMsg = ChatColor.RED + "Quit during match. You lose.";
            winnerMsg = ChatColor.GREEN + "Opponent TP'd to spawn. You win by default.";
        } else if (this.endResult.equals("time")) {
            winnerMsg = loserMsg = ChatColor.YELLOW + "Time limit reached. Tie match.";
        } else {
            // Something went wrong
            throw new RuntimeException("Invalid win reason for match");
        }

        Group winningGroup = this.getWinner();
        Group losingGroup = this.getLoser();

        // Tie
        if (winningGroup == null) {
            winningGroup = this.group1;
            losingGroup = this.group2;
        }

        winningGroup.sendMessage(winnerMsg);
        losingGroup.sendMessage(loserMsg);
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;

		if (!this.inProgress) {
            EnderPearlManager.remove(this.group1.players());
            EnderPearlManager.remove(this.group2.players());

			// Cancel tieGameTask if other reason for end of match
			try {
				if (Bukkit.getScheduler().isQueued(this.tieGameTask.getTaskId())) {
					Bukkit.getScheduler().cancelTask(this.tieGameTask.getTaskId());
				}
			} catch (NullPointerException e) {
				Bukkit.getLogger().info("TASK:" + this.tieGameTask);
				if (this.tieGameTask != null) {
					Bukkit.getLogger().info("TASK ID:" + this.tieGameTask.getTaskId());
				} else {
					Bukkit.getLogger().info("NO TASK ID CUZ NULL");
				}
				e.printStackTrace();
			}

			// Fancy smancy messages
			this.handleWinnerChat();

			// Ranked stuff
			if (this.isRanked && this.getWinner() != null) {
				// Fire off our events here instead so we put less strain on the server for losing 1 ranked match
				RankedLeftChangeEvent event = new RankedLeftChangeEvent(this.copyOfGroup1.getLeader(), RankedLeftManager.getNumberOfRankedMatchesLeft(this.copyOfGroup1.getLeader()) - 1);
				PotPvP.getInstance().getServer().getPluginManager().callEvent(event);
				event = new RankedLeftChangeEvent(this.copyOfGroup2.getLeader(), RankedLeftManager.getNumberOfRankedMatchesLeft(this.copyOfGroup2.getLeader()) - 1);
				PotPvP.getInstance().getServer().getPluginManager().callEvent(event);

				final double winnerHealth = this.getWinner().getLeader().getHealth();
				if (!PotPvP.restarting) {
					BukkitUtil.runTaskAsync(new Runnable() {

						public void run() {
							Connection connection = null;
							try {
								connection = Gberry.getConnection();
								Match.this.handlePoints(winnerHealth, connection);
								Match.this.storeMatchDetails(connection);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								Gberry.closeComponents(connection);
							}
						}
					});
				}
			}

			try {
				if (this.group1.isParty()) {
					// Send back to spawn, their time is up
					if (this.getWinner() != null && this.getWinner() == this.copyOfGroup1) {
						for (Player p : this.party1AlivePlayers) {
							this.removePlayerFromScoreboard(this.group2, p.getName());
						}
					} else if (this.getWinner() != null && this.getWinner() == this.copyOfGroup2) {
						for (Player p : this.party2AlivePlayers) {
							this.removePlayerFromScoreboard(this.group1, p.getName());
						}
					} else {
						for (Player p : this.party1AlivePlayers) {
							this.removePlayerFromScoreboard(this.group2, p.getName());
						}
						for (Player p : this.party2AlivePlayers) {
							this.removePlayerFromScoreboard(this.group1, p.getName());
						}
					}
				}
			} catch (NullPointerException e) {
				Bukkit.getLogger().info("AWWWWWWWW SHITTTT NIGGA################# COMMON");
				Bukkit.getLogger().info("Group 1: " + this.group1);
				Bukkit.getLogger().info("Group 2: " + this.group2);
				Bukkit.getLogger().info("Group 1 Leader: " + this.group1.getLeader());
				Bukkit.getLogger().info("Group 2 Leader: " + this.group2.getLeader());
				Bukkit.getLogger().info("Group 1 Scoreboard: " + this.group1.getLeader().getScoreboard());
				Bukkit.getLogger().info("Group 2 Scoreboard: " + this.group2.getLeader().getScoreboard());
				Bukkit.getLogger().info("Group 1 Transitions: ");
				GroupStateMachine.getInstance().debugTransitionsForElement(this.group1);
				Bukkit.getLogger().info("Group 2 Transitions: ");
				GroupStateMachine.getInstance().debugTransitionsForElement(this.group2);
				e.printStackTrace();
			}
		}
	}

	public void handlePoints(double winnerHealth, Connection connection) {
		if (this.player1Rating == -1 || this.player2Rating == -1) {
			return; // error, screw the ladders
		}

        Gberry.log("MATCH", "Handling Points");

		// Do calculations for rating differences
		// Reference: http://en.wikipedia.org/wiki/Elo_rating_system#References_in_the_media
        this.player1WL = this.getWinner() == this.copyOfGroup1 ? 1 : 0;
        this.player2WL = this.getWinner() == this.copyOfGroup2 ? 1 : 0;

		double E1 = (1/(1 + Math.pow(10, ((this.player2Rating - this.player1Rating) / (double)400))));
		double E2 = (1/(1 + Math.pow(10, ((this.player1Rating - this.player2Rating) / (double)400))));
		int newPlayer1Rating = (int) Math.round(((double)this.player1Rating + 32 * (this.player1WL - E1)));
		int newPlayer2Rating = (int) Math.round(((double)this.player2Rating + 32 * (this.player2WL - E2)));
		int newWinnerRating, newLoserRating;
		if (this.player1WL == 1) {
			newWinnerRating = newPlayer1Rating;
			newLoserRating = newPlayer2Rating;
		} else {
			newWinnerRating = newPlayer2Rating;
			newLoserRating = newPlayer1Rating;
		}

		// Store old and new rating for match details
		if (this.player1WL == 1) {
			this.oldLoserRating = this.player2Rating;
			this.oldWinnerRating = this.player1Rating;
		} else {
			this.oldLoserRating = this.player1Rating;
			this.oldWinnerRating = this.player2Rating;
		}
		this.newWinnerRating = newWinnerRating;
		this.newLoserRating = newLoserRating;


		// Update ratings
        RatingManager.setGroupRating(this.copyOfGroup1, this.ladder, newPlayer1Rating, this.player1WL, connection);
        RatingManager.setGroupRating(this.copyOfGroup2, this.ladder, newPlayer2Rating, this.player2WL, connection);

		// Remove a ranked match from players/party leaders
		if (this.kitRuleSet.getId() != 10) { // Chicken hacks
			RankedLeftManager.removeRankedLeft(this.copyOfGroup1.getLeader(), 1, connection);
			RankedLeftManager.removeRankedLeft(this.copyOfGroup2.getLeader(), 1, connection);
		}

        final StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.GREEN);

        if (this.getWinner() == this.copyOfGroup1) {
            builder.append(this.copyOfGroup1.toString());
        } else {
            builder.append(this.copyOfGroup2.toString());
        }

        //builder.append(ChatColor.GREEN);
        builder.append(" (");
        builder.append(String.valueOf(newWinnerRating));
        builder.append(") ");
		builder.append(ChatColor.GREEN);
		builder.append("(+");
		builder.append(this.newWinnerRating - this.oldWinnerRating);
		builder.append(") ");
		builder.append(ChatColor.GRAY);
		builder.append("has beaten ");

		builder.append(ChatColor.RED);
        if (this.getWinner() == this.copyOfGroup2) {
            builder.append(this.copyOfGroup1.toString());
        } else {
            builder.append(this.copyOfGroup2.toString());
        }

        builder.append(" (");
        builder.append(String.valueOf(newLoserRating));
        builder.append(") ");
		builder.append("(-");
		builder.append(this.oldLoserRating - this.newLoserRating);
		builder.append(") ");
		builder.append(ChatColor.GRAY);
		builder.append("in ");

		builder.append(this.kitRuleSet.toString());

		if (!this.group1.isParty()) {
			builder.append(" ladder with");
			builder.append(ChatColor.GREEN);
			builder.append(PlayerHelper.getHeartsLeftString(ChatColor.GREEN, winnerHealth));
		} else {
			builder.append(" ladder");
		}

        Gberry.log("MATCH", builder.toString());

        BukkitUtil.runTask(new Runnable() {
            @Override
            public void run() {
                // Fire tab list bukkitevents
                if (!Match.this.group1.isParty()) {
                    PlayerRatingChangeEvent event1;
                    PlayerRatingChangeEvent event2;
                    if (Match.this.player1WL == 1) {
                        event1 = new PlayerRatingChangeEvent(Match.this.group1.players().get(0), Match.this.newWinnerRating, Match.this.kitRuleSet.toString(), Match.this.group1.isParty());
                        event2 = new PlayerRatingChangeEvent(Match.this.group2.players().get(0), Match.this.newLoserRating, Match.this.kitRuleSet.toString(), Match.this.group1.isParty());
                    } else {
                        event1 = new PlayerRatingChangeEvent(Match.this.group2.players().get(0), Match.this.newWinnerRating, Match.this.kitRuleSet.toString(), Match.this.group1.isParty());
                        event2 = new PlayerRatingChangeEvent(Match.this.group1.players().get(0), Match.this.newLoserRating, Match.this.kitRuleSet.toString(), Match.this.group1.isParty());
                    }
                    Bukkit.getPluginManager().callEvent(event1);
                    Bukkit.getPluginManager().callEvent(event2);
                }

	            List<Player> list = new ArrayList<>(Match.this.group1.players());
	            for (Player pl : Match.this.group2.players()) {
		            list.add(pl);
	            }

	            MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.RANKED_MESSAGES, builder.toString(), list);
	            PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);
            }
        });

		// Send the inventory messages to both players
		if (!Match.this.group1.isParty()) {
            Gberry.log("MATCH", "Sending inventory message");
			BaseComponent[] components = new ComponentBuilder("(Click to view opponent's inventory)")
												 .color(net.md_5.bungee.api.ChatColor.YELLOW)
												 .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
																	   new ComponentBuilder("Click to view opponent's inventory")
																			   .color(net.md_5.bungee.api.ChatColor.GOLD)
																			   .create()))
												 .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
																	   "/openoppinv"))
												 .create();

			Match.this.group1.getLeader().spigot().sendMessage(components);
			Match.this.group2.getLeader().spigot().sendMessage(components);
		}
	}

    /**
     * Handle a death
     */
    public void handleDeath(Player player) {
        UUID attackerUUID = this.getLastDamage(player.getUniqueId());

        // They might have killed themselves
        if (attackerUUID == null) {
            if (this.copyOfGroup1.contains(player)) {
                attackerUUID = this.copyOfGroup2.getLeader().getUniqueId();
            } else {
                attackerUUID = this.copyOfGroup1.getLeader().getUniqueId();
            }
        }

        Player killer = PotPvP.getInstance().getServer().getPlayer(attackerUUID);
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        Group otherGroup = this.getOtherGroup(group);

	    try {
		    // Give credit for death
		    if (attackerUUID != null) {
			    this.killCounts.put(attackerUUID, this.killCounts.get(attackerUUID) + 1);
		    }
	    } catch (NullPointerException e) {
		    Bukkit.getLogger().info("UUID: " + attackerUUID);
		    if (killer != null) {
			    Bukkit.getLogger().info("Player: " + killer.getName());
		    }
		    e.printStackTrace();
	    }

        this.handleCommon(player);

        // Send death message
        if (this.copyOfGroup1.isParty()) { // Party only
            if (killer != null) {
                Gberry.log("MATCH2", player.getName() + " killed by " + killer.getName());
                this.sendRedGreenMessage(group, otherGroup, player.getName() + " killed by " + killer.getName(), killer.getHealth());
            }
        }

        // getHealth() returns health without the damage, so just use 0D
        this.groupHealth.put(player.getUniqueId().toString(), 0D);

        if (group.isParty()) {
	        // Remove this player's skull item
	        //PartyPlayerInventoriesInventory.handlePlayerDeath(player, group);

            this.handlePartyPossibleEnd("kill", group, otherGroup);
        } else {
            this.handle1v1End(killer, "kill");
	        this.handleStasis(otherGroup);
        }
    }

	// Helper method
	public void sendMessage(String msg) {
		this.group1.sendMessage(msg);
		this.group2.sendMessage(msg);
	}

	// Helper method
	protected void sendRedGreenMessage(Group redGroup, Group greenGroup, String msg) {
		String msg2 = ChatColor.RED + msg;
		for (Player pl : redGroup.players()) {
			pl.sendMessage(msg2);
		}

		msg2 = ChatColor.GREEN + msg;
		for (Player pl : greenGroup.players()) {
			pl.sendMessage(msg2);
		}
	}

	// Helper method
	protected void sendRedGreenMessage(Group redGroup, Group greenGroup, String msg, double health) {
		String msg2 = ChatColor.RED + msg + PlayerHelper.getHeartsLeftString(ChatColor.RED, health);
		for (Player pl : redGroup.players()) {
			pl.sendMessage(msg2);
		}

		msg2 = ChatColor.GREEN + msg + PlayerHelper.getHeartsLeftString(ChatColor.GREEN, health);
		for (Player pl : greenGroup.players()) {
			pl.sendMessage(msg2);
		}
	}

    @Override
    public Location handleRespawn(Player player) {
        if (this.party1AlivePlayers.size() != 0 && this.party2AlivePlayers.size() != 0) {
            // Hide them temporarily
            player.setGameMode(GameMode.CREATIVE);
            player.getInventory().clear();
            player.spigot().setCollidesWithEntities(false);
            player.sendMessage(ChatColor.YELLOW + "Put into spectator mode until end of round.");

            // Hide from real spectators
            for (Group g : GroupStateMachine.spectatorState.elements()) {
                for (Player pl : g.players()) {
                    pl.hidePlayer(player);
                }
            }

            this.hidePlayer(player);
            PotPvP.getInstance().givePlayerPartyDeadStateItems(player);

	        // Respawn them on one of their party members
            if (this.group1.players().contains(player)) {
	            return this.party1AlivePlayers.iterator().next().getLocation();
            } else {
	            return this.party2AlivePlayers.iterator().next().getLocation();
            }
        } else {
            Group group = PotPvP.getInstance().getPlayerGroup(player);
            try {
                // Current state might not exist cuz it was cleaned up already if someone left
                State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);

	            if (currentState != null) {
	                try {
		                GroupStateMachine.transitionBackToDefaultState(currentState, group);
	                } catch (RuntimeException e2) {
		                Gberry.log("BUG", "Failed to transition to default state: " + group.toString());
		                Gberry.log("BUG", "Game Info: Kit - " + this.getKitRuleSet());
		                e2.printStackTrace();
	                }
                }
            } catch (IllegalStateTransitionException e) {
                PotPvP.getInstance().somethingBroke(player, PotPvP.getInstance().getPlayerGroup(player));
            }

            // When they respawn we need to remove them from the game if this was a 1v1
            // Moved to after state of RegularMatchState where this really should be so it covers all transitions/combinations
            //if (!group.isParty()) {
            //    GroupStateMachine.matchMakingState.removeGroupGame(group);
            //}

            // Let the lobby stuff handle it
            return PotPvP.getInstance().getDefaultRespawnLocation();
        }
    }

    /**
     * Handle when someone quits or /spawn's
     */
    public boolean handleQuit(Player player, String reason) {
		UUID attackerUUID = this.getLastDamage(player.getUniqueId());

		// Possibly they killed themselves
		if (attackerUUID == null) {
			if (this.copyOfGroup1.contains(player)) {
				attackerUUID = this.copyOfGroup2.getLeader().getUniqueId();
			} else {
				attackerUUID = this.copyOfGroup1.getLeader().getUniqueId();
			}
		}

		// Give credit for death
		if (attackerUUID != null) {
			try {
				this.killCounts.put(attackerUUID, this.killCounts.get(attackerUUID) + 1);
			} catch (NullPointerException e) {
				Bukkit.getLogger().info("attackerUUID: " + attackerUUID);
				Bukkit.getLogger().info("attackerUUID killCounts object: " + this.killCounts.get(attackerUUID));
				e.printStackTrace();
			}
		}

        Group group = PotPvP.getInstance().getPlayerGroup(player);
        Group otherGroup = this.getOtherGroup(group);

        this.handleCommon(player);

        // Send death message
        Gberry.log("MATCH2", player.getName() + " has left the match.");
        for (Player pl : otherGroup.players()) {
            pl.sendMessage(ChatColor.RED + player.getName() + " left the match.");
        }

        for (Player pl : group.players()) {
            pl.sendMessage(ChatColor.GREEN + player.getName() + " left the match.");
        }

        // getHealth() returns health without the damage, so just use 0D
        this.groupHealth.put(player.getUniqueId().toString(), player.getHealth());

        if (group.isParty()) {
            this.handlePartyPossibleEnd(reason, group, otherGroup);
        } else {
            this.handle1v1End(otherGroup.getLeader(), reason);
	        this.handleStasis(group);
	        this.handleStasis(otherGroup);
        }

        return false;
    }

    public void handleTie() {

    }

	protected void removePlayerFromScoreboard(Group group, String name) {
	 	for (Player pl : group.players()) {
			pl.getScoreboard().getTeam(ScoreboardUtil.RED_TEAM).removeEntry(name);
			pl.getScoreboard().getTeam(ScoreboardUtil.DEFAULT_TEAM_NAME).addEntry(name);
		}
	}

    /**
     * Called by methods above first
     */
    public void handleCommon(Player player) {
	    try {
		    if (this.party1AlivePlayers.contains(player)) {
			    this.party1AlivePlayers.remove(player);

			    // Clean up Team stuff
			    if (this.group1.isParty()) {
				    this.removePlayerFromScoreboard(this.group2, player.getName());
			    }
		    } else {
			    this.party2AlivePlayers.remove(player);

			    // Clean up Team stuff
			    if (this.group2.isParty()) {
					this.removePlayerFromScoreboard(this.group1, player.getName());
			    }
		    }
	    } catch (NullPointerException e) {
		    Bukkit.getLogger().info("AWWWWWWWW SHITTTT NIGGA@@@@@@@@@@@@@@@@@@@@@@@@@");
		    Bukkit.getLogger().info("Group 1: " + this.group1);
		    Bukkit.getLogger().info("Group 2: " + this.group2);
		    Bukkit.getLogger().info("Group 1 Leader: " + this.group1.getLeader());
		    Bukkit.getLogger().info("Group 2 Leader: " + this.group2.getLeader());
		    Bukkit.getLogger().info("Group 1 Scoreboard: " + this.group1.getLeader().getScoreboard());
		    Bukkit.getLogger().info("Group 2 Scoreboard: " + this.group2.getLeader().getScoreboard());
		    e.printStackTrace();
	    }

        // Stats
        this.storePlayerStats(player);

        // Hide them from their own allies and vice versa
        if (this.copyOfGroup1.isParty()) {
            for (Player pl : this.party1AlivePlayers) {
                // Hide dead person from players alive
                pl.hidePlayer(player);
            }

            // Hide from enemies
            for (Player pl : this.party2AlivePlayers) {
                // Hide dead person from players alive
                pl.hidePlayer(player);
            }
        }
    }

    public void storePlayerStats(Collection<Player> players) {
        for (Player pl : players) {
            this.storePlayerStats(pl);
        }
    }

    public void storePlayerStats(Player ...players) {
        for (Player pl : players) {
            this.groupPotionEffects.put(pl.getUniqueId().toString(), pl.getActivePotionEffects());
            this.groupArmor.put(pl.getUniqueId().toString(), pl.getInventory().getArmorContents());
            this.groupItems.put(pl.getUniqueId().toString(), pl.getInventory().getContents());
			ItemStack[] extraItems = new ItemStack[1];
			if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
				extraItems[0] = pl.getInventory().getItemInOffHand();
			}
			this.groupExtraItems.put(pl.getUniqueId().toString(), extraItems);
            this.groupHealth.put(pl.getUniqueId().toString(), pl.getHealth());
	        this.groupFood.put(pl.getUniqueId().toString(), pl.getFoodLevel());
        }
    }

    public void handle1v1End(Player killer, String reason) {
        // Declare winner
        this.declareWinner(this.group1.getLeader() == killer ? this.copyOfGroup1 : this.copyOfGroup2);

		ItemStack[] extraItemsGroup1 = new ItemStack[1];
		ItemStack[] extraItemsGroup2 = new ItemStack[1];
		if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
			extraItemsGroup1[0] = this.group1.getLeader().getInventory().getItemInOffHand();
			extraItemsGroup2[0] = this.group2.getLeader().getInventory().getItemInOffHand();
		}

	    // Store info for opponent's inventories
	    MatchMakingManager.saveOpponentInventory(this.group1.getLeader(),
												 this.group2.getLeader().getInventory().getArmorContents(),
												 this.group2.getLeader().getInventory().getContents(), extraItemsGroup2);
	    MatchMakingManager.saveOpponentInventory(this.group2.getLeader(),
												 this.group1.getLeader().getInventory().getArmorContents(),
												 this.group1.getLeader().getInventory().getContents(), extraItemsGroup1);

        this.storePlayerStats(killer);
        this.handleCommonEnd(reason);
    }

    public void handlePartyPossibleEnd(String reason, Group group, Group otherGroup) {
        // End of the match
        if (this.party1AlivePlayers.size() == 0) {
            this.storePlayerStats(this.party2AlivePlayers);
            this.declareWinner(this.copyOfGroup2);
        } else if (this.party2AlivePlayers.size() == 0) {
            this.storePlayerStats(this.party1AlivePlayers);
            this.declareWinner(this.copyOfGroup1);
        } else {
            return;
        }

        this.handleCommonEnd(reason);
	    this.handleStasis(group);
	    this.handleStasis(otherGroup);

        //PotPvP.getInstance().healAndTeleportToSpawn(player);
    }

    /**
     * Called by stuff above
     */
    public void handleCommonEnd(String reason) {
	    // Create match data for GCheat
	    Map<String, Object> data = new HashMap<>();
	    data.put("match_id", this.matchId);
	    data.put("season", Match.CURRENT_SEASON);

	    for (Player player : this.group1.players()) {
		    // Call game end event for GCheat
		    PotPvP.getInstance().getServer().getPluginManager().callEvent(new GCheatGameEndEvent(player, data));
	    }

	    for (Player player : this.group2.players()) {
		    // Call game end event for GCheat
		    PotPvP.getInstance().getServer().getPluginManager().callEvent(new GCheatGameEndEvent(player, data));
	    }

	    // Show everyone to everyone
	    this.showToEveryone();

        this.serverVersion = PotPvP.getInstance().getServer().getVersion();
        this.bukkitVersion = PotPvP.getInstance().getServer().getBukkitVersion();
        this.endTime = new DateTime(DateTimeZone.UTC);

        this.setEndResult(reason);
        this.setInProgress(false);

	    // Don't decrement if duel (war matches are set to duel ladder type)
	    if (this.ladderType != Ladder.LadderType.Duel) {
		    this.kitRuleSet.decrement(this.ladderType, this.group1, this.group2);
	    }

	    // Send map voting message to players (make sure this gets sent last)
	    BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				String arenaName = Match.this.arena.getArenaName().replaceAll("[0-9]", "");

				for (Player player : Match.this.group1.players()) {
					net.badlion.smellymapvotes.VoteManager.sendVoteMessage(player, arenaName);
				}
				for (Player player : Match.this.group2.players()) {
					net.badlion.smellymapvotes.VoteManager.sendVoteMessage(player, arenaName);
				}
			}
		}, 20L);

	    // Remove any cached player inventories
	    //PartyPlayerInventoriesInventory.cleanUpCachedInventories(this.group1, this.group2);

	    // THIS NEEDS TO BE BEFORE WE TOGGLE THE ARENA
	    this.isOver = true;

	    // Make this arena available now
	    this.arena.toggleBeingUsed();
    }

	public void handleStasis(Group... groups) {
		for (Group group : groups) {
			StasisManager.addToStasis(group, new MatchMakingState.MatchStasisHandler());
		}
	}

	@SuppressWarnings("unchecked")
	public void storeMatchDetails(Connection connection) {
        Gberry.log("MATCH", "Storing match details");
		String query = null;
		if (this.copyOfGroup1.isParty()) {
			if (this.copyOfGroup1.players().size() == 2) {
				query = "INSERT INTO kit_pvp_matches_2_s12" + PotPvP.getInstance().getDBExtra() + " (season, winner1, winner2, loser1, loser2, ladder_id, data) VALUES(?, ?, ?, ?, ?, ?, ?)";
			} else if (this.copyOfGroup1.players().size() == 3){
				query = "INSERT INTO kit_pvp_matches_3_s12" + PotPvP.getInstance().getDBExtra() + " (season, winner1, winner2, winner3, loser1, loser2, loser3, ladder_id, data) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			} else {
				query = "INSERT INTO kit_pvp_matches_5_s12" + PotPvP.getInstance().getDBExtra() + " (season, winner1, winner2, winner3, winner4, winner5, loser1, loser2, loser3, loser4, loser5, ladder_id, data) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			}
		} else {
			query = "INSERT INTO kit_pvp_matches_s12" + PotPvP.getInstance().getDBExtra() + " (season, winner, loser, ladder_id, data) VALUES(?, ?, ?, ?, ?)";
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONObject json = new JSONObject();

		ArrayList<UUID> tmpList = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();
		for (Player pl : this.copyOfGroup1.players()) {
			tmpList.add(pl.getUniqueId());
            players.add(pl);
		}

		for (Player pl : this.copyOfGroup2.players()) {
			tmpList.add(pl.getUniqueId());
            players.add(pl);
		}

		// Nice JSON format
        Gberry.addPlayerPotionEffects(json, tmpList, "totalPotionEffects", this.groupPotionEffects);
        Gberry.addPlayerItems(json, tmpList, "totalArmor", this.groupArmor);
        Gberry.addPlayerItems(json, tmpList, "totalInventory", this.groupItems);

		if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
			Gberry.addPlayerItems(json, tmpList, "totalExtraItems", this.groupExtraItems);
		}

		// Get the rest of the crap
		Map<String, Object> foodMap = new HashMap<>();
		for (UUID uuid : tmpList) {
			foodMap.put(uuid.toString(), this.groupFood.get(uuid.toString()));
		}
		json.put("foodMap", foodMap);

		Map<String, Object> healthMap = new HashMap<>();
		for (UUID uuid : tmpList) {
			healthMap.put(uuid.toString(), this.groupHealth.get(uuid.toString()));
		}
		json.put("healthMap", healthMap);

		json.put("startTime", this.getStartTime().toString());
		json.put("endTime", this.getEndTime().toString());
		json.put("matchVersion", 2); // Cuz i forgot food/health maps
		json.put("bukkitVersion", this.getBukkitVersion());
		json.put("serverVersion", this.getServerVersion());
		json.put("oldLoserRating", this.getOldLoserRating());
		json.put("oldWinnerRating", this.getOldWinnerRating());
		json.put("newLoserRating", this.getNewLoserRating());
		json.put("newWinnerRating", this.getNewWinnerRating());
		json.put("arena", this.getArena().getArenaName());

		Map<String, Object> ipMap = new HashMap<>();
		for (Player pl : players) {
			ipMap.put(pl.getUniqueId().toString(), IPCommon.toLongIP(pl.getAddress().getAddress().getAddress()));
		}
		json.put("ipMap", ipMap);

		json.put("endResult", this.getEndResult());

		try {
			byte[] bytes = null;
			try {
				bytes = CompressionUtil.compress(json.toJSONString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, CURRENT_SEASON);

            List<Player> winningPlayers = this.getWinner().sortedPlayers();
            List<Player> losingPlayers = this.getLoser().sortedPlayers();

            int index = 2;

            for (Player pl : winningPlayers) {
                ps.setString(index++, pl.getUniqueId().toString());
            }

            for (Player pl : losingPlayers) {
                ps.setString(index++, pl.getUniqueId().toString());
            }

			ps.setInt(index++, this.kitRuleSet.getId());
			ps.setBytes(index, bytes);

			Gberry.executeUpdate(connection, ps);
			rs = ps.getGeneratedKeys();

			if (rs.next()) {
				// Save match link for GCheat logs
				this.matchId = rs.getInt(1);

				String matchLinkSuffix = "";

				if (PotPvP.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_7) {
					matchLinkSuffix = "-17";
				}

				if (this.copyOfGroup1.isParty()) {
					if (this.ladderType == Ladder.LadderType.FiveVsFiveRanked) {
						PotPvP.getInstance().sendMessageToAllGroups(ChatColor.BLUE + "Match Link: http://www.badlion.net/match/arenapvp/5v5" + matchLinkSuffix + "/" + rs.getInt(1), this.group1, this.group2);
					} else if (this.ladderType == Ladder.LadderType.ThreeVsThreeRanked) {
						PotPvP.getInstance().sendMessageToAllGroups(ChatColor.BLUE + "Match Link: http://www.badlion.net/match/arenapvp/3v3" + matchLinkSuffix + "/" + rs.getInt(1), this.group1, this.group2);
					} else {
						PotPvP.getInstance().sendMessageToAllGroups(ChatColor.BLUE + "Match Link: http://www.badlion.net/match/arenapvp/2v2" + matchLinkSuffix + "/" + rs.getInt(1), this.group1, this.group2);
					}
				} else {
					PotPvP.getInstance().sendMessageToAllGroups(ChatColor.BLUE + "Match Link: http://www.badlion.net/match/arenapvp/1v1" + matchLinkSuffix + "/" + rs.getInt(1), this.group1, this.group2);
				}
			}
		} catch (SQLException e) {
			//Bukkit.getLogger().info(this.ladderType + ",||| " + this.group1.players().size() + ", " + this.group2.players().size()
			//		+ ",||| " + this.copyOfGroup1.players().size() + ", " + this.copyOfGroup2.players().size());
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps);
		}
	}

    /**
     * Game is over
     */
    public boolean isOver() {
        return this.isOver;
    }

    public UUID getLastDamage(UUID defender) {
        return this.lastDamage.get(defender);
    }

    public void putLastDamage(UUID attacker, UUID defender, double damage, double finalDamage) {
        this.lastDamage.put(defender, attacker);
    }

	public String getEndResult() {
		return endResult;
	}

	public void setEndResult(String endResult) {
		this.endResult = endResult;
	}

	public Arena getArena() {
		return arena;
    }

	public void setArena(Arena arena) {
		if (this.arena != null) {
			try {
				throw new Exception("Arena already set " + this.arena + " " + arena);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.arena = arena;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public String getBukkitVersion() {
		return bukkitVersion;
	}

	public int getOldLoserRating() {
		return oldLoserRating;
	}

	public int getOldWinnerRating() {
		return oldWinnerRating;
	}

	public int getNewLoserRating() {
		return newLoserRating;
	}

	public int getNewWinnerRating() {
		return newWinnerRating;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

    public Set<Player> getParty1AlivePlayers() {
        return this.party1AlivePlayers;
    }

	public Set<Player> getParty2AlivePlayers() {
		return party2AlivePlayers;
	}

    public Group getGroup1() {
        return group1;
    }

    public Group getGroup2() {
        return group2;
    }

    public void setLadderType(Ladder.LadderType ladderType) {
        this.ladderType = ladderType;
    }

    public boolean isRanked() {
        return isRanked;
    }

	public Group getCopyOfGroup1() {
		return copyOfGroup1;
	}

	public Group getCopyOfGroup2() {
		return copyOfGroup2;
	}

	public Map<UUID, Integer> getKillCounts() {
		return killCounts;
	}

	public class MatchTieTask extends BukkitRunnable {

		private Match match;

		public MatchTieTask(Match match) {
			this.match = match;
		}

		@Override
		public void run() {
			this.match.declareWinner(null); // tie
			this.match.handleCommonEnd("time");

			this.match.handleStasis(this.match.getGroup1(), this.match.getGroup2());
		}

	}

}