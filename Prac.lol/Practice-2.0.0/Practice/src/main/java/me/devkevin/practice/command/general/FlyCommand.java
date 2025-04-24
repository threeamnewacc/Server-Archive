package me.devkevin.practice.command.general;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright 09/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class FlyCommand extends PracticeCommand {

    private final Practice plugin = Practice.getInstance();

    @Command(name = "fly", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        Player player = command.getPlayer();

        if (!(sender instanceof Player)) {
            return;
        }

        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (!coreProfile.hasDonor()) {
            player.sendMessage(PracticeLang.NO_PERMISSION);
            return;
        }

        me.devkevin.practice.profile.Profile profile2 = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (profile2.getState() != ProfileState.SPAWN) {
            player.sendMessage(CC.RED + "You can't execute that command in your current state.");
            return;
        }

        player.setAllowFlight(!player.getAllowFlight());

        if (player.getAllowFlight()) {
            player.sendMessage(CC.GREEN + "Your flight mode has been enabled.");
        } else {
            player.sendMessage(CC.RED + "Your flight mode has been disabled.");
        }
    }
}
