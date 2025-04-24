package me.devkevin.practice.events.commands;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 23/08/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class LeaveEventCommand extends PracticeCommand {

    @Command(name = "leave")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        boolean isInEvent = Practice.getInstance().getEventManager().getEventPlaying(player) != null;

        if (isInEvent) {
            leaveEvent(player);
        } else {
            player.sendMessage(CC.RED + "There is nothing to leave.");
        }
    }

    private void leaveEvent(Player player) {
        PracticeEvent event = Practice.getInstance().getEventManager().getEventPlaying(player);

        if (event == null) {
            player.sendMessage(CC.RED + "That event doesn't exist.");
            return;
        }

        if(!Practice.getInstance().getEventManager().isPlaying(player, event)) {
            player.sendMessage(CC.RED + "You are not in an event.");
            return;
        }

        event.leave(player);
    }
}
