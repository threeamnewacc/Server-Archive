package com.massivecraft.factions.cmd.serveradmin;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;

public class CmdEvent extends FCommand {

	public CmdEvent() {
		super();
		this.aliases.add("event");

		this.requiredArgs.add("faction tag");

		this.permission = Permission.SET_PERMANENT.node;
		this.disableOnLock = true;

		senderMustBePlayer = false;
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

		if (faction.isEvent()) {
			faction.setEvent(false);
			msg("<instance>" + faction.getTag() + " is no longer an event faction");
		} else {
			faction.setEvent(true);
			msg("<instance>" + faction.getTag() + " is now an event faction");
		}
	}

}
