package com.massivecraft.factions.cmd;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class CmdShow extends FCommand {

	public CmdShow() {
		this.aliases.add("show");
		this.aliases.add("who");

		// this.requiredArgs.add("");
		this.optionalArgs.put("faction tag", "yours");

		this.permission = Permission.SHOW.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		Faction factionByName = null;
		Faction factionByPlayer = null;
		boolean ownFaction = true;
		if (this.argIsSet(0)) {
			String name = argAsString(0);
			if (name != null) {
				// First we try an exact match
				factionByName = Factions.getInstance().getByTag(name);
				// Next we match player names
				FactionPlayer fplayer = FPlayers.getInstance().getByName(name);
				if (fplayer != null) {
					factionByPlayer = fplayer.getFaction();
				}
			}

			if (factionByName == null && factionByPlayer == null) {
				this.msg("<b>The faction or player \"<plugin>%s<b>\" could not be found.", name);
				return;
			}
			ownFaction = false;
		} else if (myFaction.isNone()) {
			msg(ChatColor.RED + "You are not in a faction.");
			return;
		}

		// Found a faction by the players name but not by the factions name
		if (factionByName == null && factionByPlayer != null) {
			if (factionByPlayer.isNone()) {
				msg(ChatColor.RED + "That player is not in a faction.");
				return;
			}
		}

		if (ownFaction) {
			sendFactionInfoMessage(myFaction, fme.getPlayer());
			return;
		} else {
			if (factionByName != null && factionByPlayer == null) {
				sendFactionInfoMessage(factionByName, fme.getPlayer());
				return;
			}
			if (factionByName == null && factionByPlayer != null) {
				sendFactionInfoMessage(factionByPlayer, fme.getPlayer());
				return;
			}
			if (factionByName != null && factionByPlayer != null) {
				sendFactionInfoMessage(factionByName, fme.getPlayer());
				sendFactionInfoMessage(factionByPlayer, fme.getPlayer());
				return;
			}
		}

	}

	private void sendFactionInfoMessage(Faction faction, Player player) {
		msg(p.txt.titleize(faction.getTag(fme)));
		if (!faction.isNormal()) {
			msg("<a>This faction cannot have players in it!");
			if (faction.hasHome()) {
				Location home = faction.getHome();
				msg("<a>Location: <instance>%d, %d, %d", home.getBlockX(), home.getBlockY(), home.getBlockZ());
			}
			return;
		}

		if (faction.getMotd() != null) {
			if (faction.getMotd().length() > 0 && faction == myFaction) {
				msg("<a>MOTD: " + ChatColor.GRAY + faction.getMotd());
			}
		}
		double dtr = faction.getDtr();
		if (dtr > 0) {
			msg("<a>DTR: <instance>%.2f / %.2f (%d %s)", dtr, faction.getMaxDtr(), (int) Math.ceil(dtr), dtr > 1 ? "deaths" : "death");
		} else {
			msg("<a>DTR: <instance>%.2f / %.2f <b>(RAIDABLE)", dtr, faction.getMaxDtr());
		}

		long dtrFreeze = faction.getDtrRegenCooldown() - System.currentTimeMillis();
		if (dtrFreeze > 0) {
			msg("<a>DTR freeze: <instance>%s", DurationFormatUtils.formatDurationWords(dtrFreeze, true, true));
		};

		msg("<a>Land: <instance>%d / %d", faction.getLandRounded(), faction.getMaxLandCount());

		if (faction.hasHome()) {
			Location home = faction.getHome();
			msg("<a>Faction home: <instance>%d, %d, %d", home.getBlockX(), home.getBlockY(), home.getBlockZ());
		} else {
			msg("<a>Faction home: <instance>Not set");
		}

		if (faction.isPermanent()) {
			msg("<a>This faction is permanent, remaining even with no members.");
		}

		String listpart;

		// Allies List
		Set<Faction> allies = faction.getAlliedFactions();
		if (!allies.isEmpty()) {
			StringBuilder alliesString = new StringBuilder(ChatColor.GOLD + "Allies");
			alliesString.append(ChatColor.GOLD + "(" + allies.size() + "/" + Conf.allyLimit + "): ");
			boolean first = true;
			for (Faction ally : allies) {
				if (!first) {
					alliesString.append(ChatColor.YELLOW + ", ");
				}
				first = false;
				listpart = ally.getTag(fme) + " (" + ally.getOnlinePlayerCount(true) + ")";
				alliesString.append(listpart);
			}
			msg(alliesString.toString());
		}

		List<FactionPlayer> online = new ArrayList<>(faction.getFPlayersWhereOnline(true));
		List<FactionPlayer> offline = new ArrayList<>(faction.getFPlayersWhereOnline(false));

		if (Conf.factionMemberLimit > 0) {
			msg("<a>Members: <instance>%d / %d", faction.getFPlayers().size(), Conf.factionMemberLimit - faction.getLockedSlots());
		} else {
			msg("<a>Members: <instance>%d", faction.getFPlayers().size());
		}

		if (!online.isEmpty()) {
			Collections.sort(online);
			me.sendMessage(ChatColor.GOLD + "Members online(" + online.size() + "): " + memberList(online));
		}
		if (!offline.isEmpty()) {
			Collections.sort(offline);
			me.sendMessage(ChatColor.GOLD + "Members offline(" + offline.size() + "): " + memberList(offline));
		}
	}

	private String memberList(Collection<FactionPlayer> fplayers) {
		StringJoiner online = new StringJoiner(ChatColor.YELLOW + ", ");
		StringJoiner offline = new StringJoiner(ChatColor.YELLOW + ", ");
		for (FactionPlayer factionPlayer : fplayers) {
			if (HCFactions.getInstance().killsProvider != null) {
				online.add((this.p.getDeathbanManager().isDeathBanned(factionPlayer.getUuid()) ?
				            ChatColor.DARK_RED : factionPlayer.getColorTo(fme)) +
				           factionPlayer.getNameAndSomething("") + ChatColor.GRAY + "[" + factionPlayer
						           .getColorTo(fme) + HCFactions.getInstance().killsProvider.getKills(
						factionPlayer.getUuid()) + ChatColor.GRAY + "]");
			} else {

				online.add((this.p.getDeathbanManager().isDeathBanned(factionPlayer.getUuid()) ?
				            ChatColor.DARK_RED : factionPlayer.getColorTo(fme))
				           + factionPlayer.getNameAndSomething(""));
			}
		}
		return online.merge(offline).toString();
	}
}
