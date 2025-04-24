package com.massivecraft.factions.cmd;

import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.Bukkit;

public class CmdJoin extends FCommand {

	public CmdJoin() {
		super();
		this.aliases.add("join");

		this.requiredArgs.add("faction name");
		this.optionalArgs.put("player", "you");

		this.permission = Permission.JOIN.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		Faction faction = this.argAsFaction(0);
		if (faction == null) {
			return;
		}

		FactionPlayer fplayer = this.argAsFPlayer(1, fme, false);
		boolean samePlayer = fplayer == fme;

		if (!samePlayer && !Permission.JOIN_OTHERS.has(sender, false)) {
			this.fme.getPlayer().sendFormattedMessage("{0}You don''t have permission to force a player into a faction.", CC.RED);
			return;
		}

		if (!faction.isNormal()) {
			this.fme.getPlayer().sendFormattedMessage("{0}You''re not able to join a system faction.", CC.RED);
			return;
		}

		if (faction == fplayer.getFaction()) {
			this.fme.getPlayer().sendFormattedMessage("{1}{2} {3}{0} is already a member of {1}{4}{0}.", CC.PRIMARY, CC.SECONDARY, fplayer.describeTo(fme, true), (samePlayer ? "are" : "is"), faction.getTag(fme));
			return;
		}

		if (Conf.factionMemberLimit > 0) {
			int limit = Conf.factionMemberLimit - faction.getLockedSlots();
			if (faction.getFPlayers().size() >= limit) {
				msg(" <b>!<white> The faction %s is at the limit of %d members, so %s cannot currently join.", faction.getTag(fme), limit, fplayer.describeTo(fme, false));
				return;
			}
		}

		if (fplayer.hasFaction()) {
			this.fme.getPlayer().sendFormattedMessage("{0}{1}{0} must leave {2} current faction first.", CC.RED, fplayer.describeTo(fme, true), (samePlayer ? "your" : "their"));
			return;
		}

		if (faction.isRaidable()) {
			this.fme.getPlayer().sendFormattedMessage("{0}{1}{0} cannot join that faction while it's raidable.", CC.RED, fplayer.describeTo(fme, true), (samePlayer ? "your" : "their"));
			return;
		}

		if (!(faction.isInvited(fplayer) || fme.isAdminBypassing() || Permission.JOIN_ANY.has(sender, false))) {
			this.fme.getPlayer().sendFormattedMessage("{0}You require an invite to join the faction.", CC.RED);
			if (samePlayer) {
				faction.msg("%s<instance> tried to join your faction.", fplayer.describeTo(faction, true));
			}
			return;
		}

		// trigger the join event (cancellable)
		FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayers.getInstance().get(me), faction, FPlayerJoinEvent.PlayerJoinReason.COMMAND);
		Bukkit.getServer().getPluginManager().callEvent(joinEvent);
		if (joinEvent.isCancelled()) {
			return;
		}

		this.fme.getPlayer().sendFormattedMessage("{1}{2}{0} successfully joined {1}{3}{0}.", CC.PRIMARY, CC.SECONDARY, fplayer.describeTo(fme, true), faction.getTag(fme));

		if (!samePlayer) {
			fplayer.getPlayer().sendFormattedMessage("{1}{2}{0} successfully joined {1}{3}{0}.", CC.PRIMARY, CC.SECONDARY, fplayer.describeTo(fme, true), faction.getTag(fme));
			fplayer.getPlayer().sendFormattedMessage("{1}{2}{0} moved you into the faction {1}{3}{0}.", CC.PRIMARY, CC.SECONDARY, fme.describeTo(fplayer, true), faction.getTag(fplayer));

		}
		faction.msg("<instance>%s" + CC.PRIMARY + " joined your faction.", fplayer.describeTo(faction, true));


		fplayer.resetFactionData();
		fplayer.setFaction(faction);
		faction.deinvite(fplayer);


		if (Conf.logFactionJoin) {
			if (samePlayer) {
				HCFactions.getInstance().log("%s joined the faction %s.", fplayer.getName(), faction.getTag());
			} else {
				HCFactions.getInstance().log("%s moved the player %s into the faction %s.", fme.getName(), fplayer.getName(), faction.getTag());
			}
		}
	}
}
