package net.badlion.potpvp.matchmaking;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.helpers.SpectatorHelper;
import net.badlion.potpvp.inventories.duel.RedRoverChooseFighterInventory;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class RedRoverMatch extends Match {

	private Player lastFightWinner;

	private boolean partyRedRover = false;

	private ItemStack[] lastFightWinnerItems;
	private ItemStack[] lastFightWinnerArmor;

	private Map<Group, Player> groupNextFighterMap = new HashMap<>();
	private Map<Group, BukkitTask> randomFighterTasks = new HashMap<>();

	private Set<Player> selectingFighters = new HashSet<>();

	public RedRoverMatch(Arena arena, KitRuleSet kitRuleSet) {
		super(arena, false, kitRuleSet);

		this.setLadderType(Ladder.LadderType.Duel);
	}

	public RedRoverMatch(Arena arena, KitRuleSet kitRuleSet, Map<Player, Integer> customKitSelections) {
		super(arena, false, kitRuleSet, customKitSelections);

		this.setLadderType(Ladder.LadderType.Duel);
	}

	public RedRoverMatch(Arena arena, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) {
		super(arena, false, kitRuleSet);

		this.partyRedRover = true;

		this.armorContents = armorContents;
		this.inventoryContents = inventoryContents;

		this.setLadderType(Ladder.LadderType.Duel);
	}

	@Override
	public void startGame() {
		this.addScoreboards();

		for (Group group : this.getAllGroups()) {
			PlayerHelper.healAndPrepGroupForBattle(group);
			this.initializeGroup(group);

			// Start task to choose random fighters after 30s
			this.randomFighterTasks.put(group, new ChooseRandomFighterTask(this, group, this.getAlivePlayers(group)).runTaskLater(PotPvP.getInstance(), 30 * 20));

			group.sendMessage(ChatColor.BLUE + "Now in Red Rover match against " + this.getOtherGroup(group) + " with kit " + this.kitRuleSet.getName());
		}

		// Hide everyone from each other, but show leaders (give a few ticks to get out of spawn)
		RedRoverMatch.this.hideAllPlayersFromEachOther();
		RedRoverMatch.this.showPlayer(RedRoverMatch.this.group1.getLeader(), RedRoverMatch.this.group2.getLeader());

		// Set match length time to 5 minutes per player
		this.matchLengthTime = 5 * (this.group1.players().size() + this.group2.players().size());
		this.tieGameTask = new TieTask(this).runTaskLater(PotPvP.getInstance(), 20 * 60 * this.matchLengthTime);

		// Prevent people from glitching out of arenas
		new BukkitRunnable() {
			public void run() {
				if (RedRoverMatch.this.isOver()) {
					this.cancel();
					return;
				}

				if (RedRoverMatch.this.group1.isParty()) {
					List<Player> playersToKill = new ArrayList<>();
					for (Player pl : RedRoverMatch.this.group1.players()) {
						if (pl.getLocation().getY() < 10) {
							if (RedRoverMatch.this.party1AlivePlayers.contains(pl)) {
								playersToKill.add(pl);
							} else {
								// TP this player to an alive player
								pl.teleport(RedRoverMatch.this.party1AlivePlayers.iterator().next());
							}
						}
					}

					for (Player pl : RedRoverMatch.this.group2.players()) {
						if (pl.getLocation().getY() < 10) {
							if (RedRoverMatch.this.party2AlivePlayers.contains(pl)) {
								playersToKill.add(pl);
							} else {
								// TP this player to an alive player
								pl.teleport(RedRoverMatch.this.party2AlivePlayers.iterator().next());
							}
						}
					}

					for (Player pl : playersToKill) {
						pl.setHealth(0);
					}
				} else {
					if (RedRoverMatch.this.group1.getLeader().getLocation().getY() < 10) {
						RedRoverMatch.this.group1.getLeader().setHealth(0);
					}

					if (RedRoverMatch.this.group2.getLeader().getLocation().getY() < 10) {
						RedRoverMatch.this.group2.getLeader().setHealth(0);
					}
				}
			}
		}.runTaskTimer(PotPvP.getInstance(), 5, 5);
	}

	private void initializeGroup(Group group) {
		for (final Player pl : group.players()) {
			// Play sound at spawn - EXTRA COVERAGE
			pl.playSound(pl.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);

			Gberry.safeTeleport(pl, group == this.group1 ? this.arena.getWarp1() : this.arena.getWarp2());

			// Put every player in spectator mode
			SpectatorHelper.setGameModeCreative(pl);

			// Give the party leader the player selection item
			if (pl == group.getLeader()) {
				this.selectingFighters.add(pl);
				pl.getInventory().addItem(RedRoverChooseFighterInventory.getChooseFirstFighterItem());
				pl.sendMessage(ChatColor.YELLOW + "Right click to select your first fighter!");
			} else {
				// We want to give them the item to let them leave the party
				pl.sendMessage(ChatColor.YELLOW + "You are in spectator mode, wait for your party leader to pick a fighter!");
			}

			pl.updateInventory();

			this.kitRuleSet.sendMessages(pl);

			// Play sound in arena
			pl.playSound(pl.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);

			PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(pl));
		}
	}

	private void startNextFight() {
		Player group1NextFighter = this.groupNextFighterMap.get(this.group1);
		Player group2NextFighter = this.groupNextFighterMap.get(this.group2);

		// Heal both fighters
		PlayerHelper.healAndPrepPlayerForBattle(group1NextFighter);
		PlayerHelper.healAndPrepPlayerForBattle(group2NextFighter);

		// Reload their kits
		/*if (group1NextFighter == this.lastFightWinner) {
			group1NextFighter.getInventory().setArmorContents(this.lastFightWinnerArmor);
			group1NextFighter.getInventory().setContents(this.lastFightWinnerItems);
		} else {*/
		KitHelper.loadKit(group1NextFighter, this.kitRuleSet);
		//}

		/*if (group2NextFighter == this.lastFightWinner) {
			group2NextFighter.getInventory().setArmorContents(this.lastFightWinnerArmor);
			group2NextFighter.getInventory().setContents(this.lastFightWinnerItems);
		} else {*/
		KitHelper.loadKit(group2NextFighter, this.kitRuleSet);
		//}

		// Horse stuff and teleporting
		group1NextFighter.playSound(group1NextFighter.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);
		group2NextFighter.playSound(group2NextFighter.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);
		this.kitRuleSet.teleport(group1NextFighter, this.arena.getWarp1(), this.arena);
		this.kitRuleSet.teleport(group2NextFighter, this.arena.getWarp2(), this.arena);

		// Show fighters to everyone and hide old fighter from everyone
		this.hideAllPlayersFromEachOther();
		this.showPlayer(group1NextFighter);
		this.showPlayer(group2NextFighter);

		this.sendMessage(ChatColor.GREEN + group1NextFighter.getName() + " VS " + group2NextFighter.getName() + " - " + ChatColor.BOLD + "FIGHT!");
	}

	@Override
	public void handleDeath(Player player) {
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		Group enemyGroup = this.getOtherGroup(group);
		Set<Player> alivePlayers = this.getAlivePlayers(group);

		// Handle death
		alivePlayers.remove(player);

		// Scoreboard stuff
		enemyGroup.getLeader().getScoreboard().getTeam(ScoreboardUtil.RED_TEAM).removeEntry(player.getName());

		for (Player pl : group.players()) {
			Team blueTeam = pl.getScoreboard().getTeam(ScoreboardUtil.BLUE_TEAM);
			Team redTeam = pl.getScoreboard().getTeam(ScoreboardUtil.RED_TEAM);
			blueTeam.removeEntry(player.getName());
			redTeam.addEntry(player.getName());
		}

		// Remove this player's skull item and setup for next fight
		RedRoverChooseFighterInventory.handlePlayerDeath(player, enemyGroup);
		this.lastFightWinner = this.groupNextFighterMap.get(enemyGroup);
		this.groupNextFighterMap.remove(group);

		// Save their items/armor
		this.lastFightWinnerItems = this.lastFightWinner.getInventory().getContents();
		this.lastFightWinnerArmor = this.lastFightWinner.getInventory().getArmorContents();

		// Send death message
		if (player.getKiller() != null) {
			this.sendRedGreenMessage(group, enemyGroup, player.getName() + " killed by " + player.getKiller().getName(), player.getKiller().getHealth());
		} else {
			this.sendRedGreenMessage(group, enemyGroup, player.getName() + " killed themself", -1);
		}

		// End of match?
		this.handlePossibleEnd(enemyGroup);
	}

	@Override
	public Location handleRespawn(Player player) {
		if (this.party1AlivePlayers.size() != 0 && this.party2AlivePlayers.size() != 0) {
			// Hide them temporarily
			SpectatorHelper.setGameModeCreative(player);
			player.sendMessage(ChatColor.YELLOW + "Put into spectator mode until end of round.");

			// Hide from real spectators
			for (Group group : GroupStateMachine.spectatorState.elements()) {
				for (Player pl : group.players()) {
					pl.hidePlayer(player);
				}
			}

			this.hidePlayer(player);
			//PotPvP.getInstance().givePlayerPartyDeadStateItems(player);

			return player.getLocation();
		} else {
			// Let the lobby stuff handle it
			return PotPvP.getInstance().getDefaultRespawnLocation();
		}
	}

	@Override
	public boolean handleQuit(Player player, String reason) {
		final Group group = PotPvP.getInstance().getPlayerGroup(player);
		Group enemyGroup = this.getOtherGroup(group);

		// Handle death stuff
		this.getAlivePlayers(group).remove(player);
		this.selectingFighters.remove(player);

		// Remove this player's skull item
		RedRoverChooseFighterInventory.handlePlayerDeath(player, enemyGroup);

		// Set last fight winner
		Player groupNextFighter = this.groupNextFighterMap.get(group);
		Player otherGroupNextFighter = this.groupNextFighterMap.get(enemyGroup);
		Set<Player> partyAlivePlayers = this.getAlivePlayers(group);
		if (player == groupNextFighter) {
			// They were the last winner, had no opponent yet, find a substitute or they were chosen as a fighter, but other fighter hasn't been chosen yet, find a substitute
			if (player == this.lastFightWinner || otherGroupNextFighter == null) {
				// Make sure they weren't the last person alive in their party
				if (!partyAlivePlayers.isEmpty()) {
					List<Player> alivePlayers = new ArrayList<>(partyAlivePlayers);
					Collections.shuffle(alivePlayers);

					this.groupNextFighterMap.put(group, alivePlayers.get(0));
					if (player == this.lastFightWinner) {
						this.lastFightWinner = this.groupNextFighterMap.get(group);

						// Save their items/armor
						this.lastFightWinnerItems = this.lastFightWinner.getInventory().getContents();
						this.lastFightWinnerArmor = this.lastFightWinner.getInventory().getArmorContents();
					}

					this.sendMessage(ChatColor.RED + player.getName() + " left the match. A player was randomly chosen to take his place.");
				} else {
					// All players in party are dead, game will end when we call handlePossibleEnd()
					this.sendMessage(ChatColor.RED + player.getName() + " left the match.");
				}
			} else {
				// They were currently in a fight - automatic loss
				// Set last fight winner and clear cache
				this.lastFightWinner = otherGroupNextFighter;

				// Save their items/armor
				this.lastFightWinnerItems = this.lastFightWinner.getInventory().getContents();
				this.lastFightWinnerArmor = this.lastFightWinner.getInventory().getArmorContents();

				this.groupNextFighterMap.remove(group);

				this.sendMessage(ChatColor.RED + player.getName() + " left the match and forfeited the fight.");
			}
		} else if (player == group.getLeader()) {
			// Were they the leader picking a player?
			if (this.lastFightWinner == null) {
				// Check if a fighter was already selected for the group
				if (this.groupNextFighterMap.get(group) == null) {
					// Tell the new party leader to pick the first fighter then
					BukkitUtil.runTaskNextTick(new Runnable() {
						@Override
						public void run() {
							RedRoverMatch.this.selectingFighters.add(group.getLeader());
							group.getLeader().getInventory().addItem(RedRoverChooseFighterInventory.getChooseFirstFighterItem());
							group.getLeader().sendMessage(ChatColor.YELLOW + "Right click to select your first fighter!");
						}
					});
				}
			} else {
				// Return early to prevent random people from triggering tasks and such
				return false;
			}
		} else {
			// Return early to prevent random people from triggering tasks and such
			return false;
		}

		// End of match?
		this.handlePossibleEnd(enemyGroup);

		return false;
	}

	private void handlePossibleEnd(Group killerGroup) {
		if (this.party1AlivePlayers.size() == 0) {
			this.sendRedGreenMessage(this.group1, this.group2, this.group2.toString() + " won the Red Rover match!");

			this.endGame();
		} else if (this.party2AlivePlayers.size() == 0) {
			this.sendRedGreenMessage(this.group2, this.group1, this.group1.toString() + " won the Red Rover match!");

			this.endGame();
		} else if (this.lastFightWinner != null) { // lastFightWinner is null in some conditions in handleQuit()
			// Clear winner's inventory and give them the choose fighter inventory item
			this.lastFightWinner.getInventory().clear();
			this.lastFightWinner.getInventory().setArmorContents(new ItemStack[4]);
			this.lastFightWinner.getInventory().addItem(RedRoverChooseFighterInventory.getChooseEnemyFighterItem());
			this.selectingFighters.add(this.lastFightWinner);

			// Create a task for a timeout
			Group otherGroup = this.getOtherGroup(killerGroup);
			this.randomFighterTasks.put(otherGroup, new ChooseRandomFighterTask(this, otherGroup, this.getAlivePlayers(otherGroup)).runTaskLater(PotPvP.getInstance(), 30 * 20));
			this.lastFightWinner.sendMessage(ChatColor.YELLOW + "Right click to select your next opponent!");
		}
	}

	private void endGame() {
		// Show everyone to everyone
		this.showToEveryone();

		for (Group group : this.getAllGroups()) {
			if (!this.partyRedRover) {
				try {
					// Current state might not exist cuz it was cleaned up already if someone left
					State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
					if (currentState != null) {
						GroupStateMachine.transitionBackToDefaultState(currentState, group);
					}
				} catch (IllegalStateTransitionException e) {
					PotPvP.getInstance().somethingBroke(group.getLeader(), group);
				}
			}

			// Scoreboard stuff
			for (Player pl : group.players()) {
				Team blueTeam = pl.getScoreboard().getTeam(ScoreboardUtil.BLUE_TEAM);
				Team redTeam = pl.getScoreboard().getTeam(ScoreboardUtil.RED_TEAM);

				for (String str : redTeam.getEntries()) {
					redTeam.removeEntry(str);
				}

				for (Player pl2 : group.players()) {
					blueTeam.addEntry(pl2.getName());
				}
			}
		}

		// Clean up cached inventories
		RedRoverChooseFighterInventory.cleanUpCachedInventories(this.group1, this.group2);

		// Move everyone back into original party if this is a single party red rover
		if (this.partyRedRover) {
			try {
				// Current state might not exist cuz it was cleaned up already if someone left
				State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(this.group1);
				if (currentState != null) {
					GroupStateMachine.transitionBackToDefaultState(currentState, this.group1);
				}
			} catch (IllegalStateTransitionException e) {
				PotPvP.getInstance().somethingBroke(this.group1.getLeader(), PotPvP.getInstance().getPlayerGroup(this.group1.getLeader()));
			}

			// Move players in second party to first party
			for (Player pl : this.group2.players()) {
				if (pl != this.group2.getLeader()) {
					// Once again create a temp intermediate group
					Group group = new Group(pl, true);

					// Call this after because it calls party.removePlayer()
					PotPvP.getInstance().updatePlayerGroup(pl, group);

					PartyHelper.handleLeave(pl, this.group2.getParty(), false);

					PartyHelper.addToPartyGroup(pl, this.group1.getParty(), false);
				}
			}

			// Once again create a temp intermediate group
			Group group = new Group(this.group2.getLeader(), true);

			// Call this after because it calls party.removePlayer()
			PotPvP.getInstance().updatePlayerGroup(this.group2.getLeader(), group);

			PartyHelper.handleLeave(this.group2.getLeader(), this.group2.getParty(), false);

			PartyHelper.addToPartyGroup(this.group2.getLeader(), this.group1.getParty(), false);
		}

		// THIS NEEDS TO BE BEFORE WE TOGGLE THE ARENA
		this.isOver = true;

		// Make this arena available now
		this.arena.toggleBeingUsed();
	}

	public Player getLastFightWinner() {
		return lastFightWinner;
	}

	public void setGroupNextFighter(Group group, Player selecter, Player groupNextFighter) {
		this.groupNextFighterMap.put(group, groupNextFighter);

		if (selecter != null) {
			// Don't let them change their selection
			selecter.getInventory().setItem(0, null);

			this.sendMessage(ChatColor.YELLOW + selecter.getName() + " has selected their fighter!");

			this.randomFighterTasks.remove(group).cancel();
		} else {
			if (this.lastFightWinner != null) {
				// Don't let them change their selection
				this.lastFightWinner.getInventory().setItem(0, null);

				this.sendMessage(ChatColor.YELLOW + this.lastFightWinner.getName() + " has failed to select a fighter, a random fighter has been selected!");
			} else {
				// Don't let them change their selection
				group.getLeader().getInventory().setItem(0, null);

				this.sendMessage(ChatColor.YELLOW + group.getLeader().getName() + " has failed to select a fighter, a random fighter has been selected!");
			}
		}

		Group otherGroup = this.getOtherGroup(group);

		// Are we ready to start the fight?
		if (this.groupNextFighterMap.get(otherGroup) != null) {
			this.resetArena();
		}
	}

	public boolean isSelectingFighter(Player player) {
		return selectingFighters.contains(player);
	}

	private void resetArena() {
		new ResetRedRoverArenaTask(this.arena).runTaskTimer(PotPvP.getInstance(), 20, 1L);
		this.arena.rebuild(); // Used for SkyWars
	}

	@Override
	public void handleStasis(Group... groups) {
		if (!this.partyRedRover) {
			super.handleStasis(groups);
		} else {
			// Do nothing because right after the match we get rid of the two party groups right after the match ends
		}
	}

	public class ResetRedRoverArenaTask extends Arena.CleanArenaTask {

		public ResetRedRoverArenaTask(Arena arena) {
			super(arena);
		}

		public void markAsDone() {
			// Remove players from set here because we don't want them to pickup items still
			RedRoverMatch.this.selectingFighters.clear();

			RedRoverMatch.this.arena.doneCleaning();

			// Start the next fight
			RedRoverMatch.this.startNextFight();
		}

	}

	public class ChooseRandomFighterTask extends BukkitRunnable {

		private RedRoverMatch match;

		private Group group;
		private Set<Player> partyAlivePlayers;

		public ChooseRandomFighterTask(RedRoverMatch match, Group group, Set<Player> partyAlivePlayers) {
			this.match = match;

			this.group = group;
			this.partyAlivePlayers = partyAlivePlayers;
		}

		@Override
		public void run() {
			if (this.match.isOver()) return;

			List<Player> alivePlayers = new ArrayList<>(this.partyAlivePlayers);
			Collections.shuffle(alivePlayers);

			// Not started a match yet
			if (this.match.getLastFightWinner() != null) {
				this.match.setGroupNextFighter(this.group, null, alivePlayers.get(0));
			} else {
				this.match.setGroupNextFighter(this.group, null, alivePlayers.get(0));
			}

			RedRoverMatch.this.randomFighterTasks.remove(this.group);
		}

	}

	public class TieTask extends BukkitRunnable {

		private RedRoverMatch match;

		public TieTask(RedRoverMatch match) {
			this.match = match;
		}

		@Override
		public void run() {
			if (this.match.isOver()) return;

			this.match.sendMessage(ChatColor.YELLOW + "Time limit reached. Tie match.");
			this.match.endGame();
		}

	}

}
