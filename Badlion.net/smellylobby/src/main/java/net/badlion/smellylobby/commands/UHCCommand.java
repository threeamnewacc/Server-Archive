package net.badlion.smellylobby.commands;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UHCCommand implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Cannot execute this command from console");
			return true;
		}

		final Player player = (Player) sender;

		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				String region;
				int page = -1; // Set it to -1, as they can't choose

				if (args.length == 0) {
					region = "UNDEFINED";
					page = 1;
				} else {
					try {
						page = Integer.valueOf(args[0]);
						region = "UNDEFINED";
					} catch (NumberFormatException e) {
						region = args[0];

						if (!region.equalsIgnoreCase("NA") && !region.equalsIgnoreCase("AU") && !region.equalsIgnoreCase("EU")) {
							region = "UNDEFINED";
							player.sendMessage(ChatColor.RED + "The specified region was not valid. Showing all matches...");
						}
					}

				}

				if (args.length > 1) {
					try {
						page = Integer.valueOf(args[1]);
					} catch (NumberFormatException e) {
						player.sendMessage(ChatColor.RED + "Please enter a valid page number.");
						return;
					}
				}

				// Page validation
				if (page < 1) {
					page = 1;
				} else if (page > 4) {
					page = 4;
				}

				// Get player's timezone
				String timeZone = Gberry.getTimeZone(player);

				// Get serialized upcoming UHC matches based on region and timezone
				List<String> upcomingMatches = UHCCommand.this.getUpcomingMatchesByPage(region, timeZone, page);

				// Check if it's empty
				if (upcomingMatches.isEmpty()) {
					player.sendMessage(ChatColor.RED + "No upcoming UHC matches found.");

					// This error-code system might be a good thing to setup for most plugins from now...
					// It enables us (if they find an error) just CMD + SHIFT + F and search for it,
					// Makes it easier to debug shit.
					// ^ or just search the message, if we were gonna use error codes we'd build an actual error tracking system

					// Comment out for now
					//player.sendMessage(ChatColor.RED + "If there are upcoming matches on the website but not in-game, " +
					//		"please contact a developer with this error code - #3ED.");
					return;
				}

				// Create stringbuilder and append all uhc matches
				StringBuilder sb = new StringBuilder(ChatColor.AQUA + ChatColor.BOLD.toString() + "Upcoming UHC Matches:");
				sb.append(ChatColor.BLUE);
				sb.append("\nCurrent time (Local Timezone): ");
				sb.append(DateTimeFormat.forPattern("MM-dd-yyyy HH:mm").print(DateTime.now(DateTimeZone.forID(timeZone))));

				for (String upcomingMatch : upcomingMatches) {
					sb.append("\n");
					sb.append(upcomingMatch);
				}

				sb.append(ChatColor.AQUA);
				sb.append("\nType /uhc ");
				sb.append(region.equals("UNDEFINED") ? "" : region.toUpperCase() + " ");
				sb.append(page + 1);
				sb.append(" for the next page.");

				player.sendMessage(sb.toString());
			}
		});
		return true;
	}

	/**
	 * Gets the upcoming matches for a region with their timezone
	 * This method is NOT paginated.
	 * @param region - The region (NA, EU, AU) to get the matches for
	 * @param timeZone - The timezone in which the player is on
	 * @return - List of the upcoming matches, based off the region, and assigned the timezone
	 */
	private List<String> getUpcomingUHCs(String region, String timeZone) {
		List<String> upcomingMatches = new ArrayList<>();

		String sql = "SELECT * FROM uhc_match_times ORDER BY uhc_id ASC;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = Gberry.getConnection();

			ps = connection.prepareStatement(sql);

			rs = ps.executeQuery();

			// Current time
			DateTime now = DateTime.now(DateTimeZone.forID(timeZone));

			StringBuilder sb = new StringBuilder();

			int i = 0;
			while (rs.next() && i < 15) {
				// Time of match
				DateTime dateTime = new LocalDateTime(rs.getTimestamp("uhc_time")).toDateTime(DateTimeZone.UTC).withZone(DateTimeZone.forID(timeZone));

				// Skip if game already started or if it's not being hosted
				if (!dateTime.isAfter(now) || rs.getString("uhc_hosts").equals("N/A")) continue;

				// Skip if this isn't game isn't in our scope
				if (!region.equals("UNDEFINED") && !rs.getString("region").substring(0, 2).equalsIgnoreCase(region)) continue;

				sb.append(ChatColor.GREEN);
				sb.append("- ");
				sb.append(rs.getString("uhc_game_mode"));

				sb.append(ChatColor.YELLOW);
				sb.append("\n  - Time: ");
				sb.append(DateTimeFormat.forPattern("MM-dd-yyyy HH:mm").print(dateTime));

				sb.append(ChatColor.YELLOW);
				sb.append("\n  - Hosts: ");
				sb.append(rs.getString("uhc_hosts"));

				sb.append(ChatColor.YELLOW);
				sb.append("\n  - Players: ");
				sb.append(rs.getString("uhc_max_players"));

				sb.append(ChatColor.YELLOW);
				sb.append("\n  - Server: ");
				sb.append(rs.getString("region"));

				upcomingMatches.add(sb.toString());
				sb.setLength(0);

				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return upcomingMatches;
	}

	/**
	 * Gets the upcoming matches for a region with their timezone
	 * It's paginated, meaning that it will get the specified page only.
	 * @param region - The region (NA, EU, AU) to get the matches for
	 * @param timeZone - The timezone in which the player is on
	 * @param page - The page to get (e.g. page 1)
	 * @return - List of the upcoming matches, based off the region/page #, and assigned the timezone
	 */
	public List<String> getUpcomingMatchesByPage(String region, String timeZone, int page) {
		List<String> upcomingMatches = this.getUpcomingUHCs(region, timeZone);
		List<String> newUpcomingMatches = new ArrayList<>();

		int i = 0;

		for (String upcomingMatch : upcomingMatches) {
			// Cap the page at 3 per
			if (newUpcomingMatches.size() >= 3) {
				break;
			}

			i++;

			// If it's less than pageNumber * 3 and greater than previousPageNumber * 3, add it!
			if (i <= page * 3 && i > (page - 1) * 3) {
				newUpcomingMatches.add(upcomingMatch);
			}
		}

		return newUpcomingMatches;
	}

}
