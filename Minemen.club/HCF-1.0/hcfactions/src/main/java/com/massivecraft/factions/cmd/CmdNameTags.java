package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.NameTagMode;
import com.massivecraft.factions.struct.Permission;

public class CmdNameTags extends FCommand {

	public CmdNameTags() {
		super();
		this.aliases.add("nametags");

		this.requiredArgs.add("none/relation/random");

		this.permission = Permission.STAFF.node;

		senderMustBePlayer = true;
	}

	@Override
	public void perform() {
		NameTagMode mode;
		try {
			mode = NameTagMode.valueOf(argAsString(0).toUpperCase());
		} catch (IllegalArgumentException ex) {
			msg("Mode must be none/relation/random");
			return;
		}
		fme.setNameTagMode(mode);
	}
}
