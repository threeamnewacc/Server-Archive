package me.devkevin.practice.options.command;

import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 11/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class OptionsCommand extends PracticeCommand {

    @Command(name = "poptions", aliases = {"toggle"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());
        player.openInventory(profile.getOptions().getInventory());
    }
}
