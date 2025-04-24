package com.massivecraft.factions.cmd;

import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.event.FactionPostCreateEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class CmdCreate extends FCommand {

	public CmdCreate() {
		super();
		this.aliases.add("create");

		this.requiredArgs.add("faction tag");
		// this.optionalArgs.put("", "");

		this.permission = Permission.CREATE.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		String tag = this.argAsString(0);

		if (fme.hasFaction()) {
			msg("<b>You must leave your current faction first.");
			return;
		}

		if (Factions.getInstance().isTagTaken(tag)) {
			msg("<b>That tag is already in use.");
			return;
		}

		ArrayList<String> tagValidationErrors = Factions.validateTag(tag);
		if (tagValidationErrors.size() > 0) {
			sendMessage(tagValidationErrors);
			return;
		}

		// trigger the faction creation event (cancellable)
		FactionCreateEvent createEvent = new FactionCreateEvent(me, tag);
		Bukkit.getServer().getPluginManager().callEvent(createEvent);
		if (createEvent.isCancelled()) {
			return;
		}

		Faction faction = Factions.getInstance().create();

		// TODO: Why would this even happen??? Auto increment clash??
		if (faction == null) {
			msg("<b>There was an internal error while trying to create your faction. Please try again.");
			return;
		}

		// finish setting up the Faction
		faction.setTag(tag);

		// trigger the faction join event for the creator
		FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayers.getInstance().get(me), faction, FPlayerJoinEvent.PlayerJoinReason.CREATE);
		Bukkit.getServer().getPluginManager().callEvent(joinEvent);
		// join event cannot be cancelled or you'll have an empty faction

		// finish setting up the FactionPlayer
		fme.setRole(Role.ADMIN);
		fme.setFaction(faction);
		
		fme.getPlayer().sendFormattedMessage("{0}You''ve created the faction {1}{2}{0}.", CC.PRIMARY, CC.SECONDARY, faction.getTag());

		if (Conf.logFactionCreate) {
			HCFactions.getInstance().log(fme.getName() + " created a new faction: " + tag);
		}

		FactionPostCreateEvent postCreateEvent = new FactionPostCreateEvent(me, faction);
		Bukkit.getServer().getPluginManager().callEvent(postCreateEvent);

		faction.addRenameCooldown(30);
	}

}
