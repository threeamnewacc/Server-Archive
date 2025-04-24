package com.massivecraft.factions.cmd;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CmdUnclaim extends FCommand {

	public CmdUnclaim() {
		this.aliases.add("unclaim");
		this.aliases.add("declaim");

		// this.requiredArgs.add("");
		// this.optionalArgs.put("", "");
		this.permission = Permission.UNCLAIM.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		FLocation flocation = new FLocation(fme);
		Faction otherFaction = Board.getFactionAt(flocation);

		if (otherFaction.isSafeZone()) {
			if (Permission.MANAGE_SAFE_ZONE.has(sender)) {
				Board.removeAt(flocation);
				msg("<instance>Safe zone was unclaimed.");

				if (Conf.logLandUnclaims) {
					HCFactions.getInstance().log(fme.getName() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: " + otherFaction.getTag());
				}
			} else {
				msg("<b>This is a safe zone. You lack permissions to unclaim.");
			}
			return;
		} else if (otherFaction.isWarZone()) {
			if (Permission.MANAGE_WAR_ZONE.has(sender)) {
				Board.removeAt(flocation);
				msg("<instance>War zone was unclaimed.");

				if (Conf.logLandUnclaims) {
					HCFactions.getInstance().log(fme.getName() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: " + otherFaction.getTag());
				}
			} else {
				msg("<b>This is a war zone. You lack permissions to unclaim.");
			}
			return;
		}

		if (fme.isAdminBypassing()) {
			Board.removeAt(flocation);

			otherFaction.msg("%s<instance> unclaimed some of your land.", fme.describeTo(otherFaction, true));
			msg("<instance>You unclaimed this land.");

			if (Conf.logLandUnclaims) {
				HCFactions.getInstance().log(fme.getName() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: " + otherFaction.getTag());
			}

			return;
		}

		if (!assertHasFaction()) {
			return;
		}

		if (!assertMinRole(Role.MODERATOR)) {
			return;
		}

		if (myFaction != otherFaction) {
			msg("<b>You don't own this land.");
			return;
		}

		if (myFaction.isRaidable()) {
			msg("<b>You can't unclaim land while your faction is raidable.");
			return;
		}

		if (wouldSplitClaim(otherFaction, flocation)) {
			msg("<b>You can't unclaim this land as it would split up your land!");
			return;
		}

		LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(flocation, otherFaction, fme);
		Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
		if (unclaimEvent.isCancelled()) {
			return;
		}

		Board.removeAt(flocation);
		myFaction.msg("%s<instance> unclaimed some land.", fme.describeTo(myFaction, true));

		if (Conf.logLandUnclaims) {
			HCFactions.getInstance().log(fme.getName() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: " + otherFaction.getTag());
		}
	}


	private boolean wouldSplitClaim(Faction faction, FLocation claimToRemove) {
		List<FLocation> claims = Board.getFactionClaims(faction.getId());
		List<FLocation> attatchedClaims = new ArrayList<>();
		claims.remove(claimToRemove);
		if (claims.isEmpty() || claims.size() == 1) {
			return false;
		}
		FLocation start = claims.get(0);
		claims.remove(start);
		for (FLocation chunk : getRelativeChunks(claims, start)) {
			claims.remove(chunk);
			attatchedClaims.add(chunk);
		}
		while (!claims.isEmpty()) {
			boolean foundRelative = false;
			ListIterator<FLocation> chunksIter = attatchedClaims.listIterator();
			while (chunksIter.hasNext()) {
				ListIterator<FLocation> adjChunkIter = getRelativeChunks(claims, chunksIter.next()).listIterator();
				while (adjChunkIter.hasNext()) {
					FLocation loc = adjChunkIter.next();
					claims.remove(loc);
					chunksIter.add(loc);
					foundRelative = true;
				}
			}
			if (!foundRelative) {
				return true;
			}
		}

		return false;
	}

	private List<FLocation> getRelativeChunks(List<FLocation> claims, FLocation chunk) {
		List<FLocation> attatchedClaims = new ArrayList<>();
		if (claims.contains(chunk.getRelative(1, 0))) {
			attatchedClaims.add(chunk.getRelative(1, 0));
		}
		if (claims.contains(chunk.getRelative(-1, 0))) {
			attatchedClaims.add(chunk.getRelative(-1, 0));
		}
		if (claims.contains(chunk.getRelative(0, 1))) {
			attatchedClaims.add(chunk.getRelative(0, 1));
		}
		if (claims.contains(chunk.getRelative(0, -1))) {
			attatchedClaims.add(chunk.getRelative(0, -1));
		}
		return attatchedClaims;
	}


}
