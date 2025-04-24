package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdMod extends FCommand {

	public CmdMod() {
		super();
		this.aliases.add("officer");
		this.aliases.add("mod");

		this.requiredArgs.add("player name");
		// this.optionalArgs.put("", "");

		this.permission = Permission.MOD.node;
		this.disableOnLock = true;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		FactionPlayer you = this.argAsFPlayer(0);
		if (you == null) {
			return;
		}

		boolean permAny = Permission.MOD_ANY.has(sender, false);
		Faction targetFaction = you.getFaction();

		if (targetFaction != myFaction && !permAny) {
			msg("%s<b> is not a member in your faction.", you.describeTo(fme, true));
			return;
		}

		if (fme != null && fme.getRole() != Role.ADMIN && !permAny) {
			msg("<b>You are not the faction leader.");
			return;
		}

		if (you == fme && !permAny) {
			msg("<b>The target player musn't be yourself.");
			return;
		}

		if (you.getRole() == Role.ADMIN) {
			msg("<b>The target player is a faction leader. Demote them first.");
			return;
		}

		if (you.getRole() == Role.MODERATOR) {
			// Revoke
			you.setRole(Role.NORMAL);
			targetFaction.msg("%s<instance> is no longer officer in your faction.", you.describeTo(targetFaction, true));
			msg("<instance>You have removed officer status from %s<instance>.", you.describeTo(fme, true));
		} else {
			// Give
			you.setRole(Role.MODERATOR);
			targetFaction.msg("%s<instance> was promoted to officer in your faction.", you.describeTo(targetFaction, true));
			msg("<instance>You have promoted %s<instance> to officer.", you.describeTo(fme, true));
		}
	}

}
