package com.massivecraft.factions.cmd.serveradmin.dtr;

import com.massivecraft.factions.cmd.FCommand;

public class CmdDtr extends FCommand {

	public CmdDtr() {
		super();
		this.aliases.add("dtr");
		this.addSubCommand(new CmdDtrSet());
		this.addSubCommand(new CmdDtrFreeze());
		this.addSubCommand(new CmdDtrUnfreeze());
	}

	@Override
	public void perform() {
		msg("<b>/f dtr set <faction> <dtr>");
		msg("<b>/f dtr freeze <faction>");
		msg("<b>/f dtr unfreeze <faction>");
	}
}
