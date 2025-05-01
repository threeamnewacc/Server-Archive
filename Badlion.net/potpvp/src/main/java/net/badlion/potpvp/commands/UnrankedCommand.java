package net.badlion.potpvp.commands;

import net.badlion.potpvp.Game;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.RankedLeftManager;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UnrankedCommand extends GCommandExecutor {

    public UnrankedCommand() {
        super(1, true); // 1 argument
    }

    @Override
    public void onGroupCommand(Command command, String label, String[] args) {
        Ladder ladder = Ladder.getLadder(args[0], Ladder.LadderType.OneVsOneUnranked);
        if (ladder == null) {
            this.player.sendMessage(ChatColor.RED + "Invalid ladder specified.");
            return;
        }

        if (ladder.hasLimit()) {
            if (!this.player.hasPermission(PotPvP.getUnlimitedRankedPermission())) {
	            if (RankedLeftManager.getNumberOfRankedMatchesLeft(this.player) > 0) {
                    this.player.sendMessage("§3=§b=§3=§b=§3=§b=§3=§b= " + ChatColor.YELLOW + ChatColor.BOLD + "Out of Unranked Matches" + ChatColor.RESET + " §3=§b=§3=§b=§3=§b=§3=§b=");
                    this.player.sendMessage(ChatColor.AQUA + "You have reached your daily quota of ranked matches. You can get more by donating at http://store.badlion.net/");
                    return;
                }
            }
        }

        try {
            this.currentState.transition(GroupStateMachine.matchMakingState, this.group);
        } catch (IllegalStateTransitionException e) {
            group.sendLeaderMessage(ChatColor.RED + "Cannot queue up for unranked right now.");
            return;
        }

        ladder.addGroup(this.group, 0);

        this.group.sendMessage(ChatColor.GREEN + "Added to Unranked Matchmaking");

        // Try to create a game after adding our players
        Game game = ladder.createGame();
        if (game != null) {
            if (ladder.addPlayersToGame(game)) {
                game.startGame();
            }
        }
    }

    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Use the inventory items to access the unranked queues.");
    }

}
