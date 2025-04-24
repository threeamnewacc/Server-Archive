package me.devkevin.practice.panel.commands.impl;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Copyright 15/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ForceQueueCommand extends PracticeCommand {

    @Override @Command(name = "forcequeue", permission = Rank.ADMIN, usage = "&cUsage: /forcequeue <player> <kit>")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 2) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(PracticeLang.PLAYER_NOT_FOUND, args[0]));
            return;
        }

        Profile profile = getPlugin().getProfileManager().getProfileData(target.getUniqueId());
        if (profile == null) {
            player.sendMessage(CC.translate("&cThere is no data stored for that player."));
            return;
        }

        Kit kit = getPlugin().getKitManager().getKit(args[1]);
        if (kit == null) {
            player.sendMessage(CC.translate("&cThat kit does not exist."));
            return;
        }

        getPlugin().getQueueMenu().addToQueue(target, profile, kit, QueueType.UN_RANKED);
        player.sendMessage(CC.translate("&aYou have forcefully added " + target.getName() + " into the " + kit.getName() + " queue."));
        target.sendMessage(CC.translate("&7&oYou have been forcefully added into the " + kit.getName() + " queue by " + player.getName()));
    }
}
