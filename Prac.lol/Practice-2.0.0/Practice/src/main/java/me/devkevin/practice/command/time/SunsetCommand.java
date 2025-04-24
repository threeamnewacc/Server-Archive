package me.devkevin.practice.command.time;

import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.options.item.ProfileOptionsItemState;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 11/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class SunsetCommand extends PracticeCommand {

    @Command(name = "sunset")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());

        player.setPlayerTime(12000L, true);
        profile.getOptions().setTime(ProfileOptionsItemState.SUNSET);
    }
}
