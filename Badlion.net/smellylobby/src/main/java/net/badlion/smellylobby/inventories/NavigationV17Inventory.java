package net.badlion.smellylobby.inventories;

import net.badlion.common.GetCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.smellylobby.SmellyLobby;
import net.badlion.smellylobby.helpers.NavigationInventoryHelper;
import net.badlion.smellylobby.tasks.AttentionGrabberTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NavigationV17Inventory {

	public static void initialize() {
		// Create the smelly inventory
		SmellyInventory.SmellyInventoryHandler smellyInventoryHandler = new ServersInventoryScreenHandler();
		SmellyInventory smellyInventory = new SmellyInventory(smellyInventoryHandler, 18, ChatColor.GOLD + "Choose a server!");

		SmellyInventory.FakeHolder fakeHolder = smellyInventory.getFakeHolder();

		String border = AttentionGrabberTask.MAXIMUM_ATTENTION;
		// Add items to the smelly inventory

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.WATER_BUCKET, ChatColor.GOLD + ChatColor.BOLD.toString() + "Arena PvP", border, ChatColor.AQUA + "Competitive 1v1's, party fights", ChatColor.AQUA + "and more!", border),
				ItemStackUtil.createItem(Material.POTION, (byte) 8229, ChatColor.GOLD + ChatColor.BOLD.toString() + "Practice", border, ChatColor.AQUA + "Kohi's classic Practice servers!", border),
				ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GOLD + ChatColor.BOLD.toString() + "Factions", border, ChatColor.AQUA + "Team up with other players to conquer a map", border),
				ItemStackUtil.createItem(Material.APPLE, ChatColor.BLUE + ChatColor.BOLD.toString() + "Mini UHC", border, ChatColor.AQUA + "Max 32 players, intense fights,", ChatColor.AQUA + "FFA or Teams of 2, take your pick!", border),
				ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.BLUE + ChatColor.BOLD.toString() + "Survival Games", border, ChatColor.AQUA + "Take your skills from Arena PvP", ChatColor.AQUA + "into a more competitive situation!", border),
				ItemStackUtil.createItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.GREEN + ChatColor.BOLD.toString() + "Hosted UHC", border, ChatColor.AQUA + "No natural regen, closing border,", ChatColor.AQUA + "survival of the fittest!", border),
				ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GREEN + ChatColor.BOLD.toString() + "UHC Meetup"),
				ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.GOLD + ChatColor.BOLD.toString() + "Kohi Games", border, ChatColor.AQUA + "Drop into a Vanilla world and collect items ", ChatColor.AQUA + "from chests. Last man standing wins!", border),
				ItemStackUtil.createItem(Material.CHEST, ChatColor.GOLD + ChatColor.BOLD.toString() + "Vault Battle", border, ChatColor.AQUA + "Four Teams rush to raid the other team's Vaults.", border),
				ItemStackUtil.createItem(Material.GOLD_AXE, ChatColor.GOLD + ChatColor.BOLD.toString() + "Free For All (FFA)", border, ChatColor.AQUA + "Free For Alls with different kits!", border));

		// Create UHC Meetup inventory
		Inventory uhcMeetupInventory = smellyInventory.createInventory(fakeHolder, smellyInventoryHandler, 6, 18, ChatColor.GOLD + "UHC Meetup Servers");
		uhcMeetupInventory.setItem(0, ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GOLD + ChatColor.BOLD.toString() + "North American Region", ChatColor.BLUE + "Players: 0"));
		uhcMeetupInventory.setItem(2, ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GOLD + ChatColor.BOLD.toString() + "South American Region", ChatColor.BLUE + "Players: 0"));
		uhcMeetupInventory.setItem(4, ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GOLD + ChatColor.BOLD.toString() + "European Region", ChatColor.BLUE + "Players: 0"));
		uhcMeetupInventory.setItem(6, ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GOLD + ChatColor.BOLD.toString() + "Asian Region", ChatColor.BLUE + "Players: 0"));
		uhcMeetupInventory.setItem(8, ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GOLD + ChatColor.BOLD.toString() + "Australian Region", ChatColor.BLUE + "Players: 0"));

		// Create FFA region sub-inventories
		Inventory naUHCMeetupInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) uhcMeetupInventory.getHolder()), smellyInventoryHandler,
				0, 18, ChatColor.GOLD + "NA UHC Meetup");
		Inventory saUHCMeetupInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) uhcMeetupInventory.getHolder()), smellyInventoryHandler,
				2, 18, ChatColor.GOLD + "SA UHC Meetup");
		Inventory euUHCMeetupInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) uhcMeetupInventory.getHolder()), smellyInventoryHandler,
				4, 18, ChatColor.GOLD + "EU UHC Meetup");
		Inventory asUHCMeetupInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) uhcMeetupInventory.getHolder()), smellyInventoryHandler,
				6, 18, ChatColor.GOLD + "Asian UHC Meetup");
		Inventory auUHCMeetupInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) uhcMeetupInventory.getHolder()), smellyInventoryHandler,
				8, 18, ChatColor.GOLD + "AU UHC Meetup");

		naUHCMeetupInventory.addItem(ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + ChatColor.BOLD.toString() + "UHCMeetup FFA",  ChatColor.GOLD.toString() + "0 in queue", ChatColor.GOLD.toString() + "0 in game", "", ChatColor.YELLOW + "Click to join"));

		saUHCMeetupInventory.addItem(ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + ChatColor.BOLD.toString() + "UHCMeetup FFA",  ChatColor.GOLD.toString() + "0 in queue", ChatColor.GOLD.toString() + "0 in game", "", ChatColor.YELLOW + "Click to join"));

		euUHCMeetupInventory.addItem(ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + ChatColor.BOLD.toString() + "UHCMeetup FFA",  ChatColor.GOLD.toString() + "0 in queue", ChatColor.GOLD.toString() + "0 in game", "", ChatColor.YELLOW + "Click to join"));

		asUHCMeetupInventory.addItem(ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + ChatColor.BOLD.toString() + "UHCMeetup FFA",  ChatColor.GOLD.toString() + "0 in queue", ChatColor.GOLD.toString() + "0 in game", "", ChatColor.YELLOW + "Click to join"));

		auUHCMeetupInventory.addItem(ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + ChatColor.BOLD.toString() + "UHCMeetup FFA",  ChatColor.GOLD.toString() + "0 in queue", ChatColor.GOLD.toString() + "0 in game", "", ChatColor.YELLOW + "Click to join"));

		// Create FFA inventory
		Inventory ffaInventory = smellyInventory.createInventory(fakeHolder, smellyInventoryHandler, 9, 27, ChatColor.GOLD + "FFA Servers");
		ffaInventory.setItem(0, ItemStackUtil.createItem(Material.GOLD_AXE, ChatColor.GOLD + ChatColor.BOLD.toString() + "North American Region", ChatColor.BLUE + "Players: 0"));
		ffaInventory.setItem(2, ItemStackUtil.createItem(Material.GOLD_AXE, ChatColor.GOLD + ChatColor.BOLD.toString() + "South American Region", ChatColor.BLUE + "Players: 0"));
		ffaInventory.setItem(4, ItemStackUtil.createItem(Material.GOLD_AXE, ChatColor.GOLD + ChatColor.BOLD.toString() + "European Region", ChatColor.BLUE + "Players: 0"));
		ffaInventory.setItem(6, ItemStackUtil.createItem(Material.GOLD_AXE, ChatColor.GOLD + ChatColor.BOLD.toString() + "Asian Region", ChatColor.BLUE + "Players: 0"));
		ffaInventory.setItem(8, ItemStackUtil.createItem(Material.GOLD_AXE, ChatColor.GOLD + ChatColor.BOLD.toString() + "Australian Region", ChatColor.BLUE + "Players: 0"));
		ffaInventory.setItem(18, ItemStackUtil.createItem(Material.POTION, (byte) 16392, ChatColor.GOLD + ChatColor.BOLD.toString() + "Cubecore", ChatColor.BLUE + "Players: 0"));

		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) ffaInventory.getHolder()), 18, "ccpractice");

		// Create FFA region sub-inventories
		Inventory naFFAInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) ffaInventory.getHolder()), smellyInventoryHandler,
				0, 18, ChatColor.GOLD + "NA FFA Servers");
		Inventory saFFAInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) ffaInventory.getHolder()), smellyInventoryHandler,
				2, 18, ChatColor.GOLD + "SA FFA Servers");
		Inventory euFFAInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) ffaInventory.getHolder()), smellyInventoryHandler,
				4, 18, ChatColor.GOLD + "EU FFA Servers");
		Inventory asFFAInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) ffaInventory.getHolder()), smellyInventoryHandler,
				6, 18, ChatColor.GOLD + "Asian FFA Servers");
		Inventory auFFAInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) ffaInventory.getHolder()), smellyInventoryHandler,
				8, 18, ChatColor.GOLD + "AU FFA Servers");

		naFFAInventory.addItem(ItemStackUtil.createItem(Material.POTION, (short) 8229, ChatColor.GREEN + ChatColor.BOLD.toString() + "NoDebuff FFA", ChatColor.BLUE + "Players: 0"));
		naFFAInventory.addItem(ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GREEN + ChatColor.BOLD.toString() + "SG FFA", ChatColor.BLUE + "Players: 0"));
		naFFAInventory.addItem(ItemStackUtil.createItem(Material.MUSHROOM_SOUP, ChatColor.GREEN + ChatColor.BOLD.toString() + "Soup FFA", ChatColor.BLUE + "Players: 0", "", ChatColor.RED + "Coming Soon!"));
		naFFAInventory.addItem(ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GREEN + ChatColor.BOLD.toString() + "UHC FFA", ChatColor.BLUE + "Players: 0"));

		saFFAInventory.addItem(ItemStackUtil.createItem(Material.POTION, (short) 8229, ChatColor.GREEN + ChatColor.BOLD.toString() + "NoDebuff FFA", ChatColor.BLUE + "Players: 0"));
		saFFAInventory.addItem(ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GREEN + ChatColor.BOLD.toString() + "SG FFA", ChatColor.BLUE + "Players: 0"));

		saFFAInventory.addItem(ItemStackUtil.createItem(Material.MUSHROOM_SOUP, ChatColor.GREEN + ChatColor.BOLD.toString() + "Soup FFA", ChatColor.BLUE + "Players: 0", "", ChatColor.RED + "Coming Soon!"));
		saFFAInventory.addItem(ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GREEN + ChatColor.BOLD.toString() + "UHC FFA", ChatColor.BLUE + "Players: 0"));

		euFFAInventory.addItem(ItemStackUtil.createItem(Material.POTION, (short) 8229, ChatColor.GREEN + ChatColor.BOLD.toString() + "NoDebuff FFA", ChatColor.BLUE + "Players: 0"));
		euFFAInventory.addItem(ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GREEN + ChatColor.BOLD.toString() + "SG FFA", ChatColor.BLUE + "Players: 0"));
		euFFAInventory.addItem(ItemStackUtil.createItem(Material.MUSHROOM_SOUP, ChatColor.GREEN + ChatColor.BOLD.toString() + "Soup FFA", ChatColor.BLUE + "Players: 0", "", ChatColor.RED + "Coming Soon!"));
		euFFAInventory.addItem(ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GREEN + ChatColor.BOLD.toString() + "UHC FFA", ChatColor.BLUE + "Players: 0"));

		asFFAInventory.addItem(ItemStackUtil.createItem(Material.POTION, (short) 8229, ChatColor.GREEN + ChatColor.BOLD.toString() + "NoDebuff FFA", ChatColor.BLUE + "Players: 0"));
		asFFAInventory.addItem(ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GREEN + ChatColor.BOLD.toString() + "SG FFA", ChatColor.BLUE + "Players: 0"));
		asFFAInventory.addItem(ItemStackUtil.createItem(Material.MUSHROOM_SOUP, ChatColor.GREEN + ChatColor.BOLD.toString() + "Soup FFA", ChatColor.BLUE + "Players: 0", "", ChatColor.RED + "Coming Soon!"));
		asFFAInventory.addItem(ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GREEN + ChatColor.BOLD.toString() + "UHC FFA", ChatColor.BLUE + "Players: 0"));

		auFFAInventory.addItem(ItemStackUtil.createItem(Material.POTION, (short) 8229, ChatColor.GREEN + ChatColor.BOLD.toString() + "NoDebuff FFA", ChatColor.BLUE + "Players: 0"));
		auFFAInventory.addItem(ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GREEN + ChatColor.BOLD.toString() + "SG FFA", ChatColor.BLUE + "Players: 0"));
		auFFAInventory.addItem(ItemStackUtil.createItem(Material.MUSHROOM_SOUP, ChatColor.GREEN + ChatColor.BOLD.toString() + "Soup FFA", ChatColor.BLUE + "Players: 0", "", ChatColor.RED + "Coming Soon!"));
		auFFAInventory.addItem(ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GREEN + ChatColor.BOLD.toString() + "UHC FFA", ChatColor.BLUE + "Players: 0"));

		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) naFFAInventory.getHolder()), 0, "nanodebuffffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) naFFAInventory.getHolder()), 1, "nasgffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) naFFAInventory.getHolder()), 2, "nasoupffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) naFFAInventory.getHolder()), 3, "nauhcffa");

		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) saFFAInventory.getHolder()), 0, "sanodebuffffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) saFFAInventory.getHolder()), 1, "sasgffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) saFFAInventory.getHolder()), 2, "sasoupffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) saFFAInventory.getHolder()), 3, "sauhcffa");

		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) euFFAInventory.getHolder()), 0, "eunodebuffffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) euFFAInventory.getHolder()), 1, "eusgffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) euFFAInventory.getHolder()), 2, "eusoupffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) euFFAInventory.getHolder()), 3, "euuhcffa");

		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) asFFAInventory.getHolder()), 0, "asnodebuffffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) asFFAInventory.getHolder()), 1, "assgffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) asFFAInventory.getHolder()), 2, "assoupffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) asFFAInventory.getHolder()), 3, "asuhcffa");

		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) auFFAInventory.getHolder()), 0, "aunodebuffffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) auFFAInventory.getHolder()), 1, "ausgffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) auFFAInventory.getHolder()), 2, "ausoupffa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) auFFAInventory.getHolder()), 3, "auuhcffa");

		// Create Factions inventory
		Inventory factionsInventory = smellyInventory.createInventory(fakeHolder, smellyInventoryHandler,
				2, 9, ChatColor.GOLD + "Factions Servers");
		factionsInventory.setItem(0, ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.RED + ChatColor.BOLD.toString() + "UHC Factions", ChatColor.BLUE + "Players: 0/500"));
		factionsInventory.setItem(1, ItemStackUtil.createItem(Material.REDSTONE_BLOCK, ChatColor.RED + ChatColor.BOLD.toString() + "Cubecore Factions", ChatColor.RED + "COMING SOON"));

		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) factionsInventory.getHolder()), 1, "ccfactions");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) factionsInventory.getHolder()), 0, "factions");

		// Create Kohi Games inventory
		Inventory kohiGamesInventory = smellyInventory.createInventory(fakeHolder, smellyInventoryHandler,
				7, 18, ChatColor.GOLD + "Kohi Games Regions");

		kohiGamesInventory.setItem(0, ItemStackUtil.createItem(Material.ENCHANTED_BOOK, ChatColor.BLUE + ChatColor.BOLD.toString() + "NA East Kohi Games"));
		kohiGamesInventory.setItem(4, ItemStackUtil.createItem(Material.ENCHANTED_BOOK, ChatColor.BLUE + ChatColor.BOLD.toString() + "EU Kohi Games"));


		Inventory kohiGamesNAEastInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) kohiGamesInventory.getHolder()),
				smellyInventoryHandler, 0, 54, ChatColor.GOLD + "NA East Kohi Games Servers");

		new NavigationInventoryHelper.MCPQueryTask("kohigames",
				kohiGamesNAEastInventory) {
			@Override
			public void run(JSONObject response) {
				// Fail-safe
				if (response == null) return;

				// Clear the inventory
				this.inventory.clear();

				// Add the back item
				this.inventory.setItem(this.inventory.getSize() - 1, SmellyInventory.getBackInventoryItem());

				for (Map<String, Object> server : (List<Map<String, Object>>) response.get("servers")) {
					// Fail-safe
					if (server == null) continue;

					this.inventory.addItem(NavigationInventoryHelper.createKohiGamesItem(
							(String) server.get("server_name"),
							(long) server.get("player_count"),
							(String) server.get("status"),
							server.get("in_countdown").equals("True")));
				}
			}
		};

		Inventory kohiGamesEUInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) kohiGamesInventory.getHolder()),
				smellyInventoryHandler, 4, 54, ChatColor.GOLD + "EU Kohi Games Servers");
		new NavigationInventoryHelper.MCPQueryTask("eukohigames",
				kohiGamesEUInventory) {
			@Override
			public void run(JSONObject response) {
				// Fail-safe
				if (response == null) return;

				// Clear the inventory
				this.inventory.clear();

				// Add the back item
				this.inventory.setItem(this.inventory.getSize() - 1, SmellyInventory.getBackInventoryItem());

				for (Map<String, Object> server : (List<Map<String, Object>>) response.get("servers")) {
					// Fail-safe
					if (server == null) continue;

					this.inventory.addItem(NavigationInventoryHelper.createKohiGamesItem(
							(String) server.get("server_name"),
							(long) server.get("player_count"),
							(String) server.get("status"),
							server.get("in_countdown").equals("True")));
				}
			}
		};

		// Create Vault Battle inventory
		Inventory vbInventory = smellyInventory.createInventory(fakeHolder, smellyInventoryHandler,
				8, 18, ChatColor.GOLD + "Vault Battle Regions");

		vbInventory.setItem(0, ItemStackUtil.createItem(Material.ENCHANTED_BOOK, ChatColor.BLUE + ChatColor.BOLD.toString() + "NA East Vault Battle"));
		vbInventory.setItem(4, ItemStackUtil.createItem(Material.ENCHANTED_BOOK, ChatColor.BLUE + ChatColor.BOLD.toString() + "EU Vault Battle"));

		Inventory vbNAEastInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) vbInventory.getHolder()),
				smellyInventoryHandler, 0, 54, ChatColor.GOLD + "NA East Vault Battle Servers");
		new NavigationInventoryHelper.MCPQueryTask("navaultbattle",
				vbNAEastInventory) {
			@Override
			public void run(JSONObject response) {
				// Fail-safe
				if (response == null) return;

				// Clear the inventory
				this.inventory.clear();

				// Add the back item
				this.inventory.setItem(this.inventory.getSize() - 1, SmellyInventory.getBackInventoryItem());

				for (Map<String, Object> server : (List<Map<String, Object>>) response.get("servers")) {
					// Fail-safe
					if (server == null) continue;

					this.inventory.addItem(NavigationInventoryHelper.createVBItem(
							(String) server.get("server_name"),
							(long) server.get("player_count"),
							(String) server.get("status"),
							server.get("in_countdown").equals("True")));
				}
			}
		};

		Inventory vbEUInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) vbInventory.getHolder()),
				smellyInventoryHandler, 4, 54, ChatColor.GOLD + "EU Vault Battle Servers");
		new NavigationInventoryHelper.MCPQueryTask("euvaultbattle",
				vbEUInventory) {
			@Override
			public void run(JSONObject response) {
				// Fail-safe
				if (response == null) return;

				// Clear the inventory
				this.inventory.clear();

				// Add the back item
				this.inventory.setItem(this.inventory.getSize() - 1, SmellyInventory.getBackInventoryItem());

				for (Map<String, Object> server : (List<Map<String, Object>>) response.get("servers")) {
					// Fail-safe
					if (server == null) continue;

					this.inventory.addItem(NavigationInventoryHelper.createVBItem(
							(String) server.get("server_name"),
							(long) server.get("player_count"),
							(String) server.get("status"),
							server.get("in_countdown").equals("True")));
				}
			}
		};


		// Create ArenaPvP inventory
		Inventory arenaPvPInventory = smellyInventory.createInventory(fakeHolder, smellyInventoryHandler,
				0, 18, ChatColor.GOLD + "Arena PvP Servers");
		arenaPvPInventory.setItem(0, ItemStackUtil.createItem(Material.WATER_BUCKET, ChatColor.AQUA + ChatColor.BOLD.toString() + "North American Region", ChatColor.BLUE + "Players: 0"));
		arenaPvPInventory.setItem(4, ItemStackUtil.createItem(Material.LAVA_BUCKET, ChatColor.AQUA + ChatColor.BOLD.toString() + "European Region", ChatColor.BLUE + "Players: 0"));
		arenaPvPInventory.setItem(8, ItemStackUtil.createItem(Material.MILK_BUCKET, ChatColor.AQUA + ChatColor.BOLD.toString() + "Australian Region", ChatColor.BLUE + "Players: 0"));
		arenaPvPInventory.setItem(9, ItemStackUtil.createItem(Material.LAVA_BUCKET, ChatColor.AQUA + ChatColor.BOLD.toString() + "South American Region", ChatColor.BLUE + "Players: 0"));
		arenaPvPInventory.setItem(13, ItemStackUtil.createItem(Material.WATER_BUCKET, ChatColor.AQUA + ChatColor.BOLD.toString() + "Asian Region", ChatColor.BLUE + "Players: 0"));

		// Season 13
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) arenaPvPInventory.getHolder()), 0, "s13na");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) arenaPvPInventory.getHolder()), 4, "s13eu");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) arenaPvPInventory.getHolder()), 8, "s13au");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) arenaPvPInventory.getHolder()), 9, "s13sa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) arenaPvPInventory.getHolder()), 13, "s13as");

		// Create Mini UHC inventory
		Inventory miniUHCInventory = smellyInventory.createInventory(fakeHolder, smellyInventoryHandler,
				3, 18, ChatColor.GOLD + "Mini UHC Server Types");

		miniUHCInventory.setItem(0, ItemStackUtil.createItem(Material.APPLE, ChatColor.BLUE + ChatColor.BOLD.toString() + "NA East FFA"));
		miniUHCInventory.setItem(1, ItemStackUtil.createItem(Material.APPLE, ChatColor.BLUE + ChatColor.BOLD.toString() + "NA East To2"));
		miniUHCInventory.setItem(4, ItemStackUtil.createItem(Material.APPLE, ChatColor.BLUE + ChatColor.BOLD.toString() + "EU FFA"));
		miniUHCInventory.setItem(5, ItemStackUtil.createItem(Material.APPLE, ChatColor.BLUE + ChatColor.BOLD.toString() + "EU To2"));
		miniUHCInventory.setItem(13, ItemStackUtil.createItem(Material.APPLE, ChatColor.BLUE + ChatColor.BOLD.toString() + "AU FFA"));
		miniUHCInventory.setItem(14, ItemStackUtil.createItem(Material.APPLE, ChatColor.BLUE + ChatColor.BOLD.toString() + "AU To2"));

		Inventory miniUHCNAEastFFAInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) miniUHCInventory.getHolder()),
				smellyInventoryHandler, 0, 54, ChatColor.GOLD + "NA East FFA Servers");
		Inventory miniUHCNAEastTo2Inventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) miniUHCInventory.getHolder()),
				smellyInventoryHandler, 1, 54, ChatColor.GOLD + "NA East To2 Servers");

		new NavigationInventoryHelper.APIQueryTask("http://" + GetCommon.getIpForNAEastMini() + ":20090/GetServers/RALBAv4JWgqzFn24535XJ2q8mSGhWYnY",
				miniUHCNAEastFFAInventory, miniUHCNAEastTo2Inventory) {
			@Override
			public void run(JSONObject response) {
				// Clear the inventories
				this.inventoryOne.clear();
				this.inventoryTwo.clear();

				// Add the back items
				this.inventoryOne.setItem(this.inventoryOne.getSize() - 1, SmellyInventory.getBackInventoryItem());
				this.inventoryTwo.setItem(this.inventoryTwo.getSize() - 1, SmellyInventory.getBackInventoryItem());

				for (Map<String, Object> server : (List<Map<String, Object>>) response.get("servers")) {
					// Fail-safe
					if (server == null) continue;


					int teamSize = (int) (long) server.get("teamsize");
					ItemStack itemStack = NavigationInventoryHelper.createMiniUHCItem((String) server.get("server_name"),
							(long) server.get("player_count"), (String) server.get("state"),
							(boolean) server.get("in_countdown"), teamSize,
							(JSONArray) server.get("gamemodes"));

					if (teamSize == 1) {
						this.inventoryOne.addItem(itemStack);
					} else if (teamSize == 2) {
						this.inventoryTwo.addItem(itemStack);
					}
				}
			}
		};

		Inventory miniUHCEUFFAInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) miniUHCInventory.getHolder()),
				smellyInventoryHandler, 4, 54, ChatColor.GOLD + "EU FFA Servers");
		Inventory miniUHCEUTo2Inventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) miniUHCInventory.getHolder()),
				smellyInventoryHandler, 5, 54, ChatColor.GOLD + "EU To2 Servers");

		new NavigationInventoryHelper.APIQueryTask("http://" + GetCommon.getIpForEUMini() + ":20090/GetServers/RALBAv4JWgqzFn24535XJ2q8mSGhWYnY",
				miniUHCEUFFAInventory, miniUHCEUTo2Inventory) {
			@Override
			public void run(JSONObject response) {
				// Clear the inventories
				this.inventoryOne.clear();
				this.inventoryTwo.clear();

				// Add the back items
				this.inventoryOne.setItem(this.inventoryOne.getSize() - 1, SmellyInventory.getBackInventoryItem());
				this.inventoryTwo.setItem(this.inventoryTwo.getSize() - 1, SmellyInventory.getBackInventoryItem());

				for (Map<String, Object> server : (List<Map<String, Object>>) response.get("servers")) {
					// Fail-safe
					if (server == null) continue;


					int teamSize = (int) (long) server.get("teamsize");
					ItemStack itemStack = NavigationInventoryHelper.createMiniUHCItem((String) server.get("server_name"),
							(long) server.get("player_count"), (String) server.get("state"),
							(boolean) server.get("in_countdown"), teamSize,
							(JSONArray) server.get("gamemodes"));

					if (teamSize == 1) {
						this.inventoryOne.addItem(itemStack);
					} else if (teamSize == 2) {
						this.inventoryTwo.addItem(itemStack);
					}
				}
			}
		};

		/*Inventory miniUHCNAWestFFAInventory = NavigationInventory.smellyInventory.createInventory(((SmellyInventory.FakeHolder) miniUHCInventory.getHolder()),
				smellyInventoryHandler, 9, 54, ChatColor.GOLD + "NA West FFA Servers");
		Inventory miniUHCNAWestTo2Inventory = NavigationInventory.smellyInventory.createInventory(((SmellyInventory.FakeHolder) miniUHCInventory.getHolder()),
				smellyInventoryHandler, 10, 54, ChatColor.GOLD + "NA West To2 Servers");

		new NavigationInventoryHelper.APIQueryTask("http://" + GetCommon.getIpForNAWestMini() + ":20090/GetServers/RALBAv4JWgqzFn24535XJ2q8mSGhWYnY",
				miniUHCNAWestFFAInventory, miniUHCNAWestTo2Inventory) {
			@Override
			public void run(JSONObject response) {
				// Fail-safe
				if (response == null) return;

				// Clear the inventories
				NavigationInventory.inventoryOne.clear();
				NavigationInventory.inventoryTwo.clear();

				// Add the back items
				NavigationInventory.inventoryOne.setItem(NavigationInventory.inventoryOne.getSize() - 1, SmellyInventory.getBackInventoryItem());
				NavigationInventory.inventoryTwo.setItem(NavigationInventory.inventoryTwo.getSize() - 1, SmellyInventory.getBackInventoryItem());

				for (Map<String, Object> server : (List<Map<String, Object>>) response.get("servers")) {
					// Fail-safe
					if (server == null) continue;

					int teamSize = (int) (long) server.get("teamsize");
					ItemStack itemStack = NavigationInventoryHelper.createMiniUHCItem((String) server.get("server_name"),
							(long) server.get("player_count"), (String) server.get("state"),
							(boolean) server.get("in_countdown"), teamSize,
							(JSONArray) server.get("gamemodes"));

					if (teamSize == 1) {
						this.inventoryOne.addItem(itemStack);
					} else if (teamSize == 2) {
						this.inventoryTwo.addItem(itemStack);
					}
				}
			}
		};*/

		Inventory miniUHCAUFFAInventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) miniUHCInventory.getHolder()),
				smellyInventoryHandler, 13, 54, ChatColor.GOLD + "AU FFA Servers");
		Inventory miniUHCAUTo2Inventory = smellyInventory.createInventory(((SmellyInventory.FakeHolder) miniUHCInventory.getHolder()),
				smellyInventoryHandler, 14, 54, ChatColor.GOLD + "AU To2 Servers");

		new NavigationInventoryHelper.APIQueryTask("http://" + GetCommon.getIpForAUMini() + ":20090/GetServers/RALBAv4JWgqzFn24535XJ2q8mSGhWYnY",
				miniUHCAUFFAInventory, miniUHCAUTo2Inventory) {
			@Override
			public void run(JSONObject response) {
				// Fail-safe
				if (response == null) return;

				// Clear the inventories
				this.inventoryOne.clear();
				this.inventoryTwo.clear();

				// Add the back items
				this.inventoryOne.setItem(this.inventoryOne.getSize() - 1, SmellyInventory.getBackInventoryItem());
				this.inventoryTwo.setItem(this.inventoryTwo.getSize() - 1, SmellyInventory.getBackInventoryItem());

				for (Map<String, Object> server : (List<Map<String, Object>>) response.get("servers")) {
					// Fail-safe
					if (server == null) continue;

					int teamSize = (int) (long) server.get("teamsize");
					ItemStack itemStack = NavigationInventoryHelper.createMiniUHCItem((String) server.get("server_name"),
							(long) server.get("player_count"), (String) server.get("state"),
							(boolean) server.get("in_countdown"), teamSize,
							(JSONArray) server.get("gamemodes"));

					if (teamSize == 1) {
						this.inventoryOne.addItem(itemStack);
					} else if (teamSize == 2) {
						this.inventoryTwo.addItem(itemStack);
					}
				}
			}
		};

		// Create SG inventory
		Inventory sgInventory = smellyInventory.createInventory(fakeHolder, smellyInventoryHandler, 4, 18, ChatColor.GOLD + "SG Regions");
		sgInventory.setItem(0, ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GOLD + ChatColor.BOLD.toString() + "North American Region", ChatColor.BLUE + "Players: 0"));
		sgInventory.setItem(2, ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GOLD + ChatColor.BOLD.toString() + "South American Region", ChatColor.BLUE + "Players: 0"));
		sgInventory.setItem(4, ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GOLD + ChatColor.BOLD.toString() + "European Region", ChatColor.BLUE + "Players: 0"));
		sgInventory.setItem(6, ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GOLD + ChatColor.BOLD.toString() + "Asian Region", ChatColor.BLUE + "Players: 0"));
		sgInventory.setItem(8, ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GOLD + ChatColor.BOLD.toString() + "Australian Region", ChatColor.BLUE + "Players: 0"));

		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) sgInventory.getHolder()), 0, "sg2.0na");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) sgInventory.getHolder()), 2, "sg2.0sa");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) sgInventory.getHolder()), 4, "sg2.0eu");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) sgInventory.getHolder()), 6, "sg2.0as");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) sgInventory.getHolder()), 8, "sg2.0au");

		// Create UHC inventory
		Inventory uhcInventory = smellyInventory.createInventory(fakeHolder, smellyInventoryHandler,
				5, 18, ChatColor.GOLD + "UHC Servers");

		uhcInventory.setItem(0, ItemStackUtil.createItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.GREEN + ChatColor.BOLD.toString() + "NA East 1"));
		uhcInventory.setItem(1, ItemStackUtil.createItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.GREEN + ChatColor.BOLD.toString() + "NA East 2"));
		uhcInventory.setItem(2, ItemStackUtil.createItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.GREEN + ChatColor.BOLD.toString() + "NA East 3"));
		uhcInventory.setItem(4, ItemStackUtil.createItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.GREEN + ChatColor.BOLD.toString() + "Europe 1"));
		uhcInventory.setItem(5, ItemStackUtil.createItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.GREEN + ChatColor.BOLD.toString() + "Europe 2"));
		uhcInventory.setItem(6, ItemStackUtil.createItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.GREEN + ChatColor.BOLD.toString() + "Europe 3"));
		uhcInventory.setItem(9, ItemStackUtil.createItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.GREEN + ChatColor.BOLD.toString() + "SA 1"));
		uhcInventory.setItem(13, ItemStackUtil.createItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.GREEN + ChatColor.BOLD.toString() + "Australia 1"));
		uhcInventory.setItem(14, ItemStackUtil.createItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.GREEN + ChatColor.BOLD.toString() + "Australia 2"));

		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) uhcInventory.getHolder()), 0, "uhc");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) uhcInventory.getHolder()), 1, "uhc2");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) uhcInventory.getHolder()), 2, "uhc3");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) uhcInventory.getHolder()), 4, "euuhc1");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) uhcInventory.getHolder()), 5, "euuhc2");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) uhcInventory.getHolder()), 6, "euuhc3");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) uhcInventory.getHolder()), 9, "sauhc1");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) uhcInventory.getHolder()), 13, "auuhc");
		NavigationInventoryHelper.addBungeeServerName(((SmellyInventory.FakeHolder) uhcInventory.getHolder()), 14, "auuhc2");

		// Set inventories
		NavigationInventoryHelper.setSmellyInventory(smellyInventory);
		NavigationInventoryHelper.setArenaPvPInventory(arenaPvPInventory);
		NavigationInventoryHelper.setUHCInventory(uhcInventory);
		NavigationInventoryHelper.setMiniUHCInventory(miniUHCInventory);
		NavigationInventoryHelper.setSGInventory(sgInventory);
		NavigationInventoryHelper.setFactionsInventory(factionsInventory);
		NavigationInventoryHelper.setKohiGamesInventory(kohiGamesInventory);
		NavigationInventoryHelper.setVaultBattleInventory(vbInventory);
		NavigationInventoryHelper.setFFAInventory(ffaInventory);
		NavigationInventoryHelper.setUHCMeetupInventory(uhcMeetupInventory);
	}

	private static class ServersInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		private Map<UUID, Long> connectionRequestCooldown = new HashMap<>();

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (item.getType() == Material.PAINTING) return;

			// Don't worry about items that take you to deeper inventories
			Inventory subInventory = fakeHolder.getSubInventory(slot);
			if (subInventory == null) {
				// Are they trying to join a practice server?
				if (item.getItemMeta().getDisplayName().toLowerCase().contains("practice")) {
					player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString()
							+ "Practice is now a part of ArenaPvP and CubeCore Practice is available under the FFA item!");

					BukkitUtil.closeInventory(player);
					return;
				}

				// Is this a UHC Meetup inventory?
				String inventoryName = fakeHolder.getInventory().getName().toLowerCase();
				if (inventoryName.contains("uhc meetup")) {
					SmellyLobby.getInstance().joinQueue(player, inventoryName.substring(2, 4), "UHCMeetup", "ffa", "classic");

					BukkitUtil.closeInventory(player);
					return;
				}

				if (this.canSendConnectionRequest(player)) {
					this.trackConnectionRequest(player);

					String serverName = NavigationInventoryHelper.getBungeeServerName(fakeHolder, slot);

					// Is this a MiniUHC/Unranked SG server?
					if (serverName == null) {
						serverName = item.getItemMeta().getDisplayName().substring(2);
					}

					if (serverName.startsWith("s13")) {
						String[] split = serverName.split("s13");
						NavigationInventoryHelper.temporarySendToS13(player, split[1]);
					} else if (serverName.startsWith("sg2.0")) {
						String[] split = serverName.split("sg2.0");
						NavigationInventoryHelper.sendMPGLobby(player, split[1], "sg");
					} else {
						// Hardcodes for now

						String region = serverName.substring(0, 2);

						// Don't allow non-SA bungee to connect to SA
						if (region.equalsIgnoreCase("sa") && Gberry.serverRegion != Gberry.ServerRegion.SA) {
							player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString()
									+ "You can only join South American ArenaPvP servers through the sa.badlion.net IP!");

							BukkitUtil.closeInventory(player);

							return;
						}

						// Don't allow non-AS bungee to connect to AS
						if (region.equalsIgnoreCase("as") && Gberry.serverRegion != Gberry.ServerRegion.AS) {
							player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString()
									+ "You can only join Asian ArenaPvP servers through the asia.badlion.net IP!");

							BukkitUtil.closeInventory(player);

							return;
						}

						Gberry.sendToServer(player, serverName);
					}
				}
			} else {
				BukkitUtil.openInventory(player, subInventory);
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

		private void trackConnectionRequest(Player player) {
			this.connectionRequestCooldown.put(player.getUniqueId(), System.currentTimeMillis());

			player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Connecting...");
		}

		private boolean canSendConnectionRequest(Player player) {
			Long lastConnectionRequest = this.connectionRequestCooldown.get(player.getUniqueId());
			return lastConnectionRequest == null || lastConnectionRequest + 5000 < System.currentTimeMillis();
		}

	}

}
