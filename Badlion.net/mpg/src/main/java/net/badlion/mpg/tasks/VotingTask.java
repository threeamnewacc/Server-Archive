package net.badlion.mpg.tasks;


import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGWorld;
import net.badlion.mpg.commands.VoteCommand;
import net.badlion.mpg.kits.MPGKit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class VotingTask extends BukkitRunnable {

    private int count = 0;

	private VoteCommand.VoteType voteType = ((VoteCommand.VoteType) MPG.getInstance().getConfigOption(MPG.ConfigFlag.VOTE_TYPE));

    @Override
    public void run() {
        if (this.count % 10 == 0) {
            Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.GOLD + "Vote for next " + this.voteType.getName().toLowerCase() + ":");

            for (int i = 0; i < MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.NUM_OF_VOTE_CHOICES); i++) {
	            Object voteObject = MPG.getInstance().getMPGGame().getVoteObject(i);
	            int voteObjectVotes = MPG.getInstance().getMPGGame().getNumberOfVotes(voteObject);

	            String voteObjectName = "";
	            if (this.voteType == VoteCommand.VoteType.MAP) {
		            voteObjectName = ((MPGWorld) voteObject).getGWorld().getNiceWorldName();
	            } else if (this.voteType == VoteCommand.VoteType.KIT) {
		            voteObjectName = ((MPGKit) voteObject).getName() + " Kit";
	            }

	            Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.DARK_GREEN + this.voteType.getName() + ChatColor.GOLD + " " + (i + 1) + ": " + ChatColor.RED + voteObjectName + ChatColor.DARK_GREEN + " currently has " + voteObjectVotes + " votes");
            }
            Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.DARK_GREEN + "Use the " + ChatColor.GOLD
		            + "/vote \"<" + this.voteType.getName().toLowerCase() + "_number>\"" + ChatColor.DARK_GREEN + " command to cast your vote!");
            Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.DARK_GREEN + "There are " + ChatColor.BLUE
		            + (MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.VOTING_TIME) - this.count) + ChatColor.BLUE + " seconds remaining" + ChatColor.DARK_GREEN + " to vote.");
        } else if (this.count >= 25) {
            Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.DARK_GREEN + "There are " + ChatColor.BLUE + (MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.VOTING_TIME) - this.count) + ChatColor.BLUE + " seconds remaining to vote.");
        }

        this.count++;

        if (this.count >= MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.VOTING_TIME)) {
            this.cancel();

            Object voteWinner = ((VoteCommand.VoteSelectionMethod) MPG.getInstance().getConfigOption(MPG.ConfigFlag.VOTE_SELECTION_METHOD)).calculateWinningVote();

	        MPG.getInstance().getMPGGame().setVotingEnabled(false);

            if (Bukkit.getOnlinePlayers().size() < MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.PLAYERS_TO_START)) {
                Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.RED + "Not enough players to start a game at the moment. Disregarding poll.");
            } else if (voteWinner == null) {
                Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.RED + "No "
		                + this.voteType.getName().toLowerCase() + " was chosen, starting a new vote.");
                this.startNewVotingTask();
            } else {
	            if (this.voteType == VoteCommand.VoteType.MAP) {
		            MPGWorld world = (MPGWorld) voteWinner;
		            MPG.getInstance().getMPGGame().setWorld(world);
	            } else if (this.voteType == VoteCommand.VoteType.KIT) {
		            MPGKit kit  = (MPGKit) voteWinner;
		            MPG.getInstance().getMPGGame().setKit(kit);
	            }

	            // Set last vote
	            MPG.getInstance().getMPGGame().setLastVote(voteWinner);

                // Always force here, if we actually start it will fix itself
                MPG.getInstance().getMPGGame().setGameState(MPGGame.GameState.PRE_GAME);
                MPG.getInstance().setServerState(MPG.ServerState.GAME);
            }
        }
    }

    private void startNewVotingTask() {
        MPG.getInstance().getServer().getScheduler().runTaskLater(MPG.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!MPG.getInstance().getMPGGame().isVotingEnabled()) {
                    MPG.getInstance().getMPGGame().startVotingTask();
                }
            }
        }, 100L);
    }

}

