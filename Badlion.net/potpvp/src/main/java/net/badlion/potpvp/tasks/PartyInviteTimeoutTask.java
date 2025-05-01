package net.badlion.potpvp.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.Party;
import net.badlion.potpvp.PotPvP;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PartyInviteTimeoutTask extends BukkitRunnable {
	
	private Player player;
	private Party party;
	
	public PartyInviteTimeoutTask(Player player, Party party) {
		this.party = party;
		this.player = player;
	}
	
	@Override
	public void run() {
		GroupStateMachine.partyRequestState.removePartyInvite(this.player);
		GroupStateMachine.partyRequestState.removeInvitingPlayer(this.party.getPartyLeader());

		// Could have logged off
		if (Gberry.isPlayerOnline(this.player)) {
			Group group = PotPvP.getInstance().getPlayerGroup(this.player);

			if (group != null) {
				try {
					GroupStateMachine.partyRequestState.transition(GroupStateMachine.lobbyState, group);
				} catch (IllegalStateTransitionException e) {
					e.printStackTrace();
				}
			}

			BukkitUtil.closeInventory(this.player);
		}

		this.party.getPartyLeader().sendMessage(ChatColor.BLUE + "Party invite to " + this.player.getName() + " timed out.");
		this.player.sendMessage(ChatColor.BLUE + "Party invite timed out.");
	}

	public Player getPlayer() {
		return player;
	}

	public Party getParty() {
		return party;
	}

}