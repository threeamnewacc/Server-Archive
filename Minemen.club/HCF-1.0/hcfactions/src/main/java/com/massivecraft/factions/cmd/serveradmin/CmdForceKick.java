package com.massivecraft.factions.cmd.serveradmin;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.FPlayerKickEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import org.bukkit.Bukkit;

public class CmdForceKick extends FCommand {

	public CmdForceKick() {
		super();
		this.aliases.add("forcekick");

		this.requiredArgs.add("player name");
		// this.optionalArgs.put("", "");

		this.permission = Permission.KICK.node;
		this.disableOnLock = false;

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

		if (fme == you) {
			msg("<b>You cannot kick yourself.");
			msg("<instance>You might want to: %s", p.cmdBase.cmdLeave.getUseageTemplate(false));
			return;
		}

		Faction yourFaction = you.getFaction();

		// players with admin-level "disband" permission can bypass these requirements
		if (!Permission.KICK_ANY.has(sender)) {
			if (yourFaction != myFaction) {
				msg("%s<b> is not a member of %s", you.describeTo(fme, true), myFaction.describeTo(fme));
				return;
			}

			if (you.getRole().value >= fme.getRole().value) {
				// TODO add more informative messages.
				msg("<b>Your rank is too low to kick this player.");
				return;
			}

			if (!you.isPvpTagged()) {
				msg("<b>Forcekick can only be used on pvp tagged players.");
				return;
			}
		}

		// trigger the leave event (cancellable) [reason:kicked]
		FPlayerLeaveEvent event = new FPlayerLeaveEvent(you, you.getFaction(), FPlayerLeaveEvent.PlayerLeaveReason.KICKED);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}


		yourFaction.msg("%s<instance> force kicked %s<instance> from the faction! :O", fme.describeTo(yourFaction, true), you.describeTo(yourFaction, true));
		you.msg("%s<instance> force kicked you from %s<instance>! :O", fme.describeTo(you, true), yourFaction.describeTo(you));
		yourFaction.alterDtr(-1);
		yourFaction.setDtrRegenCooldown(System.currentTimeMillis() + Conf.dtrDeathRegenCooldown * 60000);
		if (yourFaction != myFaction) {
			fme.msg("<instance>You force kicked %s<instance> from the faction %s<instance>!", you.describeTo(fme), yourFaction.describeTo(fme));
		}
		yourFaction.msg("<gold>Member force kicked: <red>%s <gold>DTR = %.2f / %.2f", you.getName(), yourFaction.getDtr(), yourFaction.getMaxDtr());
		if (yourFaction.isRaidable()) {
			yourFaction.msg("<red><b>Your faction is RAIDABLE!");
		}

		FPlayerKickEvent kickevent = new FPlayerKickEvent(fme, you, you.getFaction());
		Bukkit.getServer().getPluginManager().callEvent(kickevent);
		if (event.isCancelled()) {
			return;
		}

		if (Conf.logFactionKick) {
			HCFactions.getInstance().log((senderIsConsole ? "A console command" : fme.getName()) + " force kicked " + you.getName() + " from the faction: " + yourFaction.getTag());
		}

		if (you.getRole() == Role.ADMIN) {
			yourFaction.promoteNewLeader();
		}

		yourFaction.deinvite(you);
		you.resetFactionData();
		yourFaction.addLeaveTime();
	}
}
