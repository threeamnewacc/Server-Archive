package net.badlion.smellymapvotes;

import net.badlion.gberry.Gberry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class VoteManager implements Listener {

	private static Set<UUID> canVote = new HashSet<>();
	private static Map<UUID, String> mapNames = new HashMap<>();

	public enum ServerType {
		ARENAPVP("arenapvp"),
		SKYWARS("skywars"),
		UHC("uhc"),
		SG("sg"),
		TDM("tdm"),
		LOBBY("lobby");

		private String name;

		ServerType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		VoteManager.canVote.remove(event.getPlayer().getUniqueId());
	}

	public static void sendVoteMessage(Player player, String mapName) {
		VoteManager.canVote.add(player.getUniqueId());
		VoteManager.mapNames.put(player.getUniqueId(), mapName);

		String command = "/mapvote ";
		// So much copy-paste :D
		BaseComponent[] components = new ComponentBuilder("Please rate this map by clicking ")
				.color(ChatColor.BLUE)
				.append("[5]")
				.color(ChatColor.DARK_GREEN)
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("Amazing")
								.color(ChatColor.DARK_GREEN)
								.create()))
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + 5))
				.append(" [4]")
				.color(ChatColor.GREEN)
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("Great")
								.color(ChatColor.GREEN)
								.create()))
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + 4))
				.append(" [3]")
				.color(ChatColor.YELLOW)
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("Good")
								.color(ChatColor.YELLOW)
								.create()))
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + 3))
				.append(" [2]")
				.color(ChatColor.RED)
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("Bad")
								.color(ChatColor.RED)
								.create()))
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + 2))
				.append(" [1]")
				.color(ChatColor.DARK_RED)
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("Awful")
								.color(ChatColor.DARK_RED)
								.create()))
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + 1))
				.create();
		player.spigot().sendMessage(components);
	}

	public static void addVote(final String uuid, final int points) {
		UUID uuid2 = UUID.fromString(uuid);
		VoteManager.canVote.remove(uuid2);
		final String mapName = VoteManager.mapNames.remove(uuid2);

		SmellyMapVotes.getInstance().getServer().getScheduler().runTaskAsynchronously(SmellyMapVotes.getInstance(), new Runnable() {
			@Override
			public void run() {
				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					String query = "UPDATE smelly_map_votes SET points = ? WHERE server_type = ? AND map_name = ? AND uuid = ?;\n";
					query += "INSERT INTO smelly_map_votes (server_type, map_name, uuid, points) SELECT ?, ?, ?, ? WHERE NOT EXISTS " +
							"(SELECT 1 FROM smelly_map_votes WHERE server_type = ? AND map_name = ? AND uuid = ?);";

					ps = connection.prepareStatement(query);
					ps.setInt(1, points);
					ps.setString(2, SmellyMapVotes.getInstance().getServerType().getName());
					ps.setString(3, mapName);
					ps.setString(4, uuid);
					ps.setString(5, SmellyMapVotes.getInstance().getServerType().getName());
					ps.setString(6, mapName);
					ps.setString(7, uuid);
					ps.setInt(8, points);
					ps.setString(9, SmellyMapVotes.getInstance().getServerType().getName());
					ps.setString(10, mapName);
					ps.setString(11, uuid);

					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(ps, connection);
				}
			}
		});
	}

	public static boolean canVote(Player player) {
		return VoteManager.canVote.contains(player.getUniqueId());
	}

}
