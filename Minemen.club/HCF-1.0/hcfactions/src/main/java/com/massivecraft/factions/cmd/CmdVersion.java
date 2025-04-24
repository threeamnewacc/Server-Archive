package com.massivecraft.factions.cmd;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.struct.Permission;

public class CmdVersion extends FCommand {

	public CmdVersion() {
		this.aliases.add("version");

		// this.requiredArgs.add("");
		// this.optionalArgs.put("", "");
		this.permission = Permission.VERSION.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		msg("<instance>You are running " + HCFactions.getInstance().getDescription().getFullName());
	}
}
