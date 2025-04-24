package com.massivecraft.factions.type;

import club.minemen.spigot.chunk.FakeMultiBlockChange;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.Faction;
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ChunkWalls {

	private static final Location loc = new Location(null, 0.0, 0.0, 0.0);
	private final Predicate<FactionPlayer> showWalls;
	private final Predicate<Faction> showChunk;
	private final int updateDistance = 8;
	private Location lastUpdateLocation;

	private final Material material;
	private final int data;

	public ChunkWalls(Material material, int data, Predicate<FactionPlayer> showWalls, Predicate<Faction> showChunk) {
		this.material = material;
		this.data = data;
		this.showWalls = showWalls;
		this.showChunk = showChunk;
	}

	public void update(FactionPlayer fplayer, boolean force) {
		Player player = fplayer.getPlayer();
		if (player == null) {
			return;
		}
		player.getLocation(loc);


		if (!showWalls.test(fplayer)) {
			if (lastUpdateLocation != null) {
				lastUpdateLocation = null;
				int dist = 6;
				int chunkX = loc.getBlockX() >> 4;
				int chunkZ = loc.getBlockZ() >> 4;
				FLocation floc = new FLocation(loc.getWorld().getName(), 0, 0);
				EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
				for (int x = -dist; x <= dist; x++) {
					for (int z = -dist; z <= dist; z++) {
						floc.setX(chunkX + x);
						floc.setZ(chunkZ + z);
						if (showChunk.test(Board.getFactionAt(floc))) {
							ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(chunkX + x, chunkZ + z);
							entityPlayer.chunkCoordIntPairQueue.add(chunkCoordIntPair);
						}
					}
				}
			}
			return;
		}

		if (lastUpdateLocation != null && !force) {
			if (lastUpdateLocation.getWorld() == loc.getWorld()) {
				if (lastUpdateLocation.distanceSquared(loc) < updateDistance * updateDistance) {
					return;
				}
			}
		}

		int chunkX = loc.getBlockX() >> 4;
		int chunkZ = loc.getBlockZ() >> 4;

		// dont show walls from inside
		if (showChunkAt(chunkX, chunkZ)) {
			return;
		}

		List<Location> locationArray = new ArrayList<>();
		List<Integer> blockArray = new ArrayList<>();
		List<Integer> dataArray = new ArrayList<>();

		for (int x = chunkX - 1; x <= chunkX + 1; x++) {
			for (int z = chunkZ - 1; z <= chunkZ + 1; z++) {
				if (!showChunkAt(x, z)) {
					continue;
				}
				boolean joinXPos = showChunkAt(x + 1, z);
				boolean joinXNeg = showChunkAt(x - 1, z);
				boolean joinZPos = showChunkAt(x, z + 1);
				boolean joinZNeg = showChunkAt(x, z - 1);
				if (joinXPos && joinXNeg && joinZPos && joinZNeg) {
					continue;
				}
				Chunk chunk = loc.getWorld().getChunkAt(x, z);
				blockArray.clear();
				locationArray.clear();
				dataArray.clear();
				for (int y = Math.max(0, loc.getBlockY() - 8); y <= Math.min(loc.getBlockY() + 8, 255); y++) {
					for (int i = 0; i < 16; i++) {
						if (!joinZNeg && (i != 0 || joinXNeg) && !chunk.getBlock(i, y, 0).getType().isSolid()) {
							// - and + x
							locationArray.add(chunk.getBlock(i, y, 0).getLocation());
							blockArray.add(this.material.getId());
							dataArray.add(this.data);
						}
						if (!joinZPos && (i != 15 || joinXPos) && !chunk.getBlock(i, y, 15).getType().isSolid()) {
							// - and + x
							locationArray.add(chunk.getBlock(i, y, 15).getLocation());
							blockArray.add(this.material.getId());
							dataArray.add(this.data);
						}
						if (!joinXNeg && (i != 15 || joinZPos) && !chunk.getBlock(0, y, i).getType().isSolid()) {
							// -z and +z
							locationArray.add(chunk.getBlock(0, y, i).getLocation());
							blockArray.add(this.material.getId());
							dataArray.add(this.data);
						}
						if (!joinXPos && (i != 0 || joinZNeg) && !chunk.getBlock(15, y, i).getType().isSolid()) {
							// -z and +z
							locationArray.add(chunk.getBlock(15, y, i).getLocation());
							blockArray.add(this.material.getId());
							dataArray.add(this.data);
						}
					}
				}
				if (!blockArray.isEmpty()) {
					FakeMultiBlockChange fakeMultiBlockChange = chunk.createFakeBlockUpdate(locationArray.toArray(new Location[locationArray.size()]), blockArray.stream().mapToInt(i -> i).toArray(), dataArray.stream().mapToInt(i -> i).toArray());
					fakeMultiBlockChange.sendTo(player);
				}
			}
		}

		lastUpdateLocation = loc.clone();
	}

	private boolean showChunkAt(int x, int z) {
		return showChunk.test(Board.getFactionAt(new FLocation(loc.getWorld().getName(), x, z)));
	}
}
