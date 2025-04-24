package com.massivecraft.factions.cmd.serveradmin;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;

public class CmdSystem extends FCommand {

	public CmdSystem() {
		super();
		this.aliases.add("system");

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

		if (faction.isSystem()) {
			faction.setSystem(false);
			msg("<instance>" + faction.getTag() + " is no longer a system faction");
		} else {
			faction.setSystem(true);
			msg("<instance>" + faction.getTag() + " is now a system faction");
		}
	}

}
