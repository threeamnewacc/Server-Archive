package com.massivecraft.factions.cmd.serveradmin;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.Bukkit;

public class CmdForceJoin extends FCommand {

	public CmdForceJoin() {
		super();
		this.aliases.add("forcejoin");

		this.requiredArgs.add("faction name");

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

		// trigger the join event (cancellable)
		FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayers.getInstance().get(me), faction, FPlayerJoinEvent.PlayerJoinReason.COMMAND);
		Bukkit.getServer().getPluginManager().callEvent(joinEvent);
		if (joinEvent.isCancelled()) {
			return;
		}

		fme.msg("<instance>%s successfully joined %s.", fplayer.describeTo(fme, true), faction.getTag(fme));

		faction.msg("<instance>%s joined your faction.", fplayer.describeTo(faction, true));

		fplayer.resetFactionData();
		fplayer.setFaction(faction);
		faction.deinvite(fplayer);

		if (Conf.logFactionJoin) {
			HCFactions.getInstance().log("%s force joined the faction %s.", fplayer.getName(), faction.getTag());
		}
	}
}
