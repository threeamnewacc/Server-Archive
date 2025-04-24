package me.devkevin.practice.panel.commands.impl;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.kit.Kit;
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
public class GlobalQueueDebugCommand extends PracticeCommand {

    @Override @Command(name = "globalqueuedebug", permission = Rank.ADMIN, usage = "&cUsage: /globalqueuedebug <kit>")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        Kit kit = getPlugin().getKitManager().getKit(args[0]);
        if (kit == null) {
            player.sendMessage(CC.translate("&cThat kit does not exist."));
            return;
        }

        getPlugin().getProfileManager().getAllData().forEach(profile -> {
            getPlugin().getQueueMenu().addToQueue(Bukkit.getPlayer(profile.getUuid()), profile, kit, QueueType.UN_RANKED);
        });
        player.sendMessage(CC.translate("&aYou have forcefully added " + Bukkit.getOnlinePlayers().size() + " players into the " + kit.getName() + " queue."));
    }
}
