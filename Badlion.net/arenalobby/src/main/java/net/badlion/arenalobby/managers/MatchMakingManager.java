package net.badlion.arenalobby.managers;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.inventories.duel.DuelInventorySnapshotData;
import net.badlion.arenalobby.matchmaking.MatchMakingService;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.Inflater;

public class MatchMakingManager extends BukkitUtil.Listener {

	private static Map<Integer, Map<UUID, DuelInventorySnapshotData>> duelSnapshots = new HashMap<>();

	private static Map<Group, MatchMakingService> playerMatchMakingServiceHashMap = new HashMap<>();

	public static void addToMatchMaking(Group group, MatchMakingService service) {
		MatchMakingManager.playerMatchMakingServiceHashMap.put(group, service);
	}

	public static MatchMakingService getMatchMakingService(Group group) {
		return MatchMakingManager.playerMatchMakingServiceHashMap.get(group);
	}

	public static boolean removeFromMatchMaking(Group group, boolean transitionState) {
		MatchMakingService service = MatchMakingManager.playerMatchMakingServiceHashMap.remove(group);
		return service != null && service.removeGroup(group, transitionState);
	}

	public static void openOpponentInventory(final Integer matchId, final UUID playerId, final String name, final Player viewer) {
		if (duelSnapshots.containsKey(matchId)) {
			Map<UUID, DuelInventorySnapshotData> duelInventorySnapshotDataMap = duelSnapshots.get(matchId);
			if (duelInventorySnapshotDataMap.containsKey(playerId)) {
				viewer.openInventory(duelInventorySnapshotDataMap.get(playerId).getInventory());
				return;
			}
			return;
		}

		// Don't store party events
		new BukkitRunnable() {

			@Override
			public void run() {
				String query = "SELECT * FROM kit_pvp_matches_s14 WHERE match_id = ?;";
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				Inflater inflater = null;
				ByteArrayOutputStream bos = null;
				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setInt(1, matchId);

					rs = Gberry.executeQuery(connection, ps);
					if (rs.next()) {
						JSONParser parser = new JSONParser();
						inflater = new Inflater();
						byte[] data = rs.getBytes("data");
						inflater.setInput(data);

						bos = new ByteArrayOutputStream(data.length);
						byte[] buf = new byte[1024];
						while (!inflater.finished()) {
							int count = inflater.inflate(buf);
							bos.write(buf, 0, count);

						}
						bos.close();
						inflater.end();
						byte[] decompressedData = bos.toByteArray();

						// Decode the bytes into a String
						String outputString = new String(decompressedData, 0, decompressedData.length, "UTF-8");

						final JSONObject jsonData = (JSONObject) parser.parse(outputString);
						final Map<String, JSONArray> totalPotionEffects = (Map<String, JSONArray>) jsonData.get("totalPotionEffects");
						final Map<String, JSONArray> totalArmor = (Map<String, JSONArray>) jsonData.get("totalArmor");
						final Map<String, JSONArray> totalInventory = (Map<String, JSONArray>) jsonData.get("totalInventory");
						final Map<String, Long> foodMap = (Map<String, Long>) jsonData.get("foodMap");
						final Map<String, Double> healthMap = (Map<String, Double>) jsonData.get("healthMap");
						new BukkitRunnable() {
							@Override
							public void run() {
								Map<UUID, DuelInventorySnapshotData> duelInventorySnapshotDataMap = new HashMap<>();
								for (String id : foodMap.keySet()) {
									UUID fighterId = UUID.fromString(id);
									DuelInventorySnapshotData duelInventorySnapshotData = new DuelInventorySnapshotData(fighterId, "", totalPotionEffects.get(fighterId.toString()), totalArmor.get(fighterId.toString()), totalInventory.get(fighterId.toString()), foodMap.get(fighterId.toString()), healthMap.get(fighterId.toString()));
									duelInventorySnapshotDataMap.put(fighterId, duelInventorySnapshotData);
									if (fighterId.equals(playerId)) {
										viewer.openInventory(duelInventorySnapshotData.getInventory());
									}
								}
								duelSnapshots.put(matchId, duelInventorySnapshotDataMap);
							}
						}.runTask(ArenaLobby.getInstance());
					}
				} catch (SQLException e) {
					//Bukkit.getLogger().info(this.ladderType + ",||| " + this.team1.members().size() + ", " + this.team2.members().size()
					//		+ ",||| " + this.copyOfTeam1.members().size() + ", " + this.copyOfTeam2.members().size());
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(rs, ps, connection);
					if (inflater != null) {
						inflater.end();
					}
					if (bos != null) {
						try {
							bos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.runTaskAsynchronously(ArenaLobby.getInstance());
	}


	public static void removeOpponentInventory(UUID playerId) {
		MatchMakingManager.duelSnapshots.remove(playerId);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Group group = ArenaLobby.getInstance().getPlayerGroup(event.getPlayer());
		MatchMakingManager.removeFromMatchMaking(group, false);
	}

}
