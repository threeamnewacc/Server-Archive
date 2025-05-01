package net.badlion.arenalobby.inventories.lobby;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LobbySelectorInventory {

	private static SmellyInventory smellyInventory;

	private static Map<Integer, String> lobbyServers = new HashMap<>();

	public static void initialize() {
		SmellyInventory smellyInventory = new SmellyInventory(new LobbySelectorClickHandler(), 54,
				ChatColor.AQUA + "Arena Lobby Selector");

		smellyInventory.getFakeHolder().setParentInventory(SettingsInventory.smellyInventory.getMainInventory());

		// Fill with the kit rule set items
		LobbySelectorInventory.smellyInventory = smellyInventory;
	}

	public static void openLobbySelectorMenu(Player player) {
		BukkitUtil.openInventory(player, LobbySelectorInventory.smellyInventory.getMainInventory());
	}

	// Json data should be [{"name":"naarenalobby1", "online": 24}, {"name":"naarenalobby5", "online": 25}, ...]
	public static void updateLobbyInventory(JSONArray lobbyData) {
		LobbySelectorInventory.smellyInventory.getMainInventory().clear();
		LobbySelectorInventory.lobbyServers.clear();

		List<JSONObject> jsonObjects = new ArrayList<>();
		for (Object object : lobbyData) {
			JSONObject jsonObject = (JSONObject) object;
			String serverName = (String) jsonObject.get("name");
			int serverNumber = 0;
			Pattern pattern = Pattern.compile("[0-9]+$");
			Matcher match = pattern.matcher(serverName);
			if (match.find()) {
				serverNumber = Integer.parseInt(match.group());
			}
			jsonObject.put("server_number", serverNumber);
			jsonObjects.add(jsonObject);
		}
		Collections.sort(jsonObjects, new SortServerNumbers());

		int i = 0;
		for (JSONObject jsonObject : jsonObjects) {
			String serverName = (String) jsonObject.get("name");
			Long online = (Long) jsonObject.get("online");
			int serverNumber = (int) jsonObject.get("server_number");
			int slot = i;
			LobbySelectorInventory.lobbyServers.put(slot, serverName);
			ItemStack item = ItemStackUtil.createItem(Material.WOOL, ChatColor.BLUE + "Arena Lobby: " + ChatColor.GREEN + "#" + serverNumber);
			item.setDurability((short) (serverNumber % 16));
			item.setAmount((serverNumber <= 0) ? 1 : (serverNumber > 64) ? 64 : serverNumber);
			ItemMeta itemMeta = item.getItemMeta();
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "Online: " + online);
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);
			LobbySelectorInventory.smellyInventory.getMainInventory().setItem(slot, item);
			i++;
		}
		LobbySelectorInventory.smellyInventory.getMainInventory().setItem(53, SmellyInventory.getBackInventoryItem());
	}

	public static class SortServerNumbers implements Comparator<JSONObject> {

		@Override
		public int compare(JSONObject firstServer, JSONObject secondServer) {
			try {
				return ((int) firstServer.get("server_number") > (int) secondServer.get("server_number")) ? 1 : ((int) firstServer.get("server_number") < (int) secondServer.get("server_number")) ? -1 : 0;
			} catch (Exception e) {
			}
			return 0;

		}
	}

	private static class LobbySelectorClickHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (LobbySelectorInventory.lobbyServers.containsKey(slot)) {
				String serverName = LobbySelectorInventory.lobbyServers.get(slot);
				if (serverName != null) {
					player.sendFormattedMessage("{0}Connecting...", ChatColor.YELLOW + ChatColor.BOLD.toString());
					Gberry.sendToServer(player, serverName);
					BukkitUtil.closeInventory(player);
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
}
