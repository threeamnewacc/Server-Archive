package net.badlion.arenapvp.manager;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.gberry.utils.RatingUtil;
import net.kohi.sidebar.SidebarAPI;
import net.kohi.sidebar.item.SidebarItem;
import net.kohi.sidebar.item.StaticSidebarItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SidebarManager {

	// Only make a sidebar 1 time for each ruleset
	public static Map<KitRuleSet, SidebarItem> ruleSetSidebars = new HashMap<>();
	private static Map<Integer, SidebarItem> ratingSidebars = new HashMap<>();
	private static Map<Integer, SidebarItem> ratingSidebars2 = new HashMap<>();
	private static Map<String, SidebarItem> arenaSidebars = new HashMap<>();

	private static String time;

	public static SidebarItem clock = new StaticSidebarItem(5, "Server Time:");

	public static SidebarItem clockTime = new SidebarItem(6) {
		@Override
		public String getText() {
			return "   " + ChatColor.GOLD + time;
		}
	};

	public static SidebarItem matchTime = new StaticSidebarItem(8, "Match Time:");
	public static SidebarItem ladder = new StaticSidebarItem(14, "Ladder:");
	public static SidebarItem yourRating = new StaticSidebarItem(70, "Your Rating:");
	public static SidebarItem theirRating = new StaticSidebarItem(73, "Their Rating:");

	public static SidebarItem spacer10 = new StaticSidebarItem(10, "");
	public static SidebarItem spacer20 = new StaticSidebarItem(20, "");
	public static SidebarItem spacer80 = new StaticSidebarItem(80, "");
	public static SidebarItem spacer90 = new StaticSidebarItem(90, "");

	public static SidebarItem badlionSite = new StaticSidebarItem(99, ChatColor.AQUA + "www.badlion.net");


	public static void addSidebarItems(Player player, Match match) {
		if (!ArenaSettingsManager.getSettings(player).isSidebarEnabled()) {
			return;
		}
		if (SidebarManager.ruleSetSidebars.get(match.getKitRuleSet()) == null) {
			SidebarManager.ruleSetSidebars.put(match.getKitRuleSet(), new SidebarItem(15) {
				@Override
				public String getText() {
					return ChatColor.BLUE + "    " + match.getKitRuleSet().getName();
				}
			});
		}

		SidebarAPI.addSidebarItem(player, SidebarManager.clock);
		SidebarAPI.addSidebarItem(player, SidebarManager.clockTime);
		SidebarAPI.addSidebarItem(player, SidebarManager.matchTime);
		SidebarAPI.addSidebarItem(player, match.getMatchTime());
		SidebarAPI.addSidebarItem(player, SidebarManager.spacer10);
		SidebarAPI.addSidebarItem(player, SidebarManager.ladder);
		SidebarAPI.addSidebarItem(player, SidebarManager.ruleSetSidebars.get(match.getKitRuleSet()));
		if (match.getLadderType().equals(ArenaCommon.LadderType.RANKED_1V1)) {
			if (match.getTeams().get(0).contains(player)) {
				addRatingSidebars(player, match.getTeam1Rank(), match.getTeam2Rank());
			} else {
				addRatingSidebars(player, match.getTeam2Rank(), match.getTeam1Rank());
			}
		}


		SidebarAPI.addSidebarItem(player, SidebarManager.spacer90);
		SidebarAPI.addSidebarItem(player, SidebarManager.badlionSite);
	}

	public static void updatePlayerSidebar(Player player, Match match) {
		if (!ArenaSettingsManager.getSettings(player).isSidebarEnabled()) {
			return;
		}
		if (SidebarManager.ruleSetSidebars.get(match.getKitRuleSet()) == null) {
			SidebarManager.ruleSetSidebars.put(match.getKitRuleSet(), new SidebarItem(15) {
				@Override
				public String getText() {
					return ChatColor.BLUE + "    " + match.getKitRuleSet().getName();
				}
			});
		}

		SidebarAPI.addSidebarItem(player, match.getMatchTime());

		SidebarAPI.addSidebarItem(player, SidebarManager.ruleSetSidebars.get(match.getKitRuleSet()));
	}


	public static void addSpectatorSidebar(Player player, Match match) {
		if(!ArenaSettingsManager.getSettings(player).isSidebarEnabled()){
			return;
		}
		if (SidebarManager.ruleSetSidebars.get(match.getKitRuleSet()) == null) {
			SidebarManager.ruleSetSidebars.put(match.getKitRuleSet(), new SidebarItem(15) {
				@Override
				public String getText() {
					return ChatColor.BLUE + "    " + match.getKitRuleSet().getName();
				}
			});
		}

		SidebarAPI.addSidebarItem(player, SidebarManager.clock);
		SidebarAPI.addSidebarItem(player, SidebarManager.clockTime);
		SidebarAPI.addSidebarItem(player, SidebarManager.matchTime);
		SidebarAPI.addSidebarItem(player, match.getMatchTime());
		SidebarAPI.addSidebarItem(player, SidebarManager.spacer10);
		SidebarAPI.addSidebarItem(player, SidebarManager.ladder);
		SidebarAPI.addSidebarItem(player, SidebarManager.ruleSetSidebars.get(match.getKitRuleSet()));
		if (match.getLadderType().equals(ArenaCommon.LadderType.RANKED_1V1)) {
			addSpectatorRatingSidebars(player, match.getTeam1Rank(), match.getTeam2Rank(), match.getTeams().get(0).toString(), match.getTeams().get(1).toString());
		}
		SidebarAPI.addSidebarItem(player, SidebarManager.spacer80);

		addArenaItem(player, match);

		SidebarAPI.addSidebarItem(player, SidebarManager.spacer90);
		SidebarAPI.addSidebarItem(player, SidebarManager.badlionSite);
	}


    /* Removed, took up too much space and was not needed
    public static void addTeamInfoSidebar(Player player, Team otherTeam) {
        if (!teamSidebars.containsKey(otherTeam.getTeamId())) {
            List<SidebarItem> teamItems = new ArrayList<>();
            int i = 40;
            teamItems.add(new StaticSidebarItem(i++, ChatColor.WHITE + "Enemy Team:"));
            for (Player member : otherTeam.members()) {
                teamItems.add(new StaticSidebarItem(i++, ChatColor.RED + member.getDisguisedName()));
            }
            teamSidebars.put(otherTeam.getTeamId(), teamItems);
        }

        for (SidebarItem sidebarItem : teamSidebars.get(otherTeam.getTeamId())) {
            SidebarAPI.addSidebarItem(player, sidebarItem);
        }
    }*/


	public static void addRatingSidebars(Player player, RatingUtil.Rank yourRank, RatingUtil.Rank theirRank) {
		SidebarAPI.addSidebarItem(player, SidebarManager.yourRating);
		if (SidebarManager.ratingSidebars.get(yourRank.getRank()) == null) {
			SidebarManager.ratingSidebars.put(yourRank.getRank(), new StaticSidebarItem(71, ChatColor.GREEN + "   " + yourRank.getName()));
		}
		SidebarAPI.addSidebarItem(player, SidebarManager.ratingSidebars.get(yourRank.getRank()));

		SidebarAPI.addSidebarItem(player, SidebarManager.theirRating);
		if (SidebarManager.ratingSidebars2.get(theirRank.getRank()) == null) {
			SidebarManager.ratingSidebars2.put(theirRank.getRank(), new StaticSidebarItem(74, ChatColor.GREEN + "   " + theirRank.getName()));
		}
		SidebarAPI.addSidebarItem(player, ratingSidebars2.get(theirRank.getRank()));
	}

	public static void addSpectatorRatingSidebars(Player spectator, RatingUtil.Rank team1Rank, RatingUtil.Rank team2Rank, String team1Name, String team2Name) {
		SidebarAPI.addSidebarItem(spectator, new StaticSidebarItem(70, team1Name));

		if (SidebarManager.ratingSidebars.get(team1Rank.getRank()) == null) {
			SidebarManager.ratingSidebars.put(team1Rank.getRank(), new StaticSidebarItem(71, ChatColor.GREEN + "   " + team1Rank.getName()));
		}
		SidebarAPI.addSidebarItem(spectator, SidebarManager.ratingSidebars.get(team1Rank.getRank()));

		SidebarAPI.addSidebarItem(spectator, new StaticSidebarItem(73, team2Name));
		if (SidebarManager.ratingSidebars2.get(team2Rank.getRank()) == null) {
			SidebarManager.ratingSidebars2.put(team2Rank.getRank(), new StaticSidebarItem(74, ChatColor.GREEN + "   " + team2Rank.getName()));
		}
		SidebarAPI.addSidebarItem(spectator, ratingSidebars2.get(team2Rank.getRank()));
	}

	public static void addArenaItem(Player player, Match match) {
		if (!SidebarManager.arenaSidebars.containsKey(match.getArena().getSchematicName())) {
			SidebarManager.arenaSidebars.put(match.getArena().getSchematicName(), new StaticSidebarItem(85, "Map: " + ChatColor.GREEN + match.getArena().getNiceArenaName()));
		}
		SidebarAPI.addSidebarItem(player, SidebarManager.arenaSidebars.get(match.getArena().getSchematicName()));
	}

	public static void setTime(String time) {
		SidebarManager.time = time;
	}

	public static void removeSidebar(Player player) {
		SidebarAPI.removeAllSidebarItems(player);
	}
}
