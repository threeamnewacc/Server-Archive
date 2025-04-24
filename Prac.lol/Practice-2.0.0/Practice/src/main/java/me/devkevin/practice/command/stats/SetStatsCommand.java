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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Copyright 27/04/2021 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class SetStatsCommand extends PracticeCommand {

    @Command(name = "set.elo", permission = Rank.MANAGER)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.RED + "Usage: /set elo (player) (kit) (elo)");
            return;
        }

        OfflinePlayer target = Bukkit.getPlayer(args[0]);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage(CC.translate(CC.RED + "That Player is not online."));
            return;
        }

       // Practice.getInstance().getProfileManager().createPlayerData(target);

        Profile targetData = Practice.getInstance().getProfileManager().getProfileData(target.getUniqueId());
        Kit kit = Practice.getInstance().getKitManager().getKit(args[1]);

        if (kit == null) {
            player.sendMessage(CC.RED + "Please provide a valid Kit.");
            return;
        }

        int integer = Integer.parseInt(args[2]);

        targetData.setElo(kit.getName(), integer);
        //targetData.recalculateRank(targetData.getGlobalStats("ELO"));

        player.sendMessage(CC.GREEN + "You have been updated " +
                LandCore.getInstance().getProfileManager().getProfile(target.getUniqueId()).getGrant().getRank().getColor() + target.getName() +
                CC.GREEN + " elo.");
    }
}
