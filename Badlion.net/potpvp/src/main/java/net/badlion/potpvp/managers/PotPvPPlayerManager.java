package net.badlion.potpvp.managers;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.PotPvPPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PotPvPPlayerManager extends BukkitUtil.Listener {

    private static Map<UUID, PotPvPPlayer> players = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
	    PotPvPPlayer potPvPPlayer = new PotPvPPlayer();
        PotPvPPlayerManager.players.put(event.getPlayer().getUniqueId(), potPvPPlayer);

	    potPvPPlayer.addDebug("Debug info for player: " + event.getPlayer().getName());
	    potPvPPlayer.addDebug("Logged in");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	    PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.players.remove(event.getPlayer().getUniqueId());

	    potPvPPlayer.addDebug("Logged out");
    }

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
