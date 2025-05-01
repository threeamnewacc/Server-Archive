package net.badlion.survivalgames.managers;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.MessageUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGTeamManager;
import net.badlion.mpg.tasks.GameTimeTask;
import net.badlion.survivalgames.SurvivalGames;
import net.kohi.sidebar.SidebarAPI;
import net.kohi.sidebar.item.SidebarItem;
import net.kohi.sidebar.item.StaticSidebarItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SGSidebarManager implements Listener {

	private static int teamsLeft;
	private static int playersLeft;

	public static Map<UUID, Integer> playerKills = new ConcurrentHashMap<>();
	public static Map<MPGTeam, Integer> teamKills = new ConcurrentHashMap<>();

	private static Map<UUID, Double> playerHealth = new ConcurrentHashMap<>();

	private static Map<UUID, DateTimeZone> playerTimeZones = new ConcurrentHashMap<>();
	private static Map<DateTimeZone, String> timeZoneStrings = new ConcurrentHashMap<>();

	private static StaticSidebarItem localTimeText = new StaticSidebarItem(5, ChatColor.GREEN + "Local Time: ");

	private static StaticSidebarItem serverText = new StaticSidebarItem(25, ChatColor.GREEN + "Server: ");

	private static StaticSidebarItem serverNameText = new StaticSidebarItem(30, ChatColor.AQUA + "  " + Gberry.serverName);

	private static StaticSidebarItem spacer33 = new StaticSidebarItem(33, "");

	private static StaticSidebarItem spacer35 = new StaticSidebarItem(35, "");

	private static StaticSidebarItem spacer50 = new StaticSidebarItem(50, "");

	private static StaticSidebarItem spacer90 = new StaticSidebarItem(90, "");

	private static StaticSidebarItem badlionText = new StaticSidebarItem(100, ChatColor.AQUA + "www.badlion.net");

	public SGSidebarManager() {
		BukkitUtil.runTaskTimer(new PlayersTeamsLeftUpdateTask(), 20L);
		BukkitUtil.runTaskTimer(new UpdateLocalTimeStringsTask(), 20L);

		// Cache player info before creating sidebars
		for (Player player : SurvivalGames.getInstance().getServer().getOnlinePlayers()) {
			SGSidebarManager.playerHealth.put(player.getUniqueId(), player.getHealth());
		}

		// Add sidebar for all online players
		for (Player player : SurvivalGames.getInstance().getServer().getOnlinePlayers()) {
			this.addSidebar(player, MPGPlayerManager.getMPGPlayer(player));
		}
	}

	public static Map<UUID, Double> getPlayerHealthCache() {
		return SGSidebarManager.playerHealth;
	}

	public static Map<UUID, DateTimeZone> getPlayerTimeZones() {
		return SGSidebarManager.playerTimeZones;
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Cache player info
		SGSidebarManager.playerHealth.put(player.getUniqueId(), player.getHealth());

		this.addSidebar(player, MPGPlayerManager.getMPGPlayer(player));
	}

	@EventHandler(priority = EventPriority.LASTEST, ignoreCancelled = true)
	public void onEntityDamageEvent(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Player) {
			Player player = (Player) entity;

			SGSidebarManager.playerHealth.put(player.getUniqueId(), player.getHealth() - event.getFinalDamage());
		} else if (CombatTagPlugin.getInstance().isCombatLogger(entity)) {
			LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getCombatLoggerFromEntity(entity);

			SGSidebarManager.playerHealth.put(loggerNPC.getUUID(), loggerNPC.getEntity().getHealth() - event.getFinalDamage());
		}
	}

	@EventHandler(priority = EventPriority.LASTEST, ignoreCancelled = true)
	public void onEntityRegainHealthEvent(EntityRegainHealthEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Player) {
			Player player = (Player) entity;

			SGSidebarManager.playerHealth.put(player.getUniqueId(), player.getHealth() + event.getAmount());
		} else if (CombatTagPlugin.getInstance().isCombatLogger(entity)) {
			LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getCombatLoggerFromEntity(entity);

			SGSidebarManager.playerHealth.put(loggerNPC.getUUID(), loggerNPC.getEntity().getHealth() + event.getAmount());
		}
	}

	private void addSidebar(final Player player, final MPGPlayer mpgPlayer) {
		// Create sidebar
		SidebarAPI.addSidebarItem(player, SGSidebarManager.localTimeText);

		// Display local time
		final DateTimeZone dateTimeZone = SGSidebarManager.playerTimeZones.get(player.getUniqueId());
		SidebarAPI.addSidebarItem(player, new SidebarItem(10) {
			@Override
			public String getText() {
				return SGSidebarManager.timeZoneStrings.get(dateTimeZone);
			}
		});

		SidebarAPI.addSidebarItem(player, new SidebarItem(20) {
			@Override
			public String getText() {
				return ChatColor.GREEN + "Game Time: " + ChatColor.WHITE + GameTimeTask.getInstance().getGameTime();
			}
		});

		SidebarAPI.addSidebarItem(player, SGSidebarManager.serverText);

		SidebarAPI.addSidebarItem(player, SGSidebarManager.serverNameText);

		SidebarAPI.addSidebarItem(player, SGSidebarManager.spacer33);

		// Only show teammates health if they're a player in the game and if this is a team game
		if (MPG.GAME_TYPE == MPG.GameType.PARTY && mpgPlayer.getTeam() != null) {
			SidebarAPI.addSidebarItem(player, new SidebarItem(35) {
				@Override
				public String getText() {
					// Get teammate's UUID
					UUID uuid2 = mpgPlayer.getTeam().getUUIDs().get(0);
					if (player.getUniqueId().equals(uuid2)) uuid2 = mpgPlayer.getTeam().getUUIDs().get(1); // .equals() needed

					MPGPlayer teammateMPGPlayer = MPGPlayerManager.getMPGPlayer(uuid2);

					String usernameString = ChatColor.GOLD + teammateMPGPlayer.getUsername() + " ";

					// Set suffix
					if (teammateMPGPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
						Double d = SGSidebarManager.playerHealth.get(uuid2);

						// Just in case player hasn't logged in yet or something
						if (d == null) d = 20D;

						double health = Math.ceil(d) / 2D;
						String heartsLeft;

						if (health >= 6.5) {
							heartsLeft = ChatColor.GREEN + "(" + health + " " + MessageUtil.HEART_WITH_COLOR + ChatColor.GREEN + ")";
						} else if (health >= 3.5) {
							heartsLeft = ChatColor.YELLOW + "(" + health + " " + MessageUtil.HEART_WITH_COLOR + ChatColor.YELLOW + ")";
						} else {
							heartsLeft = ChatColor.RED + "(" + health + " " + MessageUtil.HEART_WITH_COLOR + ChatColor.RED + ")";
						}

						return usernameString + heartsLeft;
					} else if (teammateMPGPlayer.getState() == MPGPlayer.PlayerState.DC) {
						return usernameString + ChatColor.DARK_GRAY + "(DC)";
					} else if (teammateMPGPlayer.getState().ordinal() >= MPGPlayer.PlayerState.DEAD.ordinal()) {
						String heartsLeft = ChatColor.RED + "(0 " + MessageUtil.HEART_WITH_COLOR + ChatColor.RED + ")";

						return usernameString + heartsLeft;
					}

					return "ERROR";
				}
			});

			SidebarAPI.addSidebarItem(player, SGSidebarManager.spacer35);
		}

		SidebarAPI.addSidebarItem(player, new SidebarItem(40) {
			@Override
			public String getText() {
				Integer kills = SGSidebarManager.playerKills.get(player.getUniqueId());
				if (kills == null) kills = 0;

				return ChatColor.GREEN + "Your Kills: " + ChatColor.WHITE + kills;
			}
		});

		if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
			SidebarAPI.addSidebarItem(player, new SidebarItem(45) {
				@Override
				public String getText() {
					// For spectators
					if (mpgPlayer.getState() != MPGPlayer.PlayerState.PLAYER) return ChatColor.GREEN + "Team Kills: " + ChatColor.WHITE + "0";

					Integer kills = SGSidebarManager.teamKills.get(mpgPlayer.getTeam());
					if (kills == null) kills = 0;

					return ChatColor.GREEN + "Team Kills: " + ChatColor.WHITE + kills;
				}
			});
		}

		SidebarAPI.addSidebarItem(player, SGSidebarManager.spacer50);

		if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
			SidebarAPI.addSidebarItem(player, new SidebarItem(60) {
				@Override
				public String getText() {
					return ChatColor.GREEN + "Teams Left: " + ChatColor.WHITE + SGSidebarManager.teamsLeft;
				}
			});
		}

		SidebarAPI.addSidebarItem(player, new SidebarItem(70) {
			@Override
			public String getText() {
				return ChatColor.GREEN + "Players Left: " + ChatColor.WHITE + SGSidebarManager.playersLeft;
			}
		});


		SidebarAPI.addSidebarItem(player, SGSidebarManager.spacer90);

		SidebarAPI.addSidebarItem(player, SGSidebarManager.badlionText);
	}

	private class PlayersTeamsLeftUpdateTask extends BukkitRunnable {

		@Override
		public void run() {
			int teamsLeft = 0;
			for (MPGTeam mpgTeam : MPGTeamManager.getAllMPGTeams()) {
				boolean hasAlivePlayers = false;
				for (UUID uuid : mpgTeam.getUUIDs()) {
					MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(uuid);

					if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER || mpgPlayer.getState() == MPGPlayer.PlayerState.DC) {
						hasAlivePlayers = true;
						break;
					}
				}

				if (hasAlivePlayers) {
					teamsLeft++;
				}
			}

			SGSidebarManager.teamsLeft = teamsLeft;
			SGSidebarManager.playersLeft = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER).size();
		}

	}

	private class UpdateLocalTimeStringsTask extends BukkitRunnable  {

		private final DateTimeFormatter formatter = DateTimeFormat.forPattern("hh:mm:ss a, ");

		@Override
		public void run() {
			SGSidebarManager.timeZoneStrings.clear();

			DateTime now = DateTime.now();

			for (DateTimeZone timeZone : SGSidebarManager.playerTimeZones.values()) {
				if (!SGSidebarManager.timeZoneStrings.containsKey(timeZone)) {
					// JODA is TRASH
					SGSidebarManager.timeZoneStrings.put(timeZone, "  " + now.toDateTime(timeZone).toString(this.formatter) + timeZone.toTimeZone().getDisplayName(false, TimeZone.SHORT, Locale.ENGLISH));
				}
			}
		}

	}

}
