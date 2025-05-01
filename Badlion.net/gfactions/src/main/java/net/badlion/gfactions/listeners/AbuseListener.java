package net.badlion.gfactions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class AbuseListener implements Listener {

	private GFactions plugin;
	private Map<UUID, HashSet<Long>> mapOfGoldMined = new HashMap<>();
	private Map<UUID, HashSet<Long>> mapOfDiamondMined = new HashMap<>();
	public static int TOO_MANY_DIAMONDS_MINED_IN_10_MINUTES = 30;
	public static int TOO_MANY_GOLD_MINDED_IN_10_MINUTES = 50;

    public static boolean ignoreCombatLogging = true;

	public AbuseListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMine(final BlockBreakEvent event) {
		final Player player = event.getPlayer();

		// Don't count if they are in their own faction base
		FPlayer fplayer = FPlayers.i.get(player);
		Faction faction = fplayer.getFaction();

		Faction blockLocationFaction = Board.getFactionAt(event.getBlock().getLocation());
		if (faction.equals(blockLocationFaction)) {
			return;
		}

		if (event.getBlock().getType() == Material.DIAMOND_ORE) {
			HashSet<Long> list = this.mapOfDiamondMined.get(player.getUniqueId());
			if (list == null) {
				list = new HashSet<>();
				this.mapOfDiamondMined.put(player.getUniqueId(), list);
			}

			HashSet<Long> copy = new HashSet<>(list);
			for (Long time : copy) {
				// Been 10 min
				if (time + 600000 < System.currentTimeMillis()) {
					list.remove(time);
				}
			}

			list.add(System.currentTimeMillis());

			// Too much?
			boolean tooMuch = false;
			if (list.size() >= AbuseListener.TOO_MANY_DIAMONDS_MINED_IN_10_MINUTES) {
				this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "mc [WARNING]: " + player.getName() + " is mining diamonds too quickly! " + player.getUniqueId());
				tooMuch = true;
			}

			final boolean finalMuch = tooMuch;
			final Material type = event.getBlock().getType();

			// Store in DB
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
				@Override
				public void run() {
					AbuseListener.this.insertMiningRecord(player, type);

					// Abusing
					if (finalMuch) {
						AbuseListener.this.insertMiningHackerReport(player, event.getBlock().getLocation());
					}
				}
			});
		} else if (event.getBlock().getType() == Material.GOLD_ORE) {
			HashSet<Long> list = this.mapOfGoldMined.get(player.getUniqueId());
			if (list == null) {
				list = new HashSet<>();
				this.mapOfGoldMined.put(player.getUniqueId(), list);
			}

			HashSet<Long> copy = new HashSet<>(list);
			for (Long time : copy) {
				// Been 10 min
				if (time + 600000 < System.currentTimeMillis()) {
					list.remove(time);
				}
			}

			list.add(System.currentTimeMillis());

			// Too much?
			boolean tooMuch = false;
			if (list.size() >= AbuseListener.TOO_MANY_GOLD_MINDED_IN_10_MINUTES) {
				this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "mc [WARNING]: " + player.getName() + " is mining gold too quickly! " + player.getUniqueId());
				tooMuch = true;
			}

			final boolean finalMuch = tooMuch;
			final Material type = event.getBlock().getType();

			// Store in DB
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
				@Override
				public void run() {
					AbuseListener.this.insertMiningRecord(player, type);

					// Abusing
					if (finalMuch) {
						AbuseListener.this.insertMiningHackerReport(player, event.getBlock().getLocation());
					}
				}
			});
		}
	}

    /*@EventHandler
    public void onPlayerBreakMossyCobble(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.MOSSY_COBBLESTONE) {
            this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "mc [WARNING]: " + event.getPlayer().getName() + " mined a mossy cobblestone");
        }
    }*/

    @EventHandler
    public void onPlayerStopServer(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/stop") && event.getPlayer().isOp()) {
            AbuseListener.ignoreCombatLogging = true;
        }
    }

    @EventHandler
    public void onServerExcutesStop(ServerCommandEvent event) {
        if (event.getCommand().startsWith("/stop") || event.getCommand().startsWith("stop")) {
            AbuseListener.ignoreCombatLogging = true;
        }
    }

    @EventHandler
    public void onPlayerCombatLog(PlayerQuitEvent event) {
        if (!AbuseListener.ignoreCombatLogging) {
            if (GFactions.plugin.isInCombat(event.getPlayer())) {
                event.getPlayer().setHealth(0);
                event.getPlayer().sendMessage(ChatColor.RED + "You have combat logged and been killed as a result.");
            }
        }
    }

	public void insertMiningRecord(Player player, Material material) {
		String query = "INSERT INTO " + GFactions.PREFIX + "_faction_mining_records (uuid, material, mined_time) VALUES (?, ?, ?);";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, player.getUniqueId().toString());
			ps.setString(2, material.name());
			ps.setTimestamp(3, new Timestamp(new Date().getTime()));

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public void insertMiningHackerReport(Player player, Location location) {
		String query = "INSERT INTO " + GFactions.PREFIX + "_faction_xray_records (uuid, x, y, z) VALUES (?, ?, ?, ?);";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, player.getUniqueId().toString());
			ps.setInt(2, location.getBlockX());
			ps.setInt(3, location.getBlockY());
			ps.setInt(4, location.getBlockZ());

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

}
