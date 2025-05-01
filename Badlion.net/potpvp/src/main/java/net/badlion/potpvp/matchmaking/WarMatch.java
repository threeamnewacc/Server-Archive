package net.badlion.potpvp.matchmaking;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.bukkitevents.MessageEvent;
import net.badlion.potpvp.events.War;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.MessageManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WarMatch extends Match {

    private War war;

	public WarMatch(Arena arena, KitRuleSet kitRuleSet, War war,
	                ItemStack[] inventoryContents, ItemStack[] armorContents) {
		super(arena, false, kitRuleSet);

		this.war = war;
		this.matchLengthTime = 20; // 20 min
		this.setLadderType(Ladder.LadderType.Duel);

		this.inventoryContents = inventoryContents;
		this.armorContents = armorContents;
	}

    @Override
    public void handleWinnerChat() {
	    MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES,
			    ChatColor.GOLD + this.getWinner().toString() + " have won the War!",
			    null, this.war.getParticipants());
	    PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);
    }

    @Override
    public void handleCommonEnd(String reason) {
        super.handleCommonEnd(reason);

        // Don't leak memory
        this.war.endGame(false);

	    // Disband party and force everyone back into lobby state
	    for (Player pl : this.group1.players()) {
		    Group newGroup = new Group(pl, true);
		    PotPvP.getInstance().updatePlayerGroup(pl, newGroup);

		    PartyHelper.handleLeave(pl, this.group1.getParty(), false);
	    }

	    for (Player pl : this.group2.players()) {
		    Group newGroup = new Group(pl, true);
		    PotPvP.getInstance().updatePlayerGroup(pl, newGroup);

		    PartyHelper.handleLeave(pl, this.group2.getParty(), false);
	    }
    }

	@Override
	public void handleStasis(Group... groups) {
		// Do nothing because right after the match we get rid of the two party groups right after the match ends
	}

}
