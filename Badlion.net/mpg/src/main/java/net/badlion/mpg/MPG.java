package net.badlion.mpg;

import net.badlion.common.Configurator;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gpermissions.GPermissions;
import net.badlion.ministats.MiniStats;
import net.badlion.mpg.bukkitevents.MPGServerStateChangeEvent;
import net.badlion.mpg.commands.ConfigMapCommand;
import net.badlion.mpg.commands.StatsCommand;
import net.badlion.mpg.commands.TeamCommand;
import net.badlion.mpg.commands.TeleCommand;
import net.badlion.mpg.commands.VoteCommand;
import net.badlion.mpg.commands.WhitelistCommand;
import net.badlion.mpg.inventories.SkullPlayerInventory;
import net.badlion.mpg.inventories.SpectatorInventory;
import net.badlion.mpg.listeners.DisguisedListener;
import net.badlion.mpg.listeners.GlobalListener;
import net.badlion.mpg.listeners.LobbyListener;
import net.badlion.mpg.listeners.SpectatorListener;
import net.badlion.mpg.managers.MPGKitManager;
import net.badlion.mpg.managers.MPGMapManager;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGRespawnManager;
import net.badlion.mpg.tasks.MatchmakingMCPListener;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MPG extends JavaPlugin {

	// Needs to be set in the constructor of the plugin implementing
	public static String MPG_PREFIX;
	public static String MPG_GAME_NAME = "MPG";

	// Start Plugin Config Options - Should be turned on/off in the constructor ideally
	public enum GameType {
		FFA,
		PARTY;

		public GameType setup(boolean allowDisconnects, boolean allowDisguising, boolean allowRespawning,
		                      boolean allowSpectating, boolean allowWhitelisting, boolean usesVoting) {
			if (this == FFA) {
				MPG.getInstance().createSoloConfigurator();
			} else if (this == PARTY) {
				MPG.getInstance().createTeamConfigurator();
			}

			if (allowDisconnects) {
				MPG.ALLOW_DISCONNECTS = true;

				MPG.getInstance().getConfigurator().addNewBooleanOption(ConfigFlag.KILL_COMBAT_LOGGER_ON_TIMEOUT.name(), ChatColor.GOLD + "Kill Combat Logger On Timeout", null, true);
				MPG.getInstance().getConfigurator().addNewIntegerOption(ConfigFlag.MAX_DISCONNECT_LENGTH.name(), ChatColor.GOLD + "Max Disconnect Length", null, true, 5, 270); // Seconds
				MPG.getInstance().getConfigurator().addNewIntegerOption(ConfigFlag.MAX_NUM_OF_DISCONNECTS.name(), ChatColor.GOLD + "Max Number Of Disconnects", null, true, 1, Integer.MAX_VALUE);
			}

			if (allowDisguising) {
				MPG.ALLOW_DISGUISING = true;
			}

			if (allowRespawning) {
				MPG.ALLOW_RESPAWNING = true;

				MPG.getInstance().getConfigurator().addNewIntegerOption(ConfigFlag.RESPAWN_TIME.name(), ChatColor.GOLD + "Respawn Time", null, true, 0, 30); // Seconds
				MPG.getInstance().getConfigurator().addNewIntegerOption(ConfigFlag.RESPAWN_RESISTANCE_TIME.name(), ChatColor.GOLD + "Respawn Resistance Time", null, true, 0, 15); // Seconds
			}

			if (allowSpectating) {
				MPG.ALLOW_SPECTATING = true;

				if (!MPG.USES_MATCHMAKING) {
					MPG.getInstance().getConfigurator().addNewBooleanOption(ConfigFlag.ALLOW_LEAVE_SPECTATOR.name(), ChatColor.GOLD + "Allow Leave Spectator", null, true);
				}

				MPG.getInstance().getConfigurator().addNewBooleanOption(ConfigFlag.SPECTATOR_ON_DEATH.name(), ChatColor.GOLD + "Spectator On Respawn", null, true);
				MPG.getInstance().getConfigurator().addNewBooleanOption(ConfigFlag.DISABLE_SPECTATOR_ON_JOIN.name(), ChatColor.GOLD + "Disable Spectator On Join", null, true);
				MPG.getInstance().getConfigurator().addNewBooleanOption(ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY.name(), ChatColor.GOLD + "Use Skull Spectator Inventory", null, true);
				MPG.getInstance().getConfigurator().addNewBooleanOption(ConfigFlag.SPECTATOR_ON_LOGIN_AFTER_START.name(), ChatColor.GOLD + "Spectator On Login After Start", null, true);
			}

			if (allowWhitelisting) {
				MPG.ALLOW_WHITELISTING = true;
			}

			if (usesVoting) {
				MPG.USES_VOTING = true;

				MPG.getInstance().getConfigurator().addNewIntegerOption(ConfigFlag.VOTING_TIME.name(), ChatColor.GOLD + "Voting Time", null, true, 10, 60); // Seconds
				MPG.getInstance().getConfigurator().addNewIntegerOption(ConfigFlag.NUM_OF_VOTE_CHOICES.name(), ChatColor.GOLD + "Number Of Vote Choices", null, true, 2, 5);
				MPG.getInstance().getConfigurator().addNewBooleanOption(ConfigFlag.CAN_VOTE_FOR_LAST_WINNER.name(), ChatColor.GOLD + "Can Vote For Last Winner", null, false);
				MPG.getInstance().getConfigurator().addNewOption(ConfigFlag.VOTE_TYPE.name(), ChatColor.GOLD + "Vote Type", null, true,
						new Object[]{VoteCommand.VoteType.KIT, VoteCommand.VoteType.MAP});
				MPG.getInstance().getConfigurator().addNewOption(ConfigFlag.VOTE_SELECTION_METHOD.name(), ChatColor.GOLD + "Vote Selection Method", null, true,
						new Object[]{VoteCommand.VoteSelectionMethod.MAJORITY_VOTE, VoteCommand.VoteSelectionMethod.PERCENTAGE_VOTE});
			}

			// Safety check
			if (MPG.USES_MATCHMAKING) {
				if (usesVoting) throw new RuntimeException("Error using voting and matchmaking for MPG");
			}

			return this;
		}

	}

	public static GameType GAME_TYPE;

	public static boolean MANUAL_LOAD = false;

	public static boolean USES_VOTING = false;
	public static boolean USES_MATCHMAKING = false;
	public static boolean USES_MAP_VOTING = false;
	public static boolean HAS_DEATHMATCH = false;

	public static boolean ALLOW_DISCONNECTS = false;
	public static boolean ALLOW_DISGUISING = false;
	public static boolean ALLOW_SPECTATING = false;
	public static boolean ALLOW_RESPAWNING = false;
	public static boolean ALLOW_WHITELISTING = false;

	public static final int NUM_OF_DEFAULT_VOTES = 1;
	public static final int NUM_OF_DONATOR_VOTES = 2;
	public static final int NUM_OF_DONATOR_PLUS_VOTES = 3;
	public static final int NUM_OF_LION_VOTES = 4;
	public static final int NUM_OF_LION_PLUS_VOTES = 5;
	public static final int NUM_OF_OP_VOTES = 1000; // Used for testing

	public static final List<Object> VOTE_OBJECTS = new ArrayList<>();

    private static MPG plugin;

	private static Map<String, UUID> uuids = new HashMap<>();
    private static Map<UUID, String> usernames = new HashMap<>();

    private MPGGame mpgGame;

	// NOTE: Don't set this if there is no lobby
	private Location lobbySpawnLocation;

    private ServerState state = ServerState.LOADING;

	private Set<String> whitelistedPlayers = new HashSet<>();

	private final Configurator configurator = new Configurator();

    public enum ServerState {
        LOADING, LOBBY, GAME
    }

    public MPG() {
        MPG.plugin = this;
    }

    @Override
    public void onEnable() {
	    SmellyInventory.initialize(this, true);

        MiniStats.DISABLE_PLAYER_LISTENER_DEATHS = true;

        if (MPG.MPG_GAME_NAME.equals("MPG")) {
            throw new RuntimeException("MPG_GAME_NAME needs to be set");
        }

	    Gberry.enableAsyncDelayedLoginEvent = true; // Used for kits

	    SmellyInventory.initialize(this, false);

	    this.getCommand("configmap").setExecutor(new ConfigMapCommand());
	    this.getCommand("stats").setExecutor(new StatsCommand());
	    this.getCommand("tele").setExecutor(new TeleCommand());

	    if (!MPG.MANUAL_LOAD) {
		    if (MPG.USES_MATCHMAKING) {
			    // Start matchmaking MCP listener in 5 seconds
			    BukkitUtil.runTaskLater(new Runnable() {
				    @Override
				    public void run() {
					    MPG.this.getServer().getPluginManager().registerEvents(new MatchmakingMCPListener(), MPG.this);
				    }
			    }, 100L);
		    } else {
			    // Setup the game only if this server doesn't use matchmaking,
			    // servers that use matchmaking set it up later in the keep alive task
			    this.setupGame();
		    }

		    this.load(true);
	    }
    }

    @Override
    public void onDisable() {
	    // Send shutdown for matchmaking servers if this server isn't ending normally after a game
	    /*if (MPG.USES_MATCHMAKING && (this.mpgGame == null || this.mpgGame.getGameState() != MPGGame.GameState.POST_GAME)) {
		    JSONObject payload = new JSONObject();

		    payload.put("server_name", Gberry.serverName);
		    payload.put("server_region", Gberry.serverRegion.name().toLowerCase());
		    payload.put("server_type", Gberry.serverType.getInternalName());

		    System.out.println("onDisable() SHUTDOWN: " + payload);

		    JSONObject response;
		    do {
			    response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.MATCHMAKING_DEFAULT_SHUTDOWN_SERVER, payload);
		    } while (response == null);
	    }*/
    }

    public void load(boolean loadOnBoot) {
	    if (loadOnBoot) {
		    // GAME depends on MPG depends on WorldRotator
		    // MPG fires event that GAME listens to but if
		    // event is fired off when MPG loads then GAME
		    // won't be enabled at that point
		    new BukkitRunnable() {
			    public void run() {
				    MPGMapManager.initialize();

				    MPG.getInstance().setServerState(ServerState.LOBBY);
			    }
		    }.runTaskLater(MPG.plugin, 1L);
	    } else {
		    MPGMapManager.initialize();

		    MPG.getInstance().setServerState(ServerState.LOBBY);
	    }
    }

	public void setupGame() {
		// Make sure everything is configured
		if (!MPG.getInstance().isEverythingConfigured()) {
			List<Configurator.Option> options = MPG.getInstance().getConfigurator().unconfiguredOptions();
			throw new Configurator.ConfiguratorNotConfiguredException("Not everything was configured correctly in MPG: " + options);
		}

		MPGPlayerManager.initialize();

		if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.USES_KITS)) {
			// Register the kit listener
			this.getServer().getPluginManager().registerEvents(new MPGKitManager(), this);
		}

		if (MPG.ALLOW_RESPAWNING) {
			new MPGRespawnManager();
		}

		if (MPG.USES_VOTING) {
			MPG.getInstance().getCommand("vote").setExecutor(new VoteCommand());
		}

		if (MPG.ALLOW_WHITELISTING) {
			MPG.getInstance().getCommand("wl").setExecutor(new WhitelistCommand());
		}

		if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
			MPG.getInstance().getCommand("team").setExecutor(new TeamCommand());
		}

		// Don't register the disguise listener if matchmaking is enabled,
		// the matchmaker will take care of disguising players
		if (!MPG.USES_MATCHMAKING) {
			MPG.getInstance().getServer().getPluginManager().registerEvents(new DisguisedListener(), MPG.getInstance());
		}

		if (MPG.ALLOW_SPECTATING) {
			MPG.getInstance().getServer().getPluginManager().registerEvents(new SpectatorListener(), MPG.getInstance());

			SkullPlayerInventory.initialize();
			SpectatorInventory.initialize();
		}

		MPG.getInstance().getServer().getPluginManager().registerEvents(new GlobalListener(), MPG.getInstance());

		// Disable cosmetics
		Cosmetics.getInstance().disallowCosmetics();
		Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.ARROW_TRAIL, true);

		if (!MPG.USES_MATCHMAKING) {
			MPG.getInstance().getServer().getPluginManager().registerEvents(new LobbyListener(), MPG.getInstance());
		}

		MiniStats.TYPE = MPG.GAME_TYPE.name().toLowerCase();

		// Are we disguising players during countdown in a team game?
		/*if (MPG.GAME_TYPE == GameType.PARTY && this.getBooleanOption(ConfigFlag.DISGUISE_PLAYERS_DURING_COUNTDOWN)) {
			ProtocolScheduler.addHook(new ProtocolOutHook() {
				@Override
				public Object handlePacket(Player receiver, Object packet) {
					// Only process packets during the game countdown
					if (MPG.getInstance().getMPGGame().getGameState() != MPGGame.GameState.GAME_COUNTDOWN) return packet;

					if (TinyProtocolReferences.tabPacketClass.isInstance(packet)) {
						MPGPlayer mpgPlayer = getMPGPlayer(receiver);

						if (receiver.getClientVersion().ordinal() <= Player.CLIENT_VERSION.V1_7_6.ordinal()) {
							String name = TinyProtocolReferences.tabPacketName.get(packet);

							for (UUID teamUUID : mpgPlayer.getTeam().getUUIDs()) {
								MPGPlayer teamMPGPlayer = MPGPlayerManager.getMPGPlayer(teamUUID);
								if (name.equals(teamMPGPlayer.getOldDisguisedName())) {
									return null;
								}
							}
						} else {
							Object gameProfile = TinyProtocolReferences.tabPacketGameProfile.get(packet);

							if (gameProfile != null) {
								UUID uuid = TinyProtocolReferences.gameProfileUUID.get(gameProfile);
								String name = TinyProtocolReferences.gameProfileName.get(gameProfile);

								// If UUID is that of a team member's but the name is the same, don't send this packet
								for (UUID teamUUID : mpgPlayer.getTeam().getUUIDs()) {
									MPGPlayer teamMPGPlayer = MPGPlayerManager.getMPGPlayer(teamUUID);
									if (uuid == teamUUID && !name.equals(teamMPGPlayer.getUsername())) {
										return null;
									}
								}
							}
						}
					}

					return packet;
				}

				@Override
				public ProtocolPriority getPriority() {
					return ProtocolPriority.HIGHEST;
				}

			});
		}*/
	}

    public void prepPlayer(Player player) {
	    player.getInventory().clear();
	    player.getInventory().setArmorContents(null);

        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExhaustion(0);

        player.setGameMode(GameMode.SURVIVAL);
    }

	public void addMuteBanPerms(Player player) {
		String modPermission = "badlion.staff";
		String trialPermission = "badlion.staff";

		switch (Gberry.serverType) {
			case CTF:
				modPermission = "badlion.sgmod";
				trialPermission = "badlion.sgtrial";
				break;
			case FFA:
				modPermission = "badlion.kitmod";
				trialPermission = "badlion.kittrial";
				break;
			case SG:
				modPermission = "badlion.sgmod";
				trialPermission = "badlion.sgtrial";
				break;
			case SKYWARS:
				modPermission = "badlion.sgmod";
				trialPermission = "badlion.sgtrial";
				break;
			case TDM:
				modPermission = "badlion.kitmod";
				trialPermission = "badlion.kittrial";
				break;
            case UHCMEETUP:
                modPermission = "badlion.kitmod";
                trialPermission = "badlion.kittrial";
                break;
            case UHC:
                modPermission = "badlion.uhc";
                trialPermission = "badlion.uhctrial";
                break;
		}

		if (player.hasPermission(modPermission) || player.hasPermission("badlion.senior")) {
			GPermissions.giveModPermissions(player);
		} else if (player.hasPermission(trialPermission) || player.hasPermission("badlion.mod")) {
			GPermissions.giveTrialPermissions(player);
		}
	}

	public enum ConfigFlag {
		PLAYERS_TO_START, MAX_PLAYERS, START_COUNTDOWN_SECONDS, GAME_TIME_LIMIT, SCORE_LIMIT, USES_KITS,  DROP_ITEMS_ON_DEATH,

		KICK_NON_DONATORS_IF_FULL, TIME_BETWEEN_GAMES, REBOOT_ON_GAME_END,

		DEATH_MATCH_START_TIME, NUM_OF_PLAYERS_FOR_DEATH_MATCH, DEATH_MATCH_TELEPORT_COUNTDOWN_TIME,

		MAX_NUM_OF_DISCONNECTS, MAX_DISCONNECT_LENGTH,

		RESPAWN_TIME, RESPAWN_RESISTANCE_TIME,

		HEALTH_UNDER_NAME, DISGUISE_PLAYERS_DURING_COUNTDOWN, DEAD_ENTITY_ON_DEATH, KILL_COMBAT_LOGGER_ON_TIMEOUT,

		SPECTATOR_ON_DEATH, ALLOW_LEAVE_SPECTATOR, DISABLE_SPECTATOR_ON_JOIN, USE_SKULL_SPECTATOR_INVENTORY, SPECTATOR_ON_LOGIN_AFTER_START,

		VOTING_TIME, NUM_OF_VOTE_CHOICES, CAN_VOTE_FOR_LAST_WINNER, VOTE_TYPE, VOTE_SELECTION_METHOD,

		TEAM_NUMBERS, COLOR_NAME_TEAM_PREFIXES, PLAYERS_PER_TEAM, TEAM_MANAGEMENT, TEAM_FRIENDLY_FIRE, TEAM_NAME_TAGS
	}

	private void createCommonConfigurator() {
		this.configurator.addNewIntegerOption(ConfigFlag.PLAYERS_TO_START.name(), ChatColor.GOLD + "Players To Start", null, false, 2, 64);
		this.configurator.addNewIntegerOption(ConfigFlag.MAX_PLAYERS.name(), ChatColor.GOLD + "Max Players", null, true, 2, Integer.MAX_VALUE);
		this.configurator.addNewIntegerOption(ConfigFlag.START_COUNTDOWN_SECONDS.name(), ChatColor.GOLD + "Start Countdown", null, true, 5, 120); // In seconds
		this.configurator.addNewIntegerOption(ConfigFlag.GAME_TIME_LIMIT.name(), ChatColor.GOLD + "Time Limit", null, false, 60, 1800); // In seconds
		this.configurator.addNewIntegerOption(ConfigFlag.SCORE_LIMIT.name(), ChatColor.GOLD + "Score Limit", null, false, 3, 1000);
		this.configurator.addNewBooleanOption(ConfigFlag.USES_KITS.name(), ChatColor.GOLD + "Uses Kits", null, true);

		this.configurator.addNewBooleanOption(ConfigFlag.HEALTH_UNDER_NAME.name(), ChatColor.GOLD + "Health Under Name", null, true);
		this.configurator.addNewBooleanOption(ConfigFlag.DEAD_ENTITY_ON_DEATH.name(), ChatColor.GOLD + "Dead Entity On Death", null, true);
		this.configurator.addNewBooleanOption(ConfigFlag.DROP_ITEMS_ON_DEATH.name(), ChatColor.GOLD + "Drop Items On Death", null, true);

		if (MPG.USES_MATCHMAKING) {
			this.configurator.addNewBooleanOption(ConfigFlag.DISGUISE_PLAYERS_DURING_COUNTDOWN.name(), ChatColor.GOLD + "Health Under Name", null, true);
		}

		if (MPG.HAS_DEATHMATCH) {
			this.configurator.addNewIntegerOption(ConfigFlag.DEATH_MATCH_START_TIME.name(), ChatColor.GOLD + "Deathmatch Start Time", null, false, 30, 3600);
			this.configurator.addNewIntegerOption(ConfigFlag.NUM_OF_PLAYERS_FOR_DEATH_MATCH.name(), ChatColor.GOLD + "Number Players For Deathmatch", null, false, 2, 16);
			this.configurator.addNewIntegerOption(ConfigFlag.DEATH_MATCH_TELEPORT_COUNTDOWN_TIME.name(), ChatColor.GOLD + "Deathmatch Teleport Countdown Time", null, false, 5, 180);
		}

		this.configurator.addNewBooleanOption(ConfigFlag.KICK_NON_DONATORS_IF_FULL.name(), ChatColor.GOLD + "Kick Non-Donators If Full", null, false);
		this.configurator.addNewIntegerOption(ConfigFlag.TIME_BETWEEN_GAMES.name(), ChatColor.GOLD + "Time Between Games", null, false, 15, 60);
		this.configurator.addNewBooleanOption(ConfigFlag.REBOOT_ON_GAME_END.name(), ChatColor.GOLD + "Reboot On Game End", null, false);
	}

	private void createSoloConfigurator() {
		this.createCommonConfigurator();
	}

	private void createTeamConfigurator() {
		this.createCommonConfigurator();
		this.configurator.addNewBooleanOption(ConfigFlag.TEAM_NUMBERS.name(), ChatColor.GOLD + "Team Numbers", null, true);
		this.configurator.addNewBooleanOption(ConfigFlag.COLOR_NAME_TEAM_PREFIXES.name(), ChatColor.GOLD + "Color Name Team Prefixes", null, true);
		this.configurator.addNewIntegerOption(ConfigFlag.PLAYERS_PER_TEAM.name(), ChatColor.GOLD + "Players Per Team", null, true, 2, 32);
		this.configurator.addNewBooleanOption(ConfigFlag.TEAM_MANAGEMENT.name(), ChatColor.GOLD + "Team Management", null, true);
		this.configurator.addNewBooleanOption(ConfigFlag.TEAM_FRIENDLY_FIRE.name(), ChatColor.GOLD + "Team Friendly Fire", null, true);
		this.configurator.addNewBooleanOption(ConfigFlag.TEAM_NAME_TAGS.name(), ChatColor.GOLD + "Team Colored Name Tags", null, true);
	}

	public boolean isEverythingConfigured() {
	  	if (this.configurator.checkIfAllOptionsSet()) {
		    // Do some verification checks between different configuration options
		    if (MPG.ALLOW_RESPAWNING && this.getBooleanOption(ConfigFlag.SPECTATOR_ON_DEATH)) {
			    throw new Configurator.ConfigurationConflictException("Cannot allow respawning and set SPECTATOR_ON_DEATH to true");
		    }

		    if (!MPG.ALLOW_SPECTATING && this.getBooleanOption(ConfigFlag.SPECTATOR_ON_DEATH)) {
			    throw new Configurator.ConfigurationConflictException("Cannot disallow spectating and set SPECTATOR_ON_DEATH to true");
		    }

		    if (!MPG.ALLOW_SPECTATING && this.getBooleanOption(ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY)) {
			    throw new Configurator.ConfigurationConflictException("Cannot disallow spectating and set USE_SKULL_SPECTATOR_INVENTORY to true");
		    }

		    if (MPG.USES_MATCHMAKING && MatchmakingMCPListener.mpgLobbyServerSender == null) {
			    throw new Configurator.ConfigurationConflictException("MPG Server Getter is not set while USES_MATCHMAKING is true");
		    }

		    if (!MPG.USES_MATCHMAKING) {
			    if (this.getBooleanOption(ConfigFlag.KICK_NON_DONATORS_IF_FULL) == null) {
				    throw new Configurator.ConfigurationConflictException("KICK_NON_DONATORS_IF_FULL is not set while USES_MATCHMAKING is false");
			    } else if (this.getIntegerConfigOption(ConfigFlag.PLAYERS_TO_START) == null) {
				    throw new Configurator.ConfigurationConflictException("PLAYERS_TO_START is not set while USES_MATCHMAKING is false");
			    }

			    if (MPG.GAME_TYPE == GameType.PARTY && this.getIntegerConfigOption(ConfigFlag.MAX_PLAYERS) % this.getIntegerConfigOption(ConfigFlag.PLAYERS_PER_TEAM) != 0) {
				    throw new Configurator.ConfigurationConflictException("MAX_PLAYERS must be evenly divisible by PLAYERS_PER_TEAM");
			    }
		    }

		    if (this.getIntegerConfigOption(ConfigFlag.TIME_BETWEEN_GAMES) == null && this.getBooleanOption(ConfigFlag.REBOOT_ON_GAME_END) == null) {
			    throw new Configurator.ConfigurationConflictException("TIME_BETWEEN_GAMES or REBOOT_ON_GAME_END must be configured");
		    }

		    return true;
	    }

		return false;
	}

	public static MPG getInstance() {
		return MPG.plugin;
	}

    public MPGGame getMPGGame() {
        return this.mpgGame;
    }

    public void setMPGGame(MPGGame mpgGame) {
        this.mpgGame = mpgGame;
    }

	public void putUsername(UUID uuid, String username) {
		MPG.usernames.put(uuid, username);
	}

	public String getUsername(UUID uuid) {
		return MPG.usernames.get(uuid);
	}

	public void putUUID(UUID uuid, String username) {
		MPG.uuids.put(username.toLowerCase(), uuid);
	}

	public UUID getUUID(String username) {
		return MPG.uuids.get(username.toLowerCase());
	}

	public Location getLobbySpawnLocation() {
		return this.lobbySpawnLocation;
	}

	public void setLobbySpawnLocation(Location lobbySpawnLocation) {
		this.lobbySpawnLocation = lobbySpawnLocation;
	}

	public ServerState getServerState() {
		return this.state;
	}

	public void setServerState(ServerState state) {
		ServerState oldState = this.state;

		// Fire off event for plugins to load other stuff
		MPGServerStateChangeEvent event = new MPGServerStateChangeEvent(oldState, state);
		this.getServer().getPluginManager().callEvent(event);

		System.out.println("@@@@@@@@@@@ CHANGING SERVER STATE FROM " + this.state + " TO " + event.getNewState() + " ORIGINAL: " + state);

		this.state = event.getNewState();
	}

    public boolean isWhitelisted(String name) {
        return this.whitelistedPlayers.contains(name.toLowerCase());
    }

    public boolean addToWhitelist(String name) {
        return this.whitelistedPlayers.add(name.toLowerCase());
    }

    public boolean removeFromWhitelist(String name) {
        return this.whitelistedPlayers.remove(name.toLowerCase());
    }

	public Configurator getConfigurator() {
		return this.configurator;
	}

	public Object getConfigOption(ConfigFlag option) {
		return this.configurator.getOption(option.name()).getValue();
	}

	public Boolean getBooleanOption(ConfigFlag option) {
		Configurator.BooleanOption booleanOption = this.configurator.getBooleanOption(option.name());

		if (booleanOption == null) {
			return false;
		}

		return booleanOption.getValue();
	}

	public Integer getIntegerConfigOption(ConfigFlag option) {
		Configurator.IntegerOption integerOption = this.configurator.getIntegerOption(option.name());

		if (integerOption == null) {
			return -1;
		}

		return integerOption.getValue();
	}

}