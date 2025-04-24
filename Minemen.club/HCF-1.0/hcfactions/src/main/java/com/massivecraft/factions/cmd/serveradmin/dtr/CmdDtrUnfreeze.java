package com.massivecraft.factions.cmd.serveradmin.dtr;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;

public class CmdDtrUnfreeze extends FCommand {

	public CmdDtrUnfreeze() {
		this.aliases.add("unfreeze");
		this.requiredArgs.add("faction name");
		this.permission = Permission.SET_DTR.node;
	}

	@Override
	public void perform() {
		String tag = this.argAsString(0);
		Faction faction = Factions.getInstance().getByTag(tag);
		if (faction == null || !faction.isNormal()) {
			msg("<b>Faction not found. (You must use faction names not player names)");
			return;
		}
		faction.setDtrRegenCooldown(System.currentTimeMillis());
		faction.msg("<a>%s<instance> unfroze your DTR.", sender.getName());
		msg("<a>%s<instance>'s DTR is no longer frozen.", faction.getTag());
	}
}
