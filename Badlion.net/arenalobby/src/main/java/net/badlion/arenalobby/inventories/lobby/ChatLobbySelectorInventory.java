package net.badlion.arenalobby.inventories.lobby;

import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
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

public class ChatLobbySelectorInventory {

	private static SmellyInventory smellyInventory;

	private static Map<Integer, String> chatLobbies = new HashMap<>();

	public static void initialize() {
		SmellyInventory smellyInventory = new SmellyInventory(new ChatLobbySelectorClickHandler(), 18,
				ChatColor.AQUA + "Arena Chat Lobby Selector");

		smellyInventory.getFakeHolder().setParentInventory(SettingsInventory.smellyInventory.getMainInventory());

		// Fill with the kit rule set items
		ChatLobbySelectorInventory.smellyInventory = smellyInventory;
	}

	public static void openChatLobbySelectorMenu(Player player) {
		BukkitUtil.openInventory(player, ChatLobbySelectorInventory.smellyInventory.getMainInventory());
	}

	// Json data should be [{"name":"chatlobby1", "online": 24}, {"name":"chatlobby2", "online": 25}, ...]
	public static void updateChatLobbyInventory(JSONArray lobbyData) {
		ChatLobbySelectorInventory.smellyInventory.getMainInventory().clear();
		ChatLobbySelectorInventory.chatLobbies.clear();

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
			jsonObject.put("chat_lobby_number", serverNumber);
			jsonObjects.add(jsonObject);
		}
		Collections.sort(jsonObjects, new SortServerNumbers());

		// Public chat lobbies
		int i = 1; // Slot 0 reserved for premium chat lobby
		for (JSONObject jsonObject : jsonObjects) {
			if (i > 17) break;

			String chatLobby = (String) jsonObject.get("name");
			Long online = (Long) jsonObject.get("online");
			int chatLobbyNumber = (int) jsonObject.get("chat_lobby_number");

			// Premium chat lobby check
			if (chatLobby.toLowerCase().contains("premium")) {
				ChatLobbySelectorInventory.chatLobbies.put(0, "premium_chat_lobby");

				ItemStack item = ItemStackUtil.createItem(Material.GOLD_INGOT, ChatColor.AQUA + "Premium Chat Lobby");
				ItemMeta itemMeta = item.getItemMeta();
				List<String> itemLore = new ArrayList<>();
				itemLore.add(ChatColor.BLUE + "Online: " + online);
				itemMeta.setLore(itemLore);
				item.setItemMeta(itemMeta);

				ChatLobbySelectorInventory.smellyInventory.getMainInventory().setItem(0, item);
				continue;
			}

			ChatLobbySelectorInventory.chatLobbies.put(i, chatLobby);

			ItemStack item = ItemStackUtil.createItem(Material.STAINED_CLAY, ChatColor.BLUE + "Arena Chat Lobby: " + ChatColor.GREEN + "#" + chatLobbyNumber);
			item.setDurability((short) (chatLobbyNumber % 16));
			item.setAmount((chatLobbyNumber <= 0) ? 1 : (chatLobbyNumber > 64) ? 64 : chatLobbyNumber);
			ItemMeta itemMeta = item.getItemMeta();
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "Online: " + online);
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);

			ChatLobbySelectorInventory.smellyInventory.getMainInventory().setItem(i, item);

			i++;
		}

		ChatLobbySelectorInventory.smellyInventory.getMainInventory().setItem(17, SmellyInventory.getBackInventoryItem());
	}

	public static class SortServerNumbers implements Comparator<JSONObject> {

		@Override
		public int compare(JSONObject firstServer, JSONObject secondServer) {
			try {
				return ((int) firstServer.get("chat_lobby_number") > (int) secondServer.get("chat_lobby_number")) ? 1 : ((int) firstServer.get("chat_lobby_number") < (int) secondServer.get("chat_lobby_number")) ? -1 : 0;
			} catch (Exception e) {
			}
			return 0;

		}
	}

	private static class ChatLobbySelectorClickHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (ChatLobbySelectorInventory.chatLobbies.containsKey(slot)) {
				int chatLobbyNumber = -1;
				final String chatLobbyName = ChatLobbySelectorInventory.chatLobbies.get(slot);

				BukkitUtil.closeInventory(player);

				if (chatLobbyName.contains("premium")) {
					player.sendFormattedMessage("{0}Joining Chat Lobby {1}Premium{2}...", ChatColor.GOLD, ChatColor.AQUA, ChatColor.GOLD );
				} else {
					String[] split = chatLobbyName.split("_");
					chatLobbyNumber = Integer.valueOf(split[split.length - 1]);

					player.sendFormattedMessage("{0}Joining Chat Lobby {1}...", ChatColor.GOLD, ChatColor.AQUA + String.valueOf(chatLobbyNumber) + ChatColor.GOLD);
				}

				final int chatLobbyNumberFinal = chatLobbyNumber;

				// Send MCP request
				BukkitUtil.runTaskAsync(new Runnable() {
					@Override
					public void run() {
						JSONObject payload = new JSONObject();

						payload.put("uuid", player.getUniqueId().toString());
						payload.put("chat_lobby", chatLobbyName);

						try {
							JSONObject response = Gberry.contactMCP("player-arena-chat-lobby-change", payload);

							if (response.containsKey("success")) {
								if (chatLobbyName.contains("premium")) {
									player.sendFormattedMessage("{0}You have joined Chat Lobby {1}Premium{2}.", ChatColor.GREEN, ChatColor.AQUA, ChatColor.GREEN);
								} else {
									player.sendFormattedMessage("{0}You have joined Chat Lobby {1}.", ChatColor.GREEN, ChatColor.AQUA + String.valueOf(chatLobbyNumberFinal) + ChatColor.GREEN);
								}
							} else if (response.containsKey("error")) {
								String error = (String) response.get("error");

								if (error.equals("already_in_chat_lobby")) {
									player.sendFormattedMessage("{0}You are already in this chat lobby!", ChatColor.RED);
								} else if (error.equals("not_allowed_premium_chat_lobby")) {
									player.sendFormattedMessage("{0}Only Donator+ users or higher can join the premium chat lobby!", ChatColor.RED);
								} else if (error.equals("not_allowed_full_chat_lobby")) {
									player.sendFormattedMessage("{0}Only Donator+ users or higher can join the premium chat lobby!", ChatColor.RED);
								} else {
									player.sendFormattedMessage("{0}ERROR, please report this as a bug: {1}", ChatColor.RED, error);
								}
							}

						} catch (HTTPRequestFailException e) {
							Gberry.plugin.getLogger().warning(e.getResponseCode() + ": " + e.getResponse());
							e.printStackTrace();
						}
					}
				});
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
}
