package net.badlion.potpvp.commands;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.events.Event;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class JoinEventCommand extends GCommandExecutor {

	public JoinEventCommand() {
		super(1); // 1 arg required
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		// Queue them up for that event
		try {
			Event event = Event.getEvent(UUID.fromString(args[0]));
			if (event != null) {
				if (this.currentState != GroupStateMachine.lobbyState) {
					this.player.sendMessage(ChatColor.RED + "You can only join events if you are in spawn, not in a party and not in a queue.");
					return;
				}

				if (event.isStarted()) {
					this.player.sendMessage(ChatColor.RED + "Cannot join this event since it has started already.");
					return;
				}

				if (!event.addToQueue(this.player)) {
					return;
				}

				Gberry.log("EVENT2", this.player.getName() + " joining event " + event.getEventType().getName() + " with kit " + event.getKitRuleSet().getName());

				try {
					GroupStateMachine.lobbyState.transition(GroupStateMachine.matchMakingState, this.group);
				} catch (IllegalStateTransitionException e) {
					PotPvP.getInstance().somethingBroke(this.player, this.group);
				}

				// Try to start the event if we have the right number of players
				event.tryToStart();
			}
		} catch (IllegalArgumentException e) {
			this.player.sendMessage(ChatColor.RED + "Join events through the inventory item!");
			//e.printStackTrace();
		} catch (NullPointerException e) {
			Bukkit.getLogger().info("INFO@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			Bukkit.getLogger().info("UUID: " + args[0] + ", uuid object: " + UUID.fromString(args[0]));
			e.printStackTrace();
		}
	}

	@Override
	public void usage(CommandSender sender) {
		// They ran this command manually
		sender.sendMessage("Unknown command. Type \"/help\" for help.");
	}

}
