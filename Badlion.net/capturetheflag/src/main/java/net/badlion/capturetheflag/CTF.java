package net.badlion.capturetheflag;

import net.badlion.capturetheflag.gamemodes.ClassicGamemode;
import net.badlion.capturetheflag.gamemodes.MultipleFlagsGamemode;
import net.badlion.capturetheflag.gamemodes.RandomFlagSpawnGamemode;
import net.badlion.capturetheflag.inventories.CTFStatsInventory;
import net.badlion.capturetheflag.listeners.CTFGameListener;
import net.badlion.capturetheflag.listeners.CTFLobbyListener;
import net.badlion.capturetheflag.listeners.GlobalListener;
import net.badlion.capturetheflag.listeners.MPGListener;
import net.badlion.capturetheflag.listeners.PreGameListener;
import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.ministats.MiniStats;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.commands.StatsCommand;
import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.tasks.MPGKeepAliveTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.UUID;

public class CTF extends JavaPlugin {

	public static Gamemode CLASSIC_GAMEMODE;
	public static Gamemode MULTIPLE_FLAGS_GAMEMODE;
	public static Gamemode RANDOM_FLAG_SPAWN_GAMEMODE;

	private static CTF instance;

	public CTF() {
		CTF.instance = this;

		// Configure ministats
		MiniStats.SEASON = 1;
		MiniStats.TAG = "CTF";
		MiniStats.TABLE_NAME = "ctf_ministats";

		MPG.MPG_GAME_NAME = "CaptureTheFlag";

		MPG.MPG_PREFIX = ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.DARK_AQUA + "BadlionCTF" + ChatColor.GOLD + "" + ChatColor.BOLD + "] ";

		// Has deathmatch (goes into deathmatch when it goes into the two overtime modes)
		MPG.HAS_DEATHMATCH = true;

		// Set matchmaking to true
		MPG.USES_MATCHMAKING = false;

		// Implement the MPG configurator
		MPGKeepAliveTask.mpgConfigurator = new MPGKeepAliveTask.MPGConfigurator() {
			@Override
			public void configureGame(MPG.GameType gameType) {
				// Configure game
				MPG.GAME_TYPE = gameType.setup(true, true, true, true, false, false);

				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_PLAYERS.name(), 32);

				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.START_COUNTDOWN_SECONDS.name(), 30);
				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.USES_KITS.name(), true); // TODO: Might need more implementation in MPG

				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.HEALTH_UNDER_NAME.name(), false);
				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DISGUISE_PLAYERS_DURING_COUNTDOWN.name(), true);
				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DEAD_ENTITY_ON_DEATH.name(), false);
				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DROP_ITEMS_ON_DEATH.name(), false);
				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.KILL_COMBAT_LOGGER_ON_TIMEOUT.name(), true);

				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_DISCONNECT_LENGTH.name(), 30);
				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_NUM_OF_DISCONNECTS.name(), 2); // TODO: MAYBE INTEGER.MAX_VALUE, DEPENDS ON GAMEPLAY DESIGN

				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.NUM_OF_PLAYERS_FOR_DEATH_MATCH.name(), -1);

				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DEATH_MATCH_START_TIME.name(), 900); // 15 min

				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.REBOOT_ON_GAME_END.name(), true);

				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.SPECTATOR_ON_DEATH.name(), false);
				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DISABLE_SPECTATOR_ON_JOIN.name(), true);
				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY.name(), true);
				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.SPECTATOR_ON_LOGIN_AFTER_START.name(), false);

				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.RESPAWN_TIME.name(), 0);
				MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.RESPAWN_RESISTANCE_TIME.name(), 5);

				// Setup configuration for teams
				if (gameType == MPG.GameType.PARTY) {                  // TODO: ???
					MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.PLAYERS_PER_TEAM.name(), 8);
					MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TEAM_NUMBERS.name(), false);
					MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.COLOR_NAME_TEAM_PREFIXES.name(), true);
					MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TEAM_MANAGEMENT.name(), false);
					MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TEAM_FRIENDLY_FIRE.name(), false);
				}

				// Call setup game manually
				MPG.getInstance().setupGame();
			}
		};

		// Implement the MPG server getter
		MPGKeepAliveTask.mpgLobbyServerSender = new MPGKeepAliveTask.MPGLobbyServerSender() {
			@Override
			public void sendPlayersToLobby(final List<Player> players) {
				String url = "http://" + GetCommon.getIpForDB() + ":9000/arena-get-lobbies/IVxbY9cf9e8Bsqp9UpJqQVgiLvWmhi1dPEFpcI1a";
				JSONObject data = new JSONObject();
				data.put("region", Gberry.serverRegion.name().toLowerCase());
				// TODO: IMPLEMENT FOR CTF
				try {
					JSONObject e = HTTPCommon.executePOSTRequest(url, data, Gberry.mcpTimeout);
					JSONArray lobbyList = (JSONArray) e.get("lobbies");
					JSONObject bestLobby = null;

					for (Object o : lobbyList) {
						JSONObject lobby = (JSONObject) o;
						if (bestLobby == null || (long) lobby.get("online") < (long) bestLobby.get("online")) {
							bestLobby = lobby;
						}
					}

					if (bestLobby == null) {
						for (Player player : players) {
							player.sendMessage(ChatColor.RED + "Season 13 is offline right now.");
						}
					} else {
						final String server = (String) bestLobby.get("name");
						new BukkitRunnable() {
							@Override
							public void run() {
								for (Player player : players) {
									Gberry.sendToServer(player, server);
								}
							}
						}.runTask(CTF.getInstance());
					}
				} catch (HTTPRequestFailException e) {
					Gberry.plugin.getLogger().info(e.getType().name());
					Gberry.plugin.getLogger().info(e.getResponseCode() + "");
					Gberry.plugin.getLogger().info(e.getResponse());
					for (Player player : players) {
						player.sendMessage(ChatColor.RED + "An error has occurred, please report this to an administrator!");
					}
				}
			}
		};

		// Initialize all gamemodes
		CTF.CLASSIC_GAMEMODE = new ClassicGamemode();
		CTF.MULTIPLE_FLAGS_GAMEMODE = new MultipleFlagsGamemode();
		CTF.RANDOM_FLAG_SPAWN_GAMEMODE = new RandomFlagSpawnGamemode();
	}

	@Override
	public void onEnable() {

		// Set the stats inventory
		StatsCommand.getInstance().setStatsInventory(new CTFStatsInventory());

		// Set ministats player creator
		MiniStats.getInstance().setMiniStatsPlayerCreator(new CTFMiniStatsPlayer.CTFMiniStatsPlayerCreator());

		// Register Listeners
		Bukkit.getPluginManager().registerEvents(new CTFGameListener(), this);
		Bukkit.getPluginManager().registerEvents(new CTFLobbyListener(), this);
		Bukkit.getPluginManager().registerEvents(new PreGameListener(), this);
		Bukkit.getPluginManager().registerEvents(new GlobalListener(), this);
		Bukkit.getPluginManager().registerEvents(new MPGListener(), this);
	}

	@Override
	public void onDisable() {

	}

	public static CTF getInstance() {
		return CTF.instance;
	}

	public CTFGame getCTFGame() {
		return (CTFGame) MPG.getInstance().getMPGGame();
	}

}

