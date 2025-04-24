package me.devkevin.practice.command.staff;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 09/05/2021 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class StaffModeCommand extends PracticeCommand {

    @Command(name = "staff", aliases = {"modmode"}, permission = Rank.TRIAL_MOD)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());

        if (profile.isInStaffMode()) {
            player.sendMessage(CC.RED + "You are currently in Staff Mode.");
            return;
        }

        Practice.getInstance().getStaffMode().staffMode(player);
    }
}
