package net.badlion.arenalobby.helpers;

import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.logging.Level;

public class PartyHelper {

	private static ItemStack partyEventItem;

	public static void initialize() {
		// Hotbar items
		PartyHelper.partyEventItem = ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Start Party Event");
	}


	public static ItemStack getPartyEventItem() {
		return partyEventItem;
	}

	public static boolean isPlayerInventory(Inventory inventory) {
		return inventory != null && inventory.getName().endsWith("s Inventory");
	}

	public static Inventory createPlayerInventory(SmellyInventory smellyInventory, Player player) {
		Inventory inventory;

		if (smellyInventory != null) {
			inventory = ArenaLobby.getInstance().getServer().createInventory(smellyInventory.getFakeHolder(), 54,
					ChatColor.BOLD + ChatColor.AQUA.toString() + player.getName() + "'s Inventory");

			inventory.setItem(53, SmellyInventory.getCloseInventoryItem());
		} else {
			inventory = ArenaLobby.getInstance().getServer().createInventory(null, 54,
					ChatColor.BOLD + ChatColor.AQUA.toString() + player.getName() + "'s Inventory");
		}

		KitCommon.fillInventoryWithContents(inventory, player.getInventory().getArmorContents(), player.getInventory().getContents());

		return inventory;
	}

	public static void startPartyEvent(final Group group, final KitRuleSet kitRuleSet, final String event) {
		PartyHelper.startPartyEvent(group, kitRuleSet, event, 1, false);
	}

	public static void startPartyEvent(final Group group, final KitRuleSet kitRuleSet, final String event, boolean friendlyFire) {
		PartyHelper.startPartyEvent(group, kitRuleSet, event, 1, friendlyFire);
	}

	public static void startPartyEvent(final Group group, final KitRuleSet kitRuleSet, final String event, final int bestOf, final boolean friendlyFire) {
		if (kitRuleSet == null) {
			Gberry.log("PARTY", "null kitruleset detected, backing out");
			group.sendMessage(ChatColor.RED + "An error has occured, please notify an administrator and post this on the forums under bug reports. #1");
			return;
		}

		group.getLeader().sendFormattedMessage("{0}Trying to start party {1} event...", ChatColor.GREEN, event);
		new BukkitRunnable() {
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				data.put("sender_uuid", group.getLeader().getUniqueId().toString());
				data.put("server_region", Gberry.serverRegion.toString().toLowerCase());
				data.put("event_name", event);
				data.put("ladder", String.valueOf(kitRuleSet.getName()));
				data.put("arena_type", String.valueOf(kitRuleSet.getArenaType().ordinal()));
				if (friendlyFire) {
					data.put("friendly_fire", "true");
				}
				if (bestOf != 1) {
					data.put("best_of", String.valueOf(bestOf));
				}
				try {
					JSONObject response = Gberry.contactMCP("arena-party-event", data);
					ArenaLobby.getInstance().getLogger().log(Level.INFO, "[sending party event]: " + data);
					ArenaLobby.getInstance().getLogger().log(Level.INFO, "Getting partyevent response " + response);
					if (!response.equals(MCPManager.successResponse)) {
						group.getLeader().sendMessage(ChatColor.RED + "ERROR: " + partyEventErrorString((String) response.get("error")));
					}
				} catch (HTTPRequestFailException e) {
					group.getLeader().sendFormattedMessage("{0}Unable to start party event, try again later.", ChatColor.RED);
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(ArenaLobby.getInstance());
	}

	private static String partyEventErrorString(String error) {
		if (error.equals("not_in_party")) {
			return ChatColor.RED + "You are not in a party.";
		} else if (error.equals("temporarily_disabled")) {
			return ChatColor.RED + "This feature is currently disabled.";
		} else if (error.equals("not_party_leader")) {
			return ChatColor.RED + "You are not the party leader.";
		} else if (error.equals("following_player")) {
			return ChatColor.RED + "You are following someone.";
		} else if (error.equals("too_few_in_party")) {
			return ChatColor.RED + "You do not have enough players in your party.";
		} else if (error.equals("too_many_in_party")) {
			return ChatColor.RED + "You have too many players in your party.";
		} else if (error.equals("tournament_disabled")) {
			return ChatColor.RED + "Tournaments are not enabled right now.";
		} else if (error.startsWith("offline_party_member")) {
			String username = error.split(" ")[1];
			return ChatColor.YELLOW + username + ChatColor.RED + " is offline.";
		} else if (error.startsWith("member_in_queue")) {
			String username = error.split(" ")[1];
			return ChatColor.YELLOW + username + ChatColor.RED + " is in a queue.";
		} else if (error.startsWith("member_following_player")) {
			String username = error.split(" ")[1];
			return ChatColor.YELLOW + username + ChatColor.RED + " is following someone.";
		} else if (error.startsWith("member_not_on_arena")) {
			String username = error.split(" ")[1];
			return ChatColor.YELLOW + username + ChatColor.RED + " is not on arena.";
		}
		return "ERROR: " + error;
	}


}
