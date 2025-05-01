package net.badlion.arenapvp.matchmaking;

import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.CustomRuleSet;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.PotPvPPlayer;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.arenas.Arena;
import net.badlion.arenapvp.helper.KitSelectorHelper;
import net.badlion.arenapvp.helper.PlayerHelper;
import net.badlion.arenapvp.helper.SpectatorHelper;
import net.badlion.arenapvp.inventory.RedRoverChoseCaptainInventory;
import net.badlion.arenapvp.inventory.RedRoverChosePlayersInventory;
import net.badlion.arenapvp.manager.ArenaSettingsManager;
import net.badlion.arenapvp.manager.EnderPearlManager;
import net.badlion.arenapvp.manager.MatchManager;
import net.badlion.arenapvp.manager.PotPvPPlayerManager;
import net.badlion.arenapvp.manager.SidebarManager;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.arenapvp.state.RedRoverWaitingState;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gcheat.bukkitevents.GCheatGameEndEvent;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class RedRoverBattle extends Match {

	private UUID leaderId;

	private Player captain1;
	private Player captain2;

	private Team team1;
	private Team team2;

	private UUID fighter1UUID;
	private UUID fighter2UUID;

	private Player fighter1;
	private Player fighter2;

	private boolean fighter1CombatLogged;
	private boolean fighter2CombatLogged;

	private List<UUID> allPlayers = new ArrayList<>();
	private List<UUID> checkedInPlayers = new ArrayList<>();

	private boolean captain1Picking = true;

	private Map<Integer, UUID> inventoryPlayers = new HashMap<>();

	private Set<UUID> playersSentTeamPackets = new HashSet<>();

	private boolean preMadeTeams = false;

	private boolean firstPick = true;

	private BukkitTask autoPickPlayerCaptain1Task;
	private BukkitTask autoPickPlayerCaptain2Task;

	private BukkitTask pickFighersTask;
	private BukkitTask autoPickTeamsTask;
	private BukkitTask autoPickCaptainTask;
	private BukkitTask startRoundTask;


	public RedRoverBattle(Arena arena, boolean isRanked, KitRuleSet kitRuleSet, UUID matchUuid, UUID leaderId, List<UUID> allPlayers) {
		super(arena, isRanked, kitRuleSet, matchUuid);

		this.leaderId = leaderId;
		this.allPlayers = allPlayers;

		this.team1 = new Team("team_1");
		this.team2 = new Team("team_2");
	}

	public RedRoverBattle(Arena arena, boolean isRanked, KitRuleSet kitRuleSet, UUID matchUuid, List<UUID> team1Players, List<UUID> team2Players, boolean preMadeTeams) {
		super(arena, isRanked, kitRuleSet, matchUuid);

		this.allPlayers = new ArrayList<>();
		this.allPlayers.addAll(team1Players);
		this.allPlayers.addAll(team2Players);

		this.team1 = new Team(team1Players, "team_1");
		this.team2 = new Team(team2Players, "team_2");
	}

	@Override
	public void checkIn(Player player) {
		if (preMadeTeams) {
			if (team1.contains(player) && !team1.isCheckedIn(player)) {
				team1.checkIn(player);
			}
			if (team2.contains(player) && !team2.isCheckedIn(player)) {
				team2.checkIn(player);
			}
			if (team1.isTeamCheckedIn() && team2.isTeamCheckedIn()) {
				startGameDelay();
				stopTieTask();
			}
		} else {
			checkedInPlayers.add(player.getUniqueId());
			for (UUID uuid : allPlayers) {
				if (!checkedInPlayers.contains(uuid)) {
					return;
				}
			}
			//Prep and start game
			choseCaptains();
			stopTieTask();
		}
	}

	public void cleanupTasks() {
		if (this.waitingForPlayersTask != null) {
			this.waitingForPlayersTask.cancel();
		}
		if (this.autoPickCaptainTask != null) {
			this.autoPickCaptainTask.cancel();
		}
		if (this.autoPickTeamsTask != null) {
			this.autoPickTeamsTask.cancel();
		}
	}

	public void stopTieTask() {
		if (this.waitingForPlayersTask != null) {
			this.waitingForPlayersTask.cancel();
		}
	}

	public void stopAutoPickCaptainsTask() {
		if (this.autoPickCaptainTask != null) {
			this.autoPickCaptainTask.cancel();
		}
	}

	// Make the leader pick captains, if the leader is not online just end the match
	public void choseCaptains() {
		MatchManager.getActiveMatches().put(this.team1, this);
		MatchManager.getActiveMatches().put(this.team2, this);

		for (UUID playerId : this.allPlayers) {
			MatchManager.getMatchesAwaitingPlayers().remove(playerId);
		}

		Player leader = Bukkit.getPlayer(leaderId);
		if (leader != null) {
			this.setCaptain1(leader);
			this.broadcastMessage(ChatColor.GOLD + "Your team leader is now picking the second captain for the Red Rover.");
			this.broadcastMessage(ChatColor.GOLD + "They have 30 seconds to pick the captain.");

			// Start a task to auto pick the captain if the leader doesnt pick one.
			this.autoPickCaptainTask = new BukkitRunnable() {
				@Override
				public void run() {
					if (RedRoverBattle.this.captain2 == null) {
						for (UUID playerId : RedRoverBattle.this.allPlayers) {
							Player player = Bukkit.getPlayer(playerId);
							if (player != null && player != RedRoverBattle.this.captain1) {
								RedRoverBattle.this.setCaptain2(player);
								break;
							}
						}

						// Just for some extra safety incase somehow it cant pick a second captain idk
						if (RedRoverBattle.this.captain2 == null) {
							RedRoverBattle.this.broadcastMessage(ChatColor.RED + "Could not pick another captain for your red rover, ending the match.");
							RedRoverBattle.this.handleTie();
						}
					}
				}
			}.runTaskLater(ArenaPvP.getInstance(), 20 * 30);
		} else {
			// extra safety check incase somehow they logout within a small amount of time
			this.broadcastMessage(ChatColor.GOLD + "Your team leader is not online, ending the match.");
			this.handleTie();
		}
	}


	// Captain 1 should always be the leader
	public void setCaptain1(Player leader) {
		this.captain1 = leader;
		team1.getMembers().put(leader.getUniqueId(), true);
		ArenaPvP.getInstance().setPlayerTeam(leader, team1);
		RedRoverChoseCaptainInventory.openSelectSecondCaptainInventory(leader, this);
	}

	public void setCaptain2(Player player) {
		if (this.autoPickCaptainTask != null) {
			this.autoPickCaptainTask.cancel();
		}
		this.captain2 = player;
		this.team2.getMembers().put(player.getUniqueId(), true);
		ArenaPvP.getInstance().setPlayerTeam(player, team2);

		// Return and tie game if null captains
		if (this.checkForNullCaptains()) {
			this.broadcastMessage(ChatColor.GOLD + "One of your team captains is offline, ending the match.");
			this.handleTie();
			return;
		}

		this.broadcastMessage(ChatColor.GREEN + "The two captains are " + ChatColor.YELLOW + captain1.getDisguisedName() + ChatColor.GREEN + "  and " + ChatColor.YELLOW + captain2.getDisguisedName());
		this.broadcastMessage(ChatColor.YELLOW + captain1.getDisguisedName() + ChatColor.GREEN + " will pick a fighter first.");
		this.broadcastMessage(ChatColor.YELLOW + captain1.getDisguisedName() + ChatColor.GREEN + " has 10 seconds to pick a fighter.");

		this.captain1Picking = true;
		RedRoverChosePlayersInventory.openSelectPlayersInventory(this.captain1, this);
		this.startAutoPickPlayerCaptain1Task();
	}


	// Checks for a captain offline, only should be called once the second captain is set
	public boolean checkForNullCaptains() {
		return this.captain1 == null || !this.captain1.isOnline() || this.captain2 == null || !this.captain2.isOnline();
	}


	@Override
	public void startGameDelay() {
		if (this.autoPickTeamsTask != null) {
			this.autoPickTeamsTask.cancel();
		}
		this.stopAutoPickCaptainsTask();

		for (UUID playerId : allPlayers) {
			Player player = Bukkit.getPlayer(playerId);
			if (player != null) {
				BukkitUtil.closeInventory(player);
			}
			MatchManager.getMatchesAwaitingPlayers().remove(playerId);
		}


		startGame();
	}

	@Override
	public void startGame() {
		setStarted(true);
		for (Player player : team1.members()) {
			SidebarManager.addSidebarItems(player, this);

			List<Object> teamPackets = new ArrayList<>();
			teamPackets.add(team1.getCreatePacket(ChatColor.GREEN, false));
			teamPackets.add(team2.getCreatePacket(ChatColor.RED, false));
			for (Object packet : teamPackets) {
				Gberry.protocol.sendPacket(player, packet);
				Bukkit.getLogger().info("REDROVER1: Sending team create packet to: " + player.getName());
				this.playersSentTeamPackets.add(player.getUniqueId());
			}

			Gberry.safeTeleport(player, arena.getWarp1Origin());
		}
		for (Player player : team2.members()) {
			SidebarManager.addSidebarItems(player, this);

			List<Object> teamPackets = new ArrayList<>();
			teamPackets.add(team1.getCreatePacket(ChatColor.RED, false));
			teamPackets.add(team2.getCreatePacket(ChatColor.GREEN, false));
			for (Object packet : teamPackets) {
				Gberry.protocol.sendPacket(player, packet);
				Bukkit.getLogger().info("REDROVER2: Sending team create packet to: " + player.getName());
				this.playersSentTeamPackets.add(player.getUniqueId());
			}

			Gberry.safeTeleport(player, arena.getWarp2Origin());
		}
		this.broadcastMessage(ChatColor.GOLD + "RedRover: " + team1.toActiveString() + " - " + team2.toActiveString());
		this.broadcastMessage(ChatColor.GOLD + "Your captains have 10 seconds to chose their fighters, if they fail to do so they will be chosen at random.");
	}


	public void tryStartNextRound() {
		if (pickFighersTask != null) {
			pickFighersTask.cancel();
		}

		if (this.checkForNullCaptains()) {
			this.broadcastMessage(ChatColor.GOLD + "One of your team captains is offline, ending the match.");
			this.handleTie();
			return;
		}

		if (this.fighter1 == null) {
			if (this.amountOfPlayersNotPlayed() == 0) {
				this.setFighter1(captain1);
				return;
			}

			this.captain1.sendFormattedMessage("{0}Select your next fighter.", ChatColor.GOLD);

			this.broadcastMessage(ChatColor.YELLOW + captain1.getDisguisedName() + ChatColor.GREEN + " is picking a fighter for team 1.");
			this.broadcastMessage(ChatColor.YELLOW + captain1.getDisguisedName() + ChatColor.GREEN + " has 10 seconds to pick a fighter.");

			this.captain1Picking = true;
			RedRoverChosePlayersInventory.openSelectPlayersInventory(this.captain1, this);
			this.startAutoPickPlayerCaptain1Task();
			return;
		} else {
			this.captain1.sendFormattedMessage("{0}Your fighter is still alive, you can not chose a player this round.", ChatColor.GOLD);
		}
		if (this.fighter2 == null) {
			if (this.amountOfPlayersNotPlayed() == 0) {
				this.setFighter2(captain2);
				return;
			}

			this.captain2.sendFormattedMessage("{0}Select your next fighter.", ChatColor.GOLD);

			this.broadcastMessage(ChatColor.YELLOW + captain2.getDisguisedName() + ChatColor.GREEN + " is picking a fighter for team 2.");
			this.broadcastMessage(ChatColor.YELLOW + captain2.getDisguisedName() + ChatColor.GREEN + " has 10 seconds to pick a fighter.");

			this.captain1Picking = false;
			RedRoverChosePlayersInventory.openSelectPlayersInventory(this.captain2, this);
			this.startAutoPickPlayerCaptain2Task();
			return;
		} else {
			this.captain2.sendFormattedMessage(ChatColor.GOLD + "Your fighter is still alive, you can not chose a player this round.");
		}

		this.startBattle();
	}

	public void spawnInPlayerToWarp(Player player) {
		// Check that they are a fighter in the match
		if (this.fighter1 != null && player != this.fighter1) {
			if (this.fighter2 != null && player != this.fighter2) {
				return;
			}
		}

		EnderPearlManager.remove(player);

		Gberry.safeTeleport(player, this.getWarpForPlayer(player));
		State<Player> currentStateFighter1 = TeamStateMachine.getInstance().getCurrentState(player);
		if (currentStateFighter1 instanceof MatchState) {
			PlayerHelper.healPlayer(player);
			// They are already in the match state, they probably didnt die last round.
		} else if (currentStateFighter1 instanceof RedRoverWaitingState) {
			PlayerHelper.healAndPrepPlayerForBattle(player);
			try {
				currentStateFighter1.pop(player);
			} catch (IllegalStateTransitionException e) {
				e.printStackTrace();
			}
			KitSelectorHelper.giveSelectorItems(player, this.kitRuleSet);
			PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());
			potPvPPlayer.setSelectingKit(true);
			player.updateInventory();
		}
	}

	public void startBattle() {
		this.broadcastMessage(ChatColor.BLUE + "The fighters have been chosen!");
		this.broadcastMessage(ChatColor.GREEN + fighter1.getDisguisedName() + ChatColor.BLUE + " vs. " + ChatColor.GREEN + fighter2.getDisguisedName());

		RedRoverBattle.this.spawnInPlayerToWarp(fighter1);
		RedRoverBattle.this.spawnInPlayerToWarp(fighter2);

		this.startRoundTask = new BukkitRunnable() {

			int timeLeft = 5;

			@Override
			public void run() {
				if (timeLeft == 0) {

					if (fighter1 != null && fighter1.isOnline()) {
						RedRoverBattle.this.autoPickKit(fighter1);
					}
					if (fighter2 != null && fighter2.isOnline()) {
						RedRoverBattle.this.autoPickKit(fighter2);
					}

					this.cancel();
					return;
				}
				for (UUID playerId : RedRoverBattle.this.allPlayers) {
					Player member = Bukkit.getPlayer(playerId);
					if (member != null) {
						try {
							if (ArenaSettingsManager.getSettings(member).showsTitles()) {
								member.sendTitle(new ComponentBuilder("Starting in...").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create());
								member.sendSubTitle(new ComponentBuilder(timeLeft + "").color(net.md_5.bungee.api.ChatColor.GOLD).bold(true).create());
								member.setTitleTimes(0, 20, 0);
							}
						} catch (Exception ex) {
							// Just incase spigot is missing the api for whatever reason, we dont want to break the whole match
							Bukkit.getLogger().log(Level.WARNING, "Match Countdown could not send title. " + ex.getMessage());
						}

						if (member == RedRoverBattle.this.fighter1) {
							Location warp = RedRoverBattle.this.getWarpForTeam(RedRoverBattle.this.team1);
							if (member.getLocation().distance(warp) > 5) {
								member.teleport(new Location(warp.getWorld(), warp.getX(), warp.getY(), warp.getZ(), member.getLocation().getYaw(), member.getLocation().getPitch()));
							}
						}
						if (member == RedRoverBattle.this.fighter2) {
							Location warp = RedRoverBattle.this.getWarpForTeam(RedRoverBattle.this.team2);
							if (member.getLocation().distance(warp) > 5) {
								member.teleport(new Location(warp.getWorld(), warp.getX(), warp.getY(), warp.getZ(), member.getLocation().getYaw(), member.getLocation().getPitch()));
							}
						}
					}
				}
				broadcastMessage(ChatColor.GOLD + "Match starting in " + ChatColor.GREEN + timeLeft + ChatColor.GOLD + " seconds.");
				broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1.0F, 1.0F);
				timeLeft--;
			}
		}.runTaskTimer(ArenaPvP.getInstance(), 10, 20);
	}

	public void autoPickKit(Player player) {
		PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());

		// If they have not picked a kit yet pick it for them when the match starts
		if (potPvPPlayer.isSelectingKit()) {
			KitType kitType = new KitType(player.getUniqueId().toString(), this.getKitRuleSet().getName());
			Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(player.getUniqueId());

			if (kitTypeListMap != null) {
				List<Kit> kits = kitTypeListMap.get(kitType);
				if (kits != null) {
					// Load the first custom kit we can find for the player, if they don't have a custom kit load the default kit
					for (Kit kit : kits) {
						KitCommon.loadKit(player, this.getKitRuleSet(), kit.getId());
						break;
					}
				} else {
					if (this.getKitRuleSet() instanceof CustomRuleSet) {
						player.sendFormattedMessage("{0}You failed to pick a custom kit, and you have not saved any custom kits yet. Here is a cookie.", ChatColor.RED);
						player.getInventory().clear();
						player.getInventory().addItem(new ItemStack(Material.COOKIE));
					} else {
						KitCommon.loadDefaultKit(player, this.getKitRuleSet(), true);
					}
				}
			} else {
				if (this.getKitRuleSet() instanceof CustomRuleSet) {
					player.sendFormattedMessage("{0}You failed to pick a custom kit, and you have not saved any custom kits yet. Here is a cookie.", ChatColor.RED);
					player.getInventory().clear();
					player.getInventory().addItem(new ItemStack(Material.COOKIE));
				} else {
					KitCommon.loadDefaultKit(player, this.getKitRuleSet(), true);
				}
			}
			potPvPPlayer.setSelectingKit(false);
		}
	}

	@Override
	public void handleDeath(Player player) {
		State<Player> currentState = TeamStateMachine.getInstance().getCurrentState(player);
		try {
			currentState.push(TeamStateMachine.redRoverWaitingState, player);
		} catch (IllegalStateTransitionException e) {
			e.printStackTrace();
		}

		UUID attackerUUID = this.getLastDamage(player.getUniqueId());

		// They might have killed themselves
		if (attackerUUID == null) {
			if (this.team1.contains(player)) {
				attackerUUID = this.team2.members().get(0).getUniqueId();
			} else {
				attackerUUID = this.team1.members().get(0).getUniqueId();
			}
		}

		Player killer = ArenaPvP.getInstance().getServer().getPlayer(attackerUUID);
		Team group = ArenaPvP.getInstance().getPlayerTeam(player);
		group.handlePlayerDeath(player.getUniqueId());

		this.broadcastMessage(ChatColor.GOLD + "RedRover: " + this.team1.toActiveString() + " - " + this.team2.toActiveString());

		this.handleCommon(player);

		Team otherGroup;
		if (this.team1.contains(player)) {
			otherGroup = this.team2;
		} else {
			otherGroup = this.team1;
		}
		if (this.fighter1 != null && this.fighter1.getUniqueId().equals(player.getUniqueId())) {
			this.fighter1 = null;
			this.fighter1UUID = null;
			Gberry.log("REDROVER", "Player death " + player.getName() + " is fighter1");
		} else if (this.fighter2 != null && this.fighter2.getUniqueId().equals(player.getUniqueId())) {
			this.fighter2 = null;
			this.fighter2UUID = null;
			Gberry.log("REDROVER", "Player death " + player.getName() + " is fighter2");
		} else {
			Gberry.log("REDROVER", "Player death " + player.getName() + " is not a fighter");
			Gberry.log("REDROVER", "Fighter1: " + this.fighter1.getName() + " - " + this.fighter1.getUniqueId());
			Gberry.log("REDROVER", "Fighter2: " + this.fighter2.getName() + " - " + this.fighter2.getUniqueId());
		}

		// Send death message
		if (killer != null) {
			Gberry.log("MATCH2", player.getName() + " killed by " + killer.getName());
			this.sendRedGreenMessage(group, otherGroup, player.getDisguisedName() + " killed by " + killer.getDisguisedName(), killer.getHealth());
		}


		// getHealth() returns health without the damage, so just use 0D
		this.groupHealth.put(player.getUniqueId().toString(), 0D);

		this.checkWin();
	}

	public void handleLoggerDeath(UUID playerId) {
		if (this.fighter1 != null && this.fighter1.getUniqueId().equals(playerId)) {
			this.fighter1 = null;
			this.fighter1UUID = null;
			Gberry.log("REDROVER", "Player logger death " + playerId.toString() + " is fighter1");
		} else if (this.fighter2 != null && this.fighter2.getUniqueId().equals(playerId)) {
			this.fighter2 = null;
			this.fighter2UUID = null;
			Gberry.log("REDROVER", "Player logger death " + playerId.toString() + " is fighter2");
		} else {
			Gberry.log("REDROVER", "Player logger death " + playerId + " is not a fighter");
			Gberry.log("REDROVER", "Fighter1: " + this.fighter1.getName() + " - " + fighter1.getUniqueId());
			Gberry.log("REDROVER", "Fighter2: " + this.fighter2.getName() + " - " + fighter2.getUniqueId());
		}

		this.broadcastMessage(ChatColor.GOLD + "RedRover: " + this.team1.toActiveString() + " - " + this.team2.toActiveString());
	}


	public void removePlayerFromEverything(UUID uuid) {
		MatchManager.getRedRoverLoggedOutPlayers().remove(uuid);
		MatchManager.getCombatLoggedPlayers().remove(uuid);
		MatchManager.getActiveMatches().remove(uuid);
		this.allPlayers.remove(uuid);

		if (this.team1.contains(uuid)) {
			this.team1.getMembers().remove(uuid);
		}

		if (this.team2.contains(uuid)) {
			this.team2.getMembers().remove(uuid);
		}

		// Remove the player from matchmaking, they will be able to join other matches now
		new BukkitRunnable() {
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				List<String> uuids = new ArrayList<>();
				uuids.add(uuid.toString());
				data.put("uuid", uuids);
				data.put("match_id", getMatchUuid().toString());
				data.put("server_type", "arena");
				data.put("server_region", Gberry.serverRegion.toString().toLowerCase());
				data.put("ladder", getKitRuleSet().getName());
				data.put("type", getLadderType().getTag());
				MCPManager.contactMCP(MCPManager.MCP_MESSAGE.MATCHMAKING_DEFAULT_REMOVE_PLAYERS, data);
			}
		}.runTaskAsynchronously(ArenaPvP.getInstance());
	}

	@Override
	public void checkWin() {
		// Do a check that each team has at least 1 online active player, if neither of them do just tie the match
		if (!this.team1.hasActiveOnlinePlayers() && !this.team2.hasActiveOnlinePlayers()) {
			this.handleTie();
			return;
		}
		Team _winner = null;
		if (this.team1.hasActivePlayers()) {
			_winner = this.team1;
		}
		if (this.team2.hasActivePlayers()) {
			if (_winner != null) {
				_winner = null;
			} else {
				_winner = this.team2;
			}
		}
		if (_winner == null) {
			if (this.team1.hasActiveOnlinePlayers() && !this.team2.hasActiveOnlinePlayers()) {
				// Team 1 wins since the other team is offline
				_winner = this.team1;
			} else if (!this.team1.hasActiveOnlinePlayers() && this.team2.hasActiveOnlinePlayers()) {
				// Team 2 wins since team 1 has no one online that is active
				_winner = this.team2;
			}
		}

		final Team winner = _winner;

		if (winner == null) {
			this.tryStartNextRound();
		} else {
			this.broadcastMessage(ChatColor.GOLD + "RedRover: " + winner.toString() + " wins!");
			this.handleCommonEnd(winner);
		}
	}

	@Override
	public boolean checkTie(Player player) {
		if (!team1.hasActiveOnlinePlayers() && !team2.hasActiveOnlinePlayers()) {
			this.handleTie();
			return true;
		}
		return false;
	}

	@Override
	public void updateScoreboards(Player player) {
		List<Object> teamPackets = new ArrayList<>();
		if (this.playersSentTeamPackets.contains(player.getUniqueId())) {
			teamPackets.add(team1.getCreatePacket(ChatColor.BLUE, true));
			teamPackets.add(team2.getCreatePacket(ChatColor.YELLOW, true));
		} else {
			teamPackets.add(team1.getCreatePacket(ChatColor.BLUE, false));
			teamPackets.add(team2.getCreatePacket(ChatColor.YELLOW, false));
			this.playersSentTeamPackets.add(player.getUniqueId());
		}
		for (Object packet : teamPackets) {
			Gberry.protocol.sendPacket(player, packet);
			Bukkit.getLogger().info("REDROVER1: Sending team update packet to: " + player.getName());
		}
	}

	public void sendEveryoneTeamPackets() {
		for (UUID playerId : this.allPlayers) {
			Player player = Bukkit.getPlayer(playerId);
			if (player != null) {
				List<Object> teamPackets = new ArrayList<>();
				if (this.playersSentTeamPackets.contains(playerId)) {
					teamPackets.add(this.team1.getCreatePacket(ChatColor.BLUE, true));
					teamPackets.add(this.team2.getCreatePacket(ChatColor.YELLOW, true));
				} else {
					teamPackets.add(this.team1.getCreatePacket(ChatColor.BLUE, false));
					teamPackets.add(this.team2.getCreatePacket(ChatColor.YELLOW, false));
					this.playersSentTeamPackets.add(playerId);
				}
				for (Object packet : teamPackets) {
					Gberry.protocol.sendPacket(player, packet);
					Bukkit.getLogger().info("REDROVER1: Sending team update packet to: " + player.getName());
				}
			}
		}
	}

	@Override
	public void handleTie() {

		// Create match data for GCheat
		Map<String, Object> data = new HashMap<>();
		data.put("match_id", this.getMatchId());
		data.put("season", Match.CURRENT_SEASON);


		// Cleanup stuff
		for (UUID playerId : allPlayers) {
			Player player = Bukkit.getPlayer(playerId);
			//Remove match from players match state
			TeamStateMachine.matchState.removePlayerMatch(player);

			// Call game end event for GCheat
			ArenaPvP.getInstance().getServer().getPluginManager().callEvent(new GCheatGameEndEvent(player, data));

			if (player != null) {
				BukkitUtil.closeInventory(player);
			}
			MatchManager.getMatchesAwaitingPlayers().remove(playerId);
			PotPvPPlayerManager.players.remove(playerId);
		}

		List<Object> teamremovePackets = new ArrayList<>();
		teamremovePackets.add(team2.getRemovePacket());
		teamremovePackets.add(team1.getRemovePacket());

		// Only send remove packets to players that we sent them to
		for (UUID playerId : this.playersSentTeamPackets) {
			Player player = Bukkit.getPlayer(playerId);
			if (player != null) {
				if (teamremovePackets != null) {
					for (Object object : teamremovePackets) {
						Gberry.protocol.sendPacket(player, object);
					}
				}
			}
		}

		MatchManager.getActiveMatches().remove(team1);
		MatchManager.getActiveMatches().remove(team2);

		this.setEndResult("time");
		this.end();

		// All players might not of made it onto teams yet so lets just be safe and send them all in a single team
		this.mcpEndMatch(Collections.singletonList(new Team(allPlayers, "team_1")), null, -1);
	}


	public void handleCommonEnd(Team winner) {
		// Create match data for GCheat
		Map<String, Object> data = new HashMap<>();
		data.put("match_id", getMatchId());
		data.put("season", Match.CURRENT_SEASON);

		// Cleanup their potpvp player objects just incase
		MatchManager.getActiveMatches().remove(team1);
		MatchManager.getActiveMatches().remove(team2);

		List<Object> teamremovePackets = new ArrayList<>();
		teamremovePackets.add(team2.getRemovePacket());
		teamremovePackets.add(team1.getRemovePacket());

		for (UUID memberId : team1.membersIds()) {
			PotPvPPlayerManager.players.remove(memberId);
		}

		for (UUID memberId : team2.membersIds()) {
			PotPvPPlayerManager.players.remove(memberId);
		}

		for (Player player : team1.members()) {
			//Remove match from players match state
			TeamStateMachine.matchState.removePlayerMatch(player);

			// Call game end event for GCheat
			ArenaPvP.getInstance().getServer().getPluginManager().callEvent(new GCheatGameEndEvent(player, data));
		}

		for (Player player : team2.members()) {
			//Remove match from players match state
			TeamStateMachine.matchState.removePlayerMatch(player);

			// Call game end event for GCheat
			ArenaPvP.getInstance().getServer().getPluginManager().callEvent(new GCheatGameEndEvent(player, data));
		}

		// Only send remove packets to players that we sent them to
		for (UUID playerId : this.playersSentTeamPackets) {
			Player player = Bukkit.getPlayer(playerId);
			if (player != null) {
				if (teamremovePackets != null) {
					for (Object object : teamremovePackets) {
						Gberry.protocol.sendPacket(player, object);
					}
				}
			}
		}

		this.end();

		// Remove any cached player inventories
		//PartyPlayerInventoriesInventory.cleanUpCachedInventories(this.team1, this.team2);

		this.mcpEndMatch(Collections.singletonList(new Team(allPlayers, "team_1")), winner, -1);
	}


	public void handleLogout(Player player) {
		MatchManager.getRedRoverLoggedOutPlayers().put(player.getUniqueId(), this);

		// Send remove team packets if they were sent them
		if (this.playersSentTeamPackets.contains(player.getUniqueId())) {
			// SEnd them remove team packets
			try {
				List<Object> teamremovePackets = new ArrayList<>();
				teamremovePackets.add(team2.getRemovePacket());
				teamremovePackets.add(team1.getRemovePacket());
				for (Object object : teamremovePackets) {
					Gberry.protocol.sendPacket(player, object);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// Remove from sent team packets list
			this.playersSentTeamPackets.remove(player.getUniqueId());
		}
	}

	@Override
	public void end() {
		Iterator<Map.Entry<UUID, Match>> iter = MatchManager.getRedRoverLoggedOutPlayers().entrySet().iterator();

		while (iter.hasNext()) {
			Map.Entry<UUID, Match> entry = iter.next();
			if (entry.getValue() == this) {
				iter.remove();
			}
		}

		this.setInProgress(false);

		for (UUID playerId : this.allPlayers) {
			Player player = Bukkit.getPlayer(playerId);
			if (player != null) {
				if (TeamStateMachine.matchState.contains(player)) {
					SpectatorHelper.activateSpectateGameMode(player);
				} else if (TeamStateMachine.redRoverWaitingState.contains(player)) {
					State<Player> currentState = TeamStateMachine.getInstance().getCurrentState(player);
					try {
						currentState.pop(player);
					} catch (IllegalStateTransitionException e) {
						e.printStackTrace();
					}
					SpectatorHelper.activateSpectateGameMode(player);
				}
			}
		}

		EnderPearlManager.remove(team1.members());
		EnderPearlManager.remove(team2.members());
	}

	@Override
	public void updateHealthObjective(Player player, double health) {
		// TODO: DO THIS
		return;
	}

	@Override
	public void broadcastMessage(String message) {
		for (UUID playerId : allPlayers) {
			Player player = Bukkit.getPlayer(playerId);
			if (player != null) {
				player.sendMessage(message);
			}
		}
	}

	@Override
	public void broadcastSound(Sound sound, float pitch, float volume) {
		for (UUID playerId : allPlayers) {
			Player player = Bukkit.getPlayer(playerId);
			if (player != null) {
				player.playSound(player.getLocation(), sound, pitch, volume);
			}
		}
	}

	public List<UUID> getAllPlayers() {
		return allPlayers;
	}

	public Team getTeam1() {
		return team1;
	}

	public Team getTeam2() {
		return team2;
	}

	public Player getFighter1() {
		return this.fighter1;
	}

	public Player getFighter2() {
		return fighter2;
	}

	public UUID getFighter1UUID() {
		return fighter1UUID;
	}

	public UUID getFighter2UUID() {
		return fighter2UUID;
	}

	public void setFighter1(Player fighter1) {
		this.fighter1 = fighter1;
		this.fighter1UUID = fighter1.getUniqueId();

		// Add the fighter to the team
		this.team1.getMembers().put(fighter1.getUniqueId(), true);
		ArenaPvP.getInstance().setPlayerTeam(fighter1, this.team1);

		this.sendEveryoneTeamPackets();

		this.broadcastMessage(ChatColor.BLUE + this.captain1.getDisguisedName() + ChatColor.GREEN + " picked " + ChatColor.BLUE + this.fighter1.getDisguisedName());


		if (this.checkForNullCaptains()) {
			this.broadcastMessage(ChatColor.GOLD + "One of your team captains is offline, ending the match.");
			this.handleTie();
			return;
		}

		if (this.firstPick) {
			this.captain2.sendFormattedMessage("{0}Select your next fighter.", ChatColor.GOLD);

			this.broadcastMessage(ChatColor.YELLOW + captain2.getDisguisedName() + ChatColor.GREEN + " is picking a fighter for team 2.");
			this.broadcastMessage(ChatColor.YELLOW + captain2.getDisguisedName() + ChatColor.GREEN + " has 10 seconds to pick a fighter.");

			this.captain1Picking = false;
			RedRoverChosePlayersInventory.openSelectPlayersInventory(this.captain2, this);
			this.startAutoPickPlayerCaptain2Task();
			return;
		}

		// Start next round
		this.tryStartNextRound();
	}

	public void setFighter2(Player fighter2) {
		this.fighter2 = fighter2;
		this.fighter2UUID = fighter2.getUniqueId();

		// Add the fighter to the team
		this.team2.getMembers().put(fighter2.getUniqueId(), true);
		ArenaPvP.getInstance().setPlayerTeam(fighter2, this.team2);

		this.sendEveryoneTeamPackets();

		this.broadcastMessage(ChatColor.BLUE + this.captain2.getDisguisedName() + ChatColor.GREEN + " picked " + ChatColor.BLUE + this.fighter2.getDisguisedName());

		if (this.checkForNullCaptains()) {
			this.broadcastMessage(ChatColor.GOLD + "One of your team captains is offline, ending the match.");
			this.handleTie();
			return;
		}

		if (this.firstPick) {
			this.firstPick = false;

			this.setStarted(true);

			for (UUID playerId : this.allPlayers) {
				Player player = Bukkit.getPlayer(playerId);
				if (player != null) {
					SidebarManager.addSidebarItems(player, this);
				}
			}

			this.startBattle();
			return;
		}

		this.tryStartNextRound();
	}

	public void startAutoPickPlayerCaptain1Task() {
		if (this.autoPickPlayerCaptain1Task != null) {
			this.autoPickPlayerCaptain1Task.cancel();
		}
		this.autoPickPlayerCaptain1Task = new BukkitRunnable() {
			@Override
			public void run() {
				RedRoverBattle.this.autoPickPlayer(RedRoverBattle.this.team1);
			}
		}.runTaskLater(ArenaPvP.getInstance(), 20 * 10);
	}

	public void startAutoPickPlayerCaptain2Task() {
		if (this.autoPickPlayerCaptain2Task != null) {
			this.autoPickPlayerCaptain2Task.cancel();
		}
		this.autoPickPlayerCaptain2Task = new BukkitRunnable() {
			@Override
			public void run() {
				RedRoverBattle.this.autoPickPlayer(RedRoverBattle.this.team2);
			}
		}.runTaskLater(ArenaPvP.getInstance(), 20 * 10);
	}

	public Player getCaptain1() {
		return this.captain1;
	}

	public Player getCaptain2() {
		return this.captain2;
	}

	public boolean isCaptain1Picking() {
		return this.captain1Picking;
	}

	public BukkitTask getAutoPickPlayerCaptain1Task() {
		return autoPickPlayerCaptain1Task;
	}

	public BukkitTask getAutoPickPlayerCaptain2Task() {
		return autoPickPlayerCaptain2Task;
	}

	public Map<Integer, UUID> getInventoryPlayers() {
		return inventoryPlayers;
	}

	public void setCaptain1Picking(boolean captain1Picking) {
		this.captain1Picking = captain1Picking;
	}

	@Override
	public Location getWarpForPlayer(Player player) {
		if (team1.contains(player)) {
			return arena.getWarp1Origin();
		}
		if (team2.contains(player)) {
			return arena.getWarp2Origin();
		}
		return arena.getWarp1Origin();
	}

	@Override
	public Location getWarpForTeam(Team team) {
		if (team.equals(team1)) {
			return arena.getWarp1Origin();
		}
		if (team.equals(team2)) {
			return arena.getWarp2Origin();
		}
		return arena.getWarp2Origin();
	}

	@Override
	public Team getPlayersTeam(Player player) {
		if (team1.contains(player)) {
			return team1;
		} else if (team2.contains(player)) {
			return team2;
		}
		return null;
	}

	@Override
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();
		players.addAll(team1.members());
		players.addAll(team2.members());
		return players;
	}

	@Override
	public Team getOtherGroup(Team team) {
		if (team.equals(team1)) {
			return team2;
		} else if (team.equals(team2)) {
			return team1;
		}
		return null;
	}


	@Override
	public boolean contains(Player player) {
		if (allPlayers.contains(player.getUniqueId())) {
			return true;
		}
		if (team1.contains(player) || team2.contains(player)) {
			return true;
		}
		return false;
	}

	public int amountOfPlayersNotPlayed() {
		List<UUID> playerIds = getAllPlayers();
		List<UUID> players = new ArrayList<>();
		Set<UUID> team1Members = getTeam1().membersIds();
		Set<UUID> team2Members = getTeam2().membersIds();

		// Get all players not in a team already
		for (UUID playerId : playerIds) {
			if (!team1Members.contains(playerId) && !team2Members.contains(playerId)) {
				if (Bukkit.getPlayer(playerId) != null) {
					players.add(playerId);
				}
			}
		}
		return players.size();
	}

	public void autoPickPlayer(Team team) {
		if (this.checkForNullCaptains()) {
			this.broadcastMessage(ChatColor.GOLD + "One of your team captains is offline, ending the match.");
			this.handleTie();
			return;
		}

		List<UUID> playerIds = getAllPlayers();
		List<UUID> players = new ArrayList<>();
		Set<UUID> team1Members = getTeam1().membersIds();
		Set<UUID> team2Members = getTeam2().membersIds();

		// Get all players not in a team already
		for (UUID playerId : playerIds) {
			if (!team1Members.contains(playerId) && !team2Members.contains(playerId)) {
				players.add(playerId);
			}
		}

		Iterator<UUID> iterator = players.iterator();
		while (iterator.hasNext()) {
			UUID playerId = iterator.next();
			if (team == this.team1) {
				Player player = Bukkit.getPlayer(playerId);
				if (player != null) {
					if (this.captain1 == player || this.captain2 == player) {
						continue;
					}
					this.setFighter1(player);
					return;
				}
			}
			if (team == this.team2) {
				Player player = Bukkit.getPlayer(playerId);
				if (player != null) {
					if (this.captain1 == player || this.captain2 == player) {
						continue;
					}
					this.setFighter2(player);
					return;
				}
			}
		}

		// No players left set captains as fighter
		if (team == this.team1) {
			this.setFighter1(captain1);
		}
		if (team == this.team2) {
			this.setFighter2(captain2);
		}
	}

}
