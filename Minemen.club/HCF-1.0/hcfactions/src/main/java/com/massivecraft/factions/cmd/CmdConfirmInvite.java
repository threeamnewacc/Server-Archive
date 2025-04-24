package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;

public class CmdConfirmInvite extends FCommand {

	public CmdConfirmInvite() {
		super();
		this.aliases.add("confirminvite");

		this.requiredArgs.add("player name");
		// this.optionalArgs.put("", "");

		this.permission = Permission.INVITE.node;
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

		if (you.getPlayer() == null || !you.getPlayer().isOnline()) {
			msg(ChatColor.RED + "Player Not Found.");
			return;
		}

		if (you.getFaction() == myFaction) {
			msg("%s<instance> is already a member of %s", you.getName(), myFaction.getTag());
			msg("<instance>You might want to: " + p.cmdBase.cmdKick.getUseageTemplate(false));
			return;
		}

		myFaction.invite(you);

		you.msg("%s<instance> invited you to %s", fme.describeTo(you, true), myFaction.describeTo(you));
		myFaction.msg("%s<instance> invited %s<instance> to your faction.", fme.describeTo(myFaction, true), you.describeTo(myFaction));
	}

}
