package me.devkevin.practice.match.vote.commands;

import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.match.vote.Vote;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.TaskUtil;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 24/01/2023 @ 23:33
 * RateCommand / me.devkevin.practice.match.vote.commands / Practice
 */
public class RateCommand extends PracticeCommand {
    @Override @Command(name = "rate")
    public void onCommand(CommandArgs command) {
        if (!(command.getSender() instanceof Player)) {
            return;
        }

        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length != 1) {
            return;
        }

        Profile profile = getPlugin().getProfileManager().getProfileData(player.getUniqueId());

        if (profile.getLastArenaPlayed() == null) return;

        Vote vote;
        try {
            vote = Vote.valueOf(args[0]);
        }
        catch (Exception unused) {
            return;
        }

        TaskUtil.runAsync(() -> getPlugin().getVoteManager().attemptVote(player, profile.getLastArenaPlayed(), vote));
    }
}
