package net.badlion.sglobby.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.mpglobby.MPGLobby;
import net.kohi.sidebar.SidebarAPI;
import net.kohi.sidebar.item.SidebarItem;
import net.kohi.sidebar.item.StaticSidebarItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SGLobbySidebarManager implements Listener {

	// Cache of the sidebar items corresponding to each division
	private static Map<String, SidebarItem> divisionItems = new ConcurrentHashMap<>();

	private static Map<UUID, DateTimeZone> playerTimeZones = new ConcurrentHashMap<>();
	private static Map<DateTimeZone, String> timeZoneStrings = new ConcurrentHashMap<>();

	private static StaticSidebarItem localTimeText = new StaticSidebarItem(10, ChatColor.GREEN + "Local Time: ");

	private static StaticSidebarItem spacer20 = new StaticSidebarItem(20, "");

	private static StaticSidebarItem serverText = new StaticSidebarItem(25, ChatColor.GREEN + "Server: ");

	private static StaticSidebarItem serverNameText = new StaticSidebarItem(30, ChatColor.AQUA + "  " + Gberry.serverName);

	private static StaticSidebarItem spacer40 = new StaticSidebarItem(40, "");

	private static StaticSidebarItem spacer60 = new StaticSidebarItem(60, "");

	private static StaticSidebarItem ratingText = new StaticSidebarItem(70, ChatColor.GREEN + "Rating: ");

	private static StaticSidebarItem pointsText = new StaticSidebarItem(80, ChatColor.GREEN + "Points: ");

	private static StaticSidebarItem spacer90 = new StaticSidebarItem(90, "");

	private static StaticSidebarItem badlionText = new StaticSidebarItem(100, ChatColor.AQUA + "www.badlion.net");

	public SGLobbySidebarManager() {
		SidebarAPI.setSidebarTitle(ChatColor.AQUA + "Badlion SG 2.0");

		BukkitUtil.runTaskTimer(new UpdateLocalTimeStringsTask(), 20L);
	}

	@EventHandler
	public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		try {
			SGLobbySidebarManager.playerTimeZones.put(event.getUniqueId(), DateTimeZone.forID(Gberry.getTimeZone(event.getAddress())));
		} catch (IllegalArgumentException e) {
			SGLobbySidebarManager.playerTimeZones.put(event.getUniqueId(), DateTimeZone.forID("EST"));
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		SGLobbySidebarManager.updateSidebar(event.getPlayer());
	}

	public static void updateSidebar(final Player player) {
		SidebarAPI.removeAllSidebarItems(player);

		final UUID uuid = player.getUniqueId();

		SidebarAPI.addSidebarItem(player, SGLobbySidebarManager.localTimeText);

		// Display local time
		final DateTimeZone dateTimeZone = SGLobbySidebarManager.playerTimeZones.get(player.getUniqueId());
		SidebarAPI.addSidebarItem(player, new SidebarItem(15) {
			@Override
			public String getText() {
				return SGLobbySidebarManager.timeZoneStrings.get(dateTimeZone);
			}
		});

		SidebarAPI.addSidebarItem(player, SGLobbySidebarManager.spacer20);

		SidebarAPI.addSidebarItem(player, SGLobbySidebarManager.serverText);

		SidebarAPI.addSidebarItem(player, SGLobbySidebarManager.serverNameText);

		SidebarAPI.addSidebarItem(player, SGLobbySidebarManager.spacer40);

		// Create a new normal sidebar item for this player only, the getText method updates very fast async, but since we are only reading the keys from that map it should be fine.
		SidebarAPI.addSidebarItem(player, new SidebarItem(50) {
			@Override
			public String getText() {
				if (MPGLobby.getInstance().getPlayersInQueue().containsKey(uuid)) {
					return ChatColor.GREEN + "In Queue";
				} else {
					return ChatColor.YELLOW + "Not In Queue";
				}
			}
		});

		SidebarAPI.addSidebarItem(player, SGLobbySidebarManager.spacer60);
		SidebarAPI.addSidebarItem(player, SGLobbySidebarManager.ratingText);

		JSONObject sgSettings = UserDataManager.getUserData(player).getSGSettings();

		int rating = RatingManager.getPlayerRating(player.getUniqueId());
		int points = RatingUtil.Rank.getPoints(rating);
		String division = RatingUtil.getDivisionFromRating(rating);

		// Are they hiding their rating?
		if ((boolean) sgSettings.get("rating_visibility")) {
			// Has the player not finished their placement matches to show their rating?
			if (RatingManager.getPlayerMiniStats(player.getUniqueId()).getNumberOfGamesPlayed() < RatingUtil.SG_PLACEMENT_MATCHES) {
				points = -1;

				division = ChatColor.WHITE + "[Unranked]";
			}
		} else {
			points = -2;

			division = ChatColor.WHITE + ChatColor.ITALIC.toString() + "Hidden";
		}

		SidebarItem divisionItem;

		// If the map doesn't contain the string, add it for use for other players (SidebarItems can be added to more than 1 player at a time)
		if (!SGLobbySidebarManager.divisionItems.containsKey(division)) {
			divisionItem = new StaticSidebarItem(75, "  " + division);
			SGLobbySidebarManager.divisionItems.put(division, divisionItem);
		} else {
			divisionItem = SGLobbySidebarManager.divisionItems.get(division);
		}

		// Add their division item
		SidebarAPI.addSidebarItem(player, divisionItem);

		SidebarAPI.addSidebarItem(player, SGLobbySidebarManager.pointsText);

		SidebarItem pointsItem;
		if (points == -1) { // Unranked
			pointsItem = new StaticSidebarItem(85, "  " + ChatColor.WHITE + "N/A");
		} else if (points == -2) { // Hidden
			pointsItem = new StaticSidebarItem(85, ChatColor.WHITE + "  §oHidden");
		} else {
			pointsItem = new StaticSidebarItem(85, "  " + RatingUtil.Rank.getChatColorFromPoints(points).toString() + RatingUtil.Rank.getPoints(rating) + ChatColor.GRAY + "/1000");
		}

		// Add their points item
		SidebarAPI.addSidebarItem(player, pointsItem);

		SidebarAPI.addSidebarItem(player, SGLobbySidebarManager.spacer90);
		SidebarAPI.addSidebarItem(player, SGLobbySidebarManager.badlionText);
	}

	private class UpdateLocalTimeStringsTask extends BukkitRunnable {

		private final DateTimeFormatter formatter = DateTimeFormat.forPattern("hh:mm:ss a, ");

		@Override
		public void run() {
			SGLobbySidebarManager.timeZoneStrings.clear();

			DateTime now = DateTime.now();

			for (DateTimeZone timeZone : SGLobbySidebarManager.playerTimeZones.values()) {
				if (!SGLobbySidebarManager.timeZoneStrings.containsKey(timeZone)) {
					// JODA is TRASH
					SGLobbySidebarManager.timeZoneStrings.put(timeZone, "  " + now.toDateTime(timeZone).toString(this.formatter) + timeZone.toTimeZone().getDisplayName(false, TimeZone.SHORT, Locale.ENGLISH));
				}
			}
		}

	}

}
