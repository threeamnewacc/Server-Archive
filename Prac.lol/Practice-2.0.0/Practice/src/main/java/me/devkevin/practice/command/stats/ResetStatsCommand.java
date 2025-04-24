package me.devkevin.practice.command.stats;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 17/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ResetStatsCommand extends PracticeCommand {

    @Command(name = "reset.elo", permission = Rank.MANAGER)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.RED + "Usage: /reset elo (player)");
            return;
        }

        Player target = Practice.getInstance().getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(CC.RED + args[0] + " not found.");
            return;
        }

        Profile profile = Practice.getInstance().getProfileManager().getProfileData(target.getUniqueId());
        for (Kit kit : Practice.getInstance().getKitManager().getKits()) {
            profile.setElo(kit.getName(), Profile.DEFAULT_ELO);
            profile.setLosses(kit.getName(), 0);
            profile.setWins(kit.getName(), 0);
        }

        player.sendMessage(LandCore.getInstance().getProfileManager().getProfile(target.getUniqueId()).getGrant().getRank().getColor() + target.getName() + CC.YELLOW + "'s stats have been wiped.");
    }
}
