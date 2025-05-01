package net.badlion.potpvp.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarpCheckerTask extends BukkitRunnable {

	private static int START_INDEX = 1;

	private Player player;

	private Map<String, Location> warps = ArenaManager.getWarps();
	private Map<String, Location> badWarps = new HashMap<>();
	private Map<String, Integer> badWarpsNewYLevels = new HashMap<>();

	private final int limit;
	private int index = WarpCheckerTask.START_INDEX;
	private List<String> warpNames = new ArrayList<>(ArenaManager.getWarps().keySet());

	public WarpCheckerTask(int numOfWarpsToCheck, Player player) {
		this.player = player;
		this.limit = WarpCheckerTask.START_INDEX + numOfWarpsToCheck;

		// Reset player health
		player.setHealth(20D);
		player.setFallDistance(0F); // Fail-safe

		// Teleport to first warp
		player.teleport(this.warps.get(this.warpNames.get(this.index - 1)));

		// Update start index
		WarpCheckerTask.START_INDEX += numOfWarpsToCheck;
	}

	@Override
	public void run() {
		try {
			if (this.index == this.limit) throw new IndexOutOfBoundsException();

			String lastWarpName = this.warpNames.get(this.index - 1);

			// Did player fall into the void (and got teleported to spawn)?
			if (this.player.getLocation().getX() == PotPvP.getInstance().getSpawnLocation().getX()
					&& this.player.getLocation().getZ() == PotPvP.getInstance().getSpawnLocation().getZ()) {
				Bukkit.getLogger().severe("=== VOID WARP: " + lastWarpName + " @ " + this.warps.get(lastWarpName));
			} else if (this.player.getHealth() != 20) { // Did player take damage?
				this.badWarps.put(lastWarpName, this.warps.get(lastWarpName));

				// Add new y level for this warp
				this.badWarpsNewYLevels.put(lastWarpName, this.player.getLocation().getBlockY() + 3);
			}

			// Reset player health
			this.player.setHealth(20D);
			this.player.setFallDistance(0F); // Fail-safe

			String newWarpName = this.warpNames.get(this.index);

			while (newWarpName.contains("-ul") || newWarpName.contains("-ml")) {
				this.index++;
				newWarpName = this.warpNames.get(this.index);
			}

			// Teleport to next warp
			this.player.teleport(this.warps.get(newWarpName));

			this.index++;
		} catch (IndexOutOfBoundsException e) {
			// Print out all bad warps
			System.out.println("NUMBER OF BAD WARPS: " + this.badWarps.size());
			for (String warpName : this.badWarps.keySet()) {
				Bukkit.getLogger().info("BAD WARP: " + warpName + " @ " + this.badWarps.get(warpName));
			}

			// Update all the y levels of the bad warps
			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					Connection connection = null;
					PreparedStatement ps = null;

					try {
						connection = Gberry.getConnection();
						String sql = "";

						// Probably a better way to do this query w/ mappings but w/e
						for (int i = 0; i < WarpCheckerTask.this.badWarpsNewYLevels.size(); i++) {
							sql += "UPDATE build_warps SET y = ? WHERE warp_name = ?;";
						}

						ps = connection.prepareStatement(sql);

						int counter = 1;
						for (String warpName : WarpCheckerTask.this.badWarpsNewYLevels.keySet()) {
							int y = WarpCheckerTask.this.badWarpsNewYLevels.get(warpName);

							ps.setInt(counter, y);
							ps.setString(counter + 1, warpName);

							counter += 2;
						}

						Gberry.executeUpdate(connection, ps);
					} catch (SQLException ex) {
						ex.printStackTrace();
					} finally {
						Gberry.closeComponents(ps, connection);
					}
				}
			});

			this.cancel();
		}
	}

}
