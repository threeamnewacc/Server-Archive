package net.badlion.potpvp.arenas;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.blocks.CraftMassBlockUpdate;
import net.badlion.gberry.utils.blocks.MassBlockUpdate;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.matchmaking.Match;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.statemachine.State;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;

import java.util.*;


public class Arena {

	private int minX = Integer.MAX_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxZ = Integer.MIN_VALUE;

	private String arenaName;
	private Location warp1;
	private Location warp2;
	private Set<ArenaManager.ArenaType> types = new HashSet<>();
	private ArenaManager.ArenaType inUse = null;

    protected boolean beingUsed = false;
	private boolean cleaning = false;

    private Set<Item> droppedItems = new HashSet<>();
    private Set<LivingEntity> livingEntities = new HashSet<>();
    private Set<DebugBlock> blocksPlaced = new HashSet<>();
    private Map<DebugBlock, BlockData> blocksRemoved = new HashMap<>();

    public Arena(String arenaName, Location warp1, Location warp2) {
		this.arenaName = arenaName;
		this.warp1 = warp1;
		this.warp2 = warp2;
	}

	public class DebugBlock {

		private Block block;
		private Player player;
		private boolean removed;
		private Group group;
		private DateTime dateTime;
		private State state;
		private Game game;

		public DebugBlock(Block block, Player player, boolean removed) {
			this.block = block;
			this.player = player;
			this.removed = removed;
			this.dateTime = new DateTime();
			if (this.player != null) {
				this.group = PotPvP.getInstance().getPlayerGroup(player);

				this.state = GroupStateMachine.getInstance().getCurrentState(this.group);
				this.game = GameState.getGroupGame(group);
			}
		}

		public void debug() {
			String s = "ADDED";

			if (removed) s = "REMOVED";

			Gberry.log("LAG", "Time: " + dateTime);
			Gberry.log("LAG", "BLOCK " + s + " - Block info: " + block.toString());

			if (this.player != null) {
				Gberry.log("LAG", "Name: " + player.getName() + " Gamemode: " + player.getGameMode());

				Gberry.log("LAG", "State: " + this.state + " Arena Name: " + Arena.this.getArenaName());

				if (game != null) {
					Gberry.log("LAG", "Group: " + group + " Game: " + game + " Game kit: " + game.getKitRuleSet());
					Gberry.log("LAG", "Is Over: " + game.isOver());

					if (game instanceof Match) {
						Gberry.log("LAG", "Alive players: " + ((Match) game).getAlivePlayers(group) + " Ladder Type: " + ((Match) game).ladderType);
					}
				} else {
					Gberry.log("LAG", "Group: " + group + " NO CURRENT GAME");
				}
			}
		}

		public Block getBlock() {
			return block;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof DebugBlock)) {
				return false;
			}

			DebugBlock db = (DebugBlock) o;
			return this.block.equals(db.getBlock());
		}

		@Override
		public int hashCode() {
			return this.block.hashCode();
		}

	}

    public boolean isBeingUsed() {
        return this.beingUsed;
    }

    public void toggleBeingUsed() {
        if (this.beingUsed) {
			new CleanArenaTask(this).runTaskTimer(PotPvP.getInstance(), 60L, 1L);
        } else {
            Gberry.log("ARENA", "Arena " + this.arenaName + " is being toggled from false to true");

            this.beingUsed = true;
        }

	    this.minX = Integer.MAX_VALUE;
	    this.minZ = Integer.MAX_VALUE;
	    this.maxX = Integer.MIN_VALUE;
	    this.maxZ = Integer.MIN_VALUE;
    }

	public void rebuild() {

	}

	public void debug() {
		if (this.cleaning) {
			try {
				throw new Exception("Bug");
			} catch (Exception  e) {
				e.printStackTrace();
			}
		}
	}

    public void addItemDrop(Item item) {
		this.debug();
	    this.droppedItems.add(item);
    }

    public Set<Item> getDroppedItems() {
		this.debug();
        return droppedItems;
    }

    public boolean removeItemDrop(Item item) {
		this.debug();
        return this.droppedItems.remove(item);
    }

    public Set<LivingEntity> getLivingEntities() {
		this.debug();
        return livingEntities;
    }

	private void printDebug(Block block, Player player, boolean removed) {
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		Game game = GameState.getGroupGame(group);

		String s = "ADDED";

		if (removed) s = "REMOVED";

		//Gberry.log("LAG", "minX: " + minX + " this.minX: " + this.minX + ", difference: " + (minX - this.minX));
		Gberry.log("LAG", "BLOCK " + s + " - Block info: " + block.toString());
		Gberry.log("LAG", "Name: " + player.getName() + " Gamemode: " + player.getGameMode());
		Gberry.log("LAG", "State: " + GroupStateMachine.getInstance().getCurrentState(group) + " Arena Name: " + this.getArenaName());

		if (game != null) {
			Gberry.log("LAG", "Group: " + group + " Game: " + game + " Game kit: " + game.getKitRuleSet());
			Gberry.log("LAG", "Is Over: " + game.isOver());

			if (game instanceof Match) {
				Gberry.log("LAG", "Alive players: " + ((Match) game).getAlivePlayers(group) + " Ladder Type: " + ((Match) game).ladderType);
			}
		} else {
			Gberry.log("LAG", "Group: " + group + " NO CURRENT GAME");
		}

		Gberry.log("LAG", "Smelly debug lines:");
		PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId()).printDebug();

		Gberry.log("LAG", "All blocks placed:");
		for (DebugBlock bl : this.blocksPlaced) {
			bl.debug();
		}

		Gberry.log("LAG", "All blocks removed:");
		for (DebugBlock bl : this.blocksRemoved.keySet()) {
			bl.debug();
		}

		List<String> lines = GroupStateMachine.getInstance().debugTransitionsForElement(group);
		for (String line : lines) {
			Gberry.log("LAG", line);
		}

		try {
			throw new Exception("PRINT DEBUG");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addBlockPlaced(Block block) {
		this.blocksPlaced.add(new DebugBlock(block, null, false));
	}

	public void addBlockPlaced(Block block, Player player) {
		int minX = this.minX;
		int minZ = this.minZ;
		int maxX = this.maxX;
		int maxZ = this.maxZ;

		this.minX = Math.min(this.minX, block.getX());
		this.minZ = Math.min(this.minZ, block.getZ());
		this.maxX = Math.max(this.maxX, block.getX());
		this.maxZ = Math.max(this.maxZ, block.getZ());

		if (minX != Integer.MAX_VALUE && Math.abs(minX - this.minX) > 200) {
			this.printDebug(block, player, false);
		}

		if (minZ != Integer.MAX_VALUE && Math.abs(minZ - this.minZ) > 200) {
			this.printDebug(block, player, false);
		}

		if (maxX != Integer.MIN_VALUE && Math.abs(this.maxX - maxX) > 200) {
			this.printDebug(block, player, false);
		}

		if (maxZ != Integer.MIN_VALUE && Math.abs(this.maxZ - maxZ) > 200) {
			this.printDebug(block, player, false);
		}

		this.debug();
		Gberry.log("ARENA", "Adding block placed to remove later " + block.toString());
		this.blocksPlaced.add(new DebugBlock(block, player, false));
	}

	public boolean containsBlockPlaced(Block block) {
		return this.containsBlockPlaced(block, null);
	}

	public boolean containsBlockPlaced(Block block, Player player) {
		DebugBlock debugBlock = new DebugBlock(block, player, false);
		return this.blocksPlaced.contains(debugBlock);
	}

	public void addBlockRemoved(Block block, Player player) {
		int minX = this.minX;
		int minZ = this.minZ;
		int maxX = this.maxX;
		int maxZ = this.maxZ;

		this.minX = Math.min(this.minX, block.getX());
		this.minZ = Math.min(this.minZ, block.getZ());
		this.maxX = Math.max(this.maxX, block.getX());
		this.maxZ = Math.max(this.maxZ, block.getZ());

		if (minX != Integer.MAX_VALUE && Math.abs(minX - this.minX) > 200) {
			this.printDebug(block, player, true);
		}

		if (minZ != Integer.MAX_VALUE && Math.abs(minZ - this.minZ) > 200) {
			this.printDebug(block, player, true);
		}

		if (maxX != Integer.MIN_VALUE && Math.abs(this.maxX - maxX) > 200) {
			this.printDebug(block, player, true);
		}

		if (maxZ != Integer.MIN_VALUE && Math.abs(this.maxZ - maxZ) > 200) {
			this.printDebug(block, player, true);
		}

		this.debug();
		Gberry.log("ARENA", "Adding block removed to change later " + block.toString());
		this.blocksRemoved.put(new DebugBlock(block, player, true), new BlockData(block.getType(), block.getData()));
	}

	public void addBlockRemoved(Block block) {
		this.blocksRemoved.put(new DebugBlock(block, null, true), new BlockData(block.getType(), block.getData()));
	}

	public boolean containsBlockRemoved(Block block) {
		return this.containsBlockRemoved(block, null);
	}

	public boolean containsBlockRemoved(Block block, Player player) {
		DebugBlock debugBlock = new DebugBlock(block, player, true);
		return this.blocksRemoved.containsKey(debugBlock);
	}

    public String getArenaName() {
		return arenaName;
	}

	public Location getWarp1() {
		return warp1;
	}

	public Location getWarp2() {
		return warp2;
	}

	public void addType(ArenaManager.ArenaType type) {
		this.types.add(type);
	}

	public ArenaManager.ArenaType getInUse() {
		return inUse;
	}

	public void setInUse(ArenaManager.ArenaType inUse) {
		this.inUse = inUse;
	}

	public class BlockData {

        private Material material;
        private byte data;

        public BlockData(Material material, byte data) {
            this.material = material;
            this.data = data;
        }

        public Material getMaterial() {
            return material;
        }

        public byte getData() {
            return data;
        }

    }

	// By default does nothing
	public void scan() {

	}

	public void startArenaUse(Game game) {

	}

	public boolean isCleaning() {
		return cleaning;
	}

	public void doneCleaning() {
		this.cleaning = false;
	}

	public static class CleanArenaTask extends BukkitRunnable {

		Arena arena;
		Iterator<Item> itemIterator;
		Iterator<LivingEntity> entityIterator;
		Iterator<DebugBlock> blockIterator;
		Iterator<Map.Entry<DebugBlock, BlockData>> blockRemoveIterator;

		public CleanArenaTask(Arena arena) {
			this.arena = arena;
		}

		@Override
		public void run() {
			if (this.itemIterator == null) {
				this.arena.cleaning = true;
				this.itemIterator = this.arena.droppedItems.iterator();
				this.entityIterator = this.arena.livingEntities.iterator();
				this.blockIterator = this.arena.blocksPlaced.iterator();
				this.blockRemoveIterator = this.arena.blocksRemoved.entrySet().iterator();
				Gberry.log("LAG", "Arena " + this.arena.getArenaName() + " cleaning task is starting");
			}

			try {
				int i = 0;
				while (itemIterator.hasNext()) {
					Item item = itemIterator.next();
					item.remove();
					++i;

					if (i >= 50) {
						return;
					}
				}

				if (i != 0) {
					return;
				}

				while (entityIterator.hasNext()) {
					LivingEntity entity = entityIterator.next();
					entity.remove();
					++i;

					if (i >= 50) {
						return;
					}
				}

				if (i != 0) {
					return;
				}

				if (Bukkit.getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
					while (blockIterator.hasNext()) {
						Block block = blockIterator.next().getBlock();
						Location location = block.getLocation();

						Gberry.log("ARENA", "Removing block " + block.toString());

						block.setType(Material.AIR);
					}

					while (blockRemoveIterator.hasNext()) {
						Map.Entry<DebugBlock, BlockData> entry = blockRemoveIterator.next();
						Block block = entry.getKey().getBlock();
						BlockData blockData = entry.getValue();
						Location location = block.getLocation();

						Gberry.log("ARENA", "Adding block " + entry.getKey().toString());

						// Avoid [s]small[/s] BIG memory leak with liquids
						ArenaManager.removeBrokenBlock(block);
						ArenaManager.removeLiquidBlock(block);

						block.setType(entry.getValue().getMaterial());
						block.setData(entry.getValue().getData());

						location.getBlock().setType(blockData.getMaterial());
						location.getBlock().setData(blockData.getData());
					}
				} else {
					MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(PotPvP.getInstance().getSpawnLocation().getWorld(), this.arena.getArenaName());
					massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.HYBRID);

					while (blockIterator.hasNext()) {
						Block block = blockIterator.next().getBlock();
						Location location = block.getLocation();

						Gberry.log("ARENA", "Removing block " + block.toString());

						massBlockUpdate.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), Material.AIR.getId());
					}

					while (blockRemoveIterator.hasNext()) {
						Map.Entry<DebugBlock, BlockData> entry = blockRemoveIterator.next();
						Block block = entry.getKey().getBlock();
						BlockData blockData = entry.getValue();
						Location location = block.getLocation();

						Gberry.log("ARENA", "Adding block " + entry.getKey().toString());

						// Avoid [s]small[/s] BIG memory leak with liquids
						ArenaManager.removeBrokenBlock(block);
						ArenaManager.removeLiquidBlock(block);

						block.setType(entry.getValue().getMaterial());
						block.setData(entry.getValue().getData());

						massBlockUpdate.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), blockData.getMaterial().getId(), blockData.getData());
					}

					massBlockUpdate.notifyClients();
				}

				// Clean memory (#BlameSmelly)
				this.arena.droppedItems = new HashSet<>();
				this.arena.livingEntities = new HashSet<>();
				this.arena.blocksPlaced = new HashSet<>();
				this.arena.blocksRemoved = new HashMap<>();

				this.arena.setInUse(null);
				this.cancel();

				// Finally mark arena as usable
				this.markAsDone();
			} catch (ConcurrentModificationException e) {
				Bukkit.getLogger().info("arena " + this.arena.arenaName);
				e.printStackTrace();

				for (Item item : this.arena.droppedItems) {
					item.remove();
				}

				for (LivingEntity entity : this.arena.livingEntities) {
					entity.remove();
				}

				this.arena.setInUse(null);
				this.markAsDone();
				this.cancel();
			}
		}

		public void markAsDone() {
			this.arena.beingUsed = false;
			this.arena.doneCleaning();
		}

	}

}
