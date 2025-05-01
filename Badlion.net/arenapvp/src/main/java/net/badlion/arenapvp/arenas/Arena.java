package net.badlion.arenapvp.arenas;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.Game;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.listener.MCPListener;
import net.badlion.arenapvp.manager.ArenaManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.blocks.CraftMassBlockUpdate;
import net.badlion.gberry.utils.blocks.MassBlockUpdate;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;


public class Arena {

	private int minX = Integer.MAX_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxZ = Integer.MIN_VALUE;

	private Location origin;
	private String arenaName;
	private String schematicName;
	private Location warp1;
	private Location warp2;
	private Set<Integer> types = new HashSet<>();
	private String arenaTypes = "";
	private Integer inUse = null;

	private boolean beingUsed = false;
	private boolean cleaning = false;

	private Set<Item> droppedItems = new HashSet<>();
	private Set<LivingEntity> livingEntities = new HashSet<>();
	private Set<DebugBlock> blocksPlaced = new HashSet<>();
	private Map<DebugBlock, BlockData> blocksRemoved = new HashMap<>();

	public Arena(String arenaName, String schematicName, Location warp1, Location warp2, String arenaTypes) {
		this.arenaName = arenaName;
		this.schematicName = schematicName;
		this.warp1 = warp1;
		this.warp2 = warp2;
		this.arenaTypes = arenaTypes;
	}

	public class DebugBlock {

		private Block block;
		private Player player;
		private boolean removed;
		private Team team;
		private DateTime dateTime;
		//private State state;
		private Game game;

		public DebugBlock(Block block, Player player, boolean removed) {
			this.block = block;
			this.player = player;
			this.removed = removed;
			this.dateTime = new DateTime();
			if (this.player != null) {
				this.team = ArenaPvP.getInstance().getPlayerTeam(player);

				//this.state = GroupStateMachine.getInstance().getCurrentState(this.group);
				//this.game = GameState.getGroupGame(group);
			}
		}

		public void debug() {
		    /*
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
            */
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

	public void setBeingUsed(boolean beingUsed) {
		ArenaPvP.getInstance().getLogger().log(Level.INFO, "Arena: " + this.arenaName + " being used = " + beingUsed);
		this.beingUsed = beingUsed;
	}

	public void cleanArena() {
		new CleanArenaTask(this).runTaskTimer(ArenaPvP.getInstance(), 60L, 1L);
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
			} catch (Exception e) {
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
		Team team = ArenaPvP.getInstance().getPlayerTeam(player);
	    /*Game game = GameState.getGroupGame(group);

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

        List<String> lines = GroupStateMachine.getInstance().debugTransitionsForElement(team);
        for (String line : lines) {
            Gberry.log("LAG", line);
        }

        try {
            throw new Exception("PRINT DEBUG");
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
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

	public String getNiceArenaName() {
		return StringUtils.capitalize(getSchematicName().replace("s13-", ""));
	}

	public String getSchematicName() {
		return this.schematicName;
	}

	public Location getWarp1() {
		return warp1;
	}

	public Location getWarp2() {
		return warp2;
	}

	public Location getWarp1Origin() {
		int x = origin.getBlockX() + warp1.getBlockX();
		int y = origin.getBlockY() + warp1.getBlockY();
		int z = origin.getBlockZ() + warp1.getBlockZ();

		return new Location(warp1.getWorld(), x, y, z, warp1.getYaw(), warp1.getPitch());
	}

	public Location getWarp2Origin() {
		int x = origin.getBlockX() + warp2.getBlockX();
		int y = origin.getBlockY() + warp2.getBlockY();
		int z = origin.getBlockZ() + warp2.getBlockZ();

		return new Location(warp2.getWorld(), x, y, z, warp2.getYaw(), warp2.getPitch());
	}

	public void setOrigin(Location location) {
		this.origin = location;
	}

	public Location getOrigin() {
		return this.origin;
	}

	public void addType(Integer type) {
		this.types.add(type);
	}

	public Set<Integer> getArenaTypes() {
		return types;
	}

	public String getArenaTypesString() {
		return this.arenaTypes;
	}

	// Returns kitruleset id
	public Integer getInUse() {
		return inUse;
	}

	public void setInUse(Integer inUse) {
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

	public int getArenaLengthX() {
		return Math.abs(maxX - minX);
	}

	public int getArenaLengthZ() {
		return Math.abs(maxZ - minZ);
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
		this.setBeingUsed(false);

		JSONObject arenaOpen = new JSONObject();
		arenaOpen.put("server_name", Gberry.serverName);
		List<Map<String, String>> openArenas = new ArrayList<>();

		Map<String, String> info = new HashMap<>();
		info.put("name", getArenaName());
		StringBuilder types = new StringBuilder();
		for (Integer arenaType : getArenaTypes()) {
			types.append(String.valueOf(arenaType));
			types.append(",");
		}
		info.put("types", types.substring(0, types.length() - 1).toString());
		openArenas.add(info);
		arenaOpen.put("arenas", openArenas);
		arenaOpen.put("add_to_counter", "true");
		List<JSONObject> currentOpenArenas = MCPListener.data.get("open_arenas");
		if (currentOpenArenas == null) {
			currentOpenArenas = new ArrayList<>();
		}
		currentOpenArenas.add(arenaOpen);
		MCPListener.data.put("open_arenas", currentOpenArenas);
		ArenaPvP.getInstance().getLogger().log(Level.INFO, "[arena-open request] " + arenaOpen.toString());
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

				MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(ArenaPvP.getInstance().getSpawnLocation().getWorld(), this.arena.getArenaName());
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
			this.arena.doneCleaning();
		}

	}

}
