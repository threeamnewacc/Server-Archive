package net.badlion.gberry.utils.blocks;

import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.FastBlockUpdate;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CraftMassBlockUpdate implements MassBlockUpdate, Runnable {

	private final World world;
	private final FastBlockUpdate fastBlockUpdate = Bukkit.getNewFastBlockUpdate();

	private String debug = "N/A";

	private RelightingStrategy relightingStrategy = RelightingStrategy.IMMEDIATE;

	private static final int MAX_BLOCKS_PER_TIME_CHECK = 1000;
	private Queue<DeferredBlock> deferredBlocks = new ArrayDeque<DeferredBlock>();
	private BukkitTask relightTask = null;
	private long maxRelightTimePerTick = TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS);

	private int minX = Integer.MAX_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxZ = Integer.MIN_VALUE;
	private int blocksModified = 0;

	public CraftMassBlockUpdate(World world) {
		this.world = world;
	}

	public CraftMassBlockUpdate(World world, String debug) {
		this.world = world;

		this.debug = debug;
	}

	public boolean setBlock(int x, int y, int z, int blockId) {
		return this.setBlock(x, y, z, blockId, (byte) 0);
	}

	public boolean setBlock(int x, int y, int z, int blockId, byte data) {
		minX = Math.min(minX, x);
		minZ = Math.min(minZ, z);
		maxX = Math.max(maxX, x);
		maxZ = Math.max(maxZ, z);

		blocksModified++;
		int oldBlockId = world.getBlockTypeIdAt(x, y, z);
		boolean res = fastBlockUpdate.setBlockFast(world, x, y, z, blockId, data);

		if (relightingStrategy != RelightingStrategy.NEVER) {
			if (fastBlockUpdate.getBlockLightBlocking(oldBlockId) != fastBlockUpdate.getBlockLightBlocking(blockId)
					|| fastBlockUpdate.getBlockLightEmission(oldBlockId) != fastBlockUpdate.getBlockLightEmission(blockId)) {
				// lighting or light blocking by this block has changed; force a recalculation
				if (relightingStrategy == RelightingStrategy.IMMEDIATE) {
					fastBlockUpdate.recalculateBlockLighting(world, x, y, z);
				} else if (relightingStrategy == RelightingStrategy.DEFERRED || relightingStrategy == RelightingStrategy.HYBRID) {
					deferredBlocks.add(new DeferredBlock(x, y, z));
				}
			}
		}

		return res;
	}

	public void notifyClients() {
		if (relightingStrategy == RelightingStrategy.DEFERRED || relightingStrategy == RelightingStrategy.HYBRID) {
			Gberry.log("LAG", "Resetting " + deferredBlocks.size() + " blocks");
			relightTask = Bukkit.getScheduler().runTaskTimer(Gberry.plugin, this, 1L, 1L);
		}

		if (relightingStrategy != RelightingStrategy.DEFERRED) {
			Set<ChunkCoords> touched = this.calculateChunks();
			Gberry.log("LAG", "Sending " + touched.size() + " chunks now for " + this.debug);
			for (ChunkCoords cc : touched) {
				world.refreshChunk(cc.x, cc.z);
			}
		}
	}

	public void run() {
		long now = System.nanoTime();
		int n = 1;

		while (deferredBlocks.peek() != null) {
			DeferredBlock db = deferredBlocks.poll();
			// Don't consider blocks that are completely surrounded by other non-transparent blocks
			if (canAffectLighting(world, db.x, db.y, db.z)) {
				fastBlockUpdate.recalculateBlockLighting(world, db.x, db.y, db.z);
				if (n++ % MAX_BLOCKS_PER_TIME_CHECK == 0) {
					if (System.nanoTime() - now > maxRelightTimePerTick) {
						break;
					}
				}
			}
		}

		if (deferredBlocks.isEmpty()) {
			relightTask.cancel();
			relightTask = null;
			Set<ChunkCoords> touched = this.calculateChunks();
			Gberry.log("LAG", "Sending " + touched.size() + " chunks now for " + this.debug);
			for (ChunkCoords cc : touched) {
				world.refreshChunk(cc.x, cc.z);
			}
		}
	}

	public void setRelightingStrategy(RelightingStrategy strategy) {
		this.relightingStrategy = strategy;
	}

	public void setMaxRelightTimePerTick(long value, TimeUnit timeUnit) {
		maxRelightTimePerTick = timeUnit.toNanos(value);
	}

	public int getBlocksToRelight() {
		return deferredBlocks.size();
	}

	public void setDeferredBufferSize(int size) {
		if (!deferredBlocks.isEmpty()) {
			// resizing an existing buffer is not supported
			throw new IllegalStateException("setDeferredBufferSize() called after block updates made");
		}

		if (relightingStrategy != RelightingStrategy.DEFERRED && relightingStrategy != RelightingStrategy.HYBRID) {
			// reduce accidental memory wastage if called when not needed
			throw new IllegalStateException("setDeferredBufferSize() called when relighting strategy not DEFERRED or HYBRID");
		}

		deferredBlocks = new ArrayDeque<>(size);
	}

	private boolean canAffectLighting(World world, int x, int y, int z) {
		Block base  = world.getBlockAt(x, y, z);
		Block east  = base.getRelative(BlockFace.EAST);
		Block west  = base.getRelative(BlockFace.WEST);
		Block up    = base.getRelative(BlockFace.UP);
		Block down  = base.getRelative(BlockFace.DOWN);
		Block south = base.getRelative(BlockFace.SOUTH);
		Block north = base.getRelative(BlockFace.NORTH);

		return east.getType().isTransparent() ||
				west.getType().isTransparent() ||
				up.getType().isTransparent() ||
				down.getType().isTransparent() ||
				south.getType().isTransparent() ||
				north.getType().isTransparent();
	}

	private Set<ChunkCoords> calculateChunks() {
		Set<ChunkCoords> res = new HashSet<>();
		if (blocksModified == 0) {
			return res;
		}

		int x1 = minX >> 4; int x2 = maxX >> 4;
		int z1 = minZ >> 4; int z2 = maxZ >> 4;

		Gberry.log("LAG", "Normal coords: minX: " + minX + " maxX: " + maxX + " minZ: " + minZ + " maxZ: " + maxZ + " for " + this.debug);
		Gberry.log("LAG", "Chunk coords: minX: " + x1 + " maxX: " + x2 + " minZ: " + z1 + " maxZ: " + z2 + " for " + this.debug);

		for (int x = x1; x <= x2; x++) {
			for (int z = z1; z <= z2; z++) {
				res.add(new ChunkCoords(x, z));
			}
		}
		return res;
	}

	private class ChunkCoords {
		public final int x, z;
		public ChunkCoords(int x, int z) {
			this.x = x;
			this.z = z;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ChunkCoords that = (ChunkCoords) o;

			if (x != that.x) return false;
			if (z != that.z) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = x;
			result = 31 * result + z;
			return result;
		}
	}

	private class DeferredBlock {
		public final int x, y, z;

		public DeferredBlock(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

}