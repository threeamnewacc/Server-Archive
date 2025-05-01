package net.badlion.potpvp.commands;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.exceptions.NoRatingFoundException;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.RankedLeftManager;
import net.badlion.potpvp.managers.RatingManager;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RankedCommand extends GCommandExecutor {

    public RankedCommand() {
        super(1, true); // 1 argument
    }

    @Override
    public void onGroupCommand(Command command, String label, String[] args) {
        if (!PotPvP.getInstance().isAllowRankedMatches()) {
            player.sendMessage(ChatColor.RED + "Server restart soon.  Wait until after restart to play ranked matches.");
            return;
        }

        Ladder ladder = Ladder.getLadder(args[0], Ladder.LadderType.OneVsOneRanked);
        if (ladder == null) {
            this.player.sendMessage(ChatColor.RED + "Invalid ladder specified.");
            return;
        }

        if (ladder.hasLimit()) {
            if (!this.player.hasPermission(PotPvP.getUnlimitedRankedPermission())) {
                if (RankedLeftManager.getNumberOfRankedMatchesLeft(this.player) == 0) {
                    this.player.sendMessage("§3=§b=§3=§b=§3=§b=§3=§b= " + ChatColor.YELLOW + ChatColor.BOLD + "Out of Ranked Matches" + ChatColor.RESET + " §3=§b=§3=§b=§3=§b=§3=§b=");
                    this.player.sendMessage(ChatColor.AQUA + "You have reached your daily quota of ranked matches. You can get more by donating");
                    this.player.sendMessage(ChatColor.AQUA + "or by voting at http://www.badlion.net/ once a day for 20 free ranked matches.");
                    return;
                }
            }
        }

        if (this.group.isParty()) {
            this.group.sendMessage(ChatColor.RED + "You cannot queue up for ranked 1v1 with a party.");
            return;
        }

        try {
            this.currentState.transition(GroupStateMachine.matchMakingState, this.group);
        } catch (IllegalStateTransitionException e) {
            this.group.sendLeaderMessage(ChatColor.RED + "Cannot queue up for ranked right now.");
            return;
        }

        Gberry.log("MATCHMAKER", "Adding" + this.group.toString() + " to ranked " + args[0]);
        try {
            ladder.addGroup(this.group, RatingManager.getGroupRating(this.group, ladder));
        } catch (NoRatingFoundException e) {
            this.group.sendLeaderMessage(ChatColor.RED + "Error when trying to retrieve ratings. Try again in a few seconds.");

            try {
                GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.matchMakingState, this.group);
            } catch (IllegalStateTransitionException e1) {
                this.group.sendLeaderMessage(ChatColor.RED + "Something went terribly wrong. Please relog.");
            }

            return;
        }

        this.group.sendMessage(ChatColor.GREEN + "Added to Ranked Matchmaking");

        // Try to create a game after adding our players
        Game game = ladder.createGame();
        if (game != null) {
            if (ladder.addPlayersToGame(game)) {
                game.startGame();
            }
        }
    }

    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Use the inventory items to access the ranked queues.");
    }

}
