package cc.patrone.scoreboardapi;

import org.bukkit.ChatColor;

final class ScoreboardUtils {
	private ScoreboardUtils() {
	}

	static String translate(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
