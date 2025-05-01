package net.badlion.arenalobby.managers;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.PotPvPPlayer;
import net.badlion.gberry.Gberry;
import net.kohi.sidebar.SidebarAPI;
import net.kohi.sidebar.item.SidebarItem;
import net.kohi.sidebar.item.StaticSidebarItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SidebarManager {

	private static Map<UUID, List<SidebarItem>> sidebars = new HashMap<>();

	public static int totalOnArena = 0;
	public static int totalInGames = 0;

	private static SidebarItem spacer10 = new StaticSidebarItem(10, "");

	private static SidebarItem lobby;
	private static SidebarItem spacer20 = new StaticSidebarItem(20, "");

	private static Map<String, SidebarItem> rankItems;
	private static Map<Integer, SidebarItem> unrankedMatchesLeftItems;
	private static Map<Integer, SidebarItem> rankedMatchesLeftItems;

	private static SidebarItem spacer28 = new StaticSidebarItem(28, "");


	private static SidebarItem rankedMatchesLeftText = new StaticSidebarItem(30, ChatColor.GREEN + "Ranked Left:");
	private static SidebarItem unlimitedRankedText = new StaticSidebarItem(31, "    Unlimited");

	private static SidebarItem unrankedMatchesLeftText = new StaticSidebarItem(35, ChatColor.GREEN + "Unranked Left:");
	private static SidebarItem unlimitedUnRankedText = new StaticSidebarItem(36, "    Unlimited");

	private static SidebarItem spacer40 = new StaticSidebarItem(40, "");
	private static SidebarItem playersOnArenaItem;
	private static SidebarItem spacer47 = new StaticSidebarItem(47, "");

	private static SidebarItem playersInLobbyItem;
	private static SidebarItem playersInGameItem;

	private static SidebarItem spacer60 = new StaticSidebarItem(60, "");

	private static SidebarItem global;
	private static SidebarItem badlionText = new StaticSidebarItem(99, ChatColor.AQUA + "www.badlion.net");

	static {
		lobby = new StaticSidebarItem(15, ChatColor.GREEN + Gberry.serverRegion.toString() + " Lobby: " + ChatColor.WHITE + "#" + Gberry.plugin.getServerNumber());

		rankItems = new HashMap<>();
		rankItems.put("badlion.staff", new StaticSidebarItem(25, ChatColor.GREEN + "Rank: " + ChatColor.RED + "Staff"));
		rankItems.put("badlion.famousplus", new StaticSidebarItem(25, ChatColor.GREEN + "Rank: " + ChatColor.GOLD + "Famous +"));
		rankItems.put("badlion.famous", new StaticSidebarItem(25, ChatColor.GREEN + "Rank: " + ChatColor.GOLD + "Famous"));
		rankItems.put("badlion.lion", new StaticSidebarItem(25, ChatColor.GREEN + "Rank: " + ChatColor.GOLD + "Lion"));
		rankItems.put("badlion.donator+", new StaticSidebarItem(25, ChatColor.GREEN + "Rank: " + ChatColor.YELLOW + "Donator+"));
		rankItems.put("badlion.donator", new StaticSidebarItem(25, ChatColor.GREEN + "Rank: " + ChatColor.BLUE + "Donator"));
		rankItems.put("badlion.default", new StaticSidebarItem(25, ChatColor.GREEN + "Rank: " + ChatColor.WHITE + "None"));

		unrankedMatchesLeftItems = new HashMap<>();
		rankedMatchesLeftItems = new HashMap<>();

		playersOnArenaItem = new SidebarItem(45) {
			@Override
			public String getText() {
				return ChatColor.GREEN + "Players: " + ChatColor.WHITE + totalOnArena;
			}
		};
		playersInLobbyItem = new SidebarItem(49) {
			@Override
			public String getText() {
				return ChatColor.GREEN + "In Lobby: " + ChatColor.WHITE + (totalOnArena - totalInGames);
			}
		};
		playersInGameItem = new SidebarItem(50) {
			@Override
			public String getText() {
				return ChatColor.GREEN + "In Game: " + ChatColor.WHITE + totalInGames;
			}
		};

		// TODO: get global rank for the player
		global = new SidebarItem(65) {
			@Override
			public String getText() {
				return "Global: " + ChatColor.GOLD + "Silver IV";
			}
		};
	}

	public static SidebarItem getRankedMatchesLeftItem(final int matchesLeft) {
		if (!rankedMatchesLeftItems.containsKey(matchesLeft)) {
			ChatColor chatColor = ChatColor.GREEN;
			if (matchesLeft < 5) {
				chatColor = ChatColor.RED;
			} else if (matchesLeft < 10) {
				chatColor = chatColor.YELLOW;
			}
			final ChatColor finalChatColor = chatColor;
			SidebarItem item = new SidebarItem(30) {
				@Override
				public String getText() {
					return ChatColor.GREEN + "Ranked Left: " + finalChatColor + matchesLeft;
				}
			};
			rankedMatchesLeftItems.put(matchesLeft, item);
		}
		return rankedMatchesLeftItems.get(matchesLeft);
	}

	public static SidebarItem getUnrankedMatchesLeftItem(final int matchesLeft) {
		if (!unrankedMatchesLeftItems.containsKey(matchesLeft)) {
			ChatColor chatColor = ChatColor.GREEN;
			if (matchesLeft < 10) {
				chatColor = ChatColor.RED;
			} else if (matchesLeft < 20) {
				chatColor = chatColor.YELLOW;
			}
			final ChatColor finalChatColor = chatColor;
			SidebarItem item = new SidebarItem(35) {
				@Override
				public String getText() {
					return ChatColor.GREEN + "Unranked Left: " + finalChatColor + matchesLeft;
				}
			};
			unrankedMatchesLeftItems.put(matchesLeft, item);
		}
		return unrankedMatchesLeftItems.get(matchesLeft);
	}


	public static void addSidebarItems(Player player) {
		sidebars.put(player.getUniqueId(), new ArrayList<SidebarItem>());
		List<SidebarItem> side = new ArrayList<>();
		//side.add(spacer10);
		side.add(lobby);
		side.add(spacer20);
		if (player.hasPermission("badlion.staff")) {
			side.add(rankItems.get("badlion.staff"));
		} else if (player.hasPermission("badlion.famousplus")) {
			side.add(rankItems.get("badlion.famousplus"));
		} else if (player.hasPermission("badlion.famous")) {
			side.add(rankItems.get("badlion.famous"));
		} else if (player.hasPermission("badlion.lion")) {
			side.add(rankItems.get("badlion.lion"));
		} else if (player.hasPermission("badlion.donator+")) {
			side.add(rankItems.get("badlion.donator+"));
		} else if (player.hasPermission("badlion.donator")) {
			side.add(rankItems.get("badlion.donator"));
		} else {
			side.add(rankItems.get("badlion.default"));
		}
		side.add(spacer28);
		if (player.hasPermission(ArenaLobby.getUnlimitedRankedPermission())) {
			side.add(rankedMatchesLeftText);
			side.add(unlimitedRankedText);
			side.add(unrankedMatchesLeftText);
			side.add(unlimitedUnRankedText);
		} else {
			SidebarManager.tryAddMatchesLeftItems(player);
		}
		side.add(spacer40);
		side.add(playersOnArenaItem);
		side.add(spacer47);
		side.add(playersInLobbyItem);
		side.add(playersInGameItem);
		side.add(spacer60);
		//side.add(global);
		side.add(badlionText);
		sidebars.get(player.getUniqueId()).addAll(side);
		for (SidebarItem sidebarItem : side) {
			SidebarAPI.addSidebarItem(player, sidebarItem);
		}
	}

	public static void tryAddMatchesLeftItems(final Player player) {
		PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());
		if (potPvPPlayer.isRankedLeftLoaded()) {
			List<SidebarItem> side = sidebars.get(player.getUniqueId());
			SidebarItem rankedLeft = SidebarManager.getRankedMatchesLeftItem(RankedLeftManager.getNumberOfRankedMatchesLeft(player));
			SidebarItem unrankedLeft = SidebarManager.getUnrankedMatchesLeftItem(RankedLeftManager.getNumberOfUnRankedMatchesLeft(player));
			side.add(rankedLeft);
			side.add(unrankedLeft);
			SidebarAPI.addSidebarItem(player, rankedLeft);
			SidebarAPI.addSidebarItem(player, unrankedLeft);
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (player != null && player.isOnline()) {
						tryAddMatchesLeftItems(player);
					}
				}
			}.runTaskLater(ArenaLobby.getInstance(), 10);
		}
	}

	public static void removeSidebar(Player player) {
		List<SidebarItem> sidebarItems = sidebars.remove(player.getUniqueId());
		if (sidebarItems != null) {
			for (SidebarItem sidebarItem : sidebarItems) {
				SidebarAPI.removeSidebarItem(player, sidebarItem);
			}
		}
	}
}
