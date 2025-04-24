package me.devkevin.practice.events.commands;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.events.EventState;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 22/08/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class JoinEventCommand extends PracticeCommand {

    @Command(name = "join")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(CC.RED + "Usage: /join <id>");
            return;
        }

        Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());

        if (Practice.getInstance().getPartyManager().getParty(profile.getUuid()) != null || (profile.getState() != ProfileState.SPAWN)) {
            player.sendMessage(CC.RED + "Cannot execute this command in your current state.");
            return;
        }

        String eventName = args[0];

        if (eventName == null) {
            return;
        }

        if (Practice.getInstance().getEventManager().getByName(eventName) == null) {
            player.sendMessage(CC.RED + eventName + " doesn't exist.");
            return;
        }

        PracticeEvent event = Practice.getInstance().getEventManager().getByName(eventName);
        if (event.getState() != EventState.WAITING) {
            player.sendMessage(CC.RED + "You cannot join this event!");
            return;
        }

        if (event.getPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are already playing " + event.getName() + "!");
            return;
        }

        event.join(player);
    }
}
