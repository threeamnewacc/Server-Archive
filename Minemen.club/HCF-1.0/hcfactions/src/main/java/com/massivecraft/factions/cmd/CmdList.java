package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CmdList extends FCommand {

	public CmdList() {
		super();
		this.aliases.add("list");
		this.aliases.add("ls");

		// this.requiredArgs.add("");
		this.optionalArgs.put("page", "1");

		this.permission = Permission.LIST.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		List<Faction> factionList = new ArrayList<>();

		// Get all factions of online players
		for (Player player : Bukkit.getOnlinePlayers()) {
			Faction faction = FPlayers.getInstance().get(player).getFaction();
			if (faction != null && faction.isNormal()) {
				if (!factionList.contains(faction)) {
					factionList.add(faction);
				}
			}
		}

		// Then sort by how many members are online now
		Collections.sort(factionList, new Comparator<Faction>() {
			@Override
			public int compare(Faction f1, Faction f2) {
				int f1Size = f1.getOnlinePlayerCount(true);
				int f2Size = f2.getOnlinePlayerCount(true);
				if (f1Size < f2Size) {
					return 1;
				} else if (f1Size > f2Size) {
					return -1;
				}
				return 0;
			}
		});

		List<String> lines = new ArrayList<>();

		final int pageheight = 9;
		int pagenumber = this.argAsInt(0, 1);
		int pagecount = (factionList.size() / pageheight) + 1;
		if (pagenumber > pagecount) {
			pagenumber = pagecount;
		} else if (pagenumber < 1) {
			pagenumber = 1;
		}
		int start = (pagenumber - 1) * pageheight;
		int end = start + pageheight;
		if (end > factionList.size()) {
			end = factionList.size();
		}

		lines.add(p.txt.titleize("Online Faction List " + pagenumber + "/" + pagecount));

		for (Faction faction : factionList.subList(start, end)) {
			lines.add(p.txt.parse("%s<instance> %d/%d online", faction.getTag(fme), faction.getFPlayersWhereOnline(true).size(), faction.getFPlayers().size()));
		}

		sendMessage(lines);
	}
}
