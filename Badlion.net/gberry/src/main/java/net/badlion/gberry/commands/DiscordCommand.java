package net.badlion.gberry.commands;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.md_5.bungee.api.ChatColor;
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

public class DiscordCommand implements CommandExecutor {

    private String lineSep = Gberry.getLineSeparator(org.bukkit.ChatColor.GOLD);

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		final Player player = (Player) sender;

		if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
			this.sendHelpMessages(player);
			return true;
		}

		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				if (args[0].equalsIgnoreCase("remove")) {
					DiscordCommand.deleteDiscordUUID(player.getUniqueId().toString());
					player.sendMessage(DiscordCommand.this.lineSep);
					player.sendMessage(ChatColor.GREEN + "The Discord UUID for this account has been removed.");
					player.sendMessage(DiscordCommand.this.lineSep);
				} else if (args[0].equalsIgnoreCase("add")) {
					if (args.length != 2) {
						DiscordCommand.this.sendHelpMessages(player);
					} else {
						DiscordUUID discordUUID = DiscordCommand.getDiscordUUID(player.getUniqueId().toString());

						// If the Discord UUID matches
						if (discordUUID != null && discordUUID.getDiscordUUID().equals(args[1])) {
							player.sendMessage(DiscordCommand.this.lineSep);
							player.sendMessage(ChatColor.GREEN + "The Discord UUID is already set for this account.");
							player.sendMessage(DiscordCommand.this.lineSep);
							return;
						}

						// If the Discord UUID exists and does not belong to this user
						DiscordUUID discordUUID1 = DiscordCommand.getDiscordUUIDByDiscordUUID(args[1]);
						if (discordUUID1 != null && !discordUUID1.getUuid().equals(player.getUniqueId().toString())) {
							player.sendMessage(DiscordCommand.this.lineSep);
							player.sendMessage(ChatColor.RED + "Cannot use this Discord UUID at the moment. Another user is using it.");
							player.sendMessage(ChatColor.RED + "Use \"/discord remove\" from the other account first before trying to add it.");
							player.sendMessage(DiscordCommand.this.lineSep);
							return;
						}

						// Insert history
						if (discordUUID != null) {
							DiscordCommand.insertDiscordHistory(discordUUID);
						}

						// Change and send message about it being updated
						DiscordCommand.changeDiscordUUID(player.getUniqueId().toString(), args[1]);
						player.sendMessage(DiscordCommand.this.lineSep);
						player.sendMessage(ChatColor.GREEN + "Your Discord UUID has been set");
						player.sendMessage(DiscordCommand.this.lineSep);
					}
				} else {
					DiscordCommand.this.sendHelpMessages(player);
				}
			}
		});

		return true;
	}

    private void sendHelpMessages(Player player) {
        player.sendMessage(this.lineSep);
        player.sendMessage(ChatColor.AQUA + "Discord Commands:");

	    player.sendMessage(ChatColor.AQUA + "/discord help" + ChatColor.GREEN + " - Sends the discord command usage");

	    player.sendMessage(ChatColor.AQUA + "/discord add [discord_uuid]" + ChatColor.GREEN + " - Set your discord_uuid for the account you are on");

	    player.sendMessage(ChatColor.AQUA + "/discord remove" + ChatColor.GREEN + " - Remove the discord_uuid associated with this account");

        player.sendMessage(this.lineSep);
    }


    private static DiscordUUID getDiscordUUID(String uuid) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM discord_uuid WHERE uuid = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid);

            rs = ps.executeQuery();
            if (rs.next()) {
                return new DiscordUUID(uuid, rs.getString("discord_uuid"), rs.getTimestamp("time_set"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return null;
    }

    private static DiscordUUID getDiscordUUIDByDiscordUUID(String discordUUID) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM discord_uuid WHERE discord_uuid = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, discordUUID);

            rs = ps.executeQuery();
            if (rs.next()) {
                return new DiscordUUID(rs.getString("uuid"), rs.getString("discord_uuid"), rs.getTimestamp("time_set"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return null;
    }

    private static void deleteDiscordUUID(String uuid) {
        Connection connection = null;
        PreparedStatement ps = null;


        String query = "DELETE FROM discord_uuid WHERE uuid = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps, connection);
        }
    }

    private static void changeDiscordUUID(String uuid, String discordUUID) {
        Connection connection = null;
        PreparedStatement ps = null;


        String query = "UPDATE discord_accounts SET discord_uuid = ?, time_set = ? WHERE uuid = ?;\n";
        query += "INSERT INTO discord_accounts (uuid, discord_uuid, time_set) SELECT ?, ?, ? WHERE NOT EXISTS " +
                         "(SELECT 1 FROM discord_accounts WHERE uuid = ?);";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, discordUUID);
            ps.setTimestamp(2, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
            ps.setString(3, uuid);
            ps.setString(4, uuid);
            ps.setString(5, discordUUID);
            ps.setTimestamp(6, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
            ps.setString(7, uuid);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps, connection);
        }
    }

    private static void insertDiscordHistory(DiscordUUID discordUUID) {
        Connection connection = null;
        PreparedStatement ps = null;


        String query = "INSERT INTO discord_account_history (uuid, discord_uuid, time_set) VALUES (?, ?, ?);";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, discordUUID.getUuid());
            ps.setString(2, discordUUID.getDiscordUUID());
            ps.setTimestamp(3, discordUUID.getTs());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
	        Gberry.closeComponents(ps, connection);
        }
    }

    private static class DiscordUUID {

        private String uuid;
        private String discordUUID;
        private Timestamp ts;

        public DiscordUUID(String uuid, String discordUUID, Timestamp ts) {
            this.uuid = uuid;
            this.discordUUID = discordUUID;
            this.ts = ts;
        }

        public String getUuid() {
            return uuid;
        }

        public String getDiscordUUID() {
            return discordUUID;
        }

        public Timestamp getTs() {
            return ts;
        }

    }

}
