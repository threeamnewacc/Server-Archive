package net.badlion.arenapvp.listener;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.manager.MatchManager;
import net.badlion.arenapvp.manager.SpectateManager;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.MCPKeepAliveEvent;
import net.badlion.gberry.events.MCPKeepAliveFailedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class MCPListener implements Listener {

	public static Map<String, List<JSONObject>> data = new ConcurrentHashMap<>();

	private static Map<UUID, Map<String, List<JSONObject>>> pendingData = new ConcurrentHashMap<>();

	public static boolean shutdown = false;

	public static boolean resendingArenaBoot = false;

	@EventHandler
	public void onMCPKeepAlive(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			if (event.getJsonObject() != null) {
				if (event.getJsonObject().containsKey("shutdown_server")) {
					if (Boolean.valueOf((String) event.getJsonObject().get("shutdown_server"))) {
						Bukkit.getLogger().log(Level.INFO, "MCP KEEPALIVE SHUTDOWN");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
					}
				}
			}
			SpectateManager.tryTeleportPlayers();
			MatchManager.handleNewMatchs(event.getJsonObject());
			SpectateManager.handleNewSpectators(event.getJsonObject());
		}
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.SEND)) {
			JSONObject extraData = (JSONObject) event.getJsonObject().get("extra_data");
			if (!data.isEmpty()) {
				Bukkit.getLogger().log(Level.INFO, "SEND DATA: " + data.toString());
			}

			pendingData.put(event.getKeepAliveId(), new HashMap<>());

			Iterator<Map.Entry<String, List<JSONObject>>> extraDataIterator = data.entrySet().iterator();
			while (extraDataIterator.hasNext()) {
				Map.Entry<String, List<JSONObject>> entry = extraDataIterator.next();
				extraData.put(entry.getKey(), entry.getValue());
				pendingData.get(event.getKeepAliveId()).put(entry.getKey(), entry.getValue());
				extraDataIterator.remove();
			}
			if (shutdown) {
				extraData.put("ready_for_shutdown", "true");
				shutdown = false;
			}
			if (!extraData.isEmpty()) {
				Bukkit.getLogger().log(Level.INFO, "EXTRADATA: " + extraData.toString());
			}

			// Cleanup the map 15 seconds after adding the data
			new BukkitRunnable() {
				@Override
				public void run() {
					if (pendingData.containsKey(event.getKeepAliveId())) {
						pendingData.remove(event.getKeepAliveId());
					}
				}
			}.runTaskLater(ArenaPvP.getInstance(), 20 * 15);
		}
	}


	@EventHandler
	public void onMCPFailedKeepAliveEvent(MCPKeepAliveFailedEvent event) {
		if (event.isMcpError() && !MCPListener.resendingArenaBoot) {
			JSONObject data = new JSONObject();
			data.put("server_name", Gberry.serverName);
			data.put("server_region", Gberry.serverRegion.name().toLowerCase());
			MCPListener.resendingArenaBoot = true;
			new BukkitRunnable(){
				@Override
				public void run() {
					try {
						JSONObject response = Gberry.contactMCP("arena-server-boot", data);
						ArenaPvP.getInstance().getLogger().log(Level.INFO, "[Resending arena-server-boot request] " + data.toString());
						ArenaPvP.getInstance().getLogger().log(Level.INFO, "[arena-server-boot response] " + response.toString());
					} catch (HTTPRequestFailException e) {
						e.printStackTrace();
					} finally {
						MCPListener.resendingArenaBoot = false;
					}
				}
			}.runTaskAsynchronously(ArenaPvP.getInstance());
		}

		if(!pendingData.containsKey(event.getKeepAliveId())){
			return;
		}

		Map<String, List<JSONObject>> failedData = pendingData.get(event.getKeepAliveId());
		if (failedData != null) {
			Iterator<Map.Entry<String, List<JSONObject>>> failedDataIterator = failedData.entrySet().iterator();
			while (failedDataIterator.hasNext()) {
				Map.Entry<String, List<JSONObject>> entry = failedDataIterator.next();

				List<JSONObject> currentItems = data.get(entry.getKey());
				if (currentItems == null) {
					currentItems = new ArrayList<>();
				}
				currentItems.addAll(entry.getValue());

				data.put(entry.getKey(), currentItems);

				failedDataIterator.remove();
			}
			pendingData.remove(event.getKeepAliveId());
		}
	}

}
