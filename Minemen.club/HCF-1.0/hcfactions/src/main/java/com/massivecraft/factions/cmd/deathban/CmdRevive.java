package com.massivecraft.factions.cmd.deathban;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;

public class CmdRevive extends FCommand {

	public CmdRevive() {
		super();
		this.aliases.add("revive");
		this.requiredArgs.add("player name");

		this.permission = Permission.REVIVE.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		FactionPlayer target = argAsFPlayer(0);
		if (!this.p.getDeathbanManager().isDeathBanned(target.getUuid())) {
			sender.sendMessage("That player is not deathbanned");
			return;
		} else {
			this.p.getDeathbanManager().unDeathbanPlayer(target.getUuid());
			sender.sendMessage("Removed " + target.getName() + "'s deathban!");
		}
	}
}
