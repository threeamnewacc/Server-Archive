package com.massivecraft.factions.cmd.serveradmin;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;

public class CmdToggleEnd extends FCommand {

	public CmdToggleEnd() {
		super();
		this.aliases.add("toggleend");


		this.permission = Permission.BYPASS.node;
		this.disableOnLock = true;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		HCFactions.getInstance().setEndEnabled(!HCFactions.getInstance().isEndEnabled());
		msg(ChatColor.GOLD + "End is now " + (HCFactions.getInstance().isEndEnabled() ? ChatColor.GREEN + "Enabled!" : ChatColor.RED + "Disabled!"));
	}
}
