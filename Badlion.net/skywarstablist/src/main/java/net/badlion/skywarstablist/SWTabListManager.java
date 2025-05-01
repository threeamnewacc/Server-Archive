package net.badlion.skywarstablist;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.skywars.SkyWars;
import net.badlion.tablist.TabList;
import net.badlion.tablist.TabListManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class SWTabListManager extends TabListManager {

	private boolean gameStarted = false;

	public SWTabListManager() {
		super(SWTabList.getInstance());
	}

	public void createTabList(Player player) {
		TabList tabList = new TabList(player, 1, 60);

		// Set up strings
		if(this.gameStarted) {
			String worldName = SkyWars.getInstance().getCurrentGame().getGWorld().getNiceWorldName();
			String worldName2 = "";
			if(worldName.length() > 14) {
				worldName2 = worldName.substring(14);
				worldName = worldName.substring(0, 14);
			}

			if (worldName2.length() > 14) {
				worldName2 = worldName2.substring(0, 14);
			}

			tabList.setPosition(1, "§bPlaying on", false);
			tabList.setPosition(21, "§e" + worldName, false);
			tabList.setPosition(41, "§e" + worldName2, false);
		} else {
			tabList.setPosition(21, "§eIn lobby", false);
		}

		tabList.setPosition(23, "§6§lBadlion SW", false);
		tabList.setPosition(24, "§6§lBadlion SW ", false);

   	    tabList.setPosition(6, "§cServer:", false);
		tabList.setPosition(7, "§9" + Gberry.serverName, false);

		tabList.setPosition(26, "§cDonation Info:", false);
		if (player.hasPermission("badlion.sgtrial")) {
			tabList.setPosition(27, "§9 Staff Member", false);
		} else if (player.hasPermission("badlion.famous")) {
			tabList.setPosition(27, "§9 Famous", false);
		} else if (player.hasPermission("badlion.donatorplus")) {
			tabList.setPosition(27, "§9 Donator §l+", false);
		} else if (player.hasPermission("badlion.donator")) {
			tabList.setPosition(27, "§9 Donator", false);
		} else {
			tabList.setPosition(27, "§9 N/A", false);
		}

		tabList.setPosition(9, "§a-Alive-", false);
		tabList.setPosition(29, "§b-Spectating-", false);
		tabList.setPosition(49, "", false);

		// Add alive/spectating players to the tab list
		List<MPGPlayer> deadOrSpectatingPlayers = new ArrayList<>();
		int counter = 0;

		// Fill alive players
		for (MPGPlayer sgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
			int j = counter + 10;

			if (counter > 20) j += 19;
			else if (counter > 10) j += 9;

			String name = sgPlayer.getUsername();

			// /anon check
			//if (DisguiseCommand.DISGUISED_PLAYERS_NAMES.containsKey(sgPlayer.getUniqueId())) {
			//	name = DisguiseCommand.DISGUISED_PLAYERS_NAMES.get(sgPlayer.getUniqueId());
			//}

			if (name.length() > 14) {
				name = name.substring(0, 14);
			}

			tabList.setPosition(j, ChatColor.GREEN + name, false);

			counter++;
		}

		// Get all dead and spectating players
		deadOrSpectatingPlayers.addAll(MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.DEAD));
		deadOrSpectatingPlayers.addAll(MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.SPECTATOR));


		// Fill spectators
		for (MPGPlayer mpgPlayer : deadOrSpectatingPlayers) {
			// Offline check
			if (SkyWars.getInstance().getServer().getPlayer(mpgPlayer.getUniqueId()) == null) {
				continue;
			}

			int j = counter + 10;

			if (counter > 20) j += 19;
			else if (counter > 10) j += 9;

			String name = mpgPlayer.getUsername();

			// /anon check
			//if (DisguiseCommand.DISGUISED_PLAYERS_NAMES.containsKey(mpgPlayer.getUniqueId())) {
			//	name = DisguiseCommand.DISGUISED_PLAYERS_NAMES.get(mpgPlayer.getUniqueId());
			//}

			if (name.length() > 14) {
				name = name.substring(0, 14);
			}

			tabList.setPosition(j, ChatColor.AQUA + name, false);

			counter++;
		}

		// Fill blank
		for (int i = counter; i < 32; i++) {
			int j = i + 10;

			if (i > 20) j += 19;
			else if (i > 10) j += 9;

			tabList.setPosition(j, "", false);
		}

		// Send initial tab list packets
		tabList.updateInitial();

		// We want to do this at the very end because we want to set everything up first
		this.tabLists.put(player, tabList);
	}

	public void updatePlayerList() {
		Map<Integer, String> tabChanges = new HashMap<>();

		List<MPGPlayer> deadOrSpectatingPlayers = new ArrayList<>();
		int counter = 0;

		// Fill alive players
		for (MPGPlayer sgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
			int j = counter + 10;

			if (counter > 20) j += 19;
			else if (counter > 10) j += 9;

			String name = sgPlayer.getUsername();

			// /anon check
			//if (DisguiseCommand.DISGUISED_PLAYERS_NAMES.containsKey(sgPlayer.getUniqueId())) {
			//	name = DisguiseCommand.DISGUISED_PLAYERS_NAMES.get(sgPlayer.getUniqueId());
			//}

			if (name.length() > 14) {
				name = name.substring(0, 14);
			}

			tabChanges.put(j, ChatColor.GREEN + name);

			counter++;
		}

		// Get all dead and spectating players
		deadOrSpectatingPlayers.addAll(MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.DEAD));
		deadOrSpectatingPlayers.addAll(MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.SPECTATOR));


		// Fill spectators
		for (MPGPlayer mpgPlayer : deadOrSpectatingPlayers) {
			// Offline check
			if (SkyWars.getInstance().getServer().getPlayer(mpgPlayer.getUniqueId()) == null) {
				continue;
			}

			int j = counter + 10;

			if (counter > 20) j += 19;
			else if (counter > 10) j += 9;

			String name = mpgPlayer.getUsername();

			// /anon check
			//if (DisguiseCommand.DISGUISED_PLAYERS_NAMES.containsKey(mpgPlayer.getUniqueId())) {
			//	name = DisguiseCommand.DISGUISED_PLAYERS_NAMES.get(mpgPlayer.getUniqueId());
			//}

			if (name.length() > 14) {
				name = name.substring(0, 14);
			}

			tabChanges.put(j, ChatColor.AQUA + name);

			counter++;
		}

		// Fill blank
		for (int i = counter; i < 32; i++) {
			int j = i + 10;

			if (i > 20) j += 19;
			else if (i > 10) j += 9;

			tabChanges.put(j, "");
		}

		this.setAllTabListPositions(tabChanges);
	}

	public void updateMapInfo() {
		Map<Integer, String> tabChanges = new HashMap<>();

		String worldName = SkyWars.getInstance().getCurrentGame().getGWorld().getNiceWorldName();
		String worldName2 = "";
		if (worldName.length() > 14) {
			worldName2 = worldName.substring(14);
			worldName = worldName.substring(0, 14);
		}

		if (worldName2.length() > 14) {
			worldName2 = worldName2.substring(0, 14);
		}

		tabChanges.put(1, "§bPlaying on");
		tabChanges.put(21, "§e" + worldName);
		tabChanges.put(41, "§e" + worldName2);

		this.setAllTabListPositions(tabChanges);
	}

	public void setGameStarted(boolean gameStarted) {
		// Update the top 3 lines (map info)
		this.updateMapInfo();

		this.gameStarted = gameStarted;
	}

}
