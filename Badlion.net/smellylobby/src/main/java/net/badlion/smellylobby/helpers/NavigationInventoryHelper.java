package net.badlion.smellylobby.helpers;

import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.smellylobby.SmellyLobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationInventoryHelper {

	private static ItemStack navigationTool;

	public static Inventory arenaPvPInventory;
	public static Inventory practiceInventory;
	public static Inventory uhcInventory;
	public static Inventory uhcMeetupInventory;
	public static Inventory miniUHCInventory;
	public static Inventory sgInventory;
	public static Inventory factionsInventory;
	public static Inventory kohiGamesInventory;
	public static Inventory vaultBattleInventory;
	public static Inventory ffaInventory;

	private static SmellyInventory smellyInventory;

	private static Map<SmellyInventory.FakeHolder, Map<Integer, String>> bungeeServerNames = new HashMap<>();

	static {
		// Create navigation tool
		NavigationInventoryHelper.navigationTool = ItemStackUtil.createItem(Material.WATCH, ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Navigation",
				ChatColor.GREEN + "Right click with this item", ChatColor.GREEN + "in hand to join a server.");
	}

	public static ItemStack getNavigationTool() {
		return navigationTool;
	}

	public static void openNavigationInventory(Player player) {
		BukkitUtil.openInventory(player, NavigationInventoryHelper.smellyInventory.getMainInventory());
	}

	public static void openArenaPvPInventory(Player player) {
		if (NavigationInventoryHelper.arenaPvPInventory != null) {
			BukkitUtil.openInventory(player, NavigationInventoryHelper.arenaPvPInventory);
		} else {
			player.sendMessage(ChatColor.DARK_RED + "Feature currently disabled");
		}
	}

	public static void openUHCInventory(Player player) {
		if (NavigationInventoryHelper.uhcInventory != null) {
			BukkitUtil.openInventory(player, NavigationInventoryHelper.uhcInventory);
		} else {
			player.sendMessage(ChatColor.DARK_RED + "Feature currently disabled");
		}
	}

	public static void openMiniUHCInventory(Player player) {
		if (NavigationInventoryHelper.miniUHCInventory != null) {
			BukkitUtil.openInventory(player, NavigationInventoryHelper.miniUHCInventory);
		} else {
			player.sendMessage(ChatColor.DARK_RED + "Feature currently disabled");
		}
	}

	public static void openFFAInventory(Player player) {
		if (NavigationInventoryHelper.ffaInventory != null) {
			BukkitUtil.openInventory(player, NavigationInventoryHelper.ffaInventory);
		} else {
			player.sendMessage(ChatColor.DARK_RED + "Feature currently disabled");
		}
	}

	public static void openUnrankedSGInventory(Player player) {
		if (NavigationInventoryHelper.sgInventory != null) {
			BukkitUtil.openInventory(player, NavigationInventoryHelper.sgInventory);
		} else {
			player.sendMessage(ChatColor.DARK_RED + "Feature currently disabled");
		}
	}

	public static void openUHCMeetupInventory(Player player) {
		if (NavigationInventoryHelper.uhcMeetupInventory != null) {
			BukkitUtil.openInventory(player, NavigationInventoryHelper.uhcMeetupInventory);
		} else {
			player.sendMessage(ChatColor.DARK_RED + "Feature currently disabled");
		}
	}

	public static void setSmellyInventory(SmellyInventory smellyInventory) {
		NavigationInventoryHelper.smellyInventory = smellyInventory;
	}

	public static Inventory getArenaPvPInventory() {
		return NavigationInventoryHelper.arenaPvPInventory;
	}

	public static void setArenaPvPInventory(Inventory inventory) {
		NavigationInventoryHelper.arenaPvPInventory = inventory;
	}

	public static Inventory getUHCInventory() {
		return NavigationInventoryHelper.uhcInventory;
	}

	public static void setUHCInventory(Inventory inventory) {
		NavigationInventoryHelper.uhcInventory = inventory;
	}

	public static Inventory getUHCMeetupInventory() {
		return NavigationInventoryHelper.uhcMeetupInventory;
	}

	public static void setUHCMeetupInventory(Inventory inventory) {
		NavigationInventoryHelper.uhcMeetupInventory = inventory;
	}

	public static Inventory getFactionsInventory() {
		return factionsInventory;
	}

	public static void setFactionsInventory(Inventory factionsInventory) {
		NavigationInventoryHelper.factionsInventory = factionsInventory;
	}

	public static Inventory getPracticeInventory() {
		return practiceInventory;
	}

	public static void setPracticeInventory(Inventory practiceInventory) {
		NavigationInventoryHelper.practiceInventory = practiceInventory;
	}

	public static Inventory getMiniUHCInventory() {
		return NavigationInventoryHelper.miniUHCInventory;
	}

	public static void setMiniUHCInventory(Inventory inventory) {
		NavigationInventoryHelper.miniUHCInventory = inventory;
	}

	public static Inventory getSGInventory() {
		return NavigationInventoryHelper.sgInventory;
	}

	public static void setSGInventory(Inventory inventory) {
		NavigationInventoryHelper.sgInventory = inventory;
	}

	public static Inventory getKohiGamesInventory() {
		return kohiGamesInventory;
	}

	public static void setKohiGamesInventory(Inventory kohiGamesInventory) {
		NavigationInventoryHelper.kohiGamesInventory = kohiGamesInventory;
	}

	public static Inventory getVaultBattleInventory() {
		return vaultBattleInventory;
	}

	public static void setVaultBattleInventory(Inventory vaultBattleInventory) {
		NavigationInventoryHelper.vaultBattleInventory = vaultBattleInventory;
	}

	public static Inventory getFFAInventory() {
		return ffaInventory;
	}

	public static void setFFAInventory(Inventory ffaInventory) {
		NavigationInventoryHelper.ffaInventory = ffaInventory;
	}

	public static void addBungeeServerName(SmellyInventory.FakeHolder fakeHolder, int slot, String serverName) {
		Map<Integer, String> map = NavigationInventoryHelper.bungeeServerNames.get(fakeHolder);

		if (map == null) {
			map = new HashMap<>();
			NavigationInventoryHelper.bungeeServerNames.put(fakeHolder, map);
		}

		map.put(slot, serverName);
	}

	public static String getBungeeServerName(SmellyInventory.FakeHolder fakeHolder, int slot) {
		Map<Integer, String> map = NavigationInventoryHelper.bungeeServerNames.get(fakeHolder);

		if (map == null) return null;

		return NavigationInventoryHelper.bungeeServerNames.get(fakeHolder).get(slot);
	}

	public static ItemStack createMiniUHCItem(String name, long playerCount, String state, boolean inCountdown, long teamSize, JSONArray gamemodes) {
		short data;
		ChatColor displayNameColor;
		if (state.equals("PRE_START")) {
			//if (in_countdown) {
			//temp fix until I feel like figuring this out
			if (playerCount >= 24) {
				data = (short) 10;
				displayNameColor = ChatColor.LIGHT_PURPLE;
			} else {
				data = (short) 5;
				displayNameColor = ChatColor.GREEN;
			}
		} else {
			data = (short) 14;
			displayNameColor = ChatColor.RED;
		}

		List<String> lore = new ArrayList<>();
		if (teamSize == 1) {
			lore.add(ChatColor.WHITE + "FFA");
		} else {
			lore.add(ChatColor.WHITE + "To2");
		}
		String gamemodeString = "";
		List<String> gamemodesList = (List<String>) gamemodes;
		for (String gamemode : gamemodesList) {
			gamemodeString += gamemode.substring(0, 1).toUpperCase() + gamemode.substring(1, gamemode.length()) + ", ";
		}
		lore.add(ChatColor.RED + gamemodeString.substring(0, gamemodeString.length() - 2));
		lore.add(ChatColor.GREEN + "Players: " + playerCount + "/32");

		return ItemStackUtil.createItem(Material.WOOL, (int) playerCount, data, displayNameColor + name, lore);
	}

	public static ItemStack createUnrankedSGItem(String name, long playerCount, String status) {
		short data;
		ChatColor displayNameColor;
		if (status.equals("waiting")) {
			data = (short) 5;
			displayNameColor = ChatColor.GREEN;
		} else if (status.equals("voting")) {
			data = (short) 10;
			displayNameColor = ChatColor.LIGHT_PURPLE;
		} else {
			data = (short) 14;
			displayNameColor = ChatColor.RED;
		}

		return ItemStackUtil.createItem(Material.WOOL, (int) playerCount, data, displayNameColor + name,
				ChatColor.GREEN + "Players: " + playerCount + "/24");
	}

	public static ItemStack createVBItem(String name, long playerCount, String status, boolean inCountdown) {
		short data;
		ChatColor displayNameColor;
		List<String> lore = new ArrayList<>();
		if (status.equals("PRE_START")) {
			if (inCountdown) {
				data = (short) 10;
				displayNameColor = ChatColor.LIGHT_PURPLE;
				lore.add(ChatColor.LIGHT_PURPLE + "Game in progress, but you can still join!");
			} else {
				data = (short) 5;
				displayNameColor = ChatColor.GREEN;
				lore.add(ChatColor.GREEN + "Starts when 5 or more players are on each team");
			}
		} else {
			data = (short) 14;
			displayNameColor = ChatColor.RED;
			lore.add(ChatColor.RED + "Game in progress, click to spectate");
		}

		lore.add("");
		lore.add(ChatColor.GREEN + "Players: " + playerCount + "/120");
		return ItemStackUtil.createItem(Material.WOOL, (int) Math.min(playerCount, 64), data,
				displayNameColor + name.toUpperCase(), lore);
	}

	public static ItemStack createKohiGamesItem(String name, long playerCount, String status, boolean inCountdown) {
		short data;
		ChatColor displayNameColor;
		List<String> lore = new ArrayList<>();
		if (status.equals("PRE_START")) {
			if (inCountdown) {
				data = (short) 10;
				displayNameColor = ChatColor.LIGHT_PURPLE;
				lore.add(ChatColor.LIGHT_PURPLE + "Starting soon");
			} else {
				data = (short) 5;
				displayNameColor = ChatColor.GREEN;
				lore.add(ChatColor.GREEN + "Starts when 15 or more players are online");
			}
		} else {
			data = (short) 14;
			displayNameColor = ChatColor.RED;
			lore.add(ChatColor.RED + "Game in progress, click to spectate");
		}

		lore.add("");
		lore.add(ChatColor.GREEN + "Players: " + playerCount + "/40");
		return ItemStackUtil.createItem(Material.WOOL, (int) Math.min(playerCount, 64), data,
				displayNameColor + name.toUpperCase(), lore);
	}

	public static abstract class APIQueryTask extends BukkitRunnable {

		private String apiURL;

		public Inventory inventoryOne;
		public Inventory inventoryTwo;

		public APIQueryTask(String apiURL, Inventory inventoryOne) {
			this(apiURL, inventoryOne, null);
		}

		public APIQueryTask(String apiURL, Inventory inventoryOne, Inventory inventoryTwo) {
			this.apiURL = apiURL;

			this.inventoryOne = inventoryOne;
			this.inventoryTwo = inventoryTwo;

			// Run the task async
			this.runTaskTimerAsynchronously(SmellyLobby.getInstance(), 100L, 100L);
		}

		@Override
		public void run() {
			try {
				final JSONObject response = HTTPCommon.executeGETRequest(this.apiURL);

				if (response == null) {
					Bukkit.getLogger().info("JSONObject response NULL for " + this.apiURL);
					return;
				}

				SmellyLobby.getInstance().getServer().getScheduler().runTask(SmellyLobby.getInstance(), new Runnable() {
					@Override
					public void run() {
						APIQueryTask.this.run(response);
					}
				});
			} catch (HTTPRequestFailException e) {
				Bukkit.getLogger().info("Exception with HTTP code " + e.getResponseCode());
			}
		}

		/**
		 * This is always called synchronously
		 *
		 * @param response - HTTP Get request
		 */
		public abstract void run(JSONObject response);

	}

	public static abstract class MCPQueryTask extends BukkitRunnable {

		private String type;
		public Inventory inventory;

		public MCPQueryTask(String type, Inventory inventory) {
			this.type = type;
			this.inventory = inventory;
			this.runTaskTimerAsynchronously(SmellyLobby.getInstance(), 100L, 100L);
		}

		@Override
		public void run() {
			JSONObject payload = new JSONObject();
			payload.put("type", type);
			try {
				final JSONObject response = Gberry.contactMCP("lobby-get-servers", payload);
				new BukkitRunnable() {
					@Override
					public void run() {
						MCPQueryTask.this.run(response);
					}
				}.runTask(SmellyLobby.getInstance());
			} catch (HTTPRequestFailException ex) {
				SmellyLobby.getInstance().getLogger().warning("MCPQueryTask failed with HTTP code " + ex.getResponse());
			}
		}

		public abstract void run(JSONObject response);
	}

	public static void temporarySendToS13(final Player player, final String region) {
		// Hardcodes for now

		// Don't allow non-SA bungee to connect to SA
		if (region.equalsIgnoreCase("sa") && Gberry.serverRegion != Gberry.ServerRegion.SA) {
			player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString()
					+ "You can only join South American servers through the sa.badlion.net IP!");

			BukkitUtil.closeInventory(player);

			return;
		}

		// Don't allow non-AS bungee to connect to AS
		if (region.equalsIgnoreCase("as") && Gberry.serverRegion != Gberry.ServerRegion.AS) {
			player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString()
					+ "You can only join Asian servers through the asia.badlion.net IP!");

			BukkitUtil.closeInventory(player);

			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				String url = "http://" + GetCommon.getIpForDB() + ":9000/arena-get-lobbies/IVxbY9cf9e8Bsqp9UpJqQVgiLvWmhi1dPEFpcI1a";
				JSONObject data = new JSONObject();
				data.put("region", region);
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
						player.sendMessage(ChatColor.RED + "Season 13 is offline right now.");
					} else {
						final String server = (String) bestLobby.get("name");
						new BukkitRunnable() {
							@Override
							public void run() {
								Gberry.sendToServer(player, server);
							}
						}.runTask(SmellyLobby.getInstance());
					}
				} catch (HTTPRequestFailException e) {
					Gberry.plugin.getLogger().info(e.getType().name());
					Gberry.plugin.getLogger().info(e.getResponseCode() + "");
					Gberry.plugin.getLogger().info(e.getResponse());
					player.sendMessage(ChatColor.RED + "There was an error sending you to Season 13, try again in a few seconds or contact an admin if symptoms persist");
				}
			}
		}.runTaskAsynchronously(SmellyLobby.getInstance());
	}

	public static void sendMPGLobby(final Player player, final String region, final String gamemode) {
		// Hardcodes for now

		// Don't allow non-SA bungee to connect to SA
		if (region.equalsIgnoreCase("sa") && Gberry.serverRegion != Gberry.ServerRegion.SA) {
			player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString()
					+ "You can only join South American servers through the sa.badlion.net IP!");

			BukkitUtil.closeInventory(player);

			return;
		}

		// Don't allow non-AS bungee to connect to AS
		if (region.equalsIgnoreCase("as") && Gberry.serverRegion != Gberry.ServerRegion.AS) {
			player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString()
					+ "You can only join Asian servers through the asia.badlion.net IP!");

			BukkitUtil.closeInventory(player);

			return;
		}

		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				JSONObject payload = new JSONObject();

				payload.put("region", region);
				payload.put("gamemode", gamemode);

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
					player.sendMessage(ChatColor.RED + "Servers are offline right now.");
				} else {
					final String server = (String) bestLobby.get("name");
					new BukkitRunnable() {
						@Override
						public void run() {
							Gberry.sendToServer(player, server);
						}
					}.runTask(SmellyLobby.getInstance());
				}
			}
		});
	}
}
