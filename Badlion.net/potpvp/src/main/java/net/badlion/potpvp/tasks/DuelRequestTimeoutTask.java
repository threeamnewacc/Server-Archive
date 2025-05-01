package net.badlion.potpvp.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.helpers.DuelHelper;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class DuelRequestTimeoutTask extends BukkitRunnable {

	private Group group;
	private DuelHelper.DuelCreator duelCreator;

	public DuelRequestTimeoutTask(DuelHelper.DuelCreator duelCreator) {
		this.group = duelCreator.getReceiver();
		this.duelCreator = duelCreator;
	}

	@Override
	public void run() {
		Gberry.log("DUEL", "Duel request TIMED OUT");
		DuelHelper.DuelCreator duelCreator = GroupStateMachine.duelRequestState.getDuelCreator(this.group);

		// Is the duel request still active?
		if (duelCreator != null && duelCreator == this.duelCreator) {
			// Don't do this is selecting custom rule sets
			if (!duelCreator.isSelectingCustomKits()) {
				// Send messages
				duelCreator.getSender().sendMessage(ChatColor.RED + "Duel request to "
						+ duelCreator.getReceiver().getLeader().getName() + " has timed out.");
				duelCreator.getReceiver().sendMessage(ChatColor.RED + "The duel request has timed out.");

				DuelHelper.handleDuelDeny(false, this.group.getLeader(), this.group);
			}
		}
	}

}
