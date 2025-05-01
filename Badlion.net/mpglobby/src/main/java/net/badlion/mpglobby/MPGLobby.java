package net.badlion.mpglobby;

import net.badlion.cosmetics.inventories.CosmeticsInventory;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gpermissions.GPermissions;
import net.badlion.mpglobby.commands.LeaveCommand;
import net.badlion.mpglobby.inventories.GameQueueInventory;
import net.badlion.mpglobby.inventories.SettingsInventory;
import net.badlion.mpglobby.listeners.LobbyListener;
import net.badlion.mpglobby.tasks.UpdateQueueCountTask;
import net.badlion.mpglobby.tasks.VoidCheckerTask;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MPGLobby extends JavaPlugin {

	public static final int COOLDOWN_TIME = 2000;

	public static String QUEUE_INVENTORY_NAME = "MPG";
	public static String MOTD_DESCRIPTION = "Badlion MPG";

	private static MPGLobby plugin;

	protected Location spawnLocation;

	protected Location leaveQueueSignLocation;

	private Map<UUID, QueueType> playersInQueue = new HashMap<>();

	private Map<UUID, Long> settingChangeCooldown = new HashMap<>();

	public MPGLobby() {
		MPGLobby.plugin = this;
	}

    @Override
    public void onEnable() {
	    Gberry.enableAsyncQuitEvent = true;

	    SmellyInventory.initialize(this, false);

	    GameQueueInventory.initialize();

	    this.getServer().getPluginManager().registerEvents(new LobbyListener(), this);

	    this.getCommand("leave").setExecutor(new LeaveCommand());

	    new VoidCheckerTask().runTaskTimer(this, 1L, 1L);

	    new UpdateQueueCountTask().runTaskTimerAsynchronously(this, 20L, 20L);
    }

    @Override
    public void onDisable() {
        // Remove all players from queues
	    for (Player player : this.getServer().getOnlinePlayers()) {
		    this.leaveQueue(player, false);
	    }
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

	public boolean hasCooldown(UUID uuid) {
		Long time = this.settingChangeCooldown.get(uuid);

		if (time == null || System.currentTimeMillis() >= time + MPGLobby.COOLDOWN_TIME) {
			this.settingChangeCooldown.put(uuid, System.currentTimeMillis());
			return false;
		}

		return true;
	}

	public void teleportToSpawnAndGiveItems(Player player) {
		player.teleport(this.spawnLocation);

		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.setExhaustion(0);

		player.setGameMode(GameMode.SURVIVAL);

		player.getInventory().clear();
		player.getInventory().setArmorContents(null);

		player.getInventory().setHeldItemSlot(0);

		player.getInventory().setItem(0, ItemStackUtil.createItem(Material.COMPASS, ChatColor.AQUA + "Join a Game"));

		// TODO: MOVE THIS FROM SMELLYLOBBY INTO GBERRY
		// Player visibility item
		//player.getInventory().setItem(1, ItemStackUtil.createItem(Material.REDSTONE_COMPARATOR, ChatColor.AQUA + "Hide/Show Players"));

		// Settings item
		player.getInventory().setItem(7, SettingsInventory.getInstance().getOpenSettingsInventoryItem());

		// Cosmetics item
		player.getInventory().setItem(8, CosmeticsInventory.getOpenCosmeticInventoryItem());

		player.updateInventory();
	}

	/**
	 * NOTE: THIS IS CALLED ASYNC
	 */
	public String checkIfInMatch(final UUID uuid) {
		JSONObject payload = new JSONObject();

		payload.put("uuid", uuid.toString());

		JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.MATCHMAKING_DEFAULT_CHECK_GAME, payload);

		if (response.containsKey("true")) {
			return (String) response.get("true");
		}

		return null;
	}

	public void joinQueue(Player player, QueueType queueType) {
		MPGLobby.getInstance().joinQueue(player, queueType, false);
	}

	public void joinQueue(final Player player, final QueueType queueType, final boolean testRegion) {
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				JSONObject payload = new JSONObject();

				payload.put("uuid", player.getUniqueId().toString());
				//payload.put("server_type", Gberry.serverType.getInternalName());
				payload.put("type", queueType.getGameType().name().toLowerCase());
				payload.put("ladder", queueType.getLadder());
				payload.put("server_name", Gberry.serverName);
				payload.put("server_type", Gberry.serverType.getInternalName());

				if (testRegion) {
					payload.put("server_region", Gberry.ServerRegion.DE.name().toLowerCase());
				} else {
					payload.put("server_region", Gberry.serverRegion.name().toLowerCase());
				}

				System.out.println(payload);

				JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.MATCHMAKING_DEFAULT_QUEUE_UP, payload);

				System.out.println(response);
				if (response.containsKey("success")) {
					// Yolo async access here
					MPGLobby.this.playersInQueue.put(player.getUniqueId(), queueType);

					player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
					player.sendMessage(ChatColor.GREEN + "Added to " + queueType.getName() + " matchmaking");
					player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));

					// Give the player the leave queue item
					BukkitUtil.runTask(new Runnable() {
						@Override
						public void run() {
							player.getInventory().setItem(6, ItemStackUtil.createItem(Material.REDSTONE, ChatColor.GREEN + "Leave Queue"));
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
					} else if (error.startsWith("offline_party_member")) {
						String username = error.split(" ")[1];

						player.sendMessage(ChatColor.RED + "All members of your party must be online to join this queue, " + username + " is offline!");
					} else if (error.equals("too_few_in_party")) {
						player.sendMessage(ChatColor.RED + "You have too few players in your party!");
					} else if (error.equals("too_many_in_party")) {
						player.sendMessage(ChatColor.RED + "You have too many players in your party!");
					} else if (error.startsWith("already_in_queue")) {
						String username = error.split(" ")[1];

						if (player.getDisguisedName().equalsIgnoreCase(username)) {
							player.sendMessage(ChatColor.YELLOW + "Currently in queue. Wait for a match to start!");
						} else {
							player.sendMessage(ChatColor.YELLOW + username + ChatColor.RED + " is already in a queue.");
						}
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
			}
		});
	}

	public void leaveQueue(Player player, boolean verbose) {
		this.leaveQueue(player.getUniqueId(), verbose);
	}

	public void leaveQueue(final UUID uuid, final boolean verbose) {
		final Player player = this.getServer().getPlayer(uuid);

		if (player != null) {
			// Remove the leave queue item from their inventory
			player.getInventory().setItem(6, null);

			player.updateInventory();
		}

		this.playersInQueue.remove(uuid);

		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {      // TODO: HOW IS THIS WORKING ON DISABLE? PRETTY SURE ITS JUST THE LISTENER?
				JSONObject payload = new JSONObject();

				payload.put("uuid", uuid.toString());

				System.out.println(payload);

				JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.MATCHMAKING_DEFAULT_REMOVE, payload);

				System.out.println(response);

				if (response.containsKey("error")) {
					if (verbose && player != null) {
						player.sendMessage(ChatColor.RED + "You are not in a queue.");
					}
				} else if (response.containsKey("success")) {
					if (player != null) {
						if (verbose) {
							player.sendMessage(ChatColor.GREEN + "You have left the queue.");
						}
					}
				}
			}
		});
	}

	public static MPGLobby getInstance() {
		return MPGLobby.plugin;
	}

	public Location getSpawnLocation() {
		return this.spawnLocation;
	}

	public Map<UUID, QueueType> getPlayersInQueue() {
		return this.playersInQueue;
	}

	public Location getLeaveQueueSignLocation() {
		return this.leaveQueueSignLocation;
	}

}
