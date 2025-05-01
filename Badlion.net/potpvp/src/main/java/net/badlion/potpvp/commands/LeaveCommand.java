package net.badlion.potpvp.commands;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.events.Event;
import net.badlion.potpvp.managers.MatchMakingManager;
import net.badlion.potpvp.states.party.PartyState;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LeaveCommand extends GCommandExecutor {

    public LeaveCommand() {
        super(0); // 0 arg minimum
    }

    @Override
    public void onGroupCommand(Command command, String label, String[] args) {
        if (this.group.getLeader() == this.player) {
            State<Group> state = GroupStateMachine.getInstance().getCurrentState(this.group);
            if ((GroupStateMachine.matchMakingState.contains(this.group) && state == GroupStateMachine.matchMakingState)
                 || PartyState.wantsAPartner == this.player) {
                try {
                    GroupStateMachine.transitionBackToDefaultState(state, this.group);
                } catch (IllegalStateTransitionException exception) {
                    this.group.sendLeaderMessage(ChatColor.RED + "You cannot /leave at the moment because " + state.description());
                    return;
                }

                // Reset Party random
                if (PartyState.wantsAPartner == this.player) {
                    PartyState.wantsAPartner = null;
                }

                // Try to leave matchmaking
                MatchMakingManager.removeFromMatchMaking(this.group);

                // Try to leave event queues (parties can't queue up for events?)
                for (Event gameEvent : Event.getEvents().values()) {
                    Gberry.log("EVENT2", "Checking event " + gameEvent.getInfo());
                    if (!gameEvent.isStarted()) {
                        Gberry.log("EVENT2", "Trying to remove player " + player.getName() + " from " + gameEvent.getInfo());
                        if (gameEvent.removeFromQueue(this.player)) {
                            Gberry.log("EVENT2", "Successfully removed " + this.player.getName());
                        }
                    }
                }

                this.player.sendMessage(ChatColor.RED + "Left matchmaking");
            } else {
                this.player.sendMessage(ChatColor.RED + "Can only use /leave when in queue.");
            }
        } else {
            this.player.sendMessage(ChatColor.RED + "Only the leader can perform /leave.");
        }
    }

    @Override
    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command usage: /leave");
    }

}
