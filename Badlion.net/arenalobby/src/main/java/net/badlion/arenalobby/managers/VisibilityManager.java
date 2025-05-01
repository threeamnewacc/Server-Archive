package net.badlion.arenalobby.managers;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.Pair;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class VisibilityManager {

	private static Set<UUID> playersHidingEveryone = new HashSet<>();

	public static void hideEveryone(Player player) {
		if (ArenaLobby.bandwidthSavingMode) {
			// Everyone will already be hidden
			return;
		}
		VisibilityManager.playersHidingEveryone.add(player.getUniqueId());
		for (Player other : Bukkit.getServer().getOnlinePlayers()) {
			player.hidePlayer(other);
		}
	}

	public static void showEveryone(Player player) {
		if (ArenaLobby.bandwidthSavingMode) {
			// Everyone will already be hidden, we do not want to show people in these lobbies
			player.sendFormattedMessage("{0}You can not see other players in this lobby.", ChatColor.RED);
			return;
		}
		VisibilityManager.playersHidingEveryone.remove(player.getUniqueId());
		for (Player other : Bukkit.getServer().getOnlinePlayers()) {
			player.showPlayer(other);
		}
	}

	public static boolean contains(Player player){
		return VisibilityManager.playersHidingEveryone.contains(player.getUniqueId());
	}


	public static void removePlayer(Player player){
		playersHidingEveryone.remove(player.getUniqueId());
	}


	public static void handleLogin(Player player) {
		for (UUID playerId : playersHidingEveryone) {
			Player other = Bukkit.getPlayer(playerId);
			if (other != null) {
				other.hidePlayer(player);
			}
		}
	}

}
