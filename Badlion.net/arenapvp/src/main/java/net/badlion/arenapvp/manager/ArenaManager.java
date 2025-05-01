package net.badlion.arenapvp.manager;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.arenas.Arena;
import net.badlion.arenapvp.arenas.BuildUHCArena;
import net.badlion.arenapvp.listener.MCPListener;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.blocks.CraftMassBlockUpdate;
import net.badlion.gberry.utils.blocks.MassBlockUpdate;
import net.badlion.gedit.BlockData;
import net.badlion.gedit.util.SchematicUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.json.simple.JSONObject;

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
	private static Map<Integer, Set<Arena>> arenas = new HashMap<>();

	private static Map<Integer, Set<Arena>> pastedArenas = new HashMap<>();

	public static Map<Block, Arena> brokenBlocks = new HashMap<>();
	public static Map<Block, Arena> liquidBlocks = new HashMap<>();

	// The last location we pasted an arena at
	private static Location lastArena;

	private static int lastArenaLengthX = 0;
	private static int lastArenaLegnthZ = 0;

	//The minimum distance between two arenas, it could also be greater than this, but never lower
	private static int arenaDistance = 300;

	public static void initialize() {
		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {
			Bukkit.getLogger().log(Level.INFO, "Adding kit id: " + kitRuleSet.getId());
			ArenaManager.arenas.put(kitRuleSet.getId(), new HashSet<>());
		}

		ArenaManager.getAllWarpsFromDB();
		ArenaManager.loadArenasFromDB();

		JSONObject data = new JSONObject();
		data.put("server_name", Gberry.serverName);
		data.put("server_region", Gberry.serverRegion.name().toLowerCase());
		try {
			JSONObject response = Gberry.contactMCP("arena-server-boot", data);
			ArenaPvP.getInstance().getLogger().log(Level.INFO, "[arena-server-boot request] " + data.toString());
			ArenaPvP.getInstance().getLogger().log(Level.INFO, "[arena-server-boot response] " + response.toString());
			if (!response.equals(MCPManager.successResponse)) {
				ArenaPvP.getInstance().getLogger().info("NO ARENAS FOUND");
				ArenaPvP.getInstance().getLogger().info("Response: " + response);

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");

				return;
			}
			for (Map.Entry<Integer, Set<Arena>> entry : arenas.entrySet()) {
				pasteArenas(entry.getKey(), entry.getValue().size());
			}
			// Paste in all arenas

			// Tell mcp all these arenas are now open and ready for use
			JSONObject arenaOpen = new JSONObject();
			arenaOpen.put("server_name", Gberry.serverName);
			List<Map<String, String>> openArenas = new ArrayList<>();
			List<Arena> addedArenas = new ArrayList<>();
			for (Map.Entry<Integer, Set<Arena>> entry : pastedArenas.entrySet()) {
				for (Arena arena : entry.getValue()) {
					if (addedArenas.contains(arena)) {
						// Don't register the arena 2 times to mcp
						continue;
					}
					addedArenas.add(arena);
					Map<String, String> info = new HashMap<>();
					info.put("name", arena.getArenaName());
					info.put("types", arena.getArenaTypesString());
					openArenas.add(info);
				}
			}
			arenaOpen.put("arenas", openArenas);
			arenaOpen.put("add_to_counter", "false");
			List<JSONObject> currentOpenArenas = MCPListener.data.get("open_arenas");
			if (currentOpenArenas == null) {
				currentOpenArenas = new ArrayList<>();
			}
			currentOpenArenas.add(arenaOpen);
			MCPListener.data.put("open_arenas", currentOpenArenas);
			ArenaPvP.getInstance().getLogger().log(Level.INFO, "[arena-open request] " + arenaOpen.toString());
		} catch (HTTPRequestFailException e) {
			ArenaPvP.getInstance().getLogger().log(Level.SEVERE, "Could not get arenas from mcp, shutting down!");
			ArenaPvP.getInstance().getServer().shutdown();
			e.printStackTrace();
		}

	}

	// This is the method we will want to use when a duel starts since mcp will tell us the arena name
	public static Arena getArenaByName(String name) {
		for (Map.Entry<Integer, Set<Arena>> arenaSet : ArenaManager.pastedArenas.entrySet()) {
			for (Arena arena : arenaSet.getValue()) {
				if (arena.getArenaName().equals(name)) {
					return arena;
				}
			}
		}
		return null;
	}

	public static List<Arena> getAllArenasOfType(Integer arenaType) {
		List<Arena> arenas = new ArrayList<>();
		arenas.addAll(ArenaManager.arenas.get(arenaType));
		return arenas;
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

			World world = ArenaPvP.getInstance().getServer().getWorld("world");

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
			org.bukkit.Bukkit.getLogger().severe(ex.getMessage());
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

			File arenaSchematics = new File(ArenaPvP.getInstance().getDataFolder(), "arenas");
			if (!arenaSchematics.exists()) {
				arenaSchematics.mkdir();
			}
			while (rs.next()) {
				// Paste in 2 of each arena, 5 if its build uhc
				int amountToPaste = 2;
				String[] types = rs.getString("types").split(",");
				for (String type : types) {
					if (type.equals("26")) {
						amountToPaste = 5;
						break;
					}
				}

				File arenaSchematic = new File(arenaSchematics, rs.getString("arena_name") + ".schematic");
				if (!arenaSchematic.exists()) {
					continue;
				}

				// Add each arena (Not pasting them into world yet just loading them)
				for (int i = 0; i < amountToPaste; i++) {
					String arenaName = rs.getString("arena_name") + (i > 0 ? "-" + i : "");
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

						for (String type : types) {
							// Builduhc and custom kits get builduhc arena
							if (type.equals("26") || type.equals("0")) {
								Bukkit.getLogger().log(Level.INFO, "BUILDUHC ARENA: " + arenaName);
								arena = new BuildUHCArena(arenaName, schematicName, warp1Location, warp2Location, extraData, rs.getString("types"));
								break;
							} else {
								arena = new Arena(arenaName, schematicName, warp1Location, warp2Location, rs.getString("types"));
							}
						}
					} else if (warp1Location == null && warp2Location != null || warp1Location != null) {
						throw new RuntimeException("Failed to load arena " + arenaName);
					} else {
						Gberry.log("ARENAS", "Adding arena " + arenaName + " with no warps.");
						arena = new Arena(arenaName, schematicName, null, null, rs.getString("types"));
					}
					for (String type : types) {
						try {
							int enumType = Integer.parseInt(type);
							Bukkit.getLogger().log(Level.INFO, "Trying to load arena kit id: " + enumType);

							ArenaManager.arenas.get(enumType).add(arena);
							arena.addType(enumType);

							Gberry.log("ARENAS", "Adding arena " + arenaName + " to type " + enumType);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
				}
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
			ArenaManager.lastArena = new Location(ArenaPvP.getInstance().getServer().getWorlds().get(0), 900, 90, 0);
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
	public static void pasteArenas(Integer type, int limit) {
		List<Arena> arenasToPaste = new ArrayList<>();
		int i = 0;
		arenaloop:
		for (Arena arena : getAllArenasOfType(type)) {
			for (Set<Arena> arenaSet : ArenaManager.pastedArenas.values()) {
				if (arenaSet.contains(arena)) {
					// Don't paste the same arena 2 times, but we can add it to the pasted arena map with this type
					if (ArenaManager.pastedArenas.containsKey(type)) {
						Set<Arena> arenaSet1 = pastedArenas.get(type);
						if (!arenaSet1.contains(arena)) {
							arenaSet1.add(arena);
						}
					} else {
						Set<Arena> arenaSet1 = new HashSet<>();
						arenaSet1.add(arena);
						ArenaManager.pastedArenas.put(type, arenaSet1);
					}
					continue arenaloop;
				}
			}
			if (i < limit) {
				i++;
			} else {
				break;
			}
			arenasToPaste.add(arena);
		}
		if (!arenasToPaste.isEmpty()) {
			pasteArenas(type, arenasToPaste);
		}
	}

	// Paste in the list of arenas into the world
	public static void pasteArenas(Integer type, List<Arena> arenas) {
		Map<String, List<Arena>> arenasWithSameSchematic = new HashMap<>();
		for (Arena arena : arenas) {
			if (arenasWithSameSchematic.containsKey(arena.getSchematicName())) {
				arenasWithSameSchematic.get(arena.getSchematicName()).add(arena);
			} else {
				List<Arena> arenaList = new ArrayList<>();
				arenaList.add(arena);
				arenasWithSameSchematic.put(arena.getSchematicName(), arenaList);
			}
		}

		for (Map.Entry<String, List<Arena>> entry : arenasWithSameSchematic.entrySet()) {
			long startTime = System.currentTimeMillis();
			if (!ArenaPvP.getInstance().getDataFolder().exists()) {
				ArenaPvP.getInstance().getDataFolder().mkdir();
			}
			File arenaSchematics = new File(ArenaPvP.getInstance().getDataFolder(), "arenas");
			if (!arenaSchematics.exists()) {
				arenaSchematics.mkdir();
				return;
			}
			List<BlockData> blockStates = null;
			try {
				blockStates = SchematicUtil.getBlockDataFromSchematic(new File(arenaSchematics, entry.getKey() + ".schematic"));
			} catch (FileNotFoundException e) {
				Bukkit.getLogger().log(Level.INFO, "Arena Schematic Not Found: " + entry.getKey());
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			for (Arena arena : entry.getValue()) {
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

				ArenaPvP.getInstance().getLogger().log(Level.INFO, "[ARENAPASTE] " + arena.getArenaName() + ": x=" + nextPaste.getBlockX() + " z=" + nextPaste.getBlockZ());

				// Using craft mass block update rather than world edit pasting
				World mainWorld = ArenaPvP.getInstance().getServer().getWorld("world");
				MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(mainWorld);
				massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);
				massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);
				// Load schematic using GEDIT

				for (BlockData blockData : blockStates) {
					// TODO: Allow air to be placed for now, once we go live we need to have a script to delete the world on startup!
					// if (blockData.getTypeId() != 0) {
					int x = blockData.getBlockVector().getBlockX();
					int y = blockData.getBlockVector().getBlockY();
					int z = blockData.getBlockVector().getBlockZ();
					// Make sure chunks are loaded
					mainWorld.loadChunk(mainWorld.getChunkAt(new Location(mainWorld, nextPaste.getBlockX() + x, nextPaste.getBlockY() + y, nextPaste.getBlockZ() + z)));

					massBlockUpdate.setBlock(nextPaste.getBlockX() + x, nextPaste.getBlockY() + y, nextPaste.getBlockZ() + z, blockData.getTypeId(), blockData.getData());
					//}
				}
				ArenaPvP.getInstance().getLogger().log(Level.INFO, "[ARENAPASTE] " + arena.getArenaName() + " Paste done: " + (System.currentTimeMillis() - startTime) + " ms");

				massBlockUpdate.notifyClients();

				arena.setOrigin(nextPaste);

				arena.scan();

				if (pastedArenas.containsKey(type)) {
					Set<Arena> arenaSet = pastedArenas.get(type);
					arenaSet.add(arena);
				} else {
					Set<Arena> arenaSet = new HashSet<>();
					arenaSet.add(arena);
					pastedArenas.put(type, arenaSet);
				}

				ArenaManager.lastArenaLengthX = arena.getArenaLengthX();
				ArenaManager.lastArenaLegnthZ = arena.getArenaLengthZ();
				ArenaManager.lastArena = nextPaste;

			}
		}
	}

	public static Arena getBrokenBlockArena(Block block) {
		return ArenaManager.brokenBlocks.get(block);
	}

	public static void removeBrokenBlock(Block block) {
		ArenaManager.brokenBlocks.remove(block);
	}

	public static boolean containsLiquidBlock(Block block) {
		return ArenaManager.liquidBlocks.containsKey(block);
	}

	public static void removeLiquidBlock(Block block) {
		ArenaManager.liquidBlocks.remove(block);
	}

}
