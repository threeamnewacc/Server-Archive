package net.badlion.uhcmeetup;

import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.combattag.CombatTagPlugin;
import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.commands.StatsCommand;
import net.badlion.mpg.tasks.MatchmakingMCPListener;
import net.badlion.uhcmeetup.inventories.UHCMeetupStatsInventory;
import net.badlion.uhcmeetup.listeners.AlivePlayerListener;
import net.badlion.uhcmeetup.listeners.GlobalListener;
import net.badlion.uhcmeetup.listeners.MPGListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UHCMeetup extends JavaPlugin {

    private static UHCMeetup plugin;

    public UHCMeetup() {
	    UHCMeetup.plugin = this;

	    // Configure ministats
	    MiniStats.SEASON = 1;
	    MiniStats.TAG = "UHCM";
	    MiniStats.TABLE_NAME = "uhcmeetup_s1_ministats";

	    MPG.MPG_GAME_NAME = "UHCMeetup";

	    MPG.MPG_PREFIX = ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.DARK_AQUA + "UHCMeetup" + ChatColor.GOLD + "" + ChatColor.BOLD + "] ";

	    // Set manual load to true since we generate a world beforehand
	    MPG.MANUAL_LOAD = true;

	    // No deathmatch
	    MPG.HAS_DEATHMATCH = false;

	    // Set matchmaking to true
	    MPG.USES_MATCHMAKING = true;

	    // Implement the MPG configurator
	    MatchmakingMCPListener.mpgConfigurator = new MatchmakingMCPListener.MPGConfigurator() {
		    @Override
		    public void configureGame(MPG.GameType gameType, Integer playersPerTeam) {
			    // Configure game
			    MPG.GAME_TYPE = gameType.setup(true, true, false, true, false, false);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_PLAYERS.name(), 50);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.START_COUNTDOWN_SECONDS.name(), 30);

			    // Set this to false because we use the kit system in ArenaCommon
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.USES_KITS.name(), false);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.HEALTH_UNDER_NAME.name(), true);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DISGUISE_PLAYERS_DURING_COUNTDOWN.name(), true);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DEAD_ENTITY_ON_DEATH.name(), true);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DROP_ITEMS_ON_DEATH.name(), true);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.KILL_COMBAT_LOGGER_ON_TIMEOUT.name(), true);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_DISCONNECT_LENGTH.name(), 30);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_NUM_OF_DISCONNECTS.name(), 2);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TIME_BETWEEN_GAMES.name(), 30);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.REBOOT_ON_GAME_END.name(), true);

			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.SPECTATOR_ON_DEATH.name(), true);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DISABLE_SPECTATOR_ON_JOIN.name(), false);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY.name(), true);
			    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.SPECTATOR_ON_LOGIN_AFTER_START.name(), true);

			    // TODO: FFA UHC MEETUP ONLY AT THIS POINT
			    // Setup configuration for teams
			    if (gameType == MPG.GameType.PARTY) {  // TODO: PROB ENABLE TEAM #'s, MAYBE ENABLE FF?
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.PLAYERS_PER_TEAM.name(), playersPerTeam);
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TEAM_NUMBERS.name(), false);
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.COLOR_NAME_TEAM_PREFIXES.name(), false);
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TEAM_MANAGEMENT.name(), false);
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TEAM_FRIENDLY_FIRE.name(), false);
				    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.TEAM_NAME_TAGS.name(), true);
			    }

			    // Load the game now since we didn't do it in MPG onEnable()
			    // because we had to generate a world first
			    MPG.getInstance().load(false);

			    // Call setup game manually
			    MPG.getInstance().setupGame();
		    }
	    };

	    // Implement the MPG player creator
	    MatchmakingMCPListener.mpgPlayerCreator = new MatchmakingMCPListener.MPGPlayerCreator() {
		    @Override
		    public MPGPlayer createMPGPlayer(UUID uuid, String username) {
			    return new UHCMeetupPlayer(uuid, username);
		    }
	    };

	    // Implement the MPG server getter
	    MatchmakingMCPListener.mpgLobbyServerSender = new MatchmakingMCPListener.MPGLobbyServerSender() {
		    @Override
		    public void sendPlayersToLobby(final List<Player> players) {
			    String url = "http://" + GetCommon.getIpForDB() + ":9000/arena-get-lobbies/IVxbY9cf9e8Bsqp9UpJqQVgiLvWmhi1dPEFpcI1a";
			    JSONObject data = new JSONObject();
			    data.put("region", Gberry.serverRegion.name().toLowerCase());

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
					    }.runTask(UHCMeetup.getInstance());
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
    }

    @Override
    public void onEnable() {
	    // Set the stats inventory
	    StatsCommand.getInstance().setStatsInventory(new UHCMeetupStatsInventory());

	    // Set ministats player creator
	    MiniStats.getInstance().setMiniStatsPlayerCreator(new UHCMeetupMiniStatsPlayer.UHCMeetupMiniStatsPlayerCreator());

        this.getServer().getPluginManager().registerEvents(new AlivePlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new MPGListener(), this);
        this.getServer().getPluginManager().registerEvents(new GlobalListener(), this);

	    // Get rid of bad biomes
	    this.swapBiomes();

	    new UHCMeetupWorldGenerator().runTaskTimer(this, 20L, 20L);
    }

    @Override
    public void onDisable() {
	    World uhcworld = this.getServer().getWorld(UHCMeetupWorld.WORLD_NAME);

	    // Unload world
	    if (uhcworld != null) {
		    this.getServer().unloadWorld(uhcworld, false);
	    }

	    // Delete world
	    Gberry.deleteDirectory(new File(UHCMeetupWorld.WORLD_NAME));
    }

	private void swapBiomes() {
		// Swap all biomes with other biomes
		Bukkit.getServer().setBiomeBase(Biome.OCEAN, Biome.FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.RIVER, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.BEACH, Biome.TAIGA, 0);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE_HILLS, Biome.TAIGA, 0);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 0);
		Bukkit.getServer().setBiomeBase(Biome.DEEP_OCEAN, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.FOREST, 0);

		// Weird sub-biomes
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 128);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 128);
		Bukkit.getServer().setBiomeBase(Biome.SAVANNA, Biome.SAVANNA, 128);
		Bukkit.getServer().setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.DESERT, 128);

		// LIMITED threshold biomes
		Bukkit.getServer().setBiomeBase(Biome.FOREST_HILLS, Biome.FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.BIRCH_FOREST_HILLS, Biome.FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.BIRCH_FOREST_HILLS, Biome.FOREST, 128);
		Bukkit.getServer().setBiomeBase(Biome.BIRCH_FOREST_HILLS_MOUNTAINS, Biome.FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.BIRCH_FOREST_MOUNTAINS, Biome.FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.TAIGA, Biome.BIRCH_FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.TAIGA, Biome.BIRCH_FOREST, 128);
		Bukkit.getServer().setBiomeBase(Biome.TAIGA_HILLS, Biome.BIRCH_FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.TAIGA_MOUNTAINS, Biome.BIRCH_FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.ICE_PLAINS, Biome.BIRCH_FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.ICE_PLAINS, Biome.BIRCH_FOREST, 128);
		Bukkit.getServer().setBiomeBase(Biome.ICE_PLAINS_SPIKES, Biome.BIRCH_FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.MEGA_SPRUCE_TAIGA, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.MEGA_SPRUCE_TAIGA_HILLS, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.MEGA_TAIGA, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.MEGA_TAIGA, Biome.PLAINS, 128);
		Bukkit.getServer().setBiomeBase(Biome.MEGA_TAIGA_HILLS, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.COLD_BEACH, Biome.DESERT, 0);
		Bukkit.getServer().setBiomeBase(Biome.COLD_TAIGA, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.COLD_TAIGA, Biome.PLAINS, 128);
		Bukkit.getServer().setBiomeBase(Biome.COLD_TAIGA_HILLS, Biome.DESERT, 0);
		Bukkit.getServer().setBiomeBase(Biome.COLD_TAIGA_MOUNTAINS, Biome.DESERT, 0);

		// DISALLOWED threshold biomes
		Bukkit.getServer().setBiomeBase(Biome.ROOFED_FOREST_MOUNTAINS, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.MESA, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.MESA, Biome.PLAINS, 128);
		Bukkit.getServer().setBiomeBase(Biome.MESA_PLATEAU, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.MESA_PLATEAU, Biome.PLAINS, 128);
		Bukkit.getServer().setBiomeBase(Biome.MESA_BRYCE, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.MESA_PLATEAU_FOREST, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.MESA_PLATEAU_MOUNTAINS, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.MESA_PLATEAU_FOREST_MOUNTAINS, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS, Biome.DESERT, 128);
		Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS_MOUNTAINS, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS_PLUS, Biome.FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS_PLUS, Biome.FOREST, 128);
		Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS_PLUS_MOUNTAINS, Biome.FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.FROZEN_OCEAN, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.FROZEN_RIVER, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.ICE_MOUNTAINS, Biome.PLAINS, 0);
	}

	public void giveBuildUHCKitSelectionItems(UUID uuid, Player player) {
		// Create default kit item
		ItemStack defaultKit = ItemStackUtil.createItem(Material.ENCHANTED_BOOK, 1, (short) 0,
				ChatColor.GREEN + "Default " + KitRuleSet.buildUHCRuleSet.getName() + " kit.");

		ItemStack[] contents = new ItemStack[36];

		contents[8] = defaultKit;
		Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(uuid);
		if (kitTypeListMap != null) {
			KitType kitType = new KitType(uuid.toString(), KitRuleSet.buildUHCRuleSet.getName());
			List<Kit> kits = kitTypeListMap.get(kitType);
			if (kits != null) {
				for (Kit kit : kits) {
					// Create kit item
					ItemStack kitItem = ItemStackUtil.createItem(Material.ENCHANTED_BOOK, 1, (short) 0,
							ChatColor.GOLD + "Load " + KitRuleSet.buildUHCRuleSet.getName() + " kit: " + (kit.getId() + 1));

					contents[kit.getId()] = kitItem;
				}
			}
		}

		if (player != null) {
			player.getInventory().setContents(contents);
		} else {
			// Give items to their combat logger
			LivingEntity entity = CombatTagPlugin.getInstance().getLogger(uuid).getEntity();
			entity.setMetadata("CombatLoggerInventory", new FixedMetadataValue(CombatTagPlugin.getInstance(), contents));
		}
	}

	public static UHCMeetup getInstance() {
		return UHCMeetup.plugin;
	}

    public UHCMeetupGame getUHCMeetupGame() {
        return (UHCMeetupGame) MPG.getInstance().getMPGGame();
    }

}
