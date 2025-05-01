package net.badlion.gfactions.managers;

import net.badlion.gfactions.GFactions;
import net.badlion.gberry.Gberry;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class KDAManager {

	private HashMap<UUID, KDAPlayer> kdaPlayers;

	public KDAManager() {
		this.kdaPlayers = new HashMap<UUID, KDAPlayer>();
	}

	public KDAPlayer getKDAPlayer(Player player) {
		KDAPlayer kdaPlayer = this.kdaPlayers.get(player.getUniqueId());

		if (kdaPlayer != null) {
			return kdaPlayer;
		}

		kdaPlayer = new KDAPlayer(player.getUniqueId());
		this.kdaPlayers.put(player.getUniqueId(), kdaPlayer);
		return kdaPlayer;
	}

	public KDAPlayer removeKDAPlayer(Player player) {
		return this.kdaPlayers.remove(player);
	}

	public class KDAPlayer {

		private UUID uuid;
		private int kills = 0;
		private int deaths = 0;

		private KDAPlayer(final UUID uuid) {
			this.uuid = uuid;

			// Grab their old kills and deaths so we don't have to run 2 queries later to add a kill/death
			GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {
				@Override
				public void run() {
					String query = "SELECT * FROM " + GFactions.PREFIX + "_player_kills_deaths WHERE uuid = ?;";

					Connection connection = null;
					PreparedStatement ps = null;
					ResultSet rs = null;

					try {
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);
						ps.setString(1, KDAPlayer.this.uuid.toString());
						rs = Gberry.executeQuery(connection, ps);

						if (rs.next()) {
							KDAPlayer.this.kills = rs.getInt("kills");
							KDAPlayer.this.deaths = rs.getInt("deaths");
						}

					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
						if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
						if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
					}
				}
			});
		}

		public void syncWithDatabase() {
			String query = "UPDATE " + GFactions.PREFIX + "_player_kills_deaths SET kills = ?, deaths = ? WHERE uuid = ?;\n";
			query += "INSERT INTO " + GFactions.PREFIX + "_player_kills_deaths (uuid, kills, deaths, kdr) SELECT ?, ?, ?, ? WHERE NOT EXISTS " +
					"(SELECT 1 FROM " + GFactions.PREFIX + "_player_kills_deaths WHERE uuid = ?);";

			Connection connection = null;
			PreparedStatement ps = null;

			try {
				connection = Gberry.getConnection();
				ps = connection.prepareStatement(query);
				ps.setInt(1, this.kills);
				ps.setInt(2, this.deaths);
				ps.setString(3, this.uuid.toString());
				ps.setString(4, this.uuid.toString());
				ps.setInt(5, this.kills);
				ps.setInt(6, this.deaths);
				if (this.deaths != 0) {
					ps.setDouble(7, (double) this.kills / this.deaths);
				} else {
					ps.setDouble(7, this.kills);
				}
				ps.setString(8, this.uuid.toString());

				Gberry.executeUpdate(connection, ps);

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
				if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
			}
		}

		public UUID getUuid() {
			return uuid;
		}

		public void addKill() {
			this.kills++;

            // Grab their old kills and deaths so we don't have to run 2 queries later to add a kill/death
            GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {
                @Override
                public void run() {
			        KDAPlayer.this.syncWithDatabase();
                }
            });
		}

		public int getKills() {
			return kills;
		}

		public void addDeath() {
			this.deaths++;

            // Grab their old kills and deaths so we don't have to run 2 queries later to add a kill/death
            GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {
                @Override
                public void run() {
                    KDAPlayer.this.syncWithDatabase();
                }
            });
		}

		public int getDeaths() {
			return deaths;
		}

	}

}
