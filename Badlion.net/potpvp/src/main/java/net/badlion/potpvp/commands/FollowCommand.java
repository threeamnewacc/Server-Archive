package net.badlion.potpvp.commands;

import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.states.spectator.FollowState;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class FollowCommand extends GCommandExecutor {

    public FollowCommand() {
        super(1); // 1 args minimum
    }

    @Override
    public void onGroupCommand(Command command, String label, final String[] args) {
        if (this.currentState != GroupStateMachine.spectatorState) {
            this.player.sendMessage(ChatColor.RED + "You cannot follow if you are not in spectator mode. You also can only follow one person at a time.");
        } else {
            Player following = PotPvP.getInstance().getServer().getPlayerExact(args[0]);
            if (following == null) {
	            this.player.sendMessage(ChatColor.RED + "This player is not online.");
	            return;
            } else if (this.player == following) {
	            this.player.sendMessage(ChatColor.RED + "You can't follow yourself.");
	            return;
            } else if (following.hasPermission("badlion.kittrial")
		            && GroupStateMachine.spectatorState.contains(PotPvP.getInstance().getPlayerGroup(following))) {
	            this.player.sendMessage(ChatColor.RED + "Cannot spectate this staff member at the moment.");
	            return;
            }

            try {
                GroupStateMachine.spectatorState.push(GroupStateMachine.followState, this.group);
                FollowState.followerToPlayers.put(this.player.getUniqueId(), following.getUniqueId());
                Set<UUID> followers = FollowState.playerToFollowers.get(following.getUniqueId());
                /*if (followers == null) {
                    followers = new HashSet<>();
                    FollowState.playerToFollowers.put(following.getUniqueId(), followers);
                }*/

                followers.add(this.player.getUniqueId());
                this.player.teleport(following.getLocation());
                this.player.sendMessage(ChatColor.GREEN + "You are now following " + following.getName());
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
