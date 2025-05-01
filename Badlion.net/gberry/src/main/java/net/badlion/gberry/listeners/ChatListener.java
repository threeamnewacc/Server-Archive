package net.badlion.gberry.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.commands.AutoMuteCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

	private static boolean chatSpamFilter;

    private static final HashMap<String, String> playerToMessageMap = new HashMap<>();
	private static final HashMap<String, Long> playerToTimestampMap = new HashMap<>();
	private static final HashMap<String, Long> playerLinkToTimestampMap = new HashMap<>();
	private static final HashMap<String, BukkitTask> playerToTaskMap = new HashMap<>();

    public static Map<UUID, Long> spamMutedPeopleShort = new HashMap<>();
    public static Map<UUID, Long> spamMutedPeopleLong = new HashMap<>();

	public static List<ChatFilter> chatFilters = new ArrayList<>();

	private Gberry plugin;

	public ChatListener(Gberry plugin) {
		this.plugin = plugin;
		ChatListener.chatSpamFilter = this.plugin.getConfig().getBoolean("gberry.spam_filter");

		ChatListener.loadChatFilters();
	}

	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		// Filter check
		if (!ChatListener.isChatMessageValid(event.getPlayer(), event.getMessage())) {
			event.setCancelled(true);
		}
	}

	public static boolean isChatMessageValid(final Player player, String msg) {
		final String uuid = player.getUniqueId().toString();

		Long spamts = ChatListener.spamMutedPeopleShort.get(player.getUniqueId());
		Long spamts2 = ChatListener.spamMutedPeopleLong.get(player.getUniqueId());
		if ((spamts != null && spamts + 15 * 60 * 1000 > System.currentTimeMillis())
				|| (spamts2 != null && spamts2 + 60 * 60 * 1000 > System.currentTimeMillis())) {
			player.sendMessage(ChatColor.RED + "You are muted for spamming in chat.");
			return false;
		}

		if (!player.hasPermission("badlion.staff") && !player.hasPermission("badlion.uhctrial")) {
			// Phishing link check
			if (msg.toLowerCase().contains("minecraftpromotion") || msg.toLowerCase().contains("storebadlion.net")) {
				Gberry.plugin.getServer().getScheduler().runTask(Gberry.plugin, new Runnable() {
					@Override
					public void run() {
						Gberry.plugin.getServer().dispatchCommand(Gberry.plugin.getServer().getConsoleSender(),
								"ban " + player.getName() + " Posting phishing links");
					}
				});

				return false;
			}

			// Twitch/YT spammers
			if (!player.hasPermission("badlion.twitch") && !player.hasPermission("badlion.youtube")
					&& !player.hasPermission("badlion.famous") && !player.hasPermission("badlion.staff")) {
				if (msg.toLowerCase().contains("twitch.tv") || msg.toLowerCase().contains("youtube.com")
						|| msg.toLowerCase().contains("mlg.tv")) {
					Long ts = ChatListener.playerLinkToTimestampMap.get(uuid);
					if (ts != null && ts + 300000 > System.currentTimeMillis()) {
						player.sendMessage(ChatColor.RED + "Do not spam twitch and youtube links. One link every 5 minutes.");
						return false;
					} else {
						ChatListener.playerLinkToTimestampMap.put(uuid, System.currentTimeMillis());
					}
				}
			}

			Pattern p = Pattern.compile("(\\s|^)[e]+[z]+($|\\s)", Pattern.CASE_INSENSITIVE);
			if (p.matcher(msg).find()) {
				player.sendMessage(ChatColor.RED + "Do not say ez in chat, this is a mute-able offense.");
				player.sendMessage(ChatColor.YELLOW + "Be a good sport and say \"gg\" or \"gf\" instead.");
				return false;
			}

			p = Pattern.compile("(\\s|^)(rekt)($|\\s)", Pattern.CASE_INSENSITIVE);
			if (p.matcher(msg).find()) {
				player.sendMessage(ChatColor.RED + "Do not say rekt in chat, this is a mute-able offense.");
				player.sendMessage(ChatColor.YELLOW + "Be a good sport and say \"gg\" or \"gf\" instead.");
				return false;
			}

			p = Pattern.compile("(\\s|^)(hackers?|hacks?|hax|hacking|cheaters?|cheating|cheats?)($|\\s)", Pattern.CASE_INSENSITIVE);
			if (p.matcher(msg).find()) {
				player.sendMessage(ChatColor.RED + "Do not hackusate in chat! Use /report.");
				player.sendMessage(ChatColor.YELLOW + "When you hackusate in chat you allow the hacker to toggle off their cheats before a mod can check them out.");
				return false;
			}

			p = Pattern.compile("(\\s|^)(kill aura|forcefield|aimbot|autoclick|autoclickers?|groundhog)($|\\s)", Pattern.CASE_INSENSITIVE);
			if (p.matcher(msg).find()) {
				player.sendMessage(ChatColor.RED + "Do not mention different hacks in chat.");
				player.sendMessage(ChatColor.YELLOW + "Talking about different hacks may encourage other players to use them or find out more about them.");
				return false;
			}

			p = Pattern.compile("(.*\\d+\\.\\d+\\.\\d+\\.\\d+.*)", Pattern.CASE_INSENSITIVE);
			if (p.matcher(msg).find() || msg.toLowerCase().contains("mc.") || msg.toLowerCase().contains("pvp.") || msg.toLowerCase().contains("no-ip")) {
				player.sendMessage(ChatColor.RED + "Do not advertise in chat.");
				player.sendMessage(ChatColor.YELLOW + "If you persist, you will be IP banned from the network.");
				return false;
			}

			p = Pattern.compile("(.*\\d+,\\d+,\\d+,\\d+.*)", Pattern.CASE_INSENSITIVE);
			if (p.matcher(msg).find()) {
				player.sendMessage(ChatColor.RED + "Do not advertise in chat.");
				player.sendMessage(ChatColor.YELLOW + "If you persist, you will be IP banned from the network.");
				return false;
			}

			p = Pattern.compile("(\\s|^)(star wars?|starwars?|luke skywalker|skywalker|han solo|princess? leia|leia|kylo ren|kylo|rey|finn)($|\\s)", Pattern.CASE_INSENSITIVE);
			if (p.matcher(msg).find()) {
				player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DO NOT ATTEMPT TO DISCUSS STAR WARS ON BADLION!");
				player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "YOU WILL BE BANNED IF YOU PERSIST!");
				return false;
			}

			for (final ChatFilter chatFilter : ChatListener.chatFilters) {
				p = Pattern.compile(chatFilter.getRegex(), Pattern.CASE_INSENSITIVE);
				if (p.matcher(msg).find()) {
					if (chatFilter.getBanOrMute().equals("automute")) {
						ChatListener.spamMutedPeopleShort.put(player.getUniqueId(), System.currentTimeMillis());
						player.sendMessage(ChatColor.RED + "Do not spam in chat. You are muted for 15 minutes. (Won't show on punishments)");
					} else {
						Gberry.plugin.getServer().getScheduler().runTask(Gberry.plugin, new Runnable() {
							@Override
							public void run() {
								Gberry.plugin.getServer().dispatchCommand(Gberry.plugin.getServer().getConsoleSender(),
										chatFilter.getBanOrMute() + " " + player.getName() + " " + chatFilter.getPunishTime() + " " + "[GChat] " + chatFilter.getReason());
							}
						});
					}

					return false;
				}
			}

			//p = Pattern.compile("((?<=[^a-zA-Z0-9])(?:https?://|[a-zA-Z0-9]+\\.|\\b)(?:\\w+\\.){1,5}(?:com|org|edu|gov|uk|net|ca|de|jp|fr|au|us|ru|ch|it|nl|se|no|es|mil|iq|io|ac|ly|sm|sh|tv|dj)(?:/[a-zA-Z0-9]+)*)");
			p = Pattern.compile("((?:(?:https?):\\/\\/)?(?:[-\\w_]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + "\\s\\n]|$))))"); // Stolen from CraftChatMessage
			Matcher matcher = p.matcher(msg);
			while (matcher.find()) {
				String url = matcher.group(0).toLowerCase();
				if (url.contains("imgur.com") || url.contains("youtube.com") || url.contains("gyazo.com") || url.contains("badlion.net")
						|| url.contains("puu.sh") || url.contains("prntscr.com") || url.contains("twitch.tv") || url.contains("twitter.com")
						|| url.contains("plug.dj") || url.contains("mlg.tv") || url.contains("majorleaguegaming.com")
						|| url.contains("discord.gg")) {
					// Do nothing
				} else {
					player.sendMessage(ChatColor.RED + "This website is not allowed in chat.");
					return false;
				}
			}
		}

		if (!player.hasPermission("badlion.staff") && !player.hasPermission("badlion.famous")) {
			// Laglion check
			if (msg.toLowerCase().contains("laglion")) {
				ChatListener.spamMutedPeopleLong.put(player.getUniqueId(), System.currentTimeMillis());
				player.sendMessage(ChatColor.RED + "Do not spam laglion. You have been muted for an hour for server disrespect.");
				return false;
			} else if (AutoMuteCommand.automute) {
				for (String word : AutoMuteCommand.bannedWords) {
					if (msg.toLowerCase().contains(word)) {
						ChatListener.spamMutedPeopleShort.put(player.getUniqueId(), System.currentTimeMillis());
						player.sendMessage(ChatColor.RED + "Do not spam \"" + word + "\" in chat. You are muted for 15 minutes.");
						return false;
					}
				}
			}
		}

		if (ChatListener.chatSpamFilter) {
			// Spam Detection
			synchronized (ChatListener.playerToTimestampMap) {
				Long ts = ChatListener.playerToTimestampMap.get(uuid);
				if (ts != null && ts + 2500 > System.currentTimeMillis()
						&& !player.hasPermission("badlion.twitch") && !player.hasPermission("badlion.youtube")
						&& !player.hasPermission("badlion.famous") && !player.hasPermission("badlion.staff")) {
					if (!Gberry.factions) { // We'll send it ourselves for factions b/c we have to check if it's faction or alliance chat first
						player.sendMessage(ChatColor.RED + "Do not spam the chat. One message every 2.5 seconds.");
					}
					return false;
				} else {
					ChatListener.playerToTimestampMap.put(uuid, System.currentTimeMillis());
				}
			}

			// Spam Detection
			synchronized (ChatListener.playerToMessageMap) {
				String msg2 = ChatListener.playerToMessageMap.get(uuid);
				if (msg2 != null && msg2.equals(msg) && !player.hasPermission("badlion.donator")) {
					Gberry.plugin.getServer().getScheduler().runTask(Gberry.plugin, new Runnable() {
						@Override
						public void run() {
							Gberry.plugin.getServer().dispatchCommand(Gberry.plugin.getServer().getConsoleSender(), "tempmute " + uuid + " 300 Spam. Do not send the same message 2 times consecutively.");
						}
					});
				} else {
					ChatListener.playerToMessageMap.put(uuid, msg);

					// Remove 10 sec later
					ChatListener.playerToTaskMap.put(uuid, Gberry.plugin.getServer().getScheduler().runTaskLater(Gberry.plugin, new Runnable() {

						@Override
						public void run() {
							synchronized (ChatListener.playerToMessageMap) {
								// Been 10 sec, they are clear for spamming
								ChatListener.playerToMessageMap.remove(uuid);
							}
						}

					}, 10 * 20));
					return true;
				}
			}

			// They made it this far, cancel any tasks they had running
			synchronized (ChatListener.playerToTaskMap) {
				if (ChatListener.playerToTaskMap.containsKey(uuid)) {
					ChatListener.playerToTaskMap.get(uuid).cancel();
					ChatListener.playerToTaskMap.remove(uuid);
				}
			}
		}

		return true;
	}

	private static class ChatFilter {

		private String regex;
		private String punishTime;
		private String banOrMute;
		private String reason;

		public ChatFilter(ResultSet rs) throws SQLException {
			this.regex = rs.getString("regex");
			this.punishTime = rs.getString("punishment_length");
			this.reason = rs.getString("reason");

			String punishmentType = rs.getString("punishment_type");
			if (punishmentType.equalsIgnoreCase("b")) {
				this.banOrMute = "ban";
			} else if (punishmentType.equalsIgnoreCase("m")) {
				this.banOrMute = "mute";
			} else if (punishmentType.equalsIgnoreCase("k")) {
				this.banOrMute = "kick";
			} else {
				this.banOrMute = "automute";
			}
		}

		public String getRegex() {
			return regex;
		}

		public String getPunishTime() {
			return punishTime;
		}

		public String getBanOrMute() {
			return banOrMute;
		}

		public String getReason() {
			return reason;
		}

	}

	public static void loadChatFilters() {
		new BukkitRunnable() {
			public void run() {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				List<ChatFilter> filters = new ArrayList<>();

				String query = "SELECT * FROM gchat_filters;";

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					rs = Gberry.executeQuery(connection, ps);

					while (rs.next()) {
						filters.add(new ChatFilter(rs));
					}

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}

				ChatListener.chatFilters = filters;
			}
		}.runTaskAsynchronously(Gberry.plugin);
	}

}
