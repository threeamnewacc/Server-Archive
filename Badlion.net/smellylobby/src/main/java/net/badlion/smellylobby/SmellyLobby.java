package net.badlion.smellylobby;

import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.cosmetics.inventories.CosmeticsInventory;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.PlayerRunnable;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.gspigot.ProtocolOutHook;
import net.badlion.gspigot.ProtocolScheduler;
import net.badlion.smellylobby.commands.ToggleFlightCommand;
import net.badlion.smellylobby.commands.UHCCommand;
import net.badlion.smellylobby.helpers.NavigationInventoryHelper;
import net.badlion.smellylobby.inventories.NavigationV17Inventory;
import net.badlion.smellylobby.listeners.LobbyListener;
import net.badlion.smellylobby.listeners.NavigationToolListener;
import net.badlion.smellylobby.listeners.PlayerListener;
import net.badlion.smellylobby.tasks.RestartServerTask;
import net.badlion.smellylobby.tasks.UpdatePlayerCountTask;
import net.badlion.smellylobby.tasks.UpdateQueueCountTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class SmellyLobby extends JavaPlugin {

	private static int alertCount = 0;

	private Set<UUID> playersInQueue = new HashSet<>();

	public static final Set<Player> npcs = new HashSet<>();
	public static final Set<UUID> npcsUUIDs = new HashSet<>();

	private static SmellyLobby plugin;

	private Scoreboard scoreboard;
	private Location spawnLocation;

	private List<String> messageOfTheDay = new ArrayList<>();

	@Override
	public void onEnable() {
		SmellyLobby.plugin = this;

		// Enable async quit event
		Gberry.enableAsyncQuitEvent = true;

		// Commands
		this.getCommand("fly").setExecutor(new ToggleFlightCommand());
		this.getCommand("uhctimes").setExecutor(new UHCCommand());

		// Save default config if config doesn't exist
		this.saveDefaultConfig();

		// Get spawn location
		this.spawnLocation = new Location(Bukkit.getWorld("world"), 0.5, 102, 0.5, 180, 0);

		// Get MOTD
		for (String string : this.getConfig().getStringList("smellylobby.message_of_the_day")) {
			if (string.contains("&")) {
				string = string.replaceAll("&", "§");
			}

			this.messageOfTheDay.add(string);
		}

		// Register BungeeCord
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		NavigationV17Inventory.initialize();

		// Initialize listeners
		this.getServer().getPluginManager().registerEvents(new LobbyListener(), this);
		this.getServer().getPluginManager().registerEvents(new NavigationToolListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

		// Start tasks
		new RestartServerTask();
		new UpdatePlayerCountTask();
		new UpdateQueueCountTask().runTaskTimerAsynchronously(this, 20L, 20L);

		// Create all of our holograms
		Location loc = new Location(this.getSpawnLocation().getWorld(), 0, 101, -6);
		Bukkit.newHologram(loc, ChatColor.AQUA + "Badlion Now Supports 1.7/1.8/1.9/1.10!");
		loc = loc.clone().subtract(0, 0.25, 0);
		Bukkit.newHologram(loc, ChatColor.GOLD + "Some new features may be disabled.");

		// Alerts
		if (this.getConfig().getBoolean("smellylobby.main", false)) {
			final List<String> alertMessages = this.getConfig().getStringList("smellylobby.alerts");

			this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {

				@Override
				public void run() {
					String msg = alertMessages.get(alertCount++);
					List<String> commands = new ArrayList<>();
					commands.add("alert " + msg);

					Gberry.sendToAll(commands);

					if (alertCount == alertMessages.size()) {
						alertCount = 0;
					}
				}

			}, 20 * 60 * 5, 20 * 60 * 5);
		}

		this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				Gberry.distributeTask(SmellyLobby.this, new PlayerRunnable() {
					@Override
					public void run(Player player) {
						player.setSaturation(20);
						player.setExhaustion(0);
						player.setFoodLevel(20);
					}
				});

			}
		}, 200L, 200L);

		// This is the code that makes it so that your skin appears on all of our NPC's
		ProtocolScheduler.addHook(new ProtocolOutHook() {
			@Override
			public Object handlePacket(final Player receiver, Object packet) {
				if (TinyProtocolReferences.spawnPacket.isInstance(packet) && receiver.getClientVersion() == Player.CLIENT_VERSION.V1_7_6) {
					// Get the profile
					Object gameProfile = TinyProtocolReferences.spawnPacketGameProfile.get(packet);

					if (gameProfile != null) {
						UUID uuid = TinyProtocolReferences.gameProfileUUID.get(gameProfile);

						if (SmellyLobby.npcsUUIDs.contains(uuid)) {
							TinyProtocolReferences.spawnPacketGameProfile.set(packet, SmellyLobby.gameProfileWithPlayersSkin(gameProfile, receiver));
						}
					}
				} else if (TinyProtocolReferences.tabPacketClass.isInstance(packet)) {
					if (receiver.getClientVersion().ordinal() >= Player.CLIENT_VERSION.V1_8.ordinal()) {
						int action = TinyProtocolReferences.tabPacketAction.get(packet);
						Object gameProfile = TinyProtocolReferences.tabPacketGameProfile.get(packet);

						if (action == 0 && gameProfile != null) {
							UUID uuid = TinyProtocolReferences.gameProfileUUID.get(gameProfile);

							if (SmellyLobby.npcsUUIDs.contains(uuid)) {
								TinyProtocolReferences.tabPacketGameProfile.set(packet, SmellyLobby.gameProfileWithPlayersSkin(gameProfile, receiver));

								// send a remove packet some time later, sending it too soon seems to cause issues
								final Object removePacket = TinyProtocolReferences.tabPacketConstructor.invoke();
								TinyProtocolReferences.tabPacketAction.set(removePacket, 4); // action = remove
								TinyProtocolReferences.tabPacketGameProfile.set(removePacket, gameProfile);
								TinyProtocolReferences.tabPacketName.set(removePacket, TinyProtocolReferences.tabPacketName.get(packet));
								new BukkitRunnable() {
									@Override
									public void run() {
										Gberry.protocol.sendPacket(receiver, removePacket);
									}
								}.runTaskLater(SmellyLobby.this, 100);
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

		// Spawn NPCs
		this.spawnNPCs();
	}

	@Override
	public void onDisable() {
		// Remove all players from MPG queues
		for (UUID uuid : this.playersInQueue) {
			// Do this on the main thread since server is shutting down
			this.leaveQueue(uuid, false);
		}
	}

	public void giveLobbyItems(Player player) {
		Boolean visible = PlayerListener.playerVisibility.get(player.getUniqueId());

		player.getInventory().clear();
		player.getInventory().setHeldItemSlot(0);
		player.getInventory().setItem(0, NavigationInventoryHelper.getNavigationTool());

		if (visible != null) {
			player.getInventory().setItem(1, ItemStackUtil.createItem(Material.INK_SACK, (short) (!visible ? 8 : 10), ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Player Visibility - " + (!visible ? ChatColor.RED + "OFF" : ChatColor.GREEN + "ON")));
		}

		player.getInventory().setItem(8, CosmeticsInventory.getOpenCosmeticInventoryItem());

		player.updateInventory();
	}

	public void joinQueue(final Player player, final String serverRegion, final String serverType, final String type, final String ladder) {
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				JSONObject payload = new JSONObject();

				payload.put("uuid", player.getUniqueId().toString());
				payload.put("type", type);
				payload.put("ladder", ladder);

				payload.put("server_type", serverType.toLowerCase());

				payload.put("server_region", serverRegion);
				payload.put("server_name", Gberry.serverName);

				System.out.println(payload);

				try {
					JSONObject response = Gberry.contactMCP("matchmaking-default-queue-up", payload);

					System.out.println(response);
					if (response.containsKey("success")) {
						// Yolo async access here
						SmellyLobby.this.playersInQueue.add(player.getUniqueId());

						player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
						player.sendMessage(ChatColor.GREEN + "Added to " + serverType + " matchmaking");
						player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));

						// Give the player the leave queue item
						BukkitUtil.runTask(new Runnable() {
							@Override
							public void run() {
								player.getInventory().setItem(0, ItemStackUtil.createItem(Material.REDSTONE, ChatColor.GREEN + "Leave Queue"));
								player.updateInventory();
							}
						});
					} else if (response.containsKey("error")) {
						String error = (String) response.get("error");

						if (error.equals("in_a_party")) {
							player.sendMessage(ChatColor.RED + "You must leave your party to join this queue!");
						} else if (error.equals("not_in_a_party")) {
							player.sendMessage(ChatColor.RED + "You must be in a party to join this queue!");
							player.sendMessage(ChatColor.RED + "You can create a party using the '/party' command!");
						} else if (error.equals("not_party_leader")) {
							player.sendMessage(ChatColor.RED + "You must be the party leader to join this queue!");
						} else if (error.equals("offline_party_member")) {
							player.sendMessage(ChatColor.RED + "All members of your party must be online to join this queue!");
						} else if (error.equals("too_few_in_party")) {
							player.sendMessage(ChatColor.RED + "You have too few players in your party!");
						} else if (error.equals("too_many_in_party")) {
							player.sendMessage(ChatColor.RED + "You have too many players in your party!");
						} else if (error.equals("already_in_queue")) {
							player.sendMessage(ChatColor.YELLOW + "Currently in queue. Wait for a match to start!");
						} else if (error.equals("already_in_different_game_queue")) {
							player.sendMessage(ChatColor.YELLOW + "Currently in queue for a different game. Please report this as a bug.");
						} else if (error.equals("already_in_game")) {
							player.sendMessage(ChatColor.YELLOW + "Currently in a different game. Please report this as a bug.");
						} else {
							player.sendMessage(ChatColor.YELLOW + "Cannot join queue for some unknown reason.");
							player.sendMessage(ChatColor.YELLOW + "Please report this as a bug with the following: " + error);
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "Cannot join queue for some unknown reason. Please report this as a bug.");
					}
				} catch (HTTPRequestFailException e) {
					SmellyLobby.getInstance().getLogger().warning(e.getResponseCode() + ": " + e.getResponse());
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * MUST BE CALLED ASYNC
	 */
	public void leaveQueue(final UUID uuid, final boolean verbose) {
		JSONObject data = new JSONObject();
		data.put("uuid", uuid.toString());

		try {
			JSONObject response = Gberry.contactMCP("matchmaking-default-remove", data);
			SmellyLobby.getInstance().getLogger().log(Level.INFO, "[sending remove queue]: " + data);
			SmellyLobby.getInstance().getLogger().log(Level.INFO, "[response remove queue]: " + response);

			// Yolo async access here
			this.playersInQueue.remove(uuid);

			BukkitUtil.runTask(new Runnable() {
				@Override
				public void run() {
					Player player = SmellyLobby.this.getServer().getPlayer(uuid);

					if (player != null) {
						SmellyLobby.getInstance().giveLobbyItems(player);

						if (verbose) {
							player.sendMessage(ChatColor.GREEN + "You have left the queue.");
						}
					}
				}
			});

			// Yolo async access here
			SmellyLobby.this.playersInQueue.remove(uuid);

		} catch (HTTPRequestFailException e) {
			SmellyLobby.getInstance().getLogger().warning(e.getResponseCode() + ": " + e.getResponse());
			e.printStackTrace();
		}
	}

	public void spawnNPCs() {
		World world = Bukkit.getWorld("world");

		String smellyTexture = "eyJ0aW1lc3RhbXAiOjE0NTc2OTc4MjIyODYsInByb2ZpbGVJZCI6ImFhM2NmZjFlN2M0NjQxNjRiODZhZGUzMTAzNzJhMTU1IiwicHJvZmlsZU5hbWUiOiJJcmlzaF85OSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTNjOWIyZmNjOWQ2ZDljYmM1NzllNzg3ZTdjZjg2ZWNlMjY4ZDhkMTUyNTY3MDQ4NmE2ZmU5ZDc0ZWE2ZWQiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ==";
		String smellySignature = "Sk1JhkWPmYcaz2MShDGDgyyVpS+d9Gy/q3Q5+UlCET6+ooDGX1f8VQ0oZj1WBfdnNC1agEfeq+vG2nqWUxFthb4GZJpu/8ojEwWmya6qTiu0lU4BZwToNxTHbqPGy7Zi1jxK5oGtPqF90b9whnm9l7pZGZCTHSWVaVqp6zmOftTkEX767MwEcst23Gr6LPD8/bmX+ZDaRMolbMQJIBt3L+1TPS0iDgA7MFKc2GBAoVAP79XkKp6YEasNZEY2UFkFqxWIcQVJrsD6Gvzu7bVCYXmm9sFgIKyaebeUPFTqfnxrCZWdKOGZwswnBGIJ5jSevDHgkkOrOUnt44XxkQ25DemRZhfE6AccVqbLaay+9UC7sFSxAT+wuioAaFWzIdstvw3ET487IXFFD7LAl1jT32nRsJ0yFpDHoM9yJmcsazgRPFOw0jf9yc7QXV61z+h3iYBsIeWdOil3+jxQTmW7+QJ2Y71zTcS8bki7zvVQiD/lRMPc7Md7bw6+LB1dbNV3vCMPOkiX8qvBYo9R9xBHy0v6qgaNA8OM34TS0BJyQ9sfWdJILwaWdH1+Aa3OfsI9arPJyLYTrL95skrSGlmPHn8jSIvxNOpgXRbfGqaXA4OgBJBPOTrhHaNEOMygD8ggNqDg+OjZ9WpjltymBBAJpRDD2i43ew6aNk67MV74D0w=";

		Location focusLocation = new Location(world, 0.5, 0, -5.5);

		Location location = this.getFocusAngleLocation(focusLocation, new Location(world, 4.5, 98.5, -14.5));
		Player arenaPvPNPC = world.spawnNPC(UUID.randomUUID(), ChatColor.GREEN + "Arena PvP", smellyTexture, smellySignature, location);

		location = this.getFocusAngleLocation(focusLocation, new Location(world, -3.5, 98.5, -14.5));
		Player hostedUHCNPC = world.spawnNPC(UUID.randomUUID(), ChatColor.YELLOW + "Hosted UHC", smellyTexture, smellySignature, location);

		location = this.getFocusAngleLocation(focusLocation, new Location(world, -5.5, 98.5, -13.5));
		Player sgNPC = world.spawnNPC(UUID.randomUUID(), ChatColor.DARK_RED + "Survival Games", smellyTexture, smellySignature, location);

		location = this.getFocusAngleLocation(focusLocation, new Location(world, -7.5, 98.5, -11.5));
		Player ffaNPC = world.spawnNPC(UUID.randomUUID(), ChatColor.BLUE + "Free For All", smellyTexture, smellySignature, location);

		location = this.getFocusAngleLocation(focusLocation, new Location(world, 6.5, 98.5, -13.5));
		Player miniUHCNPC = world.spawnNPC(UUID.randomUUID(), ChatColor.DARK_PURPLE + "Mini UHC", smellyTexture, smellySignature, location);

		location = this.getFocusAngleLocation(focusLocation, new Location(world, 8.5, 98.5, -11.5));
		Player uhcMeetupNPC = world.spawnNPC(UUID.randomUUID(), ChatColor.GOLD + "UHC Meetup", smellyTexture, smellySignature, location);


		arenaPvPNPC.setItemInHand(new ItemStack(Material.WATER_BUCKET));
		hostedUHCNPC.setItemInHand(new ItemStack(Material.GOLDEN_APPLE));
		sgNPC.setItemInHand(new ItemStack(Material.FISHING_ROD));
		ffaNPC.setItemInHand(new ItemStack(Material.GOLD_INGOT));
		miniUHCNPC.setItemInHand(new ItemStack(Material.APPLE));
		uhcMeetupNPC.setItemInHand(new ItemStack(Material.GOLDEN_APPLE));

		ItemStack[] arenaArmor = new ItemStack[4];
		ItemStack[] hostedUHCArmor = new ItemStack[4];
		ItemStack[] sgArmor = new ItemStack[4];
		ItemStack[] tournamentArmor = new ItemStack[4];
		ItemStack[] miniUHCArmor = new ItemStack[4];
		ItemStack[] uhcMeetupArmor = new ItemStack[4];

		arenaArmor[3] = new ItemStack(Material.DIAMOND_HELMET);
		arenaArmor[3].addEnchantment(Enchantment.DURABILITY, 3);
		arenaArmor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
		arenaArmor[2].addEnchantment(Enchantment.DURABILITY, 3);
		arenaArmor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
		arenaArmor[1].addEnchantment(Enchantment.DURABILITY, 3);
		arenaArmor[0] = new ItemStack(Material.DIAMOND_BOOTS);
		arenaArmor[0].addEnchantment(Enchantment.DURABILITY, 3);
		arenaPvPNPC.getInventory().setArmorContents(arenaArmor);

		hostedUHCArmor[3] = new ItemStack(Material.IRON_HELMET);
		hostedUHCArmor[3].addEnchantment(Enchantment.DURABILITY, 3);
		hostedUHCArmor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
		hostedUHCArmor[2].addEnchantment(Enchantment.DURABILITY, 3);
		hostedUHCArmor[1] = new ItemStack(Material.IRON_LEGGINGS);
		hostedUHCArmor[1].addEnchantment(Enchantment.DURABILITY, 3);
		hostedUHCArmor[0] = new ItemStack(Material.DIAMOND_BOOTS);
		hostedUHCArmor[0].addEnchantment(Enchantment.DURABILITY, 3);
		hostedUHCNPC.getInventory().setArmorContents(hostedUHCArmor);

		sgArmor[3] = new ItemStack(Material.GOLD_HELMET);
		sgArmor[2] = new ItemStack(Material.IRON_CHESTPLATE);
		sgArmor[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS);
		sgArmor[0] = new ItemStack(Material.IRON_BOOTS);
		sgNPC.getInventory().setArmorContents(sgArmor);

		tournamentArmor[3] = new ItemStack(Material.GOLD_HELMET);
		tournamentArmor[3].addEnchantment(Enchantment.DURABILITY, 3);
		tournamentArmor[2] = new ItemStack(Material.GOLD_CHESTPLATE);
		tournamentArmor[2].addEnchantment(Enchantment.DURABILITY, 3);
		tournamentArmor[1] = new ItemStack(Material.GOLD_LEGGINGS);
		tournamentArmor[1].addEnchantment(Enchantment.DURABILITY, 3);
		tournamentArmor[0] = new ItemStack(Material.GOLD_BOOTS);
		tournamentArmor[0].addEnchantment(Enchantment.DURABILITY, 3);
		ffaNPC.getInventory().setArmorContents(tournamentArmor);

		miniUHCArmor[3] = new ItemStack(Material.IRON_HELMET);
		miniUHCArmor[3].addEnchantment(Enchantment.DURABILITY, 3);
		miniUHCArmor[2] = new ItemStack(Material.IRON_CHESTPLATE);
		miniUHCArmor[2].addEnchantment(Enchantment.DURABILITY, 3);
		miniUHCArmor[1] = new ItemStack(Material.IRON_LEGGINGS);
		miniUHCArmor[1].addEnchantment(Enchantment.DURABILITY, 3);
		miniUHCArmor[0] = new ItemStack(Material.IRON_BOOTS);
		miniUHCArmor[0].addEnchantment(Enchantment.DURABILITY, 3);
		miniUHCNPC.getInventory().setArmorContents(miniUHCArmor);

		uhcMeetupArmor[3] = new ItemStack(Material.GOLD_HELMET);
		uhcMeetupArmor[3].addEnchantment(Enchantment.DURABILITY, 3);
		uhcMeetupArmor[2] = new ItemStack(Material.IRON_CHESTPLATE);
		uhcMeetupArmor[2].addEnchantment(Enchantment.DURABILITY, 3);
		uhcMeetupArmor[1] = new ItemStack(Material.GOLD_LEGGINGS);
		uhcMeetupArmor[1].addEnchantment(Enchantment.DURABILITY, 3);
		uhcMeetupArmor[0] = new ItemStack(Material.IRON_BOOTS);
		uhcMeetupArmor[0].addEnchantment(Enchantment.DURABILITY, 3);
		uhcMeetupNPC.getInventory().setArmorContents(uhcMeetupArmor);

		SmellyLobby.npcs.add(arenaPvPNPC);
		SmellyLobby.npcs.add(hostedUHCNPC);
		SmellyLobby.npcs.add(sgNPC);
		SmellyLobby.npcs.add(ffaNPC);
		SmellyLobby.npcs.add(miniUHCNPC);
		SmellyLobby.npcs.add(uhcMeetupNPC);

		for (Player pl : SmellyLobby.npcs) {
			SmellyLobby.npcsUUIDs.add(pl.getUniqueId());
		}
	}

	private Location getFocusAngleLocation(Location focusLocation, Location location) {
		float yaw = (float) (90 + (180 * Math.atan2(location.getZ() - focusLocation.getZ(), location.getX() - focusLocation.getX()) / Math.PI));
		location.setYaw(yaw);

		return location;
	}

	public static void tryEnableFlight(Player player, UserDataManager.UserData userData) {
		if (!player.hasPermission("badlion.staff") && !player.hasPermission("badlion.donatorplus")) {
			return;
		}

		//Can't fly when morphed (We dont need this on arena lobby do we??)
		if (CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).getActiveMorph() != null) {
			player.sendMessage(ChatColor.RED + "You can't fly when morphed!");
			return;
		}


		if (userData.isLobbyFlight()) {
			player.sendMessage(ChatColor.GREEN + "Flight mode enabled. Use /fly to toggle flight.");
			player.setAllowFlight(true);
		}
	}


	public static SmellyLobby getInstance() {
		return SmellyLobby.plugin;
	}

	public Set<UUID> getPlayersInQueue() {
		return this.playersInQueue;
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}

	public Location getSpawnLocation() {
		return this.spawnLocation;
	}

	public List<String> getMessageOfTheDay() {
		return this.messageOfTheDay;
	}

	// apply a player's skin to a clone of a game profile
	private static Object gameProfileWithPlayersSkin(Object gameProfile, Player player) {
		UUID uuid = TinyProtocolReferences.gameProfileUUID.get(gameProfile);
		String name = TinyProtocolReferences.gameProfileName.get(gameProfile);
		Object playerProfile = TinyProtocolReferences.getPlayerProfile.invoke(player);
		Object playerProperties = TinyProtocolReferences.gameProfilePropertyMap.get(playerProfile);
		Collection<Object> propertyCollection = (Collection<Object>) TinyProtocolReferences.propertyMapGet.invoke(playerProperties, "textures");
		Object newProfile = TinyProtocolReferences.gameProfileConstructor.invoke(uuid, name);
		if (!propertyCollection.isEmpty()) {
			Object properties = TinyProtocolReferences.gameProfilePropertyMap.get(newProfile);
			TinyProtocolReferences.propertyMapPut.invoke(properties, "textures", propertyCollection.iterator().next());
		}
		return newProfile;
	}
}
