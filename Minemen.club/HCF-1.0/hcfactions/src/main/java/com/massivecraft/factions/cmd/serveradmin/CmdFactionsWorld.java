package com.massivecraft.factions.cmd.serveradmin;

import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;

public class CmdFactionsWorld extends FCommand {
	public CmdFactionsWorld() {
		super();
		this.aliases.add("factionsworld");

		this.permission = Permission.BYPASS.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}


	@Override
	public void perform() {
		//fme.getPlayer().teleport(new Location(Bukkit.getWorld("factions_world"), 0, 120, 0));
	}
}
