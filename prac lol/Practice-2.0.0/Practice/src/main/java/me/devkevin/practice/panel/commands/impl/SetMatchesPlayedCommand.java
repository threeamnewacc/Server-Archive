package me.devkevin.practice.panel.commands.impl;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Copyright 15/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class SetMatchesPlayedCommand extends PracticeCommand {

    @Override @Command(name = "setplayedmatches", permission = Rank.ADMIN, usage = "&cUsage: /setplayedmatches [player] [matches]")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(PracticeLang.PLAYER_NOT_FOUND, args[0]));
            return;
        }

        if (args.length == 2) {
            int amount = Integer.parseInt(args[1]);
            getPlugin().getProfileManager().getProfileData(target.getUniqueId()).setMatchesPlayed(amount);
            player.sendMessage(CC.translate(LandCore.getInstance().getProfileManager().getProfile(target.getUniqueId()).getGrant().getRank().getColor() + target.getName() + "&a's played matches has been modified. New played matches: " + amount));
        } else {
            player.sendMessage(CC.translate("&cUsage: /setplayedmatches <player> <matches>"));
        }
    }
}
