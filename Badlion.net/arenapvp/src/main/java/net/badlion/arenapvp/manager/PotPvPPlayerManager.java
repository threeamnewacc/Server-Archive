package net.badlion.arenapvp.manager;

import net.badlion.arenapvp.PotPvPPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PotPvPPlayerManager implements Listener {

	public static Map<UUID, PotPvPPlayer> players = new HashMap<>();

	public static PotPvPPlayer getPotPvPPlayer(UUID uuid) {
		return PotPvPPlayerManager.players.get(uuid);
	}

	public static void addDebug(Player player, String str) {
		PotPvPPlayerManager.addDebug(player.getUniqueId(), str);
	}

	public static void addDebug(UUID uuid, String str) {
		try {
			PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(uuid);

			potPvPPlayer.addDebug(str);
		} catch (NullPointerException e) {

		}
	}

}
