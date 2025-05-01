package net.badlion.mpglobby.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.mpglobby.MPGLobby;
import net.badlion.mpglobby.QueueType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.UUID;

public class UpdateQueueCountTask extends BukkitRunnable {

	private int counter = 0;

	public void run() {
		if (++this.counter == 10) {
			// Message players in queue
			for (Map.Entry<UUID, QueueType> entry : MPGLobby.getInstance().getPlayersInQueue().entrySet()) {
				Player player = Bukkit.getPlayer(entry.getKey());

				// Because the players in queue set isn't a concurrent data set and doesn't matter here
				if (player == null) continue;

				player.sendMessage(ChatColor.AQUA + "There are currently " + entry.getValue().getInQueuePlayerCount()
						+ " players in queue. Waiting for more players for a match to start...");
			}

			this.counter = 0;
		}

		for (final QueueType queueType : QueueType.values()) {
			JSONObject payload = new JSONObject();

			payload.put("server_region", Gberry.serverRegion.name().toLowerCase());
			payload.put("server_type", Gberry.serverType.getInternalName());
			payload.put("type", queueType.getGameType().name().toLowerCase());
			payload.put("ladder", queueType.getLadder());

			JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.MATCHMAKING_DEFAULT_QUEUE_COUNT, payload);

			System.out.println(Gberry.serverRegion.name().toLowerCase() + ": " + response);

			// Update player count for queue
			queueType.setPlayerCount(Gberry.getJSONInteger(response, "in_queue"), Gberry.getJSONInteger(response, "in_matches"));
		}
	}

}
