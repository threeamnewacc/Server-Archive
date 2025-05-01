package net.badlion.sgrankedmatchmaker.commands;

import net.badlion.gberry.Gberry;
import net.badlion.sgrankedmatchmaker.SGRankedMatchMaker;
import net.badlion.sgrankedmatchmaker.bukkitevents.RankedLeftChangeEvent;
import net.badlion.sgrankedmatchmaker.listeners.VoteListener;
import net.badlion.sgrankedmatchmaker.managers.RankedLeftManager;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class ExtraSGRankedMatchesCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
		if (args.length == 1) {
			// Fire off an event for our wonderful le SmerryPrengruin
			Player player = Bukkit.getPlayer(UUID.fromString(args[0]));
			if (player != null) {
				int rankedLeft = RankedLeftManager.DEFAULT_MAX_NUM_OF_RANKED_MATCHES_PER_DAY - RankedLeftManager.getNumberOfRankedMatchesToday(player.getUniqueId());
				RankedLeftChangeEvent event = new RankedLeftChangeEvent(player, rankedLeft, rankedLeft > 0);
				SGRankedMatchMaker.getInstance().getServer().getPluginManager().callEvent(event);
			}

			BukkitUtil.runTaskAsync(new Runnable() {
				public void run() {
					Connection connection = null;
					PreparedStatement ps = null;

					//String query = "INSERT INTO user_num_of_ranked (uuid, num_of_matches, day) VALUES(?, ?, ?) " +
					//		"ON DUPLICATE KEY UPDATE num_of_matches = num_of_matches - " + FREE_MATCHES_PER_VOTE + ";";
					String query = "UPDATE sg_user_num_of_ranked SET num_of_matches = num_of_matches - " + VoteListener.NUM_OF_MATCHES_PER_VOTE + " WHERE uuid = ? AND day = ?;\n";
					query += "INSERT INTO sg_user_num_of_ranked (uuid, num_of_matches, day) SELECT ?, ?, ? WHERE NOT EXISTS " +
							"(SELECT 1 FROM sg_user_num_of_ranked WHERE uuid = ? AND day = ?);";

					try {
						// player1
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);
						java.util.Date today = new java.util.Date();

						ps.setString(1, args[0]);
						ps.setDate(2, new java.sql.Date(today.getTime()));
						ps.setString(3, args[0]);
						ps.setInt(4, -1 * VoteListener.NUM_OF_MATCHES_PER_VOTE);
						ps.setDate(5, new java.sql.Date(today.getTime()));
						ps.setString(6, args[0]);
						ps.setDate(7, new java.sql.Date(today.getTime()));

						Gberry.executeUpdate(connection, ps);
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						if (ps != null) {
							try {
								ps.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						if (connection != null) {
							try {
								connection.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
		}
		return true;
	}

}
