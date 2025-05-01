package net.badlion.survivalgames;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.commands.StatsCommand;
import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.tasks.MatchmakingMCPListener;
import net.badlion.survivalgames.gamemodes.ClassicGamemode;
import net.badlion.survivalgames.gamemodes.TreasureHuntGamemode;
import net.badlion.survivalgames.gamemodes.UHCGamemode;
import net.badlion.survivalgames.inventories.SGStatsInventory;
import net.badlion.survivalgames.listeners.AlivePlayerListener;
import net.badlion.survivalgames.listeners.DeathMatchListener;
import net.badlion.survivalgames.listeners.GlobalListener;
import net.badlion.survivalgames.listeners.MPGListener;
import net.badlion.survivalgames.listeners.MiniStatsListener;
import net.kohi.sidebar.SidebarAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.UUID;

public class SurvivalGames extends JavaPlugin {

	public static Gamemode CLASSIC_GAMEMODE;
	public static Gamemode TREASURE_HUNT_GAMEMODE;
	public static Gamemode UHC_GAMEMODE;

    private static SurvivalGames plugin;

    public SurvivalGames() {
        SurvivalGames.plugin = this;

	    // Configure ministats
	    MiniStats.SEASON = 2;
	    MiniStats.TAG = "SG";
	    MiniStats.TABLE_NAME = "sg_s2_ministats";

	    MPG.MPG_GAME_NAME = "SurvivalGames";

	    MPG.MPG_PREFIX = ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.DARK_AQUA + "BadlionSG" + ChatColor.GOLD + "" + ChatColor.BOLD + "] ";

	    // Has deathmatch
	    MPG.HAS_DEATHMATCH = true;

	    // Set matchmaking to true
	    MPG.USES_MATCHMAKING = true;

	    // Set map voting to true
	    MPG.USES_MAP_VOTING = true;

	    // Implement the MPG configurator
	    MatchmakingMCPListener.mpgConfigurator = new MatchmakingMCPListener.MPGConfigurator() {
		    @Override
		    public void configureGame(MPG.GameType gameType, Integer playersPerTeam) {
			    // Configure game
			    MPG.GAME_TYPE = gameType.setup(true, true, false, true, false, false);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_PLAYERS.name(), 32);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.START_COUNTDOWN_SECONDS.name(), 30);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.USES_KITS.name(), false);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.HEALTH_UNDER_NAME.name(), false);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DISGUISE_PLAYERS_DURING_COUNTDOWN.name(), true);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DEAD_ENTITY_ON_DEATH.name(), true);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DROP_ITEMS_ON_DEATH.name(), true);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.KILL_COMBAT_LOGGER_ON_TIMEOUT.name(), true);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_DISCONNECT_LENGTH.name(), 30);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_NUM_OF_DISCONNECTS.name(), 2);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.NUM_OF_PLAYERS_FOR_DEATH_MATCH.name(), 4);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DEATH_MATCH_START_TIME.name(), 900); // Seconds
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DEATH_MATCH_TELEPORT_COUNTDOWN_TIME.name(), 30);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.REBOOT_ON_GAME_END.name(), true);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.SPECTATOR_ON_DEATH.name(), true);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DISABLE_SPECTATOR_ON_JOIN.name(), false);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY.name(), true);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.SPECTATOR_ON_LOGIN_AFTER_START.name(), true);

			    // Setup configuration for teams
			    if (gameType == MPG.GameType.PARTY) {
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.PLAYERS_PER_TEAM.name(), playersPerTeam);
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TEAM_NUMBERS.name(), false);
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.COLOR_NAME_TEAM_PREFIXES.name(), false);
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TEAM_MANAGEMENT.name(), false);
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TEAM_FRIENDLY_FIRE.name(), false);
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TEAM_NAME_TAGS.name(), true);
			    }

			    // Call setup game manually
			    MPG.getInstance().setupGame();

			    // Set deathmatch start time since it uses config values
			    ((SGGame) MPG.getInstance().getMPGGame()).setDeathmatchStartTime();
		    }
	    };

	    // Implement the MPG player creator
	    MatchmakingMCPListener.mpgPlayerCreator = new MatchmakingMCPListener.MPGPlayerCreator() {
		    @Override
		    public MPGPlayer createMPGPlayer(final UUID uuid, String username) {
			    // Grab player's ratings from database
			    SurvivalGames.this.getSGGame().getDBUserRatings(uuid);

			    return new SGPlayer(uuid, username);
		    }
	    };

	    // Implement the MPG server getter
	    MatchmakingMCPListener.mpgLobbyServerSender = new MatchmakingMCPListener.MPGLobbyServerSender() {
		    @Override
		    public void sendPlayersToLobby(final List<Player> players) {
			    JSONObject payload = new JSONObject();

			    payload.put("region", Gberry.serverRegion.name().toLowerCase());
			    payload.put("gamemode", "sg");

			    JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.MATCHMAKING_GET_LOBBIES, payload);
			    JSONArray lobbyList = (JSONArray) response.get("lobbies");

			    JSONObject bestLobby = null;
			    for (Object o : lobbyList) {
				    JSONObject lobby = (JSONObject) o;
				    if (bestLobby == null || (long) lobby.get("online") < (long) bestLobby.get("online")) {
					    bestLobby = lobby;
				    }
			    }

			    if (bestLobby == null) {
				    for (Player player : players) {
					    player.sendMessage(ChatColor.RED + "No available lobbies right now.");
				    }
			    } else {
				    final String server = (String) bestLobby.get("name");
				    BukkitUtil.runTask(new Runnable() {
					    @Override
					    public void run() {
						    for (Player player : players) {
							    Gberry.sendToServer(player, server);
						    }
					    }
				    });
			    }
		    }
	    };

	    // Initialize all gamemodes
	    SurvivalGames.CLASSIC_GAMEMODE = new ClassicGamemode();
	    SurvivalGames.TREASURE_HUNT_GAMEMODE = new TreasureHuntGamemode();
	    SurvivalGames.UHC_GAMEMODE = new UHCGamemode();
    }

    @Override
    public void onEnable() {
	    Gberry.loggingTags.add("NAMETAGS");

	    // Set sidebar title
	    SidebarAPI.setSidebarTitle(ChatColor.AQUA + "Badlion SG 2.0");

	    // Set the stats inventory
	    StatsCommand.getInstance().setStatsInventory(new SGStatsInventory());

	    // Set ministats player creator
	    MiniStats.getInstance().setMiniStatsPlayerCreator(new SGMiniStatsPlayer.SGMiniStatsPlayerCreator());

        this.getServer().getPluginManager().registerEvents(new AlivePlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new DeathMatchListener(), this);
        this.getServer().getPluginManager().registerEvents(new MPGListener(), this);
        this.getServer().getPluginManager().registerEvents(new GlobalListener(),this);
        this.getServer().getPluginManager().registerEvents(new MiniStatsListener(),this);
    }

    @Override
    public void onDisable() {

    }

	public Gamemode chooseRandomGamemode() {
		// Initialize event listeners
		this.getServer().getPluginManager().registerEvents(SurvivalGames.CLASSIC_GAMEMODE, this);

		return SurvivalGames.CLASSIC_GAMEMODE;

		/*Gamemode gamemode;

		// 80% chance for classic
		if (Math.random() > 0.20D) {
			gamemode = SurvivalGames.CLASSIC_GAMEMODE;
		} else {
			// 50% chance for UHC and 50% chance for Treasure Hunt
			if (Math.random() > 0.50D) {
				gamemode = SurvivalGames.UHC_GAMEMODE;
			} else {
				gamemode = SurvivalGames.TREASURE_HUNT_GAMEMODE;
			}
		}

		// Initialize event listeners
		this.getServer().getPluginManager().registerEvents(gamemode, this);

		return gamemode;*/
	}

	public static SurvivalGames getInstance() {
		return SurvivalGames.plugin;
	}

    public SGGame getSGGame() {
        return (SGGame) MPG.getInstance().getMPGGame();
    }

}
