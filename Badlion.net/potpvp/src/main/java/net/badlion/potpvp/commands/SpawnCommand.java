package net.badlion.potpvp.commands;

import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.managers.StasisManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SpawnCommand extends GCommandExecutor {

    public SpawnCommand() {
        super(0); // 0 arg minimum
    }

    @Override
    public void onGroupCommand(Command command, String label, String[] args) {
        if (this.group.isParty()) {
            this.player.sendMessage(ChatColor.RED + "Cannot use /spawn in parties.");
            return;
        }

        // If they are in matchmaking and they are in a sub-state
        State<Group> state = GroupStateMachine.getInstance().getCurrentState(this.group);
        if (GroupStateMachine.matchMakingState.contains(this.group) && state != GroupStateMachine.matchMakingState) {
            Game game = GameState.getGroupGame(this.group);
            if (game != null) {
	            // Check to make sure that they're not in stasis
	            if (StasisManager.isInStasis(this.group)) {
		            return;
	            }

	            if (!game.handleQuit(this.player, "spawn")) {
		            return;
	            }
            }

            try {
                GroupStateMachine.transitionBackToDefaultState(state, this.group);
            } catch (IllegalStateTransitionException exception) {
                this.group.sendLeaderMessage(ChatColor.RED + "You cannot /spawn at the moment because " + state.description());
                this.group.sendLeaderMessage(ChatColor.RED + "If this is an error contact an admin (i don't think this should happen -Gberry)");
            }
        } else if (GroupStateMachine.kitCreationState.contains(this.group)) {
            try {
                GroupStateMachine.transitionBackToDefaultState(state, this.group);
            } catch (IllegalStateTransitionException exception) {
                this.group.sendLeaderMessage(ChatColor.RED + "You cannot /spawn at the moment because " + state.description());
                this.group.sendLeaderMessage(ChatColor.RED + "If this is an error contact an admin (i don't think this should happen -Gberry)");
            }
        } else {
            this.player.sendMessage(ChatColor.RED + "You can only use /spawn when in a game. Use /leave to leave a queue.");
        }
    }

    @Override
    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command usage: /spawn");
    }

}
