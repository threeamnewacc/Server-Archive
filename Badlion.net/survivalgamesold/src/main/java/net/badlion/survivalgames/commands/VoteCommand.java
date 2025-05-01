package net.badlion.survivalgames.commands;

import net.badlion.survivalgames.managers.SGMapManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            if (SGMapManager.isVotingEnabled()) {
                try {
                    // If the # is valid cast the vote
                    int val = Integer.parseInt(args[0]) - 1; // - 1 because 0 index'd
                    if (val < SGMapManager.getVoteWorlds().size() && val >= 0) {
                        int numOfVotes = 1;
                        if (sender.hasPermission("badlion.lionplus")) {
                            numOfVotes = 5;
                        } else if (sender.hasPermission("badlion.lion")) {
                            numOfVotes = 4;
                        } else if (sender.hasPermission("badlion.donatorplus")) {
                            numOfVotes = 3;
                        } else if(sender.hasPermission("badlion.donator")) {
                            numOfVotes = 2;
                        }

                        if (SGMapManager.playerToVote.containsKey(((Player) sender).getUniqueId())) {
                            int oldVal = SGMapManager.playerToVote.get(((Player) sender).getUniqueId());
                            SGMapManager.getVoteWorldsVotes().put(SGMapManager.getVoteWorlds().get(oldVal), SGMapManager.getVoteWorldsVotes().get(SGMapManager.getVoteWorlds().get(oldVal)) - numOfVotes);
                        }

                        SGMapManager.getVoteWorldsVotes().put(SGMapManager.getVoteWorlds().get(val), SGMapManager.getVoteWorldsVotes().get(SGMapManager.getVoteWorlds().get(val)) + numOfVotes);
                        sender.sendMessage(ChatColor.DARK_GREEN + "You have voted for map " + args[0] + ", " + ChatColor.DARK_GREEN + SGMapManager.getVoteWorlds().get(val).getgWorld().getNiceWorldName());
                        SGMapManager.playerToVote.put(((Player)sender).getUniqueId(), val);
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
            sender.sendMessage(ChatColor.RED + "/vote [Map #]");
        }

        return true;
    }

}
