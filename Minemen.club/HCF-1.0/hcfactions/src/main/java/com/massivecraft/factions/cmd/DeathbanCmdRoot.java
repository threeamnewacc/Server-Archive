package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.deathban.CmdRevive;

public class DeathbanCmdRoot extends FCommand {

	public CmdRevive cmdRevive = new CmdRevive();

	public DeathbanCmdRoot() {
		super();
		this.aliases.add("db");
		this.allowNoSlashAccess = false;

		// this.requiredArgs.add("");
		// this.optionalArgs.put("","")
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;

		this.disableOnLock = false;

		this.setHelpShort("The deathban base command");
		this.helpLong.add(p.txt.parseTags("<instance>This command contains all deathban commands."));

		this.addSubCommand(cmdRevive);
	}

	@Override
	public void perform() {
		msg("/db revive <player name>");
	}
}
