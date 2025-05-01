package net.badlion.potpvp.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.*;
import net.badlion.potpvp.exceptions.OutOfArenasException;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ArenaManager extends BukkitUtil.Listener {

    public enum ArenaType {
	    // TODO: GAPPLE IS NEVER USED?
	    // NOTE: RED_ROVER IS UNUSED, ID/SLOT CAN BE USED FOR FUTURE ARENA TYPE
	    PEARL, NON_PEARL, BUILD_UHC, HORSE, SOUP, ARCHER, LMS, WAR, SLAUGHTER, UHC_MEETUP,
	    INFECTION, KOTH, RED_ROVER, PARTY_FFA, SKYWARS, TDM, SPLEEF, SPLEEF_FFA, BUILD_UHC_FFA, GAPPLE
    }

    private static Map<String, Location> warps = new HashMap<>();
    private static Map<ArenaType, Set<Arena>> arenas = new HashMap<>();

    private static Map<Block, Arena> brokenBlocks = new HashMap<>();
    private static Map<Block, Arena> liquidBlocks = new HashMap<>();

    public static void initialize() {
        new ArenaManager();

        for (ArenaType arenaType : ArenaType.values()) {
            ArenaManager.arenas.put(arenaType, new HashSet<Arena>());
        }

        ArenaManager.getAllWarpsFromDB();
        ArenaManager.loadArenasFromDB();
    }

    public static Arena getArena(ArenaType arenaType) throws OutOfArenasException {
        List<Arena> arenasAvailable = new ArrayList<>();
        for (Arena arena : ArenaManager.arenas.get(arenaType)) {
            if (!arena.isBeingUsed()) {
                arenasAvailable.add(arena);
            }
        }

        // Out of arenas
        if (arenasAvailable.size() == 0) {
            throw new OutOfArenasException();
        }

        int i = (int) (arenasAvailable.size() * Math.random());
        Arena arena = arenasAvailable.get(i);
        Gberry.log("ARENA", "Chose to use arena " + arena.getArenaName() + " for type " + arenaType.name());
        arena.toggleBeingUsed();
        arena.setInUse(arenaType);

        return arena;
    }

	public static int getArenasAvailable(ArenaType arenaType) {
		List<Arena> arenasAvailable = new ArrayList<>();
		for (Arena arena : ArenaManager.arenas.get(arenaType)) {
			if (!arena.isBeingUsed()) {
				arenasAvailable.add(arena);
			}
		}

		return arenasAvailable.size();
	}

    public static List<Arena> getAllArenasOfType(ArenaType arenaType) {
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

    public static void addArena(Player player, String arenaName, String types, String warp1, String warp2) {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE build_arenas SET warp_1 = ?, warp_2 = ?, types = ? WHERE arena_name = ?;\n";
            query += "INSERT INTO build_arenas (arena_name, types, warp_1, warp_2) SELECT ?, ?, ?, ? WHERE NOT EXISTS " +
                    "(SELECT 1 FROM build_arenas WHERE arena_name = ?);";

            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setString(1, warp1);
            ps.setString(2, warp2);
            ps.setString(3, types);
            ps.setString(4, arenaName);
            ps.setString(5, arenaName);
            ps.setString(6, types);
            ps.setString(7, warp1);
            ps.setString(8, warp2);
            ps.setString(9, arenaName);

            Gberry.executeUpdate(connection, ps);

	        if (player != null) {
		        player.sendMessage(ChatColor.GREEN + "Arena " + arenaName + " has been added.");
	        }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
	        Gberry.closeComponents(ps, connection);
        }
    }

    public static void addWarp(String warpName, Player player, Location location) {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE build_warps SET x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE warp_name = ?;\n";
            query += "INSERT INTO build_warps (warp_name, x, y, z, yaw, pitch) SELECT ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                    "(SELECT 1 FROM build_warps WHERE warp_name = ?);";

            connection = Gberry.getConnection();

            ps = connection.prepareStatement(query);
            ps.setDouble(1, location.getX());
            ps.setDouble(2, location.getY() + 2);
            ps.setDouble(3, location.getZ());
            ps.setFloat(4, location.getYaw());
	        ps.setFloat(5, 0); // Hardcoded pitch to 0
            ps.setString(6, warpName);
            ps.setString(7, warpName);
            ps.setDouble(8, location.getX());
            ps.setDouble(9, location.getY() + 2);
            ps.setDouble(10, location.getZ());
            ps.setFloat(11, location.getYaw());
            ps.setFloat(12, 0); // Hardcoded pitch to 0
            ps.setString(13, warpName);

            Gberry.executeUpdate(connection, ps);

	        if (player != null) {
		        player.sendMessage(ChatColor.GREEN + "Warp saved.");
	        }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
	        Gberry.closeComponents(ps, connection);
        }
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
            String sql = "SELECT * FROM build_warps;";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            World world = PotPvP.getInstance().getServer().getWorld("world");

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
            String sql = "SELECT * FROM build_arenas;";
            ps = connection.prepareStatement(sql);
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                String arenaName = rs.getString("arena_name");
                String warp1 = rs.getString("warp_1");
                String warp2 = rs.getString("warp_2");
                String extraData = rs.getString("extra_data");

                Location warp1Location = ArenaManager.warps.get(warp1);
                Location warp2Location = ArenaManager.warps.get(warp2);

                // Valid arena loaded, lets make it available
                Arena arena = null;
                if (warp1Location != null && warp2Location != null) {
	                Gberry.log("ARENAS", "Adding arena " + arenaName + " with warps " + warp1Location.toString() + " and " + warp2Location.toString());

	                String[] types = rs.getString("types").split(",");
	                for (String type : types) {
		                if (type.equals("2") || type.equals("18")) {
			                arena = new BuildUHCArena(arenaName, warp1Location, warp2Location, extraData);
		                } else if (type.equals("14")) {
			                arena = new SkyWarsArena(arenaName, warp1Location, warp2Location, extraData);
		                } else if (type.equals("9")) {
			                arena = new UHCMeetupArena(arenaName, warp1Location, warp2Location, extraData);
		                } else if (type.equals("11")) {
			                arena = new KOTHArena(arenaName, warp1Location, warp2Location, extraData);
		                } else if (type.equals("15")) {
			                arena = new TDMArena(arenaName, warp1Location, warp2Location);
		                } else {
			                arena = new Arena(arenaName, warp1Location, warp2Location);
		                }
	                }
                } else if (warp1Location == null && warp2Location != null || warp1Location != null) {
                    throw new RuntimeException("Failed to load arena " + arenaName);
                } else {
                    Gberry.log("ARENAS", "Adding arena " + arenaName + " with no warps.");
                    arena = new Arena(arenaName, null, null);
                }

                String types = rs.getString("types");
                String [] typeStrings = types.split(",");
                for (String type : typeStrings) {
                    try {
                        int enumType = Integer.parseInt(type);
                        ArenaManager.arenas.get(ArenaType.values()[enumType]).add(arena);
                        arena.addType(ArenaType.values()[enumType]);

                        Gberry.log("ARENAS", "Adding arena " + arenaName + " to type " + ArenaType.values()[enumType].name());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
	        Gberry.closeComponents(rs, ps, connection);
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

	@EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getPlayer() != null) {
            Game game = GameState.getGroupGame(PotPvP.getInstance().getPlayerGroup(event.getPlayer()));
            if (game != null) {
                Arena arena = game.getArena();
                if (arena != null && !game.isOver()) {
                    if (!arena.containsBlockRemoved(event.getBlock(), event.getPlayer())) {
                        arena.addBlockPlaced(event.getBlock(), event.getPlayer());

                        // Fixes grass turning into dirt because of block on top of grass
                        if (event.getBlockAgainst().getType() == Material.GRASS) {
                            arena.addBlockRemoved(event.getBlockAgainst(), event.getPlayer());
                            return;
                        }

                        Block block = event.getBlock().getRelative(0, -1, 0);
                        if (block.getType() == Material.GRASS) {
                            arena.addBlockRemoved(block, event.getPlayer());
                        }
                    }
                }
            }
        }
    }

	@EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getPlayer() != null) {
            Game game = GameState.getGroupGame(PotPvP.getInstance().getPlayerGroup(event.getPlayer()));
            if (game != null && !game.isOver()) {
                Arena arena = game.getArena();
                if (arena != null) {
                    // LagSpike 100k chunk bug #BlameSmelly Track arena, not the fucking player
                    ArenaManager.brokenBlocks.put(event.getBlock(), arena);

                    if (!arena.containsBlockPlaced(event.getBlock(), event.getPlayer()) && !arena.containsBlockRemoved(event.getBlock(), event.getPlayer())) {
                        arena.addBlockRemoved(event.getBlock(), event.getPlayer());
                    }
                }
            }
        }
    }

	@EventHandler
	public void onBlockStoneFormEvent(BlockStoneFormEvent event) {
		// Cancel it because if we just set to cobble, it gets set to stone after this event is called
		event.setCancelled(true);

		// Try to add the block below as a changed block (grass -> dirt b/c of block on top)
        Arena arena = ArenaManager.liquidBlocks.get(event.getBlock());
        if (arena != null && !arena.isCleaning()) {
            event.getBlock().setType(Material.COBBLESTONE);
            Block blockUnder = event.getBlock().getRelative(0, -1, 0);
            if (blockUnder.getType() == Material.GRASS) {
                arena.addBlockRemoved(blockUnder, null);
            }
        }

	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockFromToEvent(BlockFromToEvent event) {
		Arena arena = ArenaManager.liquidBlocks.get(event.getBlock());
        if (arena != null && !arena.isCleaning()) {
            event.setCancelled(false);

            ArenaManager.liquidBlocks.put(event.getToBlock(), arena);

            if (event.getToBlock().getType() != Material.WATER && event.getToBlock().getType() != Material.STATIONARY_WATER
                    && event.getToBlock().getType() != Material.LAVA && event.getToBlock().getType() != Material.STATIONARY_LAVA
                    && !arena.containsBlockPlaced(event.getToBlock()) && !arena.containsBlockRemoved(event.getToBlock())) {
	            // Possible fix?
	            if (event.getToBlock().getType() == Material.AIR) {
		            arena.addBlockPlaced(event.getToBlock());
	            }
            }
        }
	}

	@EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        if (event.getPlayer() != null) {
			Game game = GameState.getGroupGame(PotPvP.getInstance().getPlayerGroup(event.getPlayer()));
			if (game != null && !game.isOver()) {
				Arena arena = game.getArena();
				if (arena != null) {
					Block block = event.getBlockClicked().getRelative(event.getBlockFace());

					ArenaManager.liquidBlocks.put(block, arena);

					if (!arena.containsBlockRemoved(block, event.getPlayer())) {
						arena.addBlockPlaced(block, event.getPlayer());

						// Fixes grass turning into dirt because of block on top of grass
						Block blockUnder = block.getRelative(0, -1, 0);
						if (blockUnder.getType() == Material.GRASS) {
							arena.addBlockRemoved(blockUnder, event.getPlayer());
						}
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
    public void onFlintAndSteelIgniteEvent(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            Game game = GameState.getGroupGame(PotPvP.getInstance().getPlayerGroup(event.getPlayer()));
            if (game != null && !game.isOver()) {
                Arena arena = game.getArena();
                if (arena != null) {
	                if (!arena.containsBlockRemoved(event.getBlock(), event.getPlayer())) {
		                arena.addBlockPlaced(event.getBlock(), event.getPlayer());
	                }
                }
            }
        }
    }

}
