package com.massivecraft.factions.cmd.serveradmin;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;

public class CmdReload extends FCommand {

	public CmdReload() {
		super();
		this.aliases.add("reload");

		// this.requiredArgs.add("");
		this.optionalArgs.put("file", "all");

		this.permission = Permission.RELOAD.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		long timeInitStart = System.currentTimeMillis();
		String file = this.argAsString(0, "all").toLowerCase();

		String fileName;

		if (file.startsWith("c")) {
			Conf.load();
			fileName = "conf.json";
		} else if (file.startsWith("b")) {
			Board.load();
			fileName = "board.json";
		} else if (file.startsWith("f")) {
			Factions.getInstance().loadFromDisc();
			fileName = "factions.json";
		} else if (file.startsWith("plugin")) {
			FPlayers.getInstance().loadFromDisc();
			fileName = "players.json";
		} else if (file.startsWith("a")) {
			fileName = "all";
			Conf.load();
			FPlayers.getInstance().loadFromDisc();
			Factions.getInstance().loadFromDisc();
			Board.load();
		} else {
			HCFactions.getInstance().log("RELOAD CANCELLED - SPECIFIED FILE INVALID");
			msg("<b>Invalid file specified. <instance>Valid files: all, conf, board, factions, players");
			return;
		}

		long timeReload = (System.currentTimeMillis() - timeInitStart);

		msg("<instance>Reloaded <h>%s <instance>from disk, took <h>%dms<instance>.", fileName, timeReload);
	}

}
