package net.badlion.mpg.commands;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGWorld;
import net.badlion.mpg.kits.MPGKit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {

	public enum VoteType {
		MAP("Map"),
		KIT("Kit");

		private String name;

		VoteType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private VoteCommand.VoteType voteType = ((VoteCommand.VoteType) MPG.getInstance().getConfigOption(MPG.ConfigFlag.VOTE_TYPE));

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
	    if (!(sender instanceof Player)) return true;

	    Player player = (Player) sender;

        if (args.length == 1) {
            if (MPG.getInstance().getMPGGame().isVotingEnabled()) {
                try {
                    // If the # is valid cast the vote
                    int vote = Integer.parseInt(args[0]) - 1; // - 1 because 0 index'd
                    if (vote < MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.NUM_OF_VOTE_CHOICES) && vote >= 0) {
	                    // Reset their old vote
	                    MPG.getInstance().getMPGGame().resetPlayerVote(player);

	                    Object voteObject = MPG.getInstance().getMPGGame().getVoteObject(vote);

	                    String voteObjectName = "";
	                    if (this.voteType == VoteCommand.VoteType.MAP) {
		                    voteObjectName = ((MPGWorld) voteObject).getGWorld().getNiceWorldName();
	                    } else if (this.voteType == VoteCommand.VoteType.KIT) {
		                    voteObjectName = ((MPGKit) voteObject).getName() + " Kit";
	                    }

	                    // Add their vote
	                    MPG.getInstance().getMPGGame().addPlayerVote(player, vote);

                        sender.sendMessage(ChatColor.DARK_GREEN + "You have voted for " + this.voteType.getName().toLowerCase() + " " + args[0] + ", " + ChatColor.DARK_GREEN + voteObjectName);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid vote number.");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid vote number.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Voting is not currently enabled!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "/vote [" + this.voteType.getName() + " #]");
        }

        return true;
    }

	public interface VoteSelectionMethod {

		public static final VoteSelectionMethod MAJORITY_VOTE = new MajorityVote();
		public static final VoteSelectionMethod PERCENTAGE_VOTE = new PercentageVote();

		public Object calculateWinningVote();

	}

	private static class MajorityVote implements VoteSelectionMethod {

		@Override
		public Object calculateWinningVote() {
			int highestVotes = 0;
			Object highestVotesObject = null;
			for (int i = 0; i < MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.NUM_OF_VOTE_CHOICES); i++) {
				Object voteObject = MPG.getInstance().getMPGGame().getVoteObject(i);
				int voteObjectVotes = MPG.getInstance().getMPGGame().getNumberOfVotes(voteObject);

				if (voteObjectVotes > highestVotes) {
					highestVotes = voteObjectVotes;
					highestVotesObject = voteObject;
				}
			}

			return highestVotesObject;
		}

	}

	private static class PercentageVote implements VoteSelectionMethod {

		@Override
		public Object calculateWinningVote() {
			Object voteWinner = null;

			double totalVotes = 0;
			for (int i = 0; i < MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.NUM_OF_VOTE_CHOICES); i++) {
				int voteObjectVotes = MPG.getInstance().getMPGGame().getNumberOfVotes(MPG.getInstance().getMPGGame().getVoteObject(i));

				totalVotes += voteObjectVotes;
			}

			// Equal chances if no one voted
			totalVotes = MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.NUM_OF_VOTE_CHOICES);

			double random = Math.random();

			// Figure out which object this percentage corresponds to
			double d = 0;
			for (int i = 0; i < MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.NUM_OF_VOTE_CHOICES); i++) {
				Object voteObject = MPG.getInstance().getMPGGame().getVoteObject(i);
				int voteObjectVotes = MPG.getInstance().getMPGGame().getNumberOfVotes(voteObject);

				d += voteObjectVotes / totalVotes;

				if (random <= d) {
					voteWinner = voteObject;
					break;
				}
			}


			return voteWinner;
		}

	}

}
