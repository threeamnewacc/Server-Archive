package net.badlion.uhc.practice;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.practice.kits.Kit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PracticeArena {

	private static final int SPAWN_MIN_X = 6;
	private static final int SPAWN_MIN_Z = 1006;

	private static final int SPAWN_MAX_X = 346;
	private static final int SPAWN_MAX_Z = 1346;

	private static final int SPAWN_Y = 200;

	private Kit kit;

	private World world;

	private Set<UUID> players = new HashSet<>();

	public PracticeArena(Kit kit) {
		this.kit = kit;

		this.world = BadlionUHC.getInstance().getServer().getWorld("uhcpractice");
	}

	public Kit getKit() {
		return kit;
	}

	public Set<UUID> getPlayers() {
		return players;
	}

	public boolean isInArena(Player player) {
		return this.players.contains(player.getUniqueId());
	}

	public void addPlayer(Player player) {
		if (BadlionUHC.getInstance().isPractice()) {
			// Add them to the players
			this.players.add(player.getUniqueId());

			// Teleport them to a spawn point
			this.randomTeleport(player);

			// Give them the kit
			this.kit.giveItems(player);
		}
	}

	public void removePlayer(Player player, boolean teleportSpawn) {
		// Remove player from cache
		this.players.remove(player.getUniqueId());

		// Teleport them to spawn
		if (teleportSpawn) {
			player.teleport(BadlionUHC.getInstance().getSpawnLocation());
		}
	}

	public void randomTeleport(Player player) {
		player.teleport(this.getRandomSpawnLocation());
	}

	public Location getRandomSpawnLocation() {
		return new Location(this.world,
				Gberry.generateRandomInt(PracticeArena.SPAWN_MIN_X, PracticeArena.SPAWN_MAX_X) + 0.5D,
				PracticeArena.SPAWN_Y,
				Gberry.generateRandomInt(PracticeArena.SPAWN_MIN_Z, PracticeArena.SPAWN_MAX_Z) + 0.5D);
	}

}
