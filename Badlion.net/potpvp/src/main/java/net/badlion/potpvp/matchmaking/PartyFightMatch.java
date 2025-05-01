package net.badlion.potpvp.matchmaking;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PartyFightMatch extends Match {

	public PartyFightMatch(Arena arena, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) {
		super(arena, false, kitRuleSet);

		this.armorContents = armorContents;
		this.inventoryContents = inventoryContents;

		this.setLadderType(Ladder.LadderType.Duel);
	}

	@Override
	public void startGame() {
		super.startGame();

		PotPvP.printLagDebug("Party Fight with kit " + this.kitRuleSet.getName() + " has started");
	}

	@Override
	public void handleCommonEnd(String reason) {
		super.handleCommonEnd(reason);

		try {
			// Current state might not exist cuz it was cleaned up already if someone left
			State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(this.group1);
			if (currentState != null) {
				GroupStateMachine.transitionBackToDefaultState(currentState, this.group1);
			}
		} catch (IllegalStateTransitionException e) {
			PotPvP.getInstance().somethingBroke(this.group1.getLeader(), PotPvP.getInstance().getPlayerGroup(this.group1.getLeader()));
		}

		// Move players in second party to first party
		for (Player pl : this.group2.players()) {
			if (pl != this.group2.getLeader()) {
				// Once again create a temp intermediate group
				Group group = new Group(pl, true);

				// Call this after because it calls party.removePlayer()
				PotPvP.getInstance().updatePlayerGroup(pl, group);

				PartyHelper.handleLeave(pl, this.group2.getParty(), false);

				PartyHelper.addToPartyGroup(pl, this.group1.getParty(), false);
			}
		}

		// Once again create a temp intermediate group
		Group group = new Group(this.group2.getLeader(), true);

		// Call this after because it calls party.removePlayer()
		PotPvP.getInstance().updatePlayerGroup(this.group2.getLeader(), group);

		PartyHelper.handleLeave(this.group2.getLeader(), this.group2.getParty(), false);

		PartyHelper.addToPartyGroup(this.group2.getLeader(), this.group1.getParty(), false);

		PotPvP.printLagDebug("Party Fight with kit " + this.kitRuleSet.getName() + " has ended");
	}

	@Override
	public void handleStasis(Group... groups) {
		// Do nothing because right after the match we get rid of the two party groups right after the match ends
	}

}
