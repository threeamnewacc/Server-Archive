package net.badlion.gberry.managers;

import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.MCPKeepAliveEvent;
import net.badlion.gberry.events.MCPKeepAliveFailedEvent;
import net.badlion.gberry.utils.MCPUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class MCPManager {

	public static final JSONObject successResponse = new JSONObject();
	public static final JSONObject errorResponse = new JSONObject();

	public enum MCP_MESSAGE {
		BUKKIT_ENABLE,
		BUKKIT_DISABLE,
		BUKKIT_KEEP_ALIVE,
		BUKKIT_SEND_TO_SERVER,
		SYNC_SERVER,
		POST_SYNC_SERVER,
		CLEAR_SYNC_SERVER,
		SEND_TO_ALL,
		MATCHMAKING_GET_LOBBIES,
		MATCHMAKING_DEFAULT_CHECK_GAME,
		MATCHMAKING_DEFAULT_QUEUE_UP,
		MATCHMAKING_DEFAULT_QUEUE_COUNT,
		MATCHMAKING_DEFAULT_REMOVE,
		MATCHMAKING_DEFAULT_REMOVE_PLAYERS
	}

	public static double previousPlayerCount = 0;
	public static double previousTps1m = 0;
	public static double previousTps5m = 0;
	public static double previousTps15m = 0;
	public static boolean previousWhitelist = false;

	static {
		MCPManager.successResponse.put("success", "\\o/");
		MCPManager.errorResponse.put("error", ":'(");
	}

	private static Queue<String> checksumCacheQueue = new ConcurrentLinkedQueue<>();

	public static void startKeepAlive() {
		// Keep Alive + Action Receiver
		new BukkitRunnable() {

			// List of checksums from the last keepalive
			private List<String> lastChecksums = new ArrayList<>();

			@Override
			public void run() {
				final UUID keepAliveId = UUID.randomUUID();

				JSONObject data = new JSONObject();

				final List<String> checksums = new ArrayList<>();

				if (!lastChecksums.isEmpty()) {
					Iterator<String> checksumIterator = lastChecksums.iterator();
					while (checksumIterator.hasNext()) {
						checksums.add(checksumIterator.next());
					}
					data.put("checksums", checksums);
				}


				data.put("name", Gberry.serverName);
				int playerCount = Bukkit.getOnlinePlayers().size();
				if (previousPlayerCount != playerCount) {
					data.put("player_count", playerCount);
					previousPlayerCount = playerCount;
				}
				data.put("extra_data", new JSONObject());
				DecimalFormat tpsFormat = new DecimalFormat("##.##");
				double tps1m = Double.valueOf(tpsFormat.format(Gberry.getTPS(Gberry.Benchmark.ONE_MINUTE)));
				if (previousTps1m != tps1m) {
					data.put("tps_1m", tps1m);
					previousTps1m = tps1m;
				}
				double tps5m = Double.valueOf(tpsFormat.format(Gberry.getTPS(Gberry.Benchmark.FIVE_MINUTE)));
				if (previousTps5m != tps5m) {
					data.put("tps_5m", tps5m);
					previousTps5m = tps5m;
				}
				double tps15m = Double.valueOf(tpsFormat.format(Gberry.getTPS(Gberry.Benchmark.FIFTEEN_MINUTE)));
				if (previousTps15m != tps15m) {
					data.put("tps_15m", tps15m);
					previousTps15m = tps15m;
				}
				boolean whitelist = Gberry.plugin.getServer().hasWhitelist();
				if (previousWhitelist != whitelist) {
					data.put("whitelist", whitelist);
					previousWhitelist = whitelist;
				}

				// Call keep alive sync
				final MCPKeepAliveEvent sendKeepAliveEvent = new MCPKeepAliveEvent(data, MCPKeepAliveEvent.KeepAliveType.SEND, keepAliveId);
				Bukkit.getPluginManager().callEvent(sendKeepAliveEvent);
				if (!sendKeepAliveEvent.isCancelled()) {
					data = sendKeepAliveEvent.getJsonObject();
					JSONObject dataToSend = data;

					if (dataToSend.get("extra_data").toString().equals("{}")) {
						dataToSend.remove("extra_data");
					}

					final JSONObject finalDataToSend = dataToSend;
					// Send request async
					new BukkitRunnable() {
						@Override
						public void run() {
							final JSONObject response;

							try {
								response = HTTPCommon.executePOSTRequest(Gberry.mcpURL + MCP_MESSAGE.BUKKIT_KEEP_ALIVE.name().toLowerCase().replace("_", "-") + "/" + Gberry.mcpKey, finalDataToSend, Gberry.mcpTimeout);

								if (response.equals(MCPManager.errorResponse)) {
									JSONObject data = new JSONObject();
									data.put("ip", GetCommon.getIpLocalSystem());
									data.put("port", Gberry.plugin.getServer().getPort());
									data.put("name", Gberry.serverName);
									try {
										// Sent boot up request to mcp
										HTTPCommon.executePOSTRequest(Gberry.mcpURL + MCP_MESSAGE.BUKKIT_ENABLE.name().toLowerCase().replace("_", "-") + "/" + Gberry.mcpKey, data, Gberry.mcpTimeout);
									} catch (HTTPRequestFailException ex) {
										Gberry.plugin.getLogger().info("Failed to make boot up request");
										Gberry.plugin.getLogger().info(ex.getResponseCode() + "");
										Gberry.plugin.getLogger().info(ex.getResponse());
									}
									// Send mcp failed keepalive event to tell the other plugins that mcp sent back the default error
									new BukkitRunnable() {
										@Override
										public void run() {
											MCPKeepAliveFailedEvent failedEvent = new MCPKeepAliveFailedEvent(finalDataToSend, keepAliveId, true);
											Bukkit.getPluginManager().callEvent(failedEvent);
										}
									}.runTask(Gberry.plugin);
									return;
								}

								// Only remove checksums when they are confirmed to be sent
								for (String checksumSent : checksums) {
									if (lastChecksums.contains(checksumSent)) {
										lastChecksums.remove(checksumSent);
									}
								}

								final List<JSONObject> responseObjects = (List<JSONObject>) response.get("response");

								new BukkitRunnable() {
									@Override
									public void run() {
										for (JSONObject responseObject : responseObjects) {
											// Add our checksum to the queue
											if (responseObject.containsKey("checksum")) {
												String checksumString = (String) responseObject.get("checksum");

												lastChecksums.add(checksumString);

												if (MCPManager.checksumCacheQueue.contains(checksumString)) {
													// We have already got this data in the past 60 checksums, we are not going to send out the keepalive event.
													// Do not process same data 2 times
													Bukkit.getLogger().log(Level.WARNING, "KEEPALIVE sent same checksum two times. Ignoring the data from this keepalive");
													try {
														MCPManager.checksumCacheQueue.add(checksumString);
														// Only save 60 checksums
														if (MCPManager.checksumCacheQueue.size() > 60) {
															MCPManager.checksumCacheQueue.remove();
														}
													} catch (Exception e) {
														Bukkit.getLogger().log(Level.INFO, "KeepAlive: Could not add checksum to the queue");
													}
													// Continue in loop since this keepalive already came in, don't call the keepalive response event
													continue;
												} else {
													try {
														MCPManager.checksumCacheQueue.add(checksumString);
														if (MCPManager.checksumCacheQueue.size() > 60) {
															MCPManager.checksumCacheQueue.remove();
														}
													} catch (Exception e) {
														Bukkit.getLogger().log(Level.INFO, "KeepAlive: Could not add checksum to the queue");
													}
												}
											}

											MCPKeepAliveEvent responseKeepAliveEvent = new MCPKeepAliveEvent(responseObject, MCPKeepAliveEvent.KeepAliveType.RESPONSE, keepAliveId);
											Bukkit.getPluginManager().callEvent(responseKeepAliveEvent);
											if (!responseKeepAliveEvent.isCancelled()) {
												MCPUtil.handleResponse(MCP_MESSAGE.BUKKIT_KEEP_ALIVE, finalDataToSend, responseKeepAliveEvent.getJsonObject());
											}
										}
									}
								}.runTask(Gberry.plugin);
							} catch (HTTPRequestFailException e) {
								// If this fails we are queueing it up again which means that we do not need to re-add the checksums as this will be sent first.
								Gberry.plugin.getLogger().info("Failed to make keep alive request");
								Gberry.plugin.getLogger().info(e.getResponseCode() + "");
								Gberry.plugin.getLogger().info(e.getResponse());

								// Send mcp failed keepalive event and let plugins handle it
								if (finalDataToSend.containsKey("extra_data")) {
									new BukkitRunnable() {
										@Override
										public void run() {
											MCPKeepAliveFailedEvent failedEvent = new MCPKeepAliveFailedEvent(finalDataToSend, keepAliveId, false);
											Bukkit.getPluginManager().callEvent(failedEvent);
										}
									}.runTask(Gberry.plugin);
								}
							}
						}
					}.runTaskAsynchronously(Gberry.plugin);

				}
			}
		}.runTaskTimer(Gberry.plugin, 60, 20);
	}

	private static final Queue<FailedData> queuedData = new ConcurrentLinkedQueue<>();

	public static JSONObject bootMCP() {
		JSONObject data = new JSONObject();
		data.put("ip", GetCommon.getIpLocalSystem());
		data.put("port", Gberry.plugin.getServer().getPort());
		return MCPManager.contactMCP(MCP_MESSAGE.BUKKIT_ENABLE, data);
	}

	public static JSONObject shutdownMCP() {
		return MCPManager.contactMCP(MCP_MESSAGE.BUKKIT_DISABLE);
	}

	public static JSONObject contactMCP(MCP_MESSAGE msg) {
		return MCPManager.contactMCP(msg, new JSONObject());
	}

	public static JSONObject contactMCP(MCP_MESSAGE msg, JSONObject data) {
		data.put("name", Gberry.serverName);

		Iterator<FailedData> it = MCPManager.queuedData.iterator();
		while (it.hasNext()) {
			FailedData oldData = it.next();

			try {
				JSONObject response = HTTPCommon.executePOSTRequest(Gberry.mcpURL + oldData.getMsg().name().toLowerCase().replace("_", "-") + "/" + Gberry.mcpKey, oldData.getData(), Gberry.mcpTimeout);

				MCPManager.handleFailedPreviousResponse(oldData.getMsg(), oldData.getData(), response);

				it.remove();
			} catch (HTTPRequestFailException e) {
				Gberry.plugin.getLogger().info(e.getType().name());
				Gberry.plugin.getLogger().info(e.getResponseCode() + "");
				Gberry.plugin.getLogger().info(e.getResponse());
			}
		}

		try {
			JSONObject response = HTTPCommon.executePOSTRequest(Gberry.mcpURL + msg.name().toLowerCase().replace("_", "-") + "/" + Gberry.mcpKey, data, Gberry.mcpTimeout);

			MCPUtil.handleResponse(msg, data, response);

			return response;
		} catch (HTTPRequestFailException e) {
			Gberry.plugin.getLogger().info(e.getType().name());
			Gberry.plugin.getLogger().info(e.getResponseCode() + "");
			Gberry.plugin.getLogger().info(e.getResponse());

			// Timeout errors
			if (msg != MCP_MESSAGE.BUKKIT_KEEP_ALIVE && msg != MCP_MESSAGE.SYNC_SERVER
					&& msg != MCP_MESSAGE.CLEAR_SYNC_SERVER) {
				MCPManager.queuedData.add(new FailedData(msg, data));
			}
		}

		return null;
	}

	// Helper for things that already failed
	private static void handleFailedPreviousResponse(MCP_MESSAGE msg, JSONObject data, JSONObject response) {
		if (response == null) {
			return;
		}

		MCPUtil.handleResponse(msg, data, response);
	}

	private static class FailedData {

		private MCP_MESSAGE msg;
		private JSONObject data;

		public FailedData(MCP_MESSAGE msg, JSONObject data) {
			this.msg = msg;
			this.data = data;
		}

		public MCP_MESSAGE getMsg() {
			return msg;
		}

		public JSONObject getData() {
			return data;
		}

	}

}
