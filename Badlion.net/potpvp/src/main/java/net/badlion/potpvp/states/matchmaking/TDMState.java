package net.badlion.potpvp.states.matchmaking;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.KitLoadEvent;
import net.badlion.potpvp.bukkitevents.TDMCounterFilledEvent;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.inventories.tdm.TDMVoteInventory;
import net.badlion.potpvp.managers.RespawnManager;
import net.badlion.potpvp.managers.TDMManager;
import net.badlion.potpvp.tdm.TDMGame;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TDMState extends GameState implements Listener {

	public static Map<String, String> mapOfLastDamage = new HashMap<>();
	public static Map<String, Long> lastDamageTime = new HashMap<>();
	public static Map<String, Long> lastKillTime = new HashMap<>();
	public static Map<String, Integer> lastMultiKill = new HashMap<>();
	public static Map<String, BukkitTask> multiKillTaskMap = new HashMap<>();

	private Map<UUID, TDMGame.TDMTeam> assignedTeams = new HashMap<>();

	public TDMState() {
		super("tdm_state", "they are in a TDM game.", GroupStateMachine.getInstance());
	}

	@Override
	public void before(Group group, Object o) {
		super.before(group, o);

		Game game = (Game)  o;
		if (!(game instanceof TDMGame)) {
			PotPvP.getInstance().somethingBroke(group.getLeader(), group);
			return;
		}

		// Hide respawning players
		RespawnManager.handlePlayerRespawningVisibility(GameState.getGroupGame(group), group, true);

		// Reset current kills/deaths
		TDMManager.resetCurrentStats(group.getLeader().getUniqueId());

		TDMGame tdmGame = (TDMGame) game;
		tdmGame.addPlayer(group.getLeader());

		this.giveScoreboard(group.getLeader(), tdmGame);
	}

	@Override
	public void after(Group group) {
		TDMState.mapOfLastDamage.remove(group.getLeader().getName());
		TDMState.lastDamageTime.remove(group.getLeader().getName());
		TDMState.lastKillTime.remove(group.getLeader().getName());
		TDMState.lastMultiKill.remove(group.getLeader().getName());
		TDMState.multiKillTaskMap.remove(group.getLeader().getName());

		Game game = this.removeGroupGame(group);
		if (!(game instanceof TDMGame)) {
			PotPvP.getInstance().somethingBroke(group.getLeader(), group);
			return;
		}

		TDMGame tdmGame = (TDMGame) game;

		// Show respawning players
		RespawnManager.handlePlayerRespawningVisibility(game, group, false);

		for (Player player : group.players()) {
			tdmGame.removePlayer(player);

			this.removeTeams(tdmGame, player);
		}
	}

	private void removeTeams(TDMGame tdmGame, Player player) {
		for (TDMGame.TDMTeam tdmTeam : tdmGame.getTeams()) {
			ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + tdmTeam.getColor().name(), tdmTeam.getColor() + "", "");
			ScoreboardUtil.getTeam(player.getScoreboard(), tdmTeam.getName() + "pts", "", tdmTeam.getColor() + "Points: " + ChatColor.WHITE).unregister();
		}

		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "time", ChatColor.GOLD + "Time", ChatColor.GOLD + " Left: " + ChatColor.WHITE).unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ks", ChatColor.GOLD + "Kill", ChatColor.GOLD + " Streak: " + ChatColor.WHITE).unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", ChatColor.GOLD + "Dea", "ths: " + ChatColor.WHITE).unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "asst", ChatColor.GOLD + "Ass", "ists: " + ChatColor.WHITE).unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "cntr", ChatColor.GOLD + "Cou", "nter: " + ChatColor.WHITE).unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp1", "", ScoreboardUtil.SAFE_TEAM_PREFIX + " ").unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp2", "", ScoreboardUtil.SAFE_TEAM_PREFIX + "  ").unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp3", "", ScoreboardUtil.SAFE_TEAM_PREFIX + "   ").unregister();

		player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
	}

	public void addAssignedTeam(Player player, TDMGame.TDMTeam tdmTeam) {
		this.assignedTeams.put(player.getUniqueId(), tdmTeam);
	}

	public TDMGame.TDMTeam getAssignedTeam(Player player) {
		return this.assignedTeams.remove(player.getUniqueId());
	}

	public void giveScoreboard(Player player, TDMGame tdmGame) {
		Objective objective = ScoreboardUtil.getObjective(player.getScoreboard(), "tdm", DisplaySlot.SIDEBAR, ChatColor.AQUA + "Badlion TDM");

		// Get player's tdm team
		TDMGame.TDMTeam playerTeam = tdmGame.getTeam(player);

		// Setup team scoreboards
		int numOfTeams = tdmGame.getTeams().size();
		int counter = 8;
		for (TDMGame.TDMTeam tdmTeam : tdmGame.getTeams()) {
			Team team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + tdmTeam.getColor().name(), tdmTeam.getColor() + "", "");

			for (UUID uuid : tdmGame.getPlayers(tdmTeam)) {
				Player pl = PotPvP.getInstance().getServer().getPlayer(uuid);

				// Add everyone to player's scoreboard
				team.addEntry(pl.getName());

				// Add player to everyone's scoreboard
				Team team2 = ScoreboardUtil.getTeam(pl.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + playerTeam.getColor().name(), playerTeam.getColor() + "", "");
				team2.addEntry(player.getName());

			}

			// Side scoreboard
			ScoreboardUtil.getTeam(player.getScoreboard(), tdmTeam.getName() + "pts", "", tdmTeam.getColor() + "Points: " + ChatColor.WHITE).setSuffix(tdmTeam.getScore() + "");

			objective.getScore(tdmTeam.getColor() + "Points: " + ChatColor.WHITE).setScore(counter);

			counter++;
		}

		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "time", ChatColor.GOLD + "Time", ChatColor.GOLD + " Left: " + ChatColor.WHITE).setSuffix((tdmGame.getTDMTimeLimitTask()).niceTime());
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ks", ChatColor.GOLD + "Kill", ChatColor.GOLD + " Streak: " + ChatColor.WHITE).setSuffix("0");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setSuffix("0");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", ChatColor.GOLD + "Dea", "ths: " + ChatColor.WHITE).setSuffix("0");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "asst", ChatColor.GOLD + "Ass", "ists: " + ChatColor.WHITE).setSuffix("0");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "cntr", ChatColor.GOLD + "Cou", "nter: " + ChatColor.WHITE).setSuffix("0%");

		objective.getScore(ChatColor.GOLD + " Left: " + ChatColor.WHITE).setScore(numOfTeams + 9);
		objective.getScore(ChatColor.GOLD + " Streak: " + ChatColor.WHITE).setScore(6);
		objective.getScore(ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setScore(5);
		objective.getScore("ths: " + ChatColor.WHITE).setScore(4);
		objective.getScore("ists: " + ChatColor.WHITE).setScore(2);
		objective.getScore("nter: " + ChatColor.WHITE).setScore(1);

		// Spacers
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp1", "", ScoreboardUtil.SAFE_TEAM_PREFIX + " ");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp2", "", ScoreboardUtil.SAFE_TEAM_PREFIX + "  ");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp3", "", ScoreboardUtil.SAFE_TEAM_PREFIX + "   ");

		objective.getScore(" ").setScore(numOfTeams + 8);
		objective.getScore("  ").setScore(7);
		objective.getScore("   ").setScore(3);
	}

	public Player handleScoreboardDeath(Player player, TDMGame tdmGame) {
		TDMState.lastDamageTime.remove(player.getName());

		// Add to stats
		TDMManager.TDMStats tdmStats = TDMManager.addDeath(player.getUniqueId());

		Team team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", ChatColor.GOLD + "Dea", "ths: " + ChatColor.WHITE);
		team.setSuffix(tdmStats.getDeaths() + "");
		team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ks", ChatColor.GOLD + "Kill", ChatColor.GOLD + " Streak: " + ChatColor.WHITE);
		team.setSuffix("0");
		// Handle if the person who died had a kill streak
		Player killer = this.getKiller(player);
		if (killer != null) {
			if (tdmStats.getKillstreak() >= 5) {
				tdmGame.sendMessage(killer.getDisplayName() + ChatColor.DARK_AQUA + " has ended " + player.getName()
						+ "'s killstreak of " + ChatColor.YELLOW + tdmStats.getKillstreak());
			}
		}

		// Handle assist stuff
		long now = System.currentTimeMillis();
		for (TDMManager.TDMDamager tdmDamager : tdmStats.getDamagers()) {
			if (killer != null && killer.getUniqueId().equals(tdmDamager.getUuid())) {
				continue;
			}

			if (tdmDamager.getLastDamageTime() + TDMGame.ASSIST_TIME > now) {
				if (tdmDamager.getDamage() >= TDMGame.ASSIST_DAMAGE) {
					TDMManager.TDMStats assister = TDMManager.getTDMStats(tdmDamager.getUuid());
					if (assister != null) {
						assister.addAssist();
						assister.addCounter(tdmDamager.getDamage());

						Player pl = PotPvP.getInstance().getServer().getPlayer(assister.getUUID());
						if (pl != null) {
							Group group = PotPvP.getInstance().getPlayerGroup(pl);
							if (this.contains(group)) {
								team = ScoreboardUtil.getTeam(pl.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "asst", ChatColor.GOLD + "Ass", "ists: " + ChatColor.WHITE);
								team.setSuffix(assister.getAssists() + "");
								team = ScoreboardUtil.getTeam(pl.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "cntr", ChatColor.GOLD + "Cou", "nter: " + ChatColor.WHITE);
								team.setSuffix(assister.getCounter() + "%");
							}
						}
					}
				}
			}
		}

		tdmStats.clearDamage();

		// If they exist...
		if (killer != null) {
			GroupStateMachine.tdmState.addKillToScoreboard(killer, tdmGame);

			// Add kill for player
			tdmGame.addKillForTeam(killer);

			// Multikill messages
			this.handleMultiKill(killer, tdmGame);
		}

		if (player.getLastDamageCause() != null) {
			switch (player.getLastDamageCause().getCause()) {
				case BLOCK_EXPLOSION:
					tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " just got blown the hell up", player);
					break;
				case CONTACT:
					if (killer != null) {
						ChatColor killerColor = tdmGame.getTeam(killer).getColor();

						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " walked into a cactus whilst trying to escape " +
								tdmGame.getTeam(killer).getColor() + killer.getName() + PlayerHelper.getHeartsLeftString(killerColor, killer.getHealth()), player, killer);
					} else {
						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " was pricked to death", player);
					}
					break;
				case CUSTOM:
					tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " was killed by an unknown cause", player);
					break;
				case DROWNING:
					if (killer != null) {
						ChatColor killerColor = tdmGame.getTeam(killer).getColor();

						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " drowned whilst trying to escape " +
								tdmGame.getTeam(killer).getColor() + killer.getName() + PlayerHelper.getHeartsLeftString(killerColor, killer.getHealth()), player, killer);
					} else {
						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " drowned", player);
					}
					break;
				case ENTITY_ATTACK:
					if (killer != null) {
						ChatColor killerColor = tdmGame.getTeam(killer).getColor();

						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " was slain by " +
								tdmGame.getTeam(killer).getColor() + killer.getName() + PlayerHelper.getHeartsLeftString(killerColor, killer.getHealth()), player, killer);
					}
					break;
				case ENTITY_EXPLOSION:
					if (killer != null) {
						ChatColor killerColor = tdmGame.getTeam(killer).getColor();

						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " got blown the hell up by " +
								tdmGame.getTeam(killer).getColor() + killer.getName() + PlayerHelper.getHeartsLeftString(killerColor, killer.getHealth()), player, killer);
					}
					break;
				case FALL:
					if (killer != null) {
						ChatColor killerColor = tdmGame.getTeam(killer).getColor();

						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " was doomed to fall by " +
								tdmGame.getTeam(killer).getColor() + killer.getName() + PlayerHelper.getHeartsLeftString(killerColor, killer.getHealth()), player, killer);
					} else {
						if (player.getFallDistance() > 5) {
							tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " fell from a high place", player);
						} else {
							tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " hit the ground too hard", player);
						}
					}
					break;
				case FALLING_BLOCK:
					tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " got freaking squashed by a block", player);
					break;
				case FIRE:
					if (killer != null) {
						ChatColor killerColor = tdmGame.getTeam(killer).getColor();

						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " walked into a fire whilst fighting " +
								tdmGame.getTeam(killer).getColor() + killer.getName() + PlayerHelper.getHeartsLeftString(killerColor, killer.getHealth()), player, killer);
					} else {
						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " went up in flames", player);
					}
					break;
				case FIRE_TICK:
					if (killer != null) {
						ChatColor killerColor = tdmGame.getTeam(killer).getColor();

						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " was burnt to a crisp whilst fighting " +
								tdmGame.getTeam(killer).getColor() + killer.getName() + PlayerHelper.getHeartsLeftString(killerColor, killer.getHealth()), player, killer);
					} else {
						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " burned to death", player);
					}
					break;
				case LAVA:
					if (killer != null) {
						ChatColor killerColor = tdmGame.getTeam(killer).getColor();

						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY +
								" tried to swim in lava while trying to escape " + tdmGame.getTeam(killer).getColor() + killer.getName() + PlayerHelper.getHeartsLeftString(killerColor, killer.getHealth()), player, killer);
					} else {
						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " tried to swim in lava", player);
					}
					break;
				case LIGHTNING:
					tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " got lit the hell up by lightnin'", player);
					break;
				case MAGIC:
					if (killer != null) {
						ChatColor killerColor = tdmGame.getTeam(killer).getColor();

						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY +
								" was killed by " + tdmGame.getTeam(killer).getColor() + killer.getName() + ChatColor.GRAY + " using magic" + PlayerHelper.getHeartsLeftString(killerColor, killer.getHealth()), player, killer);
					} else {
						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " was killed by magic", player);
					}
					break;
				case POISON:
					tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " was poisoned", player);
					break;
				case PROJECTILE:
					if (killer != null) {
						ChatColor killerColor = tdmGame.getTeam(killer).getColor();

						// Get distance of travel
						int distance = (int) Math.floor(killer.getLocation().distance(player.getLocation()));

						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " was shot by " +
								tdmGame.getTeam(killer).getColor() + killer.getName() + PlayerHelper.getHeartsLeftString(killerColor, killer.getHealth()) + ChatColor.GRAY + " from " + distance + " blocks", player, killer);
					} else {
						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " was shot", player);
					}
					break;
				case STARVATION:
					tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " starved to death", player);
					break;
				case SUFFOCATION:
					tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " suffocated in a wall", player);
					break;
				case SUICIDE:
					tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " took his own life like a peasant", player);
					break;
				case THORNS:
					tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " killed themself by trying to kill someone LOL", player);
					break;
				case VOID:
					if (killer != null) {
						ChatColor killerColor = tdmGame.getTeam(killer).getColor();

						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY +
								" was knocked into the void by " + tdmGame.getTeam(killer).getColor() + killer.getName() + PlayerHelper.getHeartsLeftString(killerColor, killer.getHealth()), player, killer);
					} else {
						tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " fell out of the world", player);
					}
					break;
				case WITHER:
					tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " withered away", player);
					break;
			}
		} else {
			tdmGame.sendMessage(tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " died.", player);
		}

		TDMState.mapOfLastDamage.remove(player.getName());
		TDMState.lastMultiKill.remove(player.getName());
		TDMState.lastKillTime.remove(player.getName());

		return killer;
	}

	private Player getKiller(Player player) {
		String lastDamage = TDMState.mapOfLastDamage.get(player.getName());
		if (lastDamage != null) {
			Player killer = PotPvP.getInstance().getServer().getPlayerExact(lastDamage);

			if (killer != null) {
				Group group = PotPvP.getInstance().getPlayerGroup(killer);

				// Are they still in the TDM?
				if (GroupStateMachine.getInstance().getCurrentState(group) == this) {
					return killer;
				}
			}
		}

		return null;
	}

	public void addKillToScoreboard(Player player, TDMGame tdmGame) {
		// Add to stats
		TDMManager.TDMStats tdmStats = TDMManager.addKill(player.getUniqueId());

		Team team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE);
		team.setSuffix(tdmStats.getKills() + "");
		team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ks", ChatColor.GOLD + "Kill", ChatColor.GOLD + " Streak: " + ChatColor.WHITE);
		team.setSuffix(tdmStats.getKillstreak() + "");

		// Kill streak hooked in here
		this.handleKillStreak(player, tdmStats.getKillstreak(), tdmGame);
	}

	private void handleKillStreak(Player player, int killstreak, TDMGame tdmGame) {
		if (killstreak != 0 && killstreak % 5 == 0) {
			String msg = tdmGame.getTeam(player).getColor() + player.getName() + ChatColor.DARK_AQUA + " has gotten a killstreak of " + ChatColor.DARK_RED + killstreak;
			tdmGame.sendMessage(msg);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerDamageTDM(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Group group = PotPvP.getInstance().getPlayerGroup(player);

			if (this.contains(group)) {
				TDMGame game = (TDMGame) GameState.getGroupGame(group);

				Entity damager = event.getDamager();
				Player damagePlayer = null;
				if (damager instanceof Projectile) {
					damagePlayer = ((Projectile) damager).getShooter() != null && ((Projectile) damager).getShooter() instanceof Player ? (Player) ((Projectile) damager).getShooter() : null;
				} else if (damager instanceof Player) {
					damagePlayer = (Player) damager;
				}

				// Track last damage
				if (damagePlayer != null) {
					if (damagePlayer.getGameMode() == GameMode.CREATIVE) { // Cancel damage if gamemode is 1
						return;
					}

					// Don't let allies hurt each other
					if (!game.canHurt(damagePlayer, player)) {
						event.setCancelled(true);
						return;
					}

					// Don't track damage that we did to ourselves
					if (damagePlayer != player) {
						TDMState.mapOfLastDamage.put(player.getName(), damagePlayer.getName());
						TDMState.lastDamageTime.put(player.getName(), System.currentTimeMillis());
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onPlayerDamageTDMNonEntity(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Group group = PotPvP.getInstance().getPlayerGroup(event.getEntity());

			if (this.contains(group)) {
				TDMState.lastDamageTime.put(((Player) event.getEntity()).getName(), System.currentTimeMillis());
			}
		}
	}

	private void handleMultiKill(final Player player, TDMGame tdmGame) {
		Long lastKillTime = TDMState.lastKillTime.get(player.getName());
		if (lastKillTime != null) {
			// They already have a multi-kill
			int kills = TDMState.lastMultiKill.get(player.getName());

			// Cancel old task
			TDMState.multiKillTaskMap.get(player.getName()).cancel();

			String msg = "";
			if (kills == 1) {
				msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "DOUBLE KILL";
			} else if (kills == 2) {
				msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "TRIPLE KILL";
			} else if (kills == 3) {
				msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "QUADRA KILL";
			} else if (kills == 4) {
				msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "PENTA KILL!!!";
			} else if (kills == 5) {
				msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "HEXA KILL!!!";
			} else if (kills == 6) {
				msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "HEPTA KILL!!!";
			} else if (kills == 7) {
				msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "OCTA KILL!!!";
			} else if (kills == 8) {
				msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "NONA KILL!!!";
			} else if (kills == 9) {
				msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "DECA KILL!!!";
			} else {
				msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "MULTI KILL!!! (More than 10 kills)";
			}

			// PRINT THE DAMN MESSAGE
			tdmGame.sendMessage(msg);

			// Add one kill for future messages
			TDMState.lastMultiKill.put(player.getName(), kills + 1);
		} else {
			TDMState.lastMultiKill.put(player.getName(), 1);
		}

		// Always start off a new task
		TDMState.multiKillTaskMap.put(player.getName(), PotPvP.getInstance().getServer().getScheduler().runTaskLater(PotPvP.getInstance(), new Runnable() {
			public void run() {
				TDMState.lastKillTime.remove(player.getName());
				TDMState.lastMultiKill.remove(player.getName());
			}
		}, 20 * 10)); // 10s

		// Add new last kill time
		TDMState.lastKillTime.put(player.getName(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		if (this.contains(group)) {
			ItemStack item = event.getItem();

			if (item == null || item.getType().equals(Material.AIR)) return;

			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (item.getType() == TDMVoteInventory.getVoteItem().getType()) {
					TDMVoteInventory.openTDMVoteInventory((TDMGame) GameState.getGroupGame(group), player);

					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		if (this.contains(group)) {
			ChatColor color = ((TDMGame) GameState.getGroupGame(group)).getTeam(player).getColor();

			// Do they have leather armor?
			for (ItemStack item : player.getInventory().getArmorContents()) {
				if (item == null || item.getType() == Material.AIR) continue;

				if (item.getType() == Material.LEATHER_HELMET || item.getType() == Material.LEATHER_CHESTPLATE
						|| item.getType() == Material.LEATHER_LEGGINGS || item.getType() == Material.LEATHER_BOOTS) {
					// Color leather
					LeatherArmorMeta itemMeta = ((LeatherArmorMeta) item.getItemMeta());
					itemMeta.setColor(Gberry.getColorFromChatColor(color));
					item.setItemMeta(itemMeta);
				}
			}
		}
	}

	@EventHandler
	public void onCounterFilled(TDMCounterFilledEvent event) {
		Player player = PotPvP.getInstance().getServer().getPlayer(event.getUuid());
		if (player != null) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			if (this.contains(group)) {
				TDMGame game = (TDMGame) GameState.getGroupGame(group);

				for (ItemStack itemStack : game.getKitRuleSet().getTDMKillRewardItems()) {
					player.getInventory().addItem(itemStack);
				}

				player.sendMessage(TDMGame.PREFIX + ChatColor.GOLD + ChatColor.BOLD + "You were given a reward for reaching 100 assist points!");
			}
		}
	}

	@EventHandler
	public void kitLoadEvent(KitLoadEvent event) {
		Group group = PotPvP.getInstance().getPlayerGroup(event.getPlayer());
		if (this.contains(group)) {
			ItemStackUtil.addUnbreakingToArmor(event.getPlayer());
			ItemStackUtil.addUnbreakingToWeapons(event.getPlayer());
		}
	}

}
