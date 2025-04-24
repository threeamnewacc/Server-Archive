package com.massivecraft.factions.cmd.serveradmin;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;

public class CmdChatSpy extends FCommand {

	public CmdChatSpy() {
		super();
		this.aliases.add("chatspy");

		this.optionalArgs.put("on/off", "flip");

		this.permission = Permission.CHATSPY.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		fme.setSpyingChat(this.argAsBool(0, !fme.isSpyingChat()));

		if (fme.isSpyingChat()) {
			fme.msg("<instance>You have enabled chat spying mode.");
			HCFactions.getInstance().log(fme.getName() + " has ENABLED chat spying mode.");
		} else {
			fme.msg("<instance>You have disabled chat spying mode.");
			HCFactions.getInstance().log(fme.getName() + " DISABLED chat spying mode.");
		}
	}
}
