package net.badlion.arenapvp.manager;

import net.badlion.arenapvp.Team;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EnderPearlManager {

	private static Map<Player, EnderPearl> playerToPearl = new HashMap<>();

	public static void put(Player player, EnderPearl enderPearl) {
		EnderPearlManager.playerToPearl.put(player, enderPearl);
	}

	public static void remove(Collection<Player> players) {
		for (Player player : players) {
			EnderPearlManager.remove(player);
		}
	}

	public static void remove(Team team) {
		for (Player player : team.members()) {
			EnderPearlManager.remove(player);
		}
	}

	public static void remove(Player player) {
		EnderPearl enderPearl = EnderPearlManager.playerToPearl.remove(player);
		if (enderPearl != null) {
			enderPearl.remove();
		}
	}

}
