package net.badlion.potpvp.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.bukkitevents.MessageEvent;
import net.badlion.potpvp.events.Event;
import net.badlion.potpvp.events.LastManStanding;
import net.badlion.potpvp.events.Slaughter;
import net.badlion.potpvp.events.UHCMeetup;
import net.badlion.potpvp.managers.MessageManager;
import net.badlion.potpvp.managers.StasisManager;
import net.badlion.potpvp.states.matchmaking.MatchMakingState;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EventTieTask extends BukkitRunnable {

	private Event event;

	public EventTieTask(Event event) {
		this.event = event;
	}
	
	@Override
	public void run() {
		if (this.event.isOver()) return;

		if (this.event instanceof UHCMeetup || this.event instanceof LastManStanding) {
			for (Player player : this.event.getPlayers()) {
				Group group = PotPvP.getInstance().getPlayerGroup(player);
				try {
					GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.getInstance().getCurrentState(group), group);
				} catch (IllegalStateTransitionException e) {
					player.sendMessage(ChatColor.RED + "Internal error, contact an admin.");
					return;
				}

				Gberry.log("EVENT", "Removing player " + player + " from GroupGame");
			}
		} else if (this.event instanceof Slaughter) {
			Player winner = null;
			int winnerKills = 0;

			for (Player player : this.event.getPlayers()) {
				int kills = ((Slaughter) this.event).getKills(player);

				if (winner == null) {
					winner = player;
					winnerKills = kills;
				} else if (kills > winnerKills) {
					winner = player;
					winnerKills = kills;
				}
			}

			String winMessage;
			if (winnerKills > 0) {
				if (winnerKills > 1) {
					winMessage = ChatColor.YELLOW + winner.getName() + ChatColor.GOLD + " has won the Slaughter by reaching "
							+ ChatColor.YELLOW + winnerKills + ChatColor.GOLD + " kills!";
				} else {
					winMessage = ChatColor.YELLOW + winner.getName() + ChatColor.GOLD + " has won the Slaughter by reaching "
							+ ChatColor.YELLOW + "1" + ChatColor.GOLD + " kill!";
				}
			} else {
				winMessage = ChatColor.YELLOW + winner.getName() + ChatColor.GOLD + " has won the Slaughter with "
						+ ChatColor.YELLOW + 0 + ChatColor.GOLD + " kills by being the last player in the Slaughter!";
			}

			// End of game
			for (Player player : this.event.getPlayers()) {
				if (Gberry.isPlayerOnline(player)) {
					player.sendMessage(winMessage);

					StasisManager.addToStasis(PotPvP.getInstance().getPlayerGroup(player), new MatchMakingState.MatchStasisHandler());
				}

				Group group = PotPvP.getInstance().getPlayerGroup(player);
				try {
					GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.getInstance().getCurrentState(group), group);
				} catch (IllegalStateTransitionException e) {
					PotPvP.getInstance().somethingBroke(player, group);
				}

				Gberry.log("EVENT", "Removing player " + player + " from GroupGame");
			}
		}

		MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES,
				ChatColor.GOLD + "Time limit has been reached in the " + this.event.getEventType().getName() + "!",
				null, this.event.getParticipants());
		PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);

		this.event.setEventTimeLimitReached(true);
        this.event.endGame(true);
	}

}
