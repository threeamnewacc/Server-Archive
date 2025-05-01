package net.badlion.capturetheflag.manager;

import net.badlion.capturetheflag.CTFPlayer;
import net.badlion.capturetheflag.CTFTeam;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.managers.MPGTeamManager;
import net.badlion.mpg.tasks.GameTimeTask;
import net.kohi.sidebar.SidebarAPI;
import net.kohi.sidebar.item.SidebarItem;
import net.kohi.sidebar.item.StaticSidebarItem;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class SidebarManager {

	private String gameTimeString = "";

	private static final SidebarItem gameTime = new SidebarItem(15) {
		@Override
		public String getText() {
			return ChatColor.GREEN + "Game Time: " + ChatColor.WHITE + GameTimeTask.getInstance().getGameTime();
		}
	};
	private static final SidebarItem spacer20 = new StaticSidebarItem(20, "");

	// TODO: Player kill counts need a new sidebar item for each value
	private static final SidebarItem kills = new SidebarItem(25) {
		@Override
		public String getText() {
			return ChatColor.GREEN + "Your Kills: " + ChatColor.WHITE;
		}
	};

	private static final SidebarItem spacer30 = new StaticSidebarItem(30, "");

	private static Map<String, SidebarItem> teamScoreItems = new HashMap<>();


	private static final SidebarItem spacer55 = new StaticSidebarItem(55, "");

	private static final SidebarItem website = new StaticSidebarItem(99, ChatColor.AQUA + "www.badlion.net");


	public static void addSidebar(final CTFPlayer ctfPlayer) {
		SidebarAPI.addSidebarItem(ctfPlayer.getPlayer(), SidebarManager.gameTime);
		SidebarAPI.addSidebarItem(ctfPlayer.getPlayer(), SidebarManager.spacer20);
		SidebarAPI.addSidebarItem(ctfPlayer.getPlayer(), SidebarManager.kills);
		SidebarAPI.addSidebarItem(ctfPlayer.getPlayer(), SidebarManager.spacer30);

		int i = 0;
		for (MPGTeam mpgTeam : MPGTeamManager.getAllMPGTeams()) {
			final CTFTeam ctfTeam = (CTFTeam) mpgTeam;

			if (!SidebarManager.teamScoreItems.containsKey(ctfTeam.getTeamName())) {
				SidebarManager.teamScoreItems.put(ctfTeam.getTeamName(), new SidebarItem(35 + i) {
					@Override
					public String getText() {
						return ctfTeam.getColor() + ctfTeam.getTeamName() + " Score: " + ChatColor.WHITE + ctfTeam.getScore();
					}
				});
			}
			SidebarAPI.addSidebarItem(ctfPlayer.getPlayer(), SidebarManager.teamScoreItems.get(ctfTeam.getTeamName()));
			i++;
		}
		SidebarAPI.addSidebarItem(ctfPlayer.getPlayer(), SidebarManager.spacer55);
		SidebarAPI.addSidebarItem(ctfPlayer.getPlayer(), SidebarManager.website);
	}

}
