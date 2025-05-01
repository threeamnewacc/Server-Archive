package net.badlion.arenatablist.pvp;

import net.badlion.arenatablist.ArenaTabList;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.tablist.TabList;
import net.badlion.tablist.TabListManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class ArenaPvPTabListManager extends TabListManager {

	public ArenaPvPTabListManager() {
		super(ArenaTabList.getInstance());
	}

	public void createTabList(Player player) {
		TabList tabList = new TabList(player, 1, 60);

		tabList.setPosition(1, "§6§lBadlion PvP", false);
		tabList.setPosition(21, "§6§lBadlion PvP ", false);
		tabList.setPosition(41, "§6§lBadlion PvP" + (char) 0x26c7, false);

		tabList.setPosition(2, ChatColor.WHITE + ChatColor.STRIKETHROUGH.toString() + "----------  ", false);
		tabList.setPosition(22, ChatColor.WHITE + ChatColor.STRIKETHROUGH.toString() + "---------  -", false);
		tabList.setPosition(42, ChatColor.WHITE + ChatColor.STRIKETHROUGH.toString() + "--------  --", false);

		tabList.setPosition(24, "§cServer:", false);
		if (Gberry.serverName.toLowerCase().contains("lobby")) {
			tabList.setPosition(25, "§9 Lobby " + Gberry.plugin.getServerNumber(), false);
		} else {
			tabList.setPosition(25, "§9 Arena " + Gberry.plugin.getServerNumber(), false);
		}

		tabList.setPosition(27, "§cDonation Info:", false);
		if (player.hasPermission("badlion.staff")) {
			tabList.setPosition(28, "§9 Staff Member", false);
		} else if (player.hasPermission("badlion.famousplus")) {
			tabList.setPosition(28, "§9 Famous §l+", false);
		} else if (player.hasPermission("badlion.famous")) {
			tabList.setPosition(28, "§9 Famous", false);
		} else if (player.hasPermission("badlion.lion")) {
			tabList.setPosition(28, "§9 Lion", false);
		} else if (player.hasPermission("badlion.donatorplus")) {
			tabList.setPosition(28, "§9 Donator §l+", false);
		} else if (player.hasPermission("badlion.donator")) {
			tabList.setPosition(28, "§9 Donator", false);
		} else {
			tabList.setPosition(28, "§9 N/A", false);
		}

		// Send initial tab list packets
		tabList.updateInitial();

		// We want to do this at the very end because we want to set everything up first
		this.tabLists.put(player, tabList);
	}


	public void updateMatchInfo(Player player, Map<Player, Boolean> playersTeam, Map<Player, Boolean> enemyTeam, RatingUtil.Rank playersRank, RatingUtil.Rank enemyRank) {
		TabList tabList = this.getTabList(player);

		for (int b = 4; b < 20; b++) {
			tabList.setPosition(b, "", false);
			tabList.setPosition(b + 40, "", false);
		}

		tabList.setPosition(31, "", false);
		tabList.setPosition(32, "", false);

		tabList.setPosition(34, "", false);
		tabList.setPosition(35, "", false);

		if (!playersTeam.isEmpty() && !enemyTeam.isEmpty()) {
			tabList.setPosition(4, ChatColor.GREEN + "Your Team:", false);

			int i = 0;
			for (Map.Entry<Player, Boolean> member : playersTeam.entrySet()) {
				if (i + 5 > 20) {
					break;
				}
				tabList.setPosition(5 + i, trim((member.getValue() ? ChatColor.GREEN : ChatColor.RED) + member.getKey().getDisguisedName()), false);
				i++;
			}


			tabList.setPosition(44, ChatColor.RED + "Enemy Team:", false);

			i = 0;
			for (Map.Entry<Player, Boolean> member : enemyTeam.entrySet()) {
				if (i + 45 > 60) {
					break;
				}
				tabList.setPosition(45 + i, trim(trim((member.getValue() ? ChatColor.GREEN : ChatColor.RED) + member.getKey().getDisguisedName())), false);
				i++;
			}
		}

		if (playersRank != null && enemyRank != null) {
			tabList.setPosition(31, "Your Rank:", false);
			tabList.setPosition(32, "§b " + playersRank.getName(), false);

			tabList.setPosition(34, "Their Rank:", false);
			tabList.setPosition(35, "§b " + enemyRank.getName() + (char) 0x1f427, false);
		}

		tabList.update(true);
	}

	public String trim(String string) {
		if (string.length() > 16) {
			return string.substring(0, 16);
		}
		return string;
	}


}
