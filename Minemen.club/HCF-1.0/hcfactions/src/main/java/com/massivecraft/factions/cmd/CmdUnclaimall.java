package com.massivecraft.factions.cmd;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.Bukkit;

public class CmdUnclaimall extends FCommand {

	public CmdUnclaimall() {
		this.aliases.add("unclaimall");
		this.aliases.add("declaimall");

		// this.requiredArgs.add("");
		// this.optionalArgs.put("", "");
		this.permission = Permission.UNCLAIM_ALL.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		if (myFaction.isRaidable()) {
			msg("<b>You can't unclaim land while your faction is raidable.");
			return;
		}

		LandUnclaimAllEvent unclaimAllEvent = new LandUnclaimAllEvent(myFaction, fme);
		Bukkit.getServer().getPluginManager().callEvent(unclaimAllEvent);
		// this event cannot be cancelled

		Board.unclaimAll(myFaction.getId());
		myFaction.msg("%s<instance> unclaimed ALL of your faction's land.", fme.describeTo(myFaction, true));

		if (Conf.logLandUnclaims) {
			HCFactions.getInstance().log(fme.getName() + " unclaimed everything for the faction: " + myFaction.getTag());
		}
	}

}
