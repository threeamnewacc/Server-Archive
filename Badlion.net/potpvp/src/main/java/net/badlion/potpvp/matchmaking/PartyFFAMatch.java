package net.badlion.potpvp.matchmaking;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.rulesets.CustomRuleSet;
import net.badlion.potpvp.rulesets.EventRuleSet;
import net.badlion.potpvp.rulesets.HorseRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.*;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class PartyFFAMatch extends Match {

	public PartyFFAMatch(Arena arena, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) {
		super(arena, false, kitRuleSet, null);

		this.armorContents = armorContents;
		this.inventoryContents = inventoryContents;

		this.setLadderType(Ladder.LadderType.Duel);
	}

	@Override
	public void startGame() {
		PlayerHelper.healAndPrepGroupForBattle(this.group1);

		// Show party members
		List<Player> players = this.group1.players();
		for (Player pl : players) {
			for (Player pl2 : players) {
				pl.showPlayer(pl2);
				pl2.showPlayer(pl);
			}
		}

		if (this.kitRuleSet instanceof CustomRuleSet) {
			// Event kit check
			if (this.kitRuleSet instanceof EventRuleSet) {
				for (Player player : this.group1.players()) {
					player.getInventory().setContents(this.inventoryContents);
					player.getInventory().setArmorContents(this.armorContents);
				}
			} else {
				Bukkit.getLogger().info("Loaded custom kit for party ffa match wtf?");
			}
		} else {
			KitHelper.loadKits(this.group1, this.kitRuleSet);
		}

		// Get spawn point numbers
		List<Integer> spawnNumbers = new ArrayList<>();
		for (int i = 1; i <= PartyHelper.MAX_PARTY_FFA_PLAYERS; i++) {
			spawnNumbers.add(i);
		}

		Collections.shuffle(spawnNumbers);

		final Map<Player, Horse> playerToHorse = new HashMap<>();
		for (final Player pl : players) {
			// Play sound at spawn - EXTRA COVERAGE
			pl.playSound(pl.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);

			// Horse ladder?
			if (this.kitRuleSet instanceof HorseRuleSet) {
				pl.setFallDistance(0);

				// Spawn the horse
				Location location = ArenaManager.getWarp(this.arena.getArenaName() + "-" + spawnNumbers.remove(0));
				Horse horse = HorseRuleSet.createHorse(pl, location, this.arena);
				playerToHorse.put(pl, horse);
			} else {
				pl.setFallDistance(0F);

				Location location = ArenaManager.getWarp(this.arena.getArenaName() + "-" + spawnNumbers.remove(0));
				pl.teleport(location);
			}

			// Play sound in arena
			pl.playSound(pl.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);

			this.kitRuleSet.sendMessages(pl);

			PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(pl));
		}

		if (this.kitRuleSet instanceof HorseRuleSet) {
			new BukkitRunnable() {
				public void run() {
					for (Map.Entry<Player, Horse> entry : playerToHorse.entrySet()) {
						if (entry.getKey().isOnline()) {
							entry.getValue().setPassenger(entry.getKey());
						}
					}
				}
			}.runTaskLater(PotPvP.getInstance(), 1L);
		}

		this.group1.sendMessage(ChatColor.BLUE + "Now in party FFA with " + this.group1 + " and kit " + this.kitRuleSet.getName());

		this.tieGameTask = new TieTask(this).runTaskLater(PotPvP.getInstance(), 20 * 60 * this.matchLengthTime);

		// Prevent people from glitching out of arenas
		new BukkitRunnable() {
			public void run() {
				if (PartyFFAMatch.this.isOver()) {
					this.cancel();
					return;
				}

				List<Player> playersToKill = new ArrayList<>();
				for (Player pl : PartyFFAMatch.this.group1.players()) {
					if (pl.getLocation().getY() < 10) {
						if (PartyFFAMatch.this.party1AlivePlayers.contains(pl)) {
							playersToKill.add(pl);
						} else {
							// TP this player to an alive player
							pl.teleport(PartyFFAMatch.this.party1AlivePlayers.iterator().next());
						}
					}
				}

				for (Player pl : playersToKill) {
					pl.setHealth(0);
				}
			}
		}.runTaskTimer(PotPvP.getInstance(), 5, 5);

		PotPvP.printLagDebug("Party FFA with kit " + this.kitRuleSet.getName() + " has started");
	}

	@Override
	public void handleDeath(Player player) {
		// Handle death stuff
		this.party1AlivePlayers.remove(player);
		for (Player pl : this.party1AlivePlayers) {
			pl.hidePlayer(player);
		}

		// Handle scoreboard stuff
		for (Player pl : this.group1.players()) {
			Team blueTeam = pl.getScoreboard().getTeam(ScoreboardUtil.BLUE_TEAM);
			Team redTeam = pl.getScoreboard().getTeam(ScoreboardUtil.RED_TEAM);
			blueTeam.removeEntry(player.getName());
			redTeam.addEntry(player.getName());
		}

		// Remove this player's skull item
		//PartyPlayerInventoriesInventory.handlePlayerDeath(player, this.group1);

		// Send death message
		if (player.getKiller() != null) {
			for (Player pl : this.group1.players()) {
				if (pl == player.getKiller()) {
					pl.sendMessage(ChatColor.GREEN + player.getName() + " killed by " + player.getKiller().getName() + PlayerHelper.getHeartsLeftString(ChatColor.GREEN, player.getKiller().getHealth()));
				} else {
					pl.sendMessage(ChatColor.RED + player.getName() + " killed by " + player.getKiller().getName() + PlayerHelper.getHeartsLeftString(ChatColor.RED, player.getKiller().getHealth()));
				}
			}
		} else {
			this.group1.sendMessage(ChatColor.RED + player.getName() + " killed themself");
		}

		// Clean up and allow for a new match
		if (this.party1AlivePlayers.size() == 1) {
			this.group1.sendMessage(ChatColor.GREEN + this.party1AlivePlayers.iterator().next().getName() + " has won the Party FFA!");

			this.endGame();
		}
	}

	@Override
	public Location handleRespawn(Player player) {
		if (this.party1AlivePlayers.size() != 1) {
			// Hide them temporarily
			player.setGameMode(GameMode.CREATIVE);
			player.getInventory().clear();
			player.spigot().setCollidesWithEntities(false);
			player.sendMessage(ChatColor.YELLOW + "Put into spectator mode until end of round.");

			// Hide from real spectators
			for (Group group : GroupStateMachine.spectatorState.elements()) {
				for (Player pl : group.players()) {
					pl.hidePlayer(player);
				}
			}

			PotPvP.getInstance().givePlayerPartyDeadStateItems(player);

			return player.getLocation();
		} else {
			// Let the lobby stuff handle it
			return PotPvP.getInstance().getDefaultRespawnLocation();
		}
	}

	@Override
	public boolean handleQuit(Player player, String reason) {
		// Send death message
		this.group1.sendMessage(ChatColor.RED + player.getName() + " left the match.");

		// Remove from alive players if they're in there
		this.party1AlivePlayers.remove(player);

		// Clean up and allow for a new match
		if (this.party1AlivePlayers.size() == 1) {
			this.group1.sendMessage(ChatColor.GREEN + this.party1AlivePlayers.iterator().next().getName() + " has won the Party FFA!");

			this.endGame();
		}

		return false;
	}

	private void endGame() {
		// Show everyone to everyone
		this.showToEveryone();

		// Transfer groups
		try {
			// Current state might not exist cuz it was cleaned up already if someone left
			State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(this.group1);
			if (currentState != null) {
				GroupStateMachine.transitionBackToDefaultState(currentState, this.group1);
			}
		} catch (IllegalStateTransitionException e) {
			PotPvP.getInstance().somethingBroke(this.group1.getLeader(), this.group1);
		}

		// Clean up cached inventories
		//PartyPlayerInventoriesInventory.cleanUpCachedInventories(this.group1);

		// Handle scoreboard stuff
		for (Player pl : this.group1.players()) {
			Team blueTeam = pl.getScoreboard().getTeam(ScoreboardUtil.BLUE_TEAM);
			Team redTeam = pl.getScoreboard().getTeam(ScoreboardUtil.RED_TEAM);

			for (String entry : redTeam.getEntries()) {
				redTeam.removeEntry(entry);
				blueTeam.addEntry(entry);
			}
		}

		// THIS NEEDS TO BE BEFORE WE TOGGLE THE ARENA
		this.isOver = true;

		// Make this arena available now
		this.arena.toggleBeingUsed();

		PotPvP.printLagDebug("Party FFA with kit " + this.kitRuleSet.getName() + " has ended");
	}

	@Override
	public List<Player> getPlayers() {
		return this.group1.players();
	}

	@Override
	public boolean contains(Player player) {
		return this.group1.contains(player);
	}

	public class TieTask extends BukkitRunnable {

		private PartyFFAMatch partyFFAMatch;

		public TieTask(PartyFFAMatch partyFFAMatch) {
			this.partyFFAMatch = partyFFAMatch;
		}

		@Override
		public void run() {
			if (this.partyFFAMatch.isOver()) return;

			this.partyFFAMatch.getGroup1().sendMessage(ChatColor.YELLOW + "Time limit reached. Tie match.");

			this.partyFFAMatch.endGame();
		}

	}

}
