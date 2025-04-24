package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public abstract class FRelationCommand extends FCommand {

	public Relation targetRelation;

	public FRelationCommand() {
		super();
		this.requiredArgs.add("faction tag");
		// this.optionalArgs.put("player name", "you");

		this.permission = Permission.RELATION.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		Faction them = this.argAsFaction(0);
		if (them == null) {
			return;
		}

		if (!them.isNormal()) {
			msg("<b>Error: " + them.getTag() + " is a system faction");
			return;
		}

		if (them == myFaction) {
			msg("<b>Nope! You can't declare a relation to yourself :)");
			return;
		}

		if (myFaction.getRelationWish(them) == targetRelation) {
			msg("<b>You already have that relation wish set with %s.", them.getTag());
			return;
		}

		// try to set the new relation
		Relation oldRelation = myFaction.getRelationTo(them, true);
		if (targetRelation.equals(Relation.ALLY)) {
			int allys = 0;
			for (Faction otherFaction : Factions.getInstance().getAll()) {
				if (myFaction.getRelationWish(otherFaction).equals(Relation.ALLY) && otherFaction.getRelationWish(myFaction).equals(Relation.ALLY)) {
					allys++;
				}
			}
			if (allys >= Conf.allyLimit) {
				msg("<b>Your faction has reached the ally limit!");
				return;
			}
			allys = 0;
			for (Faction otherFaction : Factions.getInstance().getAll()) {
				if (them.getRelationWish(otherFaction).equals(Relation.ALLY) && otherFaction.getRelationWish(them).equals(Relation.ALLY)) {
					allys++;
				}
			}
			if (allys >= Conf.allyLimit) {
				msg("<b>%s has reached the ally limit!", them.getTag());
				return;
			}
			if (myFaction.getAllyCooldownRemaining() > 0) {
				msg("<b>Your faction is on ally cooldown (%s).",
						DurationFormatUtils.formatDurationWords(myFaction.getAllyCooldownRemaining(), true, true));
				return;
			}
			if (them.getAllyCooldownRemaining() > 0) {
				msg("<b>%s is on ally cooldown (%s).",
						them.getTag(),
						DurationFormatUtils.formatDurationWords(them.getAllyCooldownRemaining(), true, true));
				return;
			}
		}
		myFaction.setRelationWish(them, targetRelation);
		// if becoming neutral, set them both to neutral wish
		if (targetRelation == Relation.NEUTRAL) {
			them.setRelationWish(myFaction, targetRelation);
		}
		Relation currentRelation = myFaction.getRelationTo(them, true);
		ChatColor currentRelationColor = currentRelation.getColor();

		// if the relation change was successful
		if (targetRelation.value == currentRelation.value) {
			// trigger the faction relation event
			FactionRelationEvent relationEvent = new FactionRelationEvent(myFaction, them, oldRelation, currentRelation);
			Bukkit.getServer().getPluginManager().callEvent(relationEvent);

			them.msg("<instance>Your faction is now " + currentRelationColor + targetRelation.toString() + "<instance> to " + currentRelationColor + myFaction.getTag());
			myFaction.msg("<instance>Your faction is now " + currentRelationColor + targetRelation.toString() + "<instance> to " + currentRelationColor + them.getTag());
			if (targetRelation == Relation.ALLY) {
				myFaction.setlastAllyTime();
				them.setlastAllyTime();
			}
		} // inform the other faction of your request
		else {
			them.msg(currentRelationColor + myFaction.getTag() + "<instance> wishes to be your " + targetRelation.getColor() + targetRelation.toString());
			them.msg("<instance>Type <c>/f " + targetRelation + " " + myFaction.getTag() + "<instance> to accept.");
			myFaction.msg(currentRelationColor + them.getTag() + "<instance> were informed that you wish to be " + targetRelation.getColor() + targetRelation);
		}

		if (!targetRelation.isNeutral() && them.isPeaceful()) {
			them.msg("<instance>This will have no effect while your faction is peaceful.");
			myFaction.msg("<instance>This will have no effect while their faction is peaceful.");
		}

		if (!targetRelation.isNeutral() && myFaction.isPeaceful()) {
			them.msg("<instance>This will have no effect while their faction is peaceful.");
			myFaction.msg("<instance>This will have no effect while your faction is peaceful.");
		}
	}
}
