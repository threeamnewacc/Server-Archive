package net.badlion.arenapvp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.badlion.arenapvp.command.RebootCommand;
import net.badlion.arenapvp.command.SpectatorCommand;
import net.badlion.arenapvp.command.StatsCommand;
import net.badlion.arenapvp.helper.SpectatorHelper;
import net.badlion.arenapvp.listener.ArenaListener;
import net.badlion.arenapvp.listener.EnderPearlListener;
import net.badlion.arenapvp.listener.JoinLeaveRespawnListener;
import net.badlion.arenapvp.listener.MCPListener;
import net.badlion.arenapvp.listener.MatchListener;
import net.badlion.arenapvp.listener.SpectatorListener;
import net.badlion.arenapvp.listener.rulesets.AdvancedUHCListener;
import net.badlion.arenapvp.listener.rulesets.ArcherListener;
import net.badlion.arenapvp.listener.rulesets.BuffSoupListener;
import net.badlion.arenapvp.listener.rulesets.BuildUHCListener;
import net.badlion.arenapvp.listener.rulesets.ChickenListener;
import net.badlion.arenapvp.listener.rulesets.ComboBuildUHCListener;
import net.badlion.arenapvp.listener.rulesets.CustomListener;
import net.badlion.arenapvp.listener.rulesets.DiamondOCNListener;
import net.badlion.arenapvp.listener.rulesets.GodAppleListener;
import net.badlion.arenapvp.listener.rulesets.HCFListener;
import net.badlion.arenapvp.listener.rulesets.HorseListener;
import net.badlion.arenapvp.listener.rulesets.IronBuildUHCListener;
import net.badlion.arenapvp.listener.rulesets.IronOCNListener;
import net.badlion.arenapvp.listener.rulesets.LegacyListener;
import net.badlion.arenapvp.listener.rulesets.MineZListener;
import net.badlion.arenapvp.listener.rulesets.SGListener;
import net.badlion.arenapvp.listener.rulesets.SkyWarsListener;
import net.badlion.arenapvp.listener.rulesets.SoupListener;
import net.badlion.arenapvp.listener.rulesets.SpleefListener;
import net.badlion.arenapvp.listener.rulesets.UHCListener;
import net.badlion.arenapvp.listener.rulesets.VanillaListener;
import net.badlion.arenapvp.manager.ArenaManager;
import net.badlion.arenapvp.manager.ArenaSettingsManager;
import net.badlion.arenapvp.manager.MatchManager;
import net.badlion.arenapvp.manager.PotPvPPlayerManager;
import net.badlion.arenapvp.manager.RatingManager;
import net.badlion.arenapvp.manager.SidebarManager;
import net.badlion.arenapvp.manager.SpectateManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.gpermissions.GPermissions;
import net.badlion.gspigot.ProtocolOutHook;
import net.badlion.gspigot.ProtocolScheduler;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class ArenaPvP extends JavaPlugin {

	private static ArenaPvP plugin;

	public static ArenaPvP getInstance() {
		return ArenaPvP.plugin;
	}

	public ArenaPvP() {
		ArenaPvP.plugin = this;
	}

	private static Map<Player, Team> playerToTeamMap = new ConcurrentHashMap<>();
	private static Map<UUID, String> uuidToUsername = new HashMap<>();

	private Location spawnLocation;
	private Location defaultRespawnLocation;
	public static boolean restarting = false;

	private Map<Integer, ChatColor> customArmorPlayers = new HashMap<>();

	private boolean tournamentMode = false;

	public static Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

	private String dbExtra = "";

	// Tiny Protocol stuff
	private Map<Player, ConcurrentLinkedQueue<Location>> blockedBlockChangeLocations = new ConcurrentHashMap<>();
	private Map<Player, ConcurrentLinkedQueue<Location>> blockedPlayerSignUpdates = new ConcurrentHashMap<>();
	private Map<Player, Map<Location, String[]>> queuedPlayerSignUpdates = new ConcurrentHashMap<>();

	public static Set<Object> validPackets = new HashSet<>();

	private BukkitTask timeTask;
	private BukkitTask rebootTask;

	@Override
	public void onEnable() {
		Gberry.enableAsyncDelayedLoginEvent = true;

		this.saveDefaultConfig();

		this.getServer().getWorld("world").setAutoSave(this.getConfig().getBoolean("potpvp.save-world"));

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
		Gberry.loggingTags.add("SPEC");
		Gberry.loggingTags.add("LAG");
		Gberry.loggingTags.add("BUG");
		//Gberry.loggingTags.add("VISIBILITY");
		Gberry.loggingTags.add("STATE");
		Gberry.loggingTags.add("REDROVER");

		// Initialize locations
		this.spawnLocation = new Location(Bukkit.getWorld("world"), -21, 130, 1, -90, 0);
		this.defaultRespawnLocation = this.spawnLocation;

		// Initialize inventories
		SmellyInventory.initialize(this, true); // Always initialize first

		// Initialize managers
		new SpectateManager();
		new SidebarManager();
		new PotPvPPlayerManager();
		ArenaManager.initialize();

		this.getServer().getPluginManager().registerEvents(new RatingManager(), this);

		SpectatorHelper.initialize();

		// Commands
		this.getCommand("sp").setExecutor(new SpectatorCommand());
		this.getCommand("reboot").setExecutor(new RebootCommand());
		this.getCommand("stats").setExecutor(new StatsCommand());

		// Initialize listeners
		this.getServer().getPluginManager().registerEvents(new ArenaSettingsManager(), this);
		this.getServer().getPluginManager().registerEvents(new JoinLeaveRespawnListener(), this);
		this.getServer().getPluginManager().registerEvents(new MCPListener(), this);
		this.getServer().getPluginManager().registerEvents(new ArenaListener(), this);
		this.getServer().getPluginManager().registerEvents(new MatchListener(), this);
		this.getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
		this.getServer().getPluginManager().registerEvents(new EnderPearlListener(), this);

		// Register kit ruleset listeners
		this.getServer().getPluginManager().registerEvents(new AdvancedUHCListener(), this);
		this.getServer().getPluginManager().registerEvents(new ArcherListener(), this);
		this.getServer().getPluginManager().registerEvents(new BuffSoupListener(), this);
		this.getServer().getPluginManager().registerEvents(new BuildUHCListener(), this);
		this.getServer().getPluginManager().registerEvents(new ChickenListener(), this);
		this.getServer().getPluginManager().registerEvents(new ComboBuildUHCListener(), this);
		this.getServer().getPluginManager().registerEvents(new CustomListener(), this);
		this.getServer().getPluginManager().registerEvents(new DiamondOCNListener(), this);
		this.getServer().getPluginManager().registerEvents(new GodAppleListener(), this);
		this.getServer().getPluginManager().registerEvents(new HCFListener(), this);
		this.getServer().getPluginManager().registerEvents(new HorseListener(), this);
		this.getServer().getPluginManager().registerEvents(new IronBuildUHCListener(), this);
		this.getServer().getPluginManager().registerEvents(new IronOCNListener(), this);
		this.getServer().getPluginManager().registerEvents(new LegacyListener(), this);
		this.getServer().getPluginManager().registerEvents(new MineZListener(), this);
		this.getServer().getPluginManager().registerEvents(new SGListener(), this);
		this.getServer().getPluginManager().registerEvents(new SkyWarsListener(), this);
		this.getServer().getPluginManager().registerEvents(new SoupListener(), this);
		this.getServer().getPluginManager().registerEvents(new SpleefListener(), this);
		this.getServer().getPluginManager().registerEvents(new UHCListener(), this);
		this.getServer().getPluginManager().registerEvents(new VanillaListener(), this);

		// Register state listeners
		new TeamStateMachine();
		new MatchState();
		//getServer().getPluginManager().registerEvents(TeamStateMachine.matchState, this);
		this.getServer().getPluginManager().registerEvents(TeamStateMachine.followState, this);
		getServer().getPluginManager().registerEvents(TeamStateMachine.redRoverWaitingState, this);
		this.getServer().getPluginManager().registerEvents(TeamStateMachine.spectatorState, this);
		this.getServer().getPluginManager().registerEvents(TeamStateMachine.deathState, this);

		// Perma day
		World world = ArenaPvP.getInstance().getServer().getWorld("world");
		world.setTime(6000L);
		world.setGameRuleValue("doDaylightCycle", "false");

		// Disable cosmetics
		Cosmetics.getInstance().disallowCosmetics();
		Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.ARROW_TRAIL, true); // Just ArrowTrails for now

		// Register the Tiny Protocol packet listener
		ProtocolScheduler.addHook(new ProtocolOutHook() {
			@Override
			public Object handlePacket(Player receiver, Object packet) {
				if (TinyProtocolReferences.packetEntityEquipmentClass.isInstance(packet)) {
					// Is this player a spectator?
					if (TeamStateMachine.spectatorState.contains(receiver) && TeamStateMachine.spectatorState.isColorArmorEnabled(receiver)) {
						int entityId = TinyProtocolReferences.packetEntityEquipmentEntityID.get(packet);

						// Is this player in a tournament match?
						ChatColor color = ArenaPvP.this.customArmorPlayers.get(entityId);
						if (color != null) {
							ItemStack item = new ItemStack(Material.LEATHER_HELMET);//null;

							int slot = TinyProtocolReferences.getPacketEntityEquipmentSlot(packet);

							// Check to make sure this is a helmet item
							if (slot != 4) return packet;

							switch (slot) {
								case 4:
									item = new ItemStack(Material.LEATHER_HELMET);
									break;
								case 3:
									item = new ItemStack(Material.LEATHER_CHESTPLATE);
									break;
								case 2:
									item = new ItemStack(Material.LEATHER_LEGGINGS);
									break;
								case 1:
									item = new ItemStack(Material.LEATHER_BOOTS);
									break;
							}

							// Was this an armor item?
							if (item == null) return packet;

							// Color item
							LeatherArmorMeta itemMeta = ((LeatherArmorMeta) item.getItemMeta());
							itemMeta.setColor(Gberry.getColorFromChatColor(color));
							item.setItemMeta(itemMeta);

							// Change item in the packet
							try {
								// Clone packet because MC sends literally the same packet to literally everyone like literally
								Object newPacket = TinyProtocolReferences.packetEntityEquipmentClass.newInstance();

								TinyProtocolReferences.packetEntityEquipmentEntityID.set(newPacket, entityId);

								// 1.9 slot is offset by 1
								TinyProtocolReferences.setPacketEntityEquipmentSlot(newPacket, slot);

								TinyProtocolReferences.packetEntityEquipmentItem.set(newPacket, TinyProtocolReferences.getItemStackNMSCopy.invoke(null, item));

								return newPacket;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				} else if (TinyProtocolReferences.packetBlockChangeClass.isInstance(packet)) { // TODO: WE MIGHT NEED TO UPDATE BLOCK CHANGES TO THE NEW SYSTEM TOO?
					ConcurrentLinkedQueue<Location> locations = ArenaPvP.this.blockedBlockChangeLocations.get(receiver);
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
					ConcurrentLinkedQueue<Location> blockedSignUpdates = ArenaPvP.this.blockedPlayerSignUpdates.get(receiver);
					Map<Location, String[]> queuedSignUpdates = ArenaPvP.getInstance().getQueuedPlayerSignUpdates().get(receiver);
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
					}            // TODO: WHAT ABOUT 1.10?
				}

				return packet;
			}

			@Override
			public ProtocolPriority getPriority() {
				return ProtocolPriority.MEDIUM;
			}
		});


		this.timeTask = new BukkitRunnable() {
			@Override
			public void run() {
				SimpleDateFormat formater = new SimpleDateFormat("h:mm:ss a");
				Date now = new Date();
				String time = formater.format(now);
				SidebarManager.setTime(time);

				// Update all matches time left strings for sidebars
				Set<Match> duplicates = new HashSet<>(MatchManager.getActiveMatches().values().size(), 1);
				// This map can have the same match in it twice, filter those out.
				for (Match match : MatchManager.getActiveMatches().values()) {
					if (!duplicates.contains(match)) {
						duplicates.add(match);
						match.updateMatchTimeLeftString();
					}
				}
			}
		}.runTaskTimer(plugin, 0, 20);


		// Reboot the server between 2 and 3 hours, send mcp call that the server is ready to reboot and it will wait for matches to finish then mcp will shut it down.
		Random random = new Random();
		this.rebootTask = new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getLogger().log(Level.INFO, "Server is now ready to reboot. Telling mcp that we are ready to reboot in the next keepalive.");
				MCPListener.shutdown = true;
			}
		}.runTaskLater(plugin, 20 * 60 * 60 * 2 + (random.nextInt(60) * 20 * 60));
	}

	@Override
	public void onDisable() {
		timeTask.cancel();
	}

	public void givePlayerDuelStateItems(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.updateInventory();
	}

	public void givePlayerPartyDeadStateItems(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		// TODO: Add an item so the player can leave the party, need to also send mcp a request to say the player wants to leave the party
		//player.getInventory().setItem(8, PartyHelper.getLeavePartyItem());

		player.getInventory().setHeldItemSlot(0);

		player.updateInventory();
	}

	public Location getDefaultRespawnLocation() {
		return defaultRespawnLocation;
	}

	public Team getPlayerTeam(Entity entity) {
		if (!(entity instanceof Player)) {
			throw new RuntimeException("Non-Player object passed as entity to get group");
		}

		return this.getPlayerTeam((Player) entity);
	}

	public Team getPlayerTeam(Player player) {
		if (player != null && ArenaPvP.playerToTeamMap.containsKey(player)) {
			Team team = ArenaPvP.playerToTeamMap.get(player);
			return team;
		}
		return null;
	}


	public Team removePlayerTeam(Player player) {
		return ArenaPvP.playerToTeamMap.remove(player);
	}

	public void setPlayerTeam(Player player, Team team) {
		if (ArenaPvP.playerToTeamMap.containsKey(player)) {
			ArenaPvP.playerToTeamMap.remove(player);
		}
		ArenaPvP.playerToTeamMap.put(player, team);
	}

	// PLEASE NOTE THIS IS INTENDED AS A MEMORY LEAK
	public String getUsernameFromUUID(UUID uuid) {
		return ArenaPvP.uuidToUsername.get(uuid);
	}

	public void addUUIDToUsername(UUID uuid, String username) {
		ArenaPvP.uuidToUsername.put(uuid, username);
	}

	public void sendMessageToAllTeams(String msg, Team... teams) {
		for (Team team : teams) {
			for (Player p : team.members()) {
				p.sendMessage(msg);
			}
		}
	}

	public void somethingBroke(CommandSender sender, Team... teams) {
		sender.sendMessage(ChatColor.RED + "Something broke, contact an administrator.");

		for (Team team : teams) {
			for (Player pl : team.members()) {
				pl.kickPlayer("Something broke, contact an administrator if you can reproduce this error");
			}
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
		ArenaPvP.sendBlockChange(player, location, material, (byte) 0);
	}

	public static void sendBlockChange(final Player player, final Location location, Material material, byte data) {
		final ConcurrentLinkedQueue<Location> locations = ArenaPvP.getInstance().getBlockedBlockChangeLocations().get(player);

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

						ArenaPvP.getInstance().getBlockedBlockChangeLocations().put(player, locations2);
					} else {
						locations.add(location);
					}
				}
			}
		});
	}

	public static void sendSignChange(final Player player, final Location location, final String[] lines) {
		ConcurrentLinkedQueue<Location> locations = ArenaPvP.getInstance().getBlockedPlayerSignUpdates().get(player);

		if (locations == null) {
			locations = new ConcurrentLinkedQueue<>();
			ArenaPvP.getInstance().getBlockedPlayerSignUpdates().put(player, locations);
		}

		// Add location if not already in the list
		if (!locations.contains(location)) {
			locations.add(location);
			//Gberry.log("PACKET", "Adding location " + location.toString());
		}

		//for (String s : lines) {
		//    Gberry.log("PACKET", "Adding line " + s);
		//}

		Map<Location, String[]> queuedSignUpdates = ArenaPvP.getInstance().getQueuedPlayerSignUpdates().get(player);
		if (queuedSignUpdates == null) {
			queuedSignUpdates = new ConcurrentHashMap<>();
			ArenaPvP.getInstance().getQueuedPlayerSignUpdates().put(player, queuedSignUpdates);
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

	public Map<Player, ConcurrentLinkedQueue<Location>> getBlockedBlockChangeLocations() {
		return blockedBlockChangeLocations;
	}

	public Map<Player, ConcurrentLinkedQueue<Location>> getBlockedPlayerSignUpdates() {
		return blockedPlayerSignUpdates;
	}

	public Map<Player, Map<Location, String[]>> getQueuedPlayerSignUpdates() {
		return queuedPlayerSignUpdates;
	}

	public Map<Integer, ChatColor> getCustomArmorPlayers() {
		return customArmorPlayers;
	}

	public String getDBExtra() {
		return dbExtra;
	}

}
