package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import org.bukkit.Bukkit;

public class CmdAdmin extends FCommand {

	public CmdAdmin() {
		super();
		this.aliases.add("leader");
		this.aliases.add("admin");

		this.requiredArgs.add("player name");
		// this.optionalArgs.put("", "");

		this.permission = Permission.ADMIN.node;
		this.disableOnLock = true;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		FactionPlayer fyou = this.argAsFPlayer(0);
		if (fyou == null) {
			return;
		}

		boolean permAny = Permission.ADMIN_ANY.has(sender, false);
		Faction targetFaction = fyou.getFaction();

		if (targetFaction != myFaction && !permAny) {
			msg("%s<instance> is not a member in your faction.", fyou.describeTo(fme, true));
			return;
		}

		if (fme != null && fme.getRole() != Role.ADMIN && !permAny) {
			msg("<b>You are not the faction leader.");
			return;
		}

		if (fyou == fme && !permAny) {
			msg("<b>The target player musn't be yourself.");
			return;
		}

		// only perform a FPlayerJoinEvent when newLeader isn't actually in the faction
		if (fyou.getFaction() != targetFaction) {
			FPlayerJoinEvent event = new FPlayerJoinEvent(FPlayers.getInstance().get(me), targetFaction, FPlayerJoinEvent.PlayerJoinReason.LEADER);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return;
			}
		}

		FactionPlayer admin = targetFaction.getFPlayerAdmin();

		// if target player is currently admin, demote and replace him
		if (fyou == admin) {
			targetFaction.promoteNewLeader();
			msg("<instance>You have demoted %s<instance> from the position of faction leader.", fyou.describeTo(fme, true));
			fyou.msg("<instance>You have been demoted from the position of faction leader by %s<instance>.", senderIsConsole ? "a server admin" : fme.describeTo(fyou, true));
			return;
		}

		// promote target player, and demote existing admin if one exists
		if (admin != null) {
			admin.setRole(Role.MODERATOR);
		}
		fyou.setRole(Role.ADMIN);
		msg("<instance>You have promoted %s<instance> to the position of faction leader.", fyou.describeTo(fme, true));
	}

}
