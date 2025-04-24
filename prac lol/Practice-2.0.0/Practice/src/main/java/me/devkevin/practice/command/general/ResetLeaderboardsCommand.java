package me.devkevin.practice.command.general;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 14/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ResetLeaderboardsCommand extends PracticeCommand {

    @Command(name = "resetleaderboards", permission = Rank.DEVELOPER)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Practice.getInstance().getPracticeDatabase().getProfiles().drop();
        player.sendMessage(CC.GREEN + "You have been reset leaderboards");
    }
}
