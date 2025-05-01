package net.badlion.arenapvp.manager;

import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.helper.SpectatorHelper;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.arenapvp.state.MatchState;
import net.kohi.sidebar.SidebarAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class SpectateManager {

	private static Map<UUID, UUID> playersToTeleport = new HashMap<>();

	// If they don't join within 10 keepalives then remove them from the maps
	private static Map<UUID, Integer> timesTriedToTeleport = new HashMap<>();

	// Called from MCP event, adds the spectators uuids to players to teleport map, this data will come in after the player is already online
	public static void handleNewSpectators(JSONObject jsonObject) {
		if (jsonObject == null) {
			return;
		}
		List<JSONObject> pendingSpectators = (List<JSONObject>) jsonObject.get("spectators");
		if (pendingSpectators != null) {
			Bukkit.getLogger().log(Level.INFO, "SPEC: " + pendingSpectators);
			for (JSONObject spectate : pendingSpectators) {
				UUID spectator = UUID.fromString((String) spectate.get("spectator_uuid"));
				UUID target = UUID.fromString((String) spectate.get("target_uuid"));
				Player player = Bukkit.getPlayer(spectator);
				Player targetPlayer = Bukkit.getPlayer(target);
				if (player != null && targetPlayer != null) {
					if (TeamStateMachine.spectatorState.contains(player)) {
						player.teleport(targetPlayer);
						Match match = MatchState.getPlayerMatch(targetPlayer);
						if (match != null) {
							SpectateManager.setSpectatingMatch(player, match);
						}
					}
				} else {
					if (!SpectateManager.playersToTeleport.containsKey(spectator) && !SpectateManager.playersToTeleport.containsKey(target)) {
						SpectateManager.playersToTeleport.put(spectator, target);
					}
				}
			}
		}
	}

	// Tries to teleport spectators every time a mcp keep alive comes in,
	public static void tryTeleportPlayers() {
		if (playersToTeleport.isEmpty()) {
			return;
		}
		Iterator<Map.Entry<UUID, UUID>> iter = playersToTeleport.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<UUID, UUID> entry = iter.next();
			Player player = Bukkit.getPlayer(entry.getKey());
			Player targetPlayer = Bukkit.getPlayer(entry.getValue());
			if (player != null && targetPlayer != null) {
				if (TeamStateMachine.spectatorState.contains(player)) {
					player.teleport(targetPlayer);
					Match match = MatchState.getPlayerMatch(targetPlayer);
					if (match != null) {
						SpectateManager.setSpectatingMatch(player, match);
					}
					iter.remove();
				}
			} else {
				if (timesTriedToTeleport.get(entry.getKey()) != null) {
					int amount = timesTriedToTeleport.get(entry.getKey());
					if (amount > 10) {
						timesTriedToTeleport.remove(entry.getKey());
						iter.remove();
					} else {
						timesTriedToTeleport.put(entry.getKey(), amount + 1);
					}
				} else {
					timesTriedToTeleport.put(entry.getKey(), 1);
				}
			}
		}
	}


	// Adds sidebar and colors the players name for the match they are spectating
	public static void setSpectatingMatch(Player player, Match match) {
		SidebarManager.addSpectatorSidebar(player, match);
		match.updateSpectatorScoreboards(player);

		TeamStateMachine.spectatorState.setSpectatorMatch(player, match);

		player.getInventory().setItem(1, SpectatorHelper.getSpectatorTeleportItem());

		if (ArenaSettingsManager.getSettings(player).showsColoredHelmInSpec()) {
			player.getInventory().setItem(3, SpectatorHelper.getSpectatorLeatherHelmetColorsOn());
			TeamStateMachine.spectatorState.setColoredArmorEnabled(player);
		} else {
			player.getInventory().setItem(3, SpectatorHelper.getSpectatorLeatherHelmetColorsOff());
		}
	}


	public static void removeSpectatingMatch(Player player, Match match) {
		if (TeamStateMachine.spectatorState.contains(player)) {
			if (TeamStateMachine.spectatorState.getSpectatorMatch(player) != null) {
				SidebarAPI.removeAllSidebarItems(player);
				match.removeSpectatorScoreboards(player);

				player.getInventory().setItem(1, null);
				player.getInventory().setItem(3, null);

				player.updateInventory();
			}
		}
	}
}
