package net.badlion.gedit.sessions;

import net.badlion.gberry.utils.blocks.CraftMassBlockUpdate;
import net.badlion.gberry.utils.blocks.MassBlockUpdate;
import net.badlion.gedit.BlockData;
import net.badlion.gedit.GEdit;
import net.badlion.gedit.history.HistoryManager;
import net.badlion.gedit.wands.WandSelection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Session implements Cloneable {

	private UUID sessionHandler;
	private WandSelection wandSelection;

	private List<BlockData> blockStates = new ArrayList<>();

	private BlockData[][][] blockDatas;

	private Map<Location, String> blockHistory = new HashMap<>();

	private int xOffSet = 0;
	private int yOffSet = 0;
	private int zOffSet = 0;

	private boolean loadingSchematic = false;

	public Session(UUID sessionHandler, WandSelection wandSelection) {
		this.sessionHandler = sessionHandler;
		this.wandSelection = wandSelection;
	}

	public void copy(Location relativeLocation) {
		if (this.wandSelection.isValidSelection()) {
			this.blockStates = new ArrayList<>();

			this.blockDatas = this.wandSelection.getBlockDataArray();

			for (Block block : this.wandSelection.getAllBlocks()) {
				BlockVector blockVector = new BlockVector(block.getX() - relativeLocation.getBlockX(), block.getY() - relativeLocation.getBlockY(), block.getZ() - relativeLocation.getBlockZ());
				this.blockStates.add(new BlockData(block, blockVector));
			}

			// Lower left corner their position calculate the xyz different between the positions and when pasting add those three a
			Block block = this.blockStates.get(0).getBlock();
			this.xOffSet = block.getX() - relativeLocation.getBlockX();
			this.yOffSet = block.getY() - relativeLocation.getBlockY();
			this.zOffSet = block.getZ() - relativeLocation.getBlockZ();
		}
	}


	public static String getCardinalDirection(Player player) {
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
			return "N";
		} else if (22.5 <= rotation && rotation < 67.5) {
			return "NE";
		} else if (67.5 <= rotation && rotation < 112.5) {
			return "E";
		} else if (112.5 <= rotation && rotation < 157.5) {
			return "SE";
		} else if (157.5 <= rotation && rotation < 202.5) {
			return "S";
		} else if (202.5 <= rotation && rotation < 247.5) {
			return "SW";
		} else if (247.5 <= rotation && rotation < 292.5) {
			return "W";
		} else if (292.5 <= rotation && rotation < 337.5) {
			return "NW";
		} else if (337.5 <= rotation && rotation < 360.0) {
			return "N";
		} else {
			return null;
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void removeBlockHistory() {
		this.blockHistory = new HashMap<>();
	}

	//TODO: Fix directions and save stacks in history to be undone.

	//TODO: Fix wand selection so they dont reset everytime just make sure its a valid selection and then they can use it dont recreate wand selection objects per player everytime

	/***
	 * Notes
	 * East is south
	 * South is west
	 * West is north
	 * North is east
	 */
	public void stack(int offset, int times) {
		Player player = Bukkit.getPlayer(this.sessionHandler);

		if (player == null) return;

		String direction = getCardinalDirection(player);

		if (direction == null) return;

		if (direction.equals("E")) {
			this.xOffSet = 0;
			this.yOffSet = 0;
			this.zOffSet = -1 * offset;
		} else if (direction.equals("W")) {
			this.xOffSet = 0;
			this.yOffSet = 0;
			this.zOffSet = offset;
		} else if (direction.equals("N")) {
			this.xOffSet = -1 * offset;
			this.yOffSet = 0;
			this.zOffSet = 0;
		} else if (direction.equals("S")) {
			this.xOffSet = offset;
			this.yOffSet = 0;
			this.zOffSet = 0;
		}

		int tempXOffSet = this.xOffSet;
		int tempYOffSet = this.yOffSet;
		int tempZOffSet = this.zOffSet;

		MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(this.wandSelection.getPoint1().getWorld());
		massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);
		massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);

		Block firstBlock = this.blockStates.get(0).getBlock();
		Block lastBlock = this.blockStates.get(this.blockStates.size() - 1).getBlock();

		for (int i = 0; i < times; i++) {
			switch (direction) {
				case "N":
					tempXOffSet += ((-1 * Math.abs(firstBlock.getX() - lastBlock.getX())) - 1);
					break;
				case "S":
					tempXOffSet += ((Math.abs(firstBlock.getX() - lastBlock.getX())) + 1);
					break;
				case "E":
					tempZOffSet += ((-1 * Math.abs(firstBlock.getZ() - lastBlock.getZ())) - 1);
					break;
				case "W":
					tempZOffSet += ((Math.abs(firstBlock.getZ() - lastBlock.getZ())) + 1);
					break;
			}

			for (BlockData blockData : this.blockStates) {
				Block block = blockData.getBlock();

				// Make sure chunk is loaded
				block.getWorld().loadChunk(block.getChunk());

				// TODO: Block history still uses block's type and not the type from blockData
				this.blockHistory.put(new Location(block.getLocation().getWorld(), block.getLocation().getBlockX() + tempXOffSet, block.getLocation().getBlockY() + tempYOffSet, block.getLocation().getBlockZ() + tempZOffSet),
						block.getWorld().getBlockAt(block.getLocation().getBlockX() + tempXOffSet, block.getLocation().getBlockY() + tempYOffSet, block.getLocation().getBlockZ() + tempZOffSet).getTypeId() + ":" + block.getWorld().getBlockAt(block.getLocation().getBlockX() + tempXOffSet, block.getLocation().getBlockY() + tempYOffSet, block.getLocation().getBlockZ() + tempZOffSet).getData());
				massBlockUpdate.setBlock(block.getLocation().getBlockX() + tempXOffSet, block.getLocation().getBlockY() + tempYOffSet, block.getLocation().getBlockZ() + tempZOffSet, blockData.getTypeId(), blockData.getData());
			}

			tempXOffSet += this.xOffSet;
			tempYOffSet += this.yOffSet;
			tempZOffSet += this.zOffSet;

		}

		massBlockUpdate.notifyClients();
	}


	public void set(String blocks) {
		this.blockStates = new ArrayList<>();

		for (Block block : this.wandSelection.getAllBlocks()) {
			this.blockStates.add(new BlockData(block));
		}

		if (this.wandSelection.isValidSelection()) {

			if (this.blockStates.isEmpty()) {
				return;
			}

			MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(this.wandSelection.getPoint1().getWorld());
			massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);
			massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);

			Player player = Bukkit.getPlayer(this.sessionHandler);

			if (player == null) return;

			String[] parts = blocks.split(",");

			for (String s : parts) {
				String[] args = s.split(":");

				int id;
				try {
					id = Integer.valueOf(args[0]);
				} catch (NumberFormatException e) {
					player.sendMessage(GEdit.PREFIX + ChatColor.GOLD + "Here at badlion we don't appreciate fucktards. (PS. GIVE ME PROPER NUMBERS)");
					return;
				}
				if (Material.getMaterial(id) == null) {
					player.sendMessage(GEdit.PREFIX + ChatColor.RED + "Please only provide proper item ids.");
					return;
				}
			}

			Random rand = new Random();

			int id;
			byte data = -1;

			for (BlockData blockData : this.blockStates) {
				Block block = blockData.getBlock();

				int index = rand.nextInt(parts.length);

				if (parts[index].contains(":")) {
					String[] b = parts[index].split(":");
					try {
						id = Integer.valueOf(b[0]);
						data = Byte.valueOf(b[1]);
					} catch (Exception e) {
						player.sendMessage(GEdit.PREFIX + ChatColor.GOLD + "Here at badlion we don't appreciate fucktards. (PS. GIVE ME PROPER NUMBERS)");
						return;
					}
				} else {
					try {
						id = Integer.valueOf(parts[index]);
					} catch (NumberFormatException e) {
						player.sendMessage(GEdit.PREFIX + ChatColor.GOLD + "Here at badlion we don't appreciate fucktards. (PS. GIVE ME PROPER NUMBERS)");
						return;
					}
				}

				if (data > 0) {
					this.blockHistory.put(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()), blockData.getTypeId() + ":" + blockData.getData());
					massBlockUpdate.setBlock(block.getX(), block.getY(), block.getZ(), id, data);
				} else {
					this.blockHistory.put(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()), blockData.getTypeId() + ":" + blockData.getData());
					massBlockUpdate.setBlock(block.getX(), block.getY(), block.getZ(), id);
				}
			}

			massBlockUpdate.notifyClients();
			player.sendMessage(GEdit.PREFIX + ChatColor.DARK_GREEN + this.blockStates.size() + " block(s) have been changed.");
			HistoryManager.savePaste(player);
		}
	}

	public void paste(Location relativeLocation) {
		//TODO: Make sure pastes dont go over y level of 255 and notify player that blocks that went over didnt get pasted.
		//TODO: Keep one wand session per player and dont reset it.
		boolean above = false;

		if (this.blockStates.isEmpty()) {
			//Gberry.broadcastMessage("Debug: BlockStates are empty");
			return;
		}
		Long start = System.currentTimeMillis();
		Bukkit.getLogger().log(Level.INFO, "Pasting SCHEMATIC Start: " + start);

		MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(relativeLocation.getWorld());
		massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);
		massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);

		if (!this.blockStates.get(0).isFromSchematic()) {
			Block firstBlock = this.blockStates.get(0).getBlock();
			for (BlockData blockData : this.blockStates) {
				Block block = blockData.getBlock();

				// Make sure chunk is loaded
				block.getWorld().loadChunk(block.getChunk());

				int xDiff = block.getX() - firstBlock.getX();
				int yDiff = block.getY() - firstBlock.getY();
				int zDiff = block.getZ() - firstBlock.getZ();

				// TODO: Block history still uses block's type and not the type from blockData
				this.blockHistory.put(new Location(relativeLocation.getWorld(), relativeLocation.getBlockX() + this.xOffSet + xDiff, relativeLocation.getBlockY() + this.yOffSet + yDiff, relativeLocation.getBlockZ() + this.zOffSet + zDiff), relativeLocation.getWorld().getBlockAt(relativeLocation.getBlockX() + this.xOffSet + xDiff, relativeLocation.getBlockY() + this.yOffSet + yDiff, relativeLocation.getBlockZ() + this.zOffSet + zDiff).getTypeId() + ":" + relativeLocation.getWorld().getBlockAt(relativeLocation.getBlockX() + this.xOffSet + xDiff, relativeLocation.getBlockY() + this.yOffSet + yDiff, relativeLocation.getBlockZ() + this.zOffSet + zDiff).getData());
				massBlockUpdate.setBlock(relativeLocation.getBlockX() + this.xOffSet + xDiff, relativeLocation.getBlockY() + this.yOffSet + yDiff, relativeLocation.getBlockZ() + this.zOffSet + zDiff, blockData.getTypeId(), blockData.getData());
			}
		} else {
			// Dealing with block vector not a block since its from a schematic
			for (BlockData blockData : this.blockStates) {
				BlockVector blockVector = blockData.getBlockVector();

				// Make sure chunk is loaded
				relativeLocation.getWorld().loadChunk(relativeLocation.clone().add(blockVector).getChunk());

				// TODO: Block history still uses block's type and not the type from blockData
				Location pasteLocation = relativeLocation.clone().add(blockVector);
				this.blockHistory.put(pasteLocation, pasteLocation.getBlock().getTypeId() + ":" + pasteLocation.getBlock().getData());
				massBlockUpdate.setBlock(pasteLocation.getBlockX() + this.xOffSet, pasteLocation.getBlockY() + this.yOffSet, pasteLocation.getBlockZ() + this.zOffSet, blockData.getTypeId(), blockData.getData());
			}

		}
		massBlockUpdate.notifyClients();
		Bukkit.getLogger().log(Level.INFO, "Pasting SCHEMATIC Finish: " + (System.currentTimeMillis() - start));

	}

	// Paste in schematic or region so that all non-visible blocks are air. Made for s13 bandwidth saving
	public void pasteHollow(Location relativeLocation, Player player) {
		boolean above = false;
		if (this.blockStates.isEmpty()) {
			//Gberry.broadcastMessage("Debug: BlockStates are empty");
			return;
		}
		Long start = System.currentTimeMillis();
		int blocks = 0;
		int blocksSaved = 0;

		MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(relativeLocation.getWorld());
		massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);
		massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);

		for (int x = 0; x < this.blockDatas.length; x++) {
			for (int y = 0; y < this.blockDatas[x].length; y++) {
				for (int z = 0; z < this.blockDatas[x][y].length; z++) {
					BlockData blockData = this.blockDatas[x][y][z];
					BlockVector blockVector = blockData.getBlockVector();

					// Make sure chunk is loaded
					relativeLocation.getWorld().loadChunk(relativeLocation.clone().add(blockVector).getChunk());

					// TODO: Block history still uses block's type and not the type from blockData
					Location pasteLocation = relativeLocation.clone().add(blockVector);
					this.blockHistory.put(pasteLocation, pasteLocation.getBlock().getTypeId() + ":" + pasteLocation.getBlock().getData());
					if (this.hasTransparentBlockAdjacent(x + 1, y, z)
							|| this.hasTransparentBlockAdjacent(x - 1, y, z)
							|| this.hasTransparentBlockAdjacent(x, y + 1, z)
							|| this.hasTransparentBlockAdjacent(x, y - 1, z)
							|| this.hasTransparentBlockAdjacent(x, y, z + 1)
							|| this.hasTransparentBlockAdjacent(x, y, z - 1)) {
						// Place block since it has transparent block adjacent
						if (blockData.getTypeId() != 0) {
							blocks++;
						}
						massBlockUpdate.setBlock(pasteLocation.getBlockX() + this.xOffSet, pasteLocation.getBlockY() + this.yOffSet, pasteLocation.getBlockZ() + this.zOffSet, blockData.getTypeId(), blockData.getData());
					} else {
						// Place air since solid blocks all around
						blocksSaved++;
						massBlockUpdate.setBlock(pasteLocation.getBlockX() + this.xOffSet, pasteLocation.getBlockY() + this.yOffSet, pasteLocation.getBlockZ() + this.zOffSet, 0, blockData.getData());
					}
				}
			}
		}
		massBlockUpdate.notifyClients();
		Bukkit.getLogger().log(Level.INFO, "Pasting SCHEMATIC Finish: " + (System.currentTimeMillis() - start));
		player.sendMessage(ChatColor.GREEN + "Hollow Paste - Blocks Pasted: " + blocks + " Blocks Saved: " + blocksSaved);
	}

	private boolean hasTransparentBlockAdjacent(int x, int y, int z) {
		// Make sure the array contains this block otherwise return true since it is the edge of the data
		if (x >= 0 && y >= 0 && z >= 0) {
			if (this.blockDatas.length > x && this.blockDatas[x].length > y && this.blockDatas[x][y].length > z) {
				Material material = Material.getMaterial(this.blockDatas[x][y][z].getTypeId());
				if (material.isBlock() && !material.isTransparent() && material.isSolid() && material.isOccluding()) {
					return false;
				}
			}
		}
		return true;
	}

	public List<BlockData> getBlockStates() {
		return blockStates;
	}

	public void undo() {
		if (this.blockHistory.isEmpty()) {
			return;
		}

		World world = this.blockHistory.keySet().iterator().next().getWorld();
		MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(world);
		massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);
		massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);

		for (Map.Entry<Location, String> i : this.blockHistory.entrySet()) {
			String[] parts = i.getValue().split(":");
			int id = Integer.valueOf(parts[0]);
			byte data = Byte.valueOf(parts[1]);
			Location loc = i.getKey();
			massBlockUpdate.setBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), id, data);
		}

		massBlockUpdate.notifyClients();
	}

	public void invalidate() {
		SessionManager.getSessions().remove(this.getSessionHandler());
	}

	public UUID getSessionHandler() {
		return sessionHandler;
	}

	public void setSessionHandler(UUID sessionHandler) {
		this.sessionHandler = sessionHandler;
	}

	public WandSelection getWandSelection() {
		return wandSelection;
	}

	public void setWandSelection(WandSelection wandSelection) {
		this.wandSelection = wandSelection;
	}

	public BlockData[][][] getBlockDatas() {
		return blockDatas;
	}

	public void setLoadingSchematic(boolean loadingSchematic) {
		this.loadingSchematic = loadingSchematic;
	}

	public void setxOffSet(int xOffSet) {
		this.xOffSet = xOffSet;
	}

	public void setyOffSet(int yOffSet) {
		this.yOffSet = yOffSet;
	}

	public void setzOffSet(int zOffSet) {
		this.zOffSet = zOffSet;
	}

	public int getxOffSet() {
		return xOffSet;
	}

	public int getyOffSet() {
		return yOffSet;
	}

	public int getzOffSet() {
		return zOffSet;
	}
}
