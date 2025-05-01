package net.badlion.arenalobby.tasks;

import net.badlion.arenalobby.helpers.DuelHelper;
import net.badlion.arenalobby.managers.DuelRequestManager;
import net.badlion.gberry.Gberry;
import org.bukkit.scheduler.BukkitRunnable;

public class DuelRequestTimeoutTask extends BukkitRunnable {

	private DuelHelper.DuelCreator duelCreator;

	public DuelRequestTimeoutTask(DuelHelper.DuelCreator duelCreator) {
		this.duelCreator = duelCreator;
	}

	@Override
	public void run() {
		Gberry.log("DUEL", "Duel request TIMED OUT");
		DuelHelper.DuelCreator duelCreator = DuelRequestManager.getDuelCreator(this.duelCreator.getSenderId());

		// Is the duel request still active?
		if (duelCreator != null && duelCreator == this.duelCreator) {
			// Don't do this is selecting custom rule sets
			// Send messages
	        /*
			if(duelCreator.getSender() != null) {
				duelCreator.getSender().sendMessage(ChatColor.RED + "Duel request to "
						+ duelCreator.getReceiverName() + " has timed out.");
			}
			if(duelCreator.getReceiverIfOnline() != null) {
				duelCreator.getReceiverIfOnline().sendMessage(ChatColor.RED + "The duel request has timed out.");
			}*/
			DuelHelper.handleDuelDeny(false, this.duelCreator.getReceiverId(), this.duelCreator.getSenderId());

		}
	}

}
