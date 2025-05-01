package net.badlion.arenalobby.managers;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StasisManager {

	private static Set<Group> groups = new HashSet<>();
	private static Set<Group> groups2 = new HashSet<>();
	private static Set<Group> groups3 = new HashSet<>();

	private static Map<Group, StasisHandler> handlers = new HashMap<>();

	public static void initialize() {
		new StasisTask().runTaskTimer(ArenaLobby.getInstance(), 0, 20L);
	}

	private static Set<Group> getList() {
		long currentTick = ArenaLobby.getInstance().getServer().getCurrentTick();

		if (currentTick % 60 >= 40) {
			return StasisManager.groups;
		} else if (currentTick % 60 >= 20) {
			return StasisManager.groups2;
		}

		return StasisManager.groups3;
	}

	private static Set<Group> getLastList() {
		long currentTick = ArenaLobby.getInstance().getServer().getCurrentTick();

		if (currentTick % 60 >= 40) {
			return StasisManager.groups2;
		} else if (currentTick % 60 >= 20) {
			return StasisManager.groups3;
		}

		return StasisManager.groups;
	}

	public static void addToStasis(Group group, StasisHandler handler) {
		Set<Group> groups = getLastList();
		groups.add(group);
		StasisManager.handlers.put(group, handler);

		for (Player player : group.players()) {
			if (player.isDead()) {
				continue;
			}

			player.getInventory().clear();
			player.setGameMode(GameMode.CREATIVE);
			player.setFlying(true);
		}
	}

	public static boolean isInStasis(Group group) {
		return StasisManager.handlers.containsKey(group);
	}

	/**
	 * Interface for handling stasis results
	 */
	public interface StasisHandler {

		void run(Group group);

	}

	private static class StasisTask extends BukkitRunnable {

		@Override
		public void run() {
			Set<Group> groups = StasisManager.getList();
			Iterator<Group> it = groups.iterator();

			// Transition them out of their current state and put them back into lobby/party state
			while (it.hasNext()) {
				Group group = it.next();

				StasisHandler handler = StasisManager.handlers.remove(group);
				if (handler != null) {
					handler.run(group);
				}

				it.remove();
			}
		}

	}

}
