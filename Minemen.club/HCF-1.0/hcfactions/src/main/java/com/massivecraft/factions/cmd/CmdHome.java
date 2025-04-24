package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.type.HomeTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class CmdHome extends FCommand {

	public CmdHome() {
		super();
		this.aliases.add("home");

		// this.requiredArgs.add("");
		// this.optionalArgs.put("", "");
		this.permission = Permission.HOME.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		// TODO: Hide this command on help also.
		if (!Conf.homesEnabled) {
			fme.msg("<b>Sorry, Faction homes are disabled on this server.");
			return;
		}

		if (!Conf.homesTeleportCommandEnabled) {
			fme.msg("<b>Sorry, the ability to teleport to Faction homes is disabled on this server.");
			return;
		}

		if (!myFaction.hasHome()) {
			fme.msg("<b>Your faction does not have a home. Tell an officer to use /f sethome");
			return;
		}

		if (fme.hasPvpProtection()) {
			fme.msg("<b>You cannot teleport to your faction home while PvP protected. Please use <instance>/pvp enable <b>if you wish to enable PvP.");
			return;
		}

		if (fme.isPvpTagged()) {
			fme.msg("<b>You cannot teleport to your faction home while PvP tagged. Please wait at least <instance>%d seconds<b>.", fme.getPvpTagRemaining() / 1000);
			return;
		}

		if (!Conf.homesTeleportAllowedFromDifferentWorld && me.getWorld().getUID() != myFaction.getHome().getWorld().getUID()) {
			fme.msg("<b>You cannot teleport to your faction home while in a different world.");
			return;
		}

		Faction faction = Board.getFactionAt(new FLocation(me.getLocation()));
		Location loc = me.getLocation().clone();

		if (!faction.isSafeZone()) {
			if (p.getHomeTasks().containsKey(fme.getPlayer())) {
				fme.msg("You are already waiting to teleport home!");
				return;
			}
			p.getHomeTasks().put(fme.getPlayer(), new HomeTask(p, fme.getPlayer()));
		} else {
			myFaction.getHome().getChunk().load();
			me.teleport(myFaction.getHome());
			HomeTask.doEffect(myFaction.getHome().clone().add(0, 1, 0));
			me.sendMessage(ChatColor.GREEN + "Teleported home.");
		}
	}
}
