package com.massivecraft.factions.cmd;


import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.type.StuckTask;

public class CmdStuck extends FCommand {

	public CmdStuck() {
		this.aliases.add("stuck");

		this.permission = Permission.STUCK.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		Faction faction = Board.getFactionAt(new FLocation(fme.getPlayer().getLocation()));
		if (faction.isNone() || faction.isWarZone() || faction.isSafeZone() || faction.isSystem()) {
			msg("<red>You can only use this command in another factions claims.");
			return;
		}
		if (fme.hasFaction()) {
			if (fme.getFaction().isNormal()) {
				if (fme.getFaction().hasHome()) {
					msg("<red>Your faction has a home, please use /f home instead of /f stuck.");
				}
				if (fme.getFaction().equals(faction)) {
					msg("<red>You can not use this in your own claim.");
					return;
				}
			}
		}
		FLocation fLocation = Board.getClosestNonClaimed(new FLocation(fme.getPlayer().getLocation()));
		if (fLocation == null) {
			fme.msg("<red>Unable to find a safe spot to teleport you to.");
			return;
		}
		if (p.getStuckTasks().containsKey(fme.getPlayer())) {
			fme.msg("You are already waiting to teleport out!");
			return;
		}
		p.getStuckTasks().put(fme.getPlayer(), new StuckTask(p, fme.getPlayer()));
	}

}
