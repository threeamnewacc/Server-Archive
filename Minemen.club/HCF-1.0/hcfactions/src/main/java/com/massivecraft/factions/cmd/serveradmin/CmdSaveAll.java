package com.massivecraft.factions.cmd.serveradmin;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;

public class CmdSaveAll extends FCommand {

	public CmdSaveAll() {
		super();
		this.aliases.add("saveall");
		this.aliases.add("save");

		// this.requiredArgs.add("");
		// this.optionalArgs.put("", "");
		this.permission = Permission.SAVE.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		long start = System.currentTimeMillis();
		FPlayers.getInstance().saveToDisc();
		Factions.getInstance().saveToDisc();
		Board.save();
		Conf.save();
		msg("<instance>Factions saved to disk in %dms", System.currentTimeMillis() - start);
	}

}
