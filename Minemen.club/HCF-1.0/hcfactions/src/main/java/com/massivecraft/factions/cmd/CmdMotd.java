package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;

import java.util.stream.Collectors;

public class CmdMotd extends FCommand {

	public CmdMotd() {
		this.aliases.add("motd");

		// this.requiredArgs.add("");
		this.errorOnToManyArgs = false;

		this.permission = Permission.MOTD.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		Faction faction = myFaction;
		if (args.isEmpty()) {
			faction.setMotd("");
			msg(ChatColor.GREEN + "MOTD has been cleared.");
			return;
		}
		String motd = args.stream().collect(Collectors.joining(" "));
		if (!faction.setMotd(motd)) {
			msg(ChatColor.RED + "Failed to set MOTD, the message was too long.");
		} else {
			msg(ChatColor.GOLD + "MOTD: " + ChatColor.GRAY + motd);
		}
	}

}
