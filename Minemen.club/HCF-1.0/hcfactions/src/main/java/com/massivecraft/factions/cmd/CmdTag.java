package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.MiscUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class CmdTag extends FCommand {

	public CmdTag() {
		this.aliases.add("tag");

		this.requiredArgs.add("faction tag");
		// this.optionalArgs.put("", "");

		this.permission = Permission.TAG.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		String tag = this.argAsString(0);

		// TODO does not first test cover selfcase?
		if (Factions.getInstance().isTagTaken(tag) && !MiscUtil.getComparisonString(tag).equals(myFaction.getComparisonTag())) {
			msg("<b>That tag is already taken.");
			return;
		}

		if (!fme.isAdminBypassing() && myFaction.hasRenameCooldown()) {
			msg("<b>Please wait a minute before renaming your faction.");
			return;
		}

		ArrayList<String> errors = new ArrayList<String>();
		errors.addAll(Factions.validateTag(tag));
		if (errors.size() > 0) {
			sendMessage(errors);
			return;
		}

		// trigger the faction rename event (cancellable)
		FactionRenameEvent renameEvent = new FactionRenameEvent(fme, tag);
		Bukkit.getServer().getPluginManager().callEvent(renameEvent);
		if (renameEvent.isCancelled()) {
			return;
		}

		String oldtag = myFaction.getTag();
		myFaction.setTag(tag);

		// Inform
		myFaction.msg("%s<instance> changed your faction tag to %s", fme.describeTo(myFaction, true), myFaction.getTag(myFaction));
		for (Faction faction : Factions.getInstance().getAll()) {
			if (faction == myFaction) {
				continue;
			}
			faction.msg("<instance>The faction %s<instance> changed their name to %s.", fme.getColorTo(faction) + oldtag, myFaction.getTag(faction));
		}
		myFaction.addRenameCooldown(30);
	}

}
