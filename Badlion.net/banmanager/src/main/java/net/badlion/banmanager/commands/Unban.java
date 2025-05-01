package net.badlion.banmanager.commands;

import net.badlion.banmanager.BanManager;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Unban implements CommandExecutor {

	public static Map<UUID, Timestamp> banTimes = new ConcurrentHashMap<>();
	private BanManager plugin;

	public Unban(BanManager plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Correct usage is /unban <username>");
			return true;
		}

		final String unbanUUID = sender instanceof Player ? ((Player) sender).getUniqueId().toString() :
				args.length == 2 && args[2].split("-").length == 5 ? UUID.fromString(args[2]).toString() : BanManager.CONSOLE_SENDER;

		final Player banned = Bukkit.getPlayerExact(args[0]);
		Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			public void run() {
				String bannedUUID;
				if (banned != null) {
					bannedUUID = banned.getUniqueId().toString();
				} else {
					UUID uuid = Gberry.getOfflineUUID(args[0]);
					if (uuid != null) {
						bannedUUID = uuid.toString();
					} else {
						bannedUUID = null;
					}
				}

				if (bannedUUID == null) {
					sender.sendMessage(ChatColor.RED + args[0] + "'s UUID could not be found!");
					return;
				}

				final Timestamp ts = banTimes.get(UUID.fromString(bannedUUID));
				Gberry.log("BM", "Unban timestamp " + ts);
				final boolean recentlyBanned = ts != null && ts.getTime() + 300000 > System.currentTimeMillis();
				//if (sender instanceof Player && !recentlyBanned) {
				//    sender.sendMessage(ChatColor.RED + "Can only unban people for up to 5 minutes after being punished. Use the website instead.");
				//    return;
				//}

				Unban.this.plugin.pSync("unban", bannedUUID);

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				String query = "SELECT * FROM punishments WHERE type = " + BanManager.PUNISHMENT_TYPE.BAN.ordinal() + " AND punished_uuid = ? ORDER BY punishment_time DESC LIMIT 1;";
				try {
					connection = BanManager.getConnection();
					ps = connection.prepareStatement(query);
					ps.setString(1, bannedUUID);
					rs = ps.executeQuery();

					if (!rs.next()) {
						sender.sendMessage(ChatColor.RED + "Error, could not find database ban record;");
						return;
					}

					if (recentlyBanned) {
						query = "DELETE FROM punishments WHERE punishment_id = ?;";
					} else {
						query = "UPDATE punishments SET unpunish_time = ?, unpunisher_uuid = ? WHERE punishment_id = ?;";
					}

					ps = connection.prepareStatement(query);

					if (recentlyBanned) {
						ps.setInt(1, rs.getInt("punishment_id"));
					} else {
						ps.setTimestamp(1, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
						ps.setString(2, unbanUUID);
						ps.setInt(3, rs.getInt("punishment_id"));
					}

					ps.executeUpdate();
					sender.sendMessage(ChatColor.RED + args[0] + " is now unbanned!");
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(rs, ps, connection);
				}
			}
		});

		return true;
	}
}
