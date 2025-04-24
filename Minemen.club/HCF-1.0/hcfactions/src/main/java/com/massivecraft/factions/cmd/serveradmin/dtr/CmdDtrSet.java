package com.massivecraft.factions.cmd.serveradmin.dtr;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;

public class CmdDtrSet extends FCommand {

	public CmdDtrSet() {
		this.aliases.add("set");
		this.requiredArgs.add("faction name");
		this.requiredArgs.add("dtr");
		this.permission = Permission.SET_DTR.node;
	}

	@Override
	public void perform() {
		String tag = this.argAsString(0);
		double dtr = this.argAsDouble(1);
		Faction faction = Factions.getInstance().getByTag(tag);
		if (faction == null || !faction.isNormal()) {
			msg("<b>Faction not found. (You must use faction names not player names)");
			return;
		}
		faction.setDtr(dtr);
		faction.msg("<a>%s<instance> set your DTR to <a>%.2f<instance>.", sender.getName(), faction.getDtr());
		msg("<a>%s<instance>'s DTR is now <a>%.2f<instance>.", faction.getTag(), faction.getDtr());
	}
}
