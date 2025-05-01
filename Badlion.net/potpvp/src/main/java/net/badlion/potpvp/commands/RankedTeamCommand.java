package net.badlion.potpvp.commands;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.exceptions.NoRatingFoundException;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.RankedLeftManager;
import net.badlion.potpvp.managers.RatingManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RankedTeamCommand extends GCommandExecutor {

    public RankedTeamCommand() {
        super(1, true); // 1 argument
    }

    @Override
    public void onGroupCommand(Command command, String label, String[] args) {
        if (!PotPvP.getInstance().isAllowRankedMatches()) {
            player.sendMessage(ChatColor.RED + "Server restart soon.  Wait until after restart to play ranked matches.");
            return;
        }

        Ladder ladder = null;
        if (args[1].equals("2")) {
            if (this.group.players().size() != 2) {
                this.group.sendMessage(ChatColor.RED + "You need to have 2 in your party to queue up for 2v2 ranked teams.");
                return;
            }

            ladder = Ladder.getLadder(args[0], Ladder.LadderType.TwoVsTwoRanked);
        } else if (args[1].equals("3")) {
            if (this.group.players().size() != 3) {
                this.group.sendMessage(ChatColor.RED + "You need to have 3 in your party to queue up for 3v3 ranked teams.");
                return;
            }

            ladder = Ladder.getLadder(args[0], Ladder.LadderType.ThreeVsThreeRanked);
        } else if (args[1].equals("5")) {
            if (this.group.players().size() != 5) {
                this.group.sendMessage(ChatColor.RED + "You need to have 5 in your party to queue up for 5v5 ranked teams.");
                return;
            }

            ladder = Ladder.getLadder(KitRuleSet.SG_BUILD_UHC_LADDER_NAME, Ladder.LadderType.FiveVsFiveRanked);
        }

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

        // Simple logic checks
        if (!this.group.isParty()) {
            this.group.sendMessage(ChatColor.RED + "You cannot queue up for ranked teams without 2 players in a party.");
            return;
        } else if (this.group.getLeader() != this.player) {
            this.player.sendMessage(ChatColor.RED + "Only party leader can queue up.");
            return;
        }

	    if (this.group.hasDeadPlayers()) {
		    this.player.sendMessage(ChatColor.RED + "All players must be alive in your party to queue up.");
		    this.player.sendMessage(ChatColor.YELLOW + "Dead players: " + this.group.getDeadPlayerString());

		    BukkitUtil.closeInventory(this.player);
		    return;
	    }

        try {
            this.currentState.transition(GroupStateMachine.matchMakingState, this.group);
        } catch (IllegalStateTransitionException e) {
            this.group.sendLeaderMessage(ChatColor.RED + "Cannot queue up for ranked right now.");
            return;
        }

        try {
            ladder.addGroup(this.group, RatingManager.getGroupRating(this.group, ladder));
        } catch (NoRatingFoundException e) {
            // They don't have a party rating yet (if this one gets fucked up whatever)
            ladder.addGroup(this.group, RatingManager.DEFAULT_RATING);
        }

        this.group.sendMessage(ChatColor.GREEN + "Added to Ranked Team Matchmaking");

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
