package me.devkevin.practice.events.commands;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.events.EventState;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 22/08/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class EventManagerCommand extends PracticeCommand {

    @Command(name = "eventmanager", permission = Rank.ADMIN)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 2) {
            player.sendMessage(CC.RED + "Usage: /eventmanager <start/end/status/cooldown> <event>");
            return;
        }

        String action = args[0];
        String eventName = args[1];

        if (Practice.getInstance().getEventManager().getByName(eventName) == null) {
            player.sendMessage(CC.RED + "That event doesn't exist.");
            return;
        }

        PracticeEvent event = Practice.getInstance().getEventManager().getByName(eventName);

        if(action.toUpperCase().equalsIgnoreCase("START") && event.getState() == EventState.WAITING) {
            event.getCountdownTask().setTimeUntilStart(5);
            player.sendMessage(CC.RED + "Event was force started.");

        } else if(action.toUpperCase().equalsIgnoreCase("END")  && event.getState() == EventState.STARTED) {
            event.end();
            player.sendMessage(CC.RED + "Event was cancelled.");

        } else if(action.toUpperCase().equalsIgnoreCase("STATUS")) {

            String[] message = new String[] {
                    CC.YELLOW + "Event: " + CC.WHITE + event.getName(),
                    CC.YELLOW + "Host: " + CC.WHITE + (event.getHost() == null ? "Player Left" : event.getHost().getName()),
                    CC.YELLOW + "Players: " + CC.WHITE + event.getPlayers().size() + "/" + event.getLimit(),
                    CC.YELLOW + "State: " + CC.WHITE + event.getState().name(),
            };

            player.sendMessage(message);

        } else if(action.toUpperCase().equalsIgnoreCase("COOLDOWN")) {

            Practice.getInstance().getEventManager().setCooldown(0L);
            player.sendMessage(CC.RED + "Event cooldown was cancelled.");
        } else {
            player.sendMessage(CC.RED + "Usage: /eventmanager <start/end/status/cooldown> <event>");
        }
    }
}
