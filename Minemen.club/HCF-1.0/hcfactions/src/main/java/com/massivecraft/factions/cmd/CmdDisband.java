package com.massivecraft.factions.cmd;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.FactionPostDisbandEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import org.bukkit.Bukkit;

public class CmdDisband extends FCommand {

	public CmdDisband() {
		super();
		this.aliases.add("disband");

		// this.requiredArgs.add("");
		this.optionalArgs.put("faction tag", "yours");

		this.permission = Permission.DISBAND.node;
		this.disableOnLock = true;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		// The faction, default to your own.. but null if console sender.
		Faction faction = this.argAsFaction(0, fme == null ? null : myFaction);
		if (faction == null) {
			return;
		}

		boolean isMyFaction = fme == null ? false : faction == myFaction;

		if (isMyFaction) {
			if (!assertMinRole(Role.ADMIN)) {
				return;
			}
			if (myFaction.isRaidable()) {
				msg("<b>You can't disband your faction while it is raidable.");
				return;
			}
		} else {
			if (!Permission.DISBAND_ANY.has(sender, true)) {
				return;
			}
		}

		if (!faction.isNormal()) {
			msg("<instance>You cannot disband the Wilderness, SafeZone, or WarZone.");
			return;
		}
		if (faction.isPermanent()) {
			msg("<instance>This faction is designated as permanent, so you cannot disband it.");
			return;
		}

		FactionDisbandEvent disbandEvent = new FactionDisbandEvent(me, faction.getId());
		Bukkit.getServer().getPluginManager().callEvent(disbandEvent);
		if (disbandEvent.isCancelled()) {
			return;
		}

		// Send FPlayerLeaveEvent for each player in the faction
		for (FactionPlayer fplayer : faction.getFPlayers()) {
			Bukkit.getServer().getPluginManager().callEvent(new FPlayerLeaveEvent(fplayer, faction, FPlayerLeaveEvent.PlayerLeaveReason.DISBAND));
		}

		// Inform all players
		for (FactionPlayer fplayer : FPlayers.getInstance().getOnline()) {
			String who = senderIsConsole ? "A server admin" : fme.describeTo(fplayer);
			if (fplayer.getFaction() == faction) {
				fplayer.msg("<h>%s<instance> disbanded your faction.", who);
			} else {
				fplayer.msg("<h>%s<instance> disbanded the faction %s.", who, faction.getTag(fplayer));
			}
		}
		if (Conf.logFactionDisband) {
			HCFactions.getInstance().log("The faction " + faction.getTag() + " (" + faction.getId() + ") was disbanded by " + (senderIsConsole ? "console command" : fme.getName()) + ".");
		}

		FactionPostDisbandEvent postDisbandEvent = new FactionPostDisbandEvent(me, faction);
		Bukkit.getServer().getPluginManager().callEvent(postDisbandEvent);

		faction.detach();
	}
}
