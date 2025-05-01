package net.badlion.arenarender;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.blocks.CraftMassBlockUpdate;
import net.badlion.gberry.utils.blocks.MassBlockUpdate;
import net.badlion.gedit.BlockData;
import net.badlion.gedit.util.SchematicUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ArenaManager extends BukkitUtil.Listener {


	private static Map<String, Location> warps = new HashMap<>();
	private static Set<Arena> arenas = new HashSet<>();

	private static Set<Arena> pastedArenas = new HashSet<>();

	// The last location we pasted an arena at
	private static Location lastArena;

	private static int lastArenaLengthX = 0;
	private static int lastArenaLegnthZ = 0;

	//The minimum distance between two arenas, it could also be greater than this, but never lower
	private static int arenaDistance = 300;

	public static void initialize() {

		ArenaManager.getAllWarpsFromDB();
		ArenaManager.loadArenasFromDB();


		ArenaManager.pasteArenas();
	}


	public static Location getWarp(String name) {
		return ArenaManager.warps.get(name);
	}

	public static Map<String, Location> getWarps() {
		return ArenaManager.warps;
	}

	/**
	 * Should be called sync
	 */
	private static void getAllWarpsFromDB() {
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getUnsafeConnection();
			String sql = "SELECT * FROM build_warps_s14;";
			ps = connection.prepareStatement(sql);
			rs = ps.executeQuery();

			World world = ArenaRender.getInstance().getServer().getWorld("world");

			while (rs.next()) {
				Location location = new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch"));
				ArenaManager.warps.put(rs.getString("warp_name"), location);

				// Load render distance chunks
				int x = location.getChunk().getX();
				int z = location.getChunk().getZ();
				for (int i = x - 6; i <= x + 6; i++) {
					for (int j = z - 6; j <= z + 6; j++) {
						world.getChunkAt(i, j).load();
					}
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			Bukkit.getLogger().severe(ex.getMessage());
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}
	}

	private static void loadArenasFromDB() {
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getUnsafeConnection();
			String sql = "SELECT * FROM build_arenas_s14;";
			ps = connection.prepareStatement(sql);
			rs = Gberry.executeQuery(connection, ps);

			while (rs.next()) {
				// Paste in 2 of each arena, 5 if its build uhc

				String arenaName = rs.getString("arena_name");
				String schematicName = rs.getString("arena_name");
				String warp1 = rs.getString("warp_1");
				String warp2 = rs.getString("warp_2");
				String extraData = rs.getString("extra_data");

				Location warp1Location = ArenaManager.warps.get(warp1).clone();
				Location warp2Location = ArenaManager.warps.get(warp2).clone();

				// Valid arena loaded, lets make it available
				Arena arena = null;
				if (warp1Location != null && warp2Location != null) {
					Gberry.log("ARENAS", "Adding arena " + arenaName + " with warps " + warp1Location.toString() + " and " + warp2Location.toString());
					arena = new Arena(arenaName, schematicName, warp1Location, warp2Location, rs.getString("types"));
				} else if (warp1Location == null && warp2Location != null || warp1Location != null) {
					throw new RuntimeException("Failed to load arena " + arenaName);
				} else {
					Gberry.log("ARENAS", "Adding arena " + arenaName + " with no warps.");
					arena = new Arena(arenaName, schematicName, null, null, rs.getString("types"));
				}
				ArenaManager.arenas.add(arena);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}
	}

	// Arena Auto Pasting kind of in a spiral pattern around 0,0
	// No two arenas should ever run into each other

	public static Location getNextPastePoint(int nextArenaLengthX, int nextArenaLengthZ) {
		if (ArenaManager.lastArena == null) {
			// Start at 900, 0 to give buffer room for spawn/kit editing at 0,0
			ArenaManager.lastArena = new Location(ArenaRender.getInstance().getServer().getWorlds().get(0), 900, 90, 0);
		}
		final int x = (int) ArenaManager.lastArena.getX();
		final int z = (int) ArenaManager.lastArena.getZ();
		Location nextPos = ArenaManager.lastArena.clone();
		if (x > z) {
			if (-x >= z) {
				nextPos.setX(nextPos.getX() - (ArenaManager.lastArenaLengthX + nextArenaLengthX + arenaDistance));
				return nextPos;
			}
			nextPos.setZ(nextPos.getZ() - (ArenaManager.lastArenaLegnthZ + nextArenaLengthZ + arenaDistance));
			return nextPos;
		}
		if (x < z) {
			if (-x < z) {
				nextPos.setX(nextPos.getX() + (ArenaManager.lastArenaLengthX + nextArenaLengthX + arenaDistance));
				return nextPos;
			}
			nextPos.setZ(nextPos.getZ() + (ArenaManager.lastArenaLegnthZ + nextArenaLengthZ + arenaDistance));
			return nextPos;
		}

		if (x <= 0) {
			nextPos.setZ(nextPos.getZ() + (ArenaManager.lastArenaLegnthZ + nextArenaLengthZ + arenaDistance));
			return nextPos;
		}
		nextPos.setZ(nextPos.getZ() - (ArenaManager.lastArenaLegnthZ + nextArenaLengthZ + arenaDistance));
		return nextPos;
	}


	// Paste a certain number of arenas of a type with no duplicate arenas being pasted if an arena has two types
	public static void pasteArenas() {
		List<Arena> arenasToPaste = new ArrayList<>();
		int i = 0;
		for (Arena arena : ArenaManager.arenas) {
			arenasToPaste.add(arena);
		}
		if (!arenasToPaste.isEmpty()) {
			pasteArenas(arenasToPaste);
		}
	}

	// Paste in the list of arenas into the world
	public static void pasteArenas(List<Arena> arenas) {

		for (Arena arena : arenas) {
			long startTime = System.currentTimeMillis();
			if (!ArenaRender.getInstance().getDataFolder().exists()) {
				ArenaRender.getInstance().getDataFolder().mkdir();
			}
			File arenaSchematics = new File(ArenaRender.getInstance().getDataFolder(), "arenas");
			if (!arenaSchematics.exists()) {
				arenaSchematics.mkdir();
				return;
			}
			List<BlockData> blockStates = null;
			try {
				blockStates = SchematicUtil.getBlockDataFromSchematic(new File(arenaSchematics, arena.getSchematicName() + ".schematic"));
			} catch (FileNotFoundException e) {
				Bukkit.getLogger().log(Level.INFO, "Arena Schematic Not Found: " + arena.getSchematicName());
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}


			final Location nextPaste = getNextPastePoint(arena.getArenaLengthX(), arena.getArenaLengthZ());

			if (nextPaste.getBlockX() > 0) {
				nextPaste.subtract((nextPaste.getBlockX() % 16), 0, 0);
			} else {
				nextPaste.add((nextPaste.getBlockX() % 16), 0, 0);
			}
			if (nextPaste.getBlockZ() > 0) {
				nextPaste.subtract((nextPaste.getBlockZ() % 16), 0, 0);
			} else {
				nextPaste.add((nextPaste.getBlockZ() % 16), 0, 0);
			}

			ArenaRender.getInstance().getLogger().log(Level.INFO, "[ARENAPASTE] " + arena.getArenaName() + ": x=" + nextPaste.getBlockX() + " z=" + nextPaste.getBlockZ());

			// Using craft mass block update rather than world edit pasting
			World mainWorld = ArenaRender.getInstance().getServer().getWorld("world");
			MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(mainWorld);
			massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);
			massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);
			// Load schematic using GEDIT

			List<Chunk> addedChunks = new ArrayList();
			for (BlockData blockData : blockStates) {
				if (blockData.getTypeId() == Material.GLASS.getId()) {
					continue;
				}
				int x = blockData.getBlockVector().getBlockX();
				int y = blockData.getBlockVector().getBlockY();
				int z = blockData.getBlockVector().getBlockZ();
				if (blockData.getTypeId() != 0) {
					arena.checkForMinMaxLocation(nextPaste.getBlockX() + x, nextPaste.getBlockY() + y, nextPaste.getBlockZ() + z);
				}
				Chunk chunk = mainWorld.getChunkAt(new Location(mainWorld, nextPaste.getBlockX() + x, nextPaste.getBlockY() + y, nextPaste.getBlockZ() + z));
				if (!addedChunks.contains(chunk)) {
					JSONArray jsonArray = new JSONArray();
					jsonArray.add(Integer.valueOf(chunk.getX()));
					jsonArray.add(Integer.valueOf(chunk.getZ()));
					arena.getChunkList().add(jsonArray);
					addedChunks.add(chunk);
				}
				mainWorld.loadChunk(chunk);

				massBlockUpdate.setBlock(nextPaste.getBlockX() + x, nextPaste.getBlockY() + y, nextPaste.getBlockZ() + z, blockData.getTypeId(), blockData.getData());
			}
			ArenaRender.getInstance().getLogger().log(Level.INFO, "[ARENAPASTE] " + arena.getArenaName() + " Paste done: " + (System.currentTimeMillis() - startTime) + " ms");

			massBlockUpdate.notifyClients();

			arena.setOrigin(nextPaste);

			pastedArenas.add(arena);

			arena.generateRenderConfig();

			ArenaManager.lastArenaLengthX = arena.getArenaLengthX();
			ArenaManager.lastArenaLegnthZ = arena.getArenaLengthZ();
			ArenaManager.lastArena = nextPaste;

		}

	}

}
