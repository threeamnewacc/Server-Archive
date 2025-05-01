package net.badlion.smellylobby.tasks;

import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.smellylobby.SmellyLobby;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.UUID;

import static net.badlion.smellylobby.helpers.NavigationInventoryHelper.uhcMeetupInventory;

public class UpdateQueueCountTask extends BukkitRunnable {

	private int counter = 0;

	public void run() {
		if (++this.counter == 10) {
			int numberOfPlayersInQueue = SmellyLobby.getInstance().getPlayersInQueue().size();

			// Message players in queue
			for (UUID uuid : SmellyLobby.getInstance().getPlayersInQueue()) {
				Player player = Bukkit.getPlayer(uuid);

				// Because the players in queue set isn't a concurrent data set and doesn't matter here
				if (player == null) continue;

				player.sendMessage(ChatColor.AQUA + "There are currently " + numberOfPlayersInQueue
						+ " players in queue. Waiting for more players for a match to start...");
			}

			this.counter = 0;
		}

		for (final Gberry.ServerRegion region : Gberry.ServerRegion.values()) {
			// UHC Meetup queue count
			JSONObject payload = new JSONObject();

			payload.put("server_region", region.name().toLowerCase());
			payload.put("server_type", Gberry.ServerType.UHCMEETUP.getInternalName());
			payload.put("type", "ffa");
			payload.put("ladder", "classic");

			try {
				final JSONObject response = Gberry.contactMCP("matchmaking-default-queue-count", payload);

				if (response == null || response.equals(MCPManager.errorResponse)) {
					return;
				}
				// Update player count for queue
				BukkitUtil.runTask(new Runnable() {
					@Override
					public void run() {
						ItemStack regionItem = null;
						ItemStack ffaItem = null;

						switch (region) {
							case NA:
								regionItem = uhcMeetupInventory.getItem(0);
								ffaItem = ((SmellyInventory.FakeHolder) uhcMeetupInventory.getHolder()).getSubInventory(0).getItem(0);
								break;
							case SA:
								regionItem = uhcMeetupInventory.getItem(2);
								ffaItem = ((SmellyInventory.FakeHolder) uhcMeetupInventory.getHolder()).getSubInventory(2).getItem(0);
								break;
							case EU:
								regionItem = uhcMeetupInventory.getItem(4);
								ffaItem = ((SmellyInventory.FakeHolder) uhcMeetupInventory.getHolder()).getSubInventory(4).getItem(0);
								break;
							case AS:
								regionItem = uhcMeetupInventory.getItem(6);
								ffaItem = ((SmellyInventory.FakeHolder) uhcMeetupInventory.getHolder()).getSubInventory(6).getItem(0);
								break;
							case AU:
								regionItem = uhcMeetupInventory.getItem(8);
								ffaItem = ((SmellyInventory.FakeHolder) uhcMeetupInventory.getHolder()).getSubInventory(8).getItem(0);
								break;
							default:
								// DEV region or something else
								return;
						}

						UpdatePlayerCountTask.updateItemLore(regionItem, Gberry.getJSONInteger(response, "in_queue") + Gberry.getJSONInteger(response, "in_matches"));
						UpdateQueueCountTask.this.updateItemLore(ffaItem, Gberry.getJSONInteger(response, "in_queue"), Gberry.getJSONInteger(response, "in_matches"));
					}
				});
			} catch (HTTPRequestFailException e) {
				SmellyLobby.getInstance().getLogger().warning(e.getResponseCode() + ": " + e.getResponse());
				e.printStackTrace();
			}
		}
	}

	public void updateItemLore(ItemStack item, final int inQueuePlayerCount, final int inGamePlayerCount) {
		// Update player count in item lore
		ItemStackUtil.setLore(item, ChatColor.GOLD.toString() + inQueuePlayerCount + " in queue",
				ChatColor.GOLD.toString() + inGamePlayerCount + " in game", "", ChatColor.YELLOW + "Click to join");

	}

}
