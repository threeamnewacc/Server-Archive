package net.badlion.arenalobby;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.badlion.arenalobby.commands.AcceptDuelCommand;
import net.badlion.arenalobby.commands.ForceCommand;
import net.badlion.arenalobby.commands.JoinQueueCommand;
import net.badlion.arenalobby.commands.LeaveCommand;
import net.badlion.arenalobby.commands.RankupDebugCommand;
import net.badlion.arenalobby.commands.RatingCommand;
import net.badlion.arenalobby.commands.RebootCommand;
import net.badlion.arenalobby.commands.ResetEloCommand;
import net.badlion.arenalobby.commands.SettingsDbConversionCommand;
import net.badlion.arenalobby.commands.SpawnCommand;
import net.badlion.arenalobby.commands.StateCommand;
import net.badlion.arenalobby.commands.StatsCommand;
import net.badlion.arenalobby.commands.ToggleFlightCommand;
import net.badlion.arenalobby.commands.ViewOpponentInventoryCommand;
import net.badlion.arenalobby.helpers.ItemStackHelper;
import net.badlion.arenalobby.helpers.KitCreationHelper;
import net.badlion.arenalobby.helpers.LobbyItemHelper;
import net.badlion.arenalobby.helpers.MatchmakingHelper;
import net.badlion.arenalobby.helpers.NameTagHelper;
import net.badlion.arenalobby.helpers.PartyHelper;
import net.badlion.arenalobby.helpers.RatingSignsHelper;
import net.badlion.arenalobby.inventories.clan.ClanRanked5v5Inventory;
import net.badlion.arenalobby.inventories.duel.DuelChooseKitInventory;
import net.badlion.arenalobby.inventories.duel.DuelRequestInventory;
import net.badlion.arenalobby.inventories.kitcreation.CustomKitCreationInventories;
import net.badlion.arenalobby.inventories.lobby.ChatLobbySelectorInventory;
import net.badlion.arenalobby.inventories.lobby.EventQueueInventory;
import net.badlion.arenalobby.inventories.lobby.FFAInventory;
import net.badlion.arenalobby.inventories.lobby.KitCreationKitSelectionInventory;
import net.badlion.arenalobby.inventories.lobby.LobbySelectorInventory;
import net.badlion.arenalobby.inventories.lobby.Ranked1v1Inventory;
import net.badlion.arenalobby.inventories.lobby.RankedInventory;
import net.badlion.arenalobby.inventories.lobby.SettingsInventory;
import net.badlion.arenalobby.inventories.lobby.SpawnSelectorInventory;
import net.badlion.arenalobby.inventories.lobby.TournamentsInventory;
import net.badlion.arenalobby.inventories.lobby.Unranked1v1Inventory;
import net.badlion.arenalobby.inventories.party.PartyEventsInventory;
import net.badlion.arenalobby.inventories.party.PartyFFAChooseKitInventory;
import net.badlion.arenalobby.inventories.party.PartyFightChooseKitInventory;
import net.badlion.arenalobby.inventories.party.PartyTournamentChooseKitInventory;
import net.badlion.arenalobby.inventories.party.Ranked2v2Inventory;
import net.badlion.arenalobby.inventories.party.Ranked3v3Inventory;
import net.badlion.arenalobby.inventories.party.RedRoverChooseKitInventory;
import net.badlion.arenalobby.listeners.ChunkFixListener;
import net.badlion.arenalobby.listeners.GlobalListener;
import net.badlion.arenalobby.listeners.MCPListener;
import net.badlion.arenalobby.listeners.PlayerListener;
import net.badlion.arenalobby.listeners.StateListener;
import net.badlion.arenalobby.listeners.VoteListener;
import net.badlion.arenalobby.managers.ArenaSettingsManager;
import net.badlion.arenalobby.managers.DonatorManager;
import net.badlion.arenalobby.managers.LadderManager;
import net.badlion.arenalobby.managers.MatchMakingManager;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.arenalobby.managers.RankedLeftManager;
import net.badlion.arenalobby.managers.RatingManager;
import net.badlion.arenalobby.managers.SidebarManager;
import net.badlion.arenalobby.managers.SpawnPointManager;
import net.badlion.arenalobby.managers.StasisManager;
import net.badlion.arenalobby.managers.VoteManager;
import net.badlion.cmdsigns.CmdSigns;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.gguard.GGuard;
import net.badlion.gpermissions.GPermissions;
import net.badlion.gspigot.ProtocolOutHook;
import net.badlion.gspigot.ProtocolScheduler;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.smellymapvotes.SmellyMapVotes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class ArenaLobby extends JavaPlugin {

	private static ArenaLobby plugin;
	private static CmdSigns cmdSignsPlugin;
	private static GGuard gGuardPlugin;

	public static GroupStateMachine stateMachine = new GroupStateMachine(); // NEED TO ASSIGN TO STATIC VARIABLE B/C GroupStateMachine.getInstance()
	private static Map<Player, Group> playerToGroupMap = new ConcurrentHashMap<>();
	private static Map<UUID, String> uuidToUsername = new HashMap<>();

	public static Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

	private Location spawnLocation;
	private Location kitCreationLocation;
	private Location defaultRespawnLocation;
	private static boolean allowRankedMatches = true;
	public static boolean restarting = false;
	public static boolean bandwidthSavingMode = false; // Hides all players in the spawn and disables cosmetics

	private BukkitTask rebootTask;

	private boolean tournamentMode;

	private Map<Integer, ChatColor> customArmorPlayers = new HashMap<>();

	private static String unlimitedRankedPermission = "PVPServer.unlimitedRanked";

	// Tiny Protocol stuff
	private Map<Player, ConcurrentLinkedQueue<Location>> blockedBlockChangeLocations = new ConcurrentHashMap<>();
	private Map<Player, ConcurrentLinkedQueue<Location>> blockedPlayerSignUpdates = new ConcurrentHashMap<>();
	private Map<Player, Map<Location, String[]>> queuedPlayerSignUpdates = new ConcurrentHashMap<>();

	public static Set<Object> validPackets = new HashSet<>();

	private Set<UUID> playersInQueue = new HashSet<>();

	public ArenaLobby() {
		ArenaLobby.plugin = this;
	}

	@Override
	public void onEnable() {
		Gberry.enableAsyncDelayedLoginEvent = true;

		this.saveDefaultConfig();

		this.tournamentMode = this.getConfig().getBoolean("potpvp.tournament-mode", false);

		this.getServer().getWorld("world").setAutoSave(this.getConfig().getBoolean("potpvp.save-world"));

		if (Gberry.serverRegion.equals(Gberry.ServerRegion.SA)) {
			ArenaLobby.bandwidthSavingMode = true;
		}

		// For debugging
		//Gberry.loggingTags.add("RATINGSIGNS");
		//Gberry.loggingTags.add("PACKET");
		//Gberry.loggingTags.add("GROUP");
		//Gberry.loggingTags.add("DUEL");
		//Gberry.loggingTags.add("RATING");
		//Gberry.loggingTags.add("SM");
		//Gberry.loggingTags.add("ARENAS");
		//Gberry.loggingTags.add("ARENA");
		//Gberry.loggingTags.add("KIT2");
		//Gberry.loggingTags.add("EVENT");
		//Gberry.loggingTags.add("EVENT2");
		//Gberry.loggingTags.add("MATCH");
		//Gberry.loggingTags.add("MATCH2");
		//Gberry.loggingTags.add("LMS");
		//Gberry.loggingTags.add("PARTY");
		//Gberry.loggingTags.add("KIT");
		//Gberry.loggingTags.add("SLAUGHTER");
		//Gberry.loggingTags.add("INTERACT");
		//Gberry.loggingTags.add("INV");
		//Gberry.loggingTags.add("FFA");
		//Gberry.loggingTags.add("TDM");
		//Gberry.loggingTags.add("SPEC");
		//Gberry.loggingTags.add("LAG");
		Gberry.loggingTags.add("BUG");
		//Gberry.loggingTags.add("KEEPALIVE");

		JSONObject data = new JSONObject();
		data.put("server_name", Gberry.serverName);
		data.put("server_region", Gberry.serverRegion.name().toLowerCase());
		try {
			JSONObject response = Gberry.contactMCP("arena-lobby-boot", data);
			ArenaLobby.getInstance().getLogger().log(Level.INFO, "[arena-lobby-boot request] " + data.toString());
			if (response == null || !response.equals(MCPManager.successResponse)) {
				ArenaLobby.getInstance().getLogger().info("!!!!!!!!!!!  SERVER COULDN'T REGISTER WITH MCP");
				ArenaLobby.getInstance().getLogger().info("Response: " + response);

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");

				return;
			}
			ArenaLobby.getInstance().getLogger().log(Level.INFO, "[arena-lobby-boot response] " + response.toString());
		} catch (HTTPRequestFailException e) {
			ArenaLobby.getInstance().getLogger().log(Level.SEVERE, "Could not get connect to mcp, shutting down!");
			ArenaLobby.getInstance().getServer().shutdown();
			e.printStackTrace();
		}

		// Setup map voting
		SmellyMapVotes.getInstance().setServerType(net.badlion.smellymapvotes.VoteManager.ServerType.ARENAPVP);

		// Disable cosmetics
		Cosmetics.getInstance().disallowCosmetics();
		if (!bandwidthSavingMode) {
			Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.PARTICLE, true);
		}

		ArenaLobby.cmdSignsPlugin = (CmdSigns) this.getServer().getPluginManager().getPlugin("CmdSigns");
		ArenaLobby.gGuardPlugin = (GGuard) this.getServer().getPluginManager().getPlugin("GGuard");

		// Initialize locations
		this.spawnLocation = new Location(Bukkit.getWorld("world"), -21, 130, 1, -90, 0);
		this.kitCreationLocation = new Location(Bukkit.getWorld("world"), 489.5, 77, -5.5, 0, 0);
		this.defaultRespawnLocation = this.spawnLocation;

		// Initialize command executors
		this.getCommand("accept").setExecutor(new AcceptDuelCommand());
		this.getCommand("force").setExecutor(new ForceCommand());
		this.getCommand("leave").setExecutor(new LeaveCommand());
		this.getCommand("openoppinv").setExecutor(new ViewOpponentInventoryCommand());
		this.getCommand("resetelo").setExecutor(new ResetEloCommand());
		this.getCommand("rating").setExecutor(new RatingCommand());
		this.getCommand("spawn").setExecutor(new SpawnCommand());
		this.getCommand("state").setExecutor(new StateCommand());
		this.getCommand("stats").setExecutor(new StatsCommand());
		this.getCommand("reboot").setExecutor(new RebootCommand());
		this.getCommand("joinqueue").setExecutor(new JoinQueueCommand());
		this.getCommand("fly").setExecutor(new ToggleFlightCommand());


		this.getCommand("rankup").setExecutor(new RankupDebugCommand());

		this.getCommand("convertalldbsettings").setExecutor(new SettingsDbConversionCommand());

		// Initialize helpers
		LobbyItemHelper.initialize();
		ItemStackHelper.initialize();
		KitCreationHelper.initialize();
		MatchmakingHelper.initialize();
		PartyHelper.initialize();
		new RatingSignsHelper();
		RatingSignsHelper.initialize();

		// Initialize inventories
		SmellyInventory.initialize(this, true); // Always initialize first
		CustomKitCreationInventories.initialize();
		DuelChooseKitInventory.fillDuelChooseKitInventories(); // Initialized from KitHelper
		DuelRequestInventory.initialize();
		//FFAInventory.initialize();
		KitCreationKitSelectionInventory.fillKitCreationSelectionInventory(); // Initialized from KitHelper
		PartyEventsInventory.initialize();
		PartyFightChooseKitInventory.initialize();
		PartyFFAChooseKitInventory.initialize();
		FFAInventory.initialize();
		//PartyListInventory.initialize();
		//PartyPlayerInventoriesInventory.initialize();
		//PartyRequestInventory.initialize();
		//Ranked1v1Inventory.initialize();
		//Ranked3v3Inventory.initialize();    // TODO: CLEAN UP
		//Ranked5v5Inventory.initialize();
		RedRoverChooseKitInventory.initialize();
		PartyTournamentChooseKitInventory.initialize();
		SettingsInventory.initialize();
		LobbySelectorInventory.initialize();
		ChatLobbySelectorInventory.initialize();
		//SpectateEventInventory.initialize();
		//SpectateFFAInventory.initialize();

		// Initialize managers
		new LadderManager();
		LadderManager.registerActiveRulesetLadders();
		new DonatorManager();
		new MatchMakingManager();
		new PotPvPPlayerManager();
		new RankedLeftManager();
		new RatingManager();
		new SidebarManager();
		SpawnPointManager.initialize();
		StasisManager.initialize();
		VoteManager.initialize();

		SpawnSelectorInventory.initialize();
		EventQueueInventory.initialize();
		RankedInventory.fillRankedInventory();
		TournamentsInventory.fillTournamentsInventory();
		Unranked1v1Inventory.initialize(); // Initialized from KitHelper
		Ranked1v1Inventory.initialize();
		Ranked2v2Inventory.initialize();
		Ranked3v3Inventory.initialize();
		ClanRanked5v5Inventory.initialize();


		// Initialize listeners
		new GlobalListener();
		new StateListener();
		new VoteListener();
		this.getServer().getPluginManager().registerEvents(new ResetEloCommand(), this); // Had to implement Bukkit Listener
		this.getServer().getPluginManager().registerEvents(new PotPvPPlayerManager(), this);
		this.getServer().getPluginManager().registerEvents(new MCPListener(), this);
		this.getServer().getPluginManager().registerEvents(new ChunkFixListener(), this);

		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

		this.getServer().getPluginManager().registerEvents(new NameTagHelper(), this);

		// Register state listeners
		this.getServer().getPluginManager().registerEvents(GroupStateMachine.kitCreationState, this);
		this.getServer().getPluginManager().registerEvents(GroupStateMachine.lobbyState, this);
		this.getServer().getPluginManager().registerEvents(GroupStateMachine.matchMakingState, this);

		this.getServer().getPluginManager().registerEvents(new ArenaSettingsManager(), this);


		// Initialize TDMs
		/* TODO: Events will be done in another plugin for s13

        if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_7) {
            new TDMGame(2, 30, KitRuleSet.tdmRuleSet);

            // Force update on TDM inventory
            TDMInventory.updateTDMInventory(true);
        }
        */
		// Perma day
		World world = ArenaLobby.getInstance().getServer().getWorld("world");
		world.setTime(6000L);
		world.setGameRuleValue("doDaylightCycle", "false");

		// Reboot the server between 3 and 5 hours, send mcp call that the server is ready to reboot and it will wait for matches to finish then mcp will shut it down.
		Random random = new Random();
		this.rebootTask = new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getLogger().log(Level.INFO, "Server is now ready to reboot. Telling mcp that we are ready to reboot in the next keepalive.");
				MCPListener.shutdown = true;
			}
		}.runTaskLater(plugin, 20 * 60 * 60 * 3 + (random.nextInt(120) * 20 * 60));

		// TODO: Pretty sure the sign packet updates are for the kit editing area so we will keep those
		// Register the Tiny Protocol packet listener
		ProtocolScheduler.addHook(new ProtocolOutHook() {
			@Override
			public Object handlePacket(Player receiver, Object packet) {
				if (TinyProtocolReferences.packetBlockChangeClass.isInstance(packet)) { // TODO: WE MIGHT NEED TO UPDATE BLOCK CHANGES TO THE NEW SYSTEM TOO?
					ConcurrentLinkedQueue<Location> locations = ArenaLobby.this.blockedBlockChangeLocations.get(receiver);
					if (locations != null) {
						// Check if locations match
						for (Location loc : locations) {
							if (loc.getBlockX() == TinyProtocolReferences.getPacketBlockChangeCoord(packet, 'x')
									&& loc.getBlockY() == TinyProtocolReferences.getPacketBlockChangeCoord(packet, 'y')
									&& loc.getBlockZ() == TinyProtocolReferences.getPacketBlockChangeCoord(packet, 'z')) {
								return null;
							}
						}
					}
				} else if (TinyProtocolReferences.packetUpdateSignClass.isInstance(packet) && TinyProtocolReferences.isSignUpdate(packet)) {
					// Check to see if sign update is blocked
					ConcurrentLinkedQueue<Location> blockedSignUpdates = ArenaLobby.this.blockedPlayerSignUpdates.get(receiver);
					Map<Location, String[]> queuedSignUpdates = ArenaLobby.getInstance().getQueuedPlayerSignUpdates().get(receiver);
					if (blockedSignUpdates != null && queuedSignUpdates != null) {
						// Check if locations match
						for (Location loc : blockedSignUpdates) {
							if (loc.getX() == TinyProtocolReferences.getPacketUpdateSignCoord(packet, 'x')
									&& loc.getY() == TinyProtocolReferences.getPacketUpdateSignCoord(packet, 'y')
									&& loc.getZ() == TinyProtocolReferences.getPacketUpdateSignCoord(packet, 'z')) {
								if (queuedSignUpdates.containsKey(loc)) {
									String[] newLines = queuedSignUpdates.remove(loc);

									// Sanitize
									int i = 0;
									for (String s : newLines) {
										if (s == null) {
											s = "";
											newLines[i] = "";
										}

										if (s.length() > 15) {
											newLines[i] = s.substring(0, 15);
										}

										i++;
									}

									TinyProtocolReferences.setPacketUpdateSignLines(packet, newLines);

									// Stop
									return packet;
								}
							}
						}
					}
				}

				return packet;
			}

			@Override
			public ProtocolPriority getPriority() {
				return ProtocolPriority.MEDIUM;
			}
		});

	}

	@Override
	public void onDisable() {
		// Remove all players from event queues
		for (UUID uuid : this.playersInQueue) {
			// Call on main thread since the server is shutting down
			this.leaveQueue(uuid, true);
		}
	}

	/**
	 * MUST BE CALLED ASYNC
	 */
	public void leaveQueue(final UUID uuid, boolean shutdown) {
		JSONObject data = new JSONObject();
		data.put("uuid", uuid.toString());

		try {
			JSONObject response = Gberry.contactMCP("matchmaking-default-remove", data);
			ArenaLobby.getInstance().getLogger().log(Level.INFO, "[sending remove queue]: " + data);
			ArenaLobby.getInstance().getLogger().log(Level.INFO, "[response remove queue]: " + response);

			if (!shutdown) {
				// Yolo async access here
				this.playersInQueue.remove(uuid);
			}

		} catch (HTTPRequestFailException e) {
			e.printStackTrace();
		}
	}

	public void givePlayerDuelStateItems(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.updateInventory();
	}

	public Location getDefaultRespawnLocation() {
		return defaultRespawnLocation;
	}

	public static ArenaLobby getInstance() {
		return ArenaLobby.plugin;
	}

	public CmdSigns getCmdSignsPlugin() {
		return ArenaLobby.cmdSignsPlugin;
	}

	public GGuard getgGuardPlugin() {
		return gGuardPlugin;
	}

	public boolean isAllowRankedMatches() {
		return ArenaLobby.allowRankedMatches;
	}

	public static String getUnlimitedRankedPermission() {
		return ArenaLobby.unlimitedRankedPermission;
	}

	public void setAllowRankedMatches(boolean allowRankedMatches) {
		ArenaLobby.allowRankedMatches = allowRankedMatches;
	}

	public Group getPlayerGroup(Entity entity) {
		if (!(entity instanceof Player)) {
			throw new RuntimeException("Non-Player object passed as entity to get group");
		}

		return this.getPlayerGroup((Player) entity);
	}

	public Group getPlayerGroup(Player player) {
		Group group = ArenaLobby.playerToGroupMap.get(player);
		if (group == null) {
			player.sendFormattedMessage("{0}You have glitched out of the PotPvP system. You have been removed to prevent corruption.", ChatColor.RED);
			player.kickPlayer("Report to an admin if you can reproduce how to end up glitched.");
			PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId()).printDebug();
			throw new RuntimeException("Null group requested for " + player.getName());
		}

		return group;
	}

	public void handlePlayerLeaveGroup(Player player, Group group) {
		Gberry.log("GROUP", "Cleaning group up with player " + player.getName());
		GroupStateMachine.getInstance().cleanupElement(group);
	}

	public void updatePlayerGroup(Player player, Group group) {
		if (group == null) {
			throw new IllegalArgumentException("group cannot be null");
		}

		Group oldGroup = ArenaLobby.playerToGroupMap.get(player);
		if (oldGroup != null) {
			// Debug stuff
			if (Gberry.loggingTags.contains("SM")) {
				List<String> lines = GroupStateMachine.getInstance().debugTransitionsForElement(oldGroup);
				for (String line : lines) {
					Gberry.log("SM", line);
				}
			}

			// Cleanup group stuff if needed
			//try {
			this.handlePlayerLeaveGroup(player, oldGroup);
			//} catch (RuntimeException e) {
			// Another player was in the same group and this method has already
			// been called and the old group has already been cleaned up
			//}
		}

		ArenaLobby.playerToGroupMap.put(player, group);
	}

	public Group removePlayerGroup(Player player) {
		return ArenaLobby.playerToGroupMap.remove(player);
	}

	// PLEASE NOTE THIS IS INTENDED AS A MEMORY LEAK
	public String getUsernameFromUUID(UUID uuid) {
		return ArenaLobby.uuidToUsername.get(uuid);
	}

	public void addUUIDToUsername(UUID uuid, String username) {
		ArenaLobby.uuidToUsername.put(uuid, username);
	}

	public void sendMessageToAllGroups(String msg, Group... groups) {
		for (Group group : groups) {
			for (Player p : group.players()) {
				p.sendMessage(msg);
			}
		}
	}

	public void somethingBroke(CommandSender sender, Group... groups) {
		sender.sendMessage(ChatColor.RED + "Something broke, contact an administrator.");

		for (Group group : groups) {
			for (String line : GroupStateMachine.getInstance().debugTransitionsForElement(group)) {
				Bukkit.getLogger().info(line);
			}

			for (Player pl : group.players()) {
				pl.kickPlayer("Something broke, contact an administrator if you can reproduce this error");
			}
		}
	}

	public void healAndTeleportToSpawn(Player player) {
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.teleport(SpawnPointManager.getNextSpawnPoint());
		UserDataManager.UserData userData = UserDataManager.getUserData(player);
		if (userData != null && userData.isLobbyFlight() && (player.hasPermission("badlion.staff") || player.hasPermission("badlion.donatorplus"))) {
			player.setAllowFlight(true);
		} else {
			player.setAllowFlight(false);
		}
		player.spigot().setCollidesWithEntities(true);
		player.setGameMode(GameMode.SURVIVAL);

		// Douse the player
		player.setFireTicks(0);

		// Clear all buffs on them too...not sure when we wouldn't want to do this
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

	}

	public void addMuteBanPerms(Player player) {
		if (player.hasPermission("badlion.kitmod")) {
			GPermissions.giveModPermissions(player);
		} else if (player.hasPermission("badlion.kittrial")) {
			GPermissions.giveTrialPermissions(player);
		}
	}

	public static void sendBlockChange(Player player, Location location, Material material) {
		ArenaLobby.sendBlockChange(player, location, material, (byte) 0);
	}

	public static void sendBlockChange(final Player player, final Location location, Material material, byte data) {
		final ConcurrentLinkedQueue<Location> locations = ArenaLobby.getInstance().getBlockedBlockChangeLocations().get(player);

		// Remove location if we're currently blocking packets
		if (locations != null) {
			locations.remove(location);
		}

		player.sendBlockChange(location, material, data);

		// Do this in one tick to let the packet above go through
		BukkitUtil.runTaskNextTick(new Runnable() {
			@Override
			public void run() {
				if (Gberry.isPlayerOnline(player)) {
					if (locations == null) {
						ConcurrentLinkedQueue<Location> locations2 = new ConcurrentLinkedQueue<>();

						locations2.add(location);

						ArenaLobby.getInstance().getBlockedBlockChangeLocations().put(player, locations2);
					} else {
						locations.add(location);
					}
				}
			}
		});
	}

	public static void sendSignChange(final Player player, final Location location, final String[] lines) {
		ConcurrentLinkedQueue<Location> locations = ArenaLobby.getInstance().getBlockedPlayerSignUpdates().get(player);

		if (locations == null) {
			locations = new ConcurrentLinkedQueue<>();
			ArenaLobby.getInstance().getBlockedPlayerSignUpdates().put(player, locations);
		}

		// Add location if not already in the list
		if (!locations.contains(location)) {
			locations.add(location);
			//Gberry.log("PACKET", "Adding location " + location.toString());
		}

		//for (String s : lines) {
		//    Gberry.log("PACKET", "Adding line " + s);
		//}

		Map<Location, String[]> queuedSignUpdates = ArenaLobby.getInstance().getQueuedPlayerSignUpdates().get(player);
		if (queuedSignUpdates == null) {
			queuedSignUpdates = new ConcurrentHashMap<>();
			ArenaLobby.getInstance().getQueuedPlayerSignUpdates().put(player, queuedSignUpdates);
		}

		queuedSignUpdates.put(location, lines);

		//for (Map.Entry<Location, String[]> keyValue : queuedSignUpdates.entrySet()) {
		//    Gberry.log("PACKET", "Location " + keyValue.getKey());
		//    for (String s : keyValue.getValue()) {
		//        Gberry.log("PACKET", "LINE2 " + s);
		//    }
		//}
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public Location getKitCreationLocation() {
		return kitCreationLocation;
	}

	public Map<Player, ConcurrentLinkedQueue<Location>> getBlockedBlockChangeLocations() {
		return blockedBlockChangeLocations;
	}

	public Map<Player, ConcurrentLinkedQueue<Location>> getBlockedPlayerSignUpdates() {
		return blockedPlayerSignUpdates;
	}

	public Map<Player, Map<Location, String[]>> getQueuedPlayerSignUpdates() {
		return queuedPlayerSignUpdates;
	}

	public boolean isTournamentMode() {
		return tournamentMode;
	}

	public Map<Integer, ChatColor> getCustomArmorPlayers() {
		return customArmorPlayers;
	}

	public Set<UUID> getPlayersInQueue() {
		return this.playersInQueue;
	}

}
