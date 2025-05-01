package net.badlion.potpvp.commands;

import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.states.spectator.FollowState;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnfollowCommand extends GCommandExecutor {

    public UnfollowCommand() {
        super(0); // No args required
    }

    @Override
    public void onGroupCommand(Command command, String label, final String[] args) {
        if (this.currentState != GroupStateMachine.followState) {
            this.player.sendMessage(ChatColor.RED + "Not currently following anyone.");
        } else {
            UUID followingUUID = FollowState.followerToPlayers.remove(this.player.getUniqueId());
	        Player following = PotPvP.getInstance().getServer().getPlayer(followingUUID);
	        FollowState.playerToFollowers.get(following.getUniqueId()).remove(this.player.getUniqueId());

            /*if (FollowState.playerToFollowers.get(following.getUniqueId()).size() == 0) {
                FollowState.playerToFollowers.remove(following.getUniqueId());
            }*/

            try {
                GroupStateMachine.followState.pop(this.group);
                this.player.sendMessage(ChatColor.GREEN + "No longer following " + following.getName());
            } catch (IllegalStateTransitionException e) {
                PotPvP.getInstance().somethingBroke(this.player, this.group);
            }
        }
    }

    @Override
    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command usage: /follow [name]");
    }

}
