package com.massivecraft.factions.cmd.serveradmin.dtr;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import org.apache.commons.lang.time.DurationFormatUtils;

public class CmdDtrFreeze extends FCommand {

	public CmdDtrFreeze() {
		this.aliases.add("freeze");
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
		faction.setDtrRegenCooldown(System.currentTimeMillis() + Conf.dtrDeathRegenCooldown * 60000);
		long dtrFreeze = faction.getDtrRegenCooldown() - System.currentTimeMillis();
		String freezeStr = DurationFormatUtils.formatDurationWords(dtrFreeze, true, true);
		faction.msg("<a>%s<instance> set your DTR freeze time to <a>%s<instance>.", sender.getName(), freezeStr);
		msg("<a>%s<instance>'s DTR freeze time is now <a>%s<instance>.", faction.getTag(), freezeStr);
	}
}
