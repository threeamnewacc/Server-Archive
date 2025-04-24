package me.devkevin.practice.panel.commands.impl;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright 15/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class GlobalEloDebugCommand extends PracticeCommand {

    @Override @Command(name = "elodebug", permission = Rank.ADMIN, usage = "&cUsage: /elodebug <player>")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(PracticeLang.PLAYER_NOT_FOUND, args[0]));
            return;
        }

        Profile profileTarget = getPlugin().getProfileManager().getProfileData(target.getUniqueId());
        AtomicInteger eloValue = new AtomicInteger(0);

        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&aRanked kits: " + getPlugin().getKitManager().getRankedKits().size()));
        player.sendMessage(CC.translate("&agetGlobalElo(): " + profileTarget.getGlobalElo()));
        player.sendMessage(CC.translate("&aTotal getGlobalElo Sum: " + eloValue.get()));
        player.sendMessage(CC.translate("&aCalculated getGlobalElo(): " + (eloValue.get() / getPlugin().getKitManager().getRankedKits().size())));
        player.sendMessage(CC.CHAT_BAR);
    }
}
