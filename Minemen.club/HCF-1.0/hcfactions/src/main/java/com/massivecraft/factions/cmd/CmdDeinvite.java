package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.struct.Permission;

public class CmdDeinvite extends FCommand {

	public CmdDeinvite() {
		super();
		this.aliases.add("deinvite");
		this.aliases.add("deinv");

		this.requiredArgs.add("player name");
		// this.optionalArgs.put("", "");

		this.permission = Permission.DEINVITE.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		FactionPlayer you = this.argAsFPlayer(0);
		if (you == null) {
			return;
		}

		if (you.getFaction() == myFaction) {
			msg("%s<instance> is already a member of %s", you.getName(), myFaction.getTag());
			msg("<instance>You might want to: %s", p.cmdBase.cmdKick.getUseageTemplate(false));
			return;
		}

		myFaction.deinvite(you);

		you.msg("%s<instance> revoked your invitation to <h>%s<instance>.", fme.describeTo(you), myFaction.describeTo(you));

		myFaction.msg("%s<instance> revoked %s's<instance> invitation.", fme.describeTo(myFaction), you.describeTo(myFaction));
	}

}
