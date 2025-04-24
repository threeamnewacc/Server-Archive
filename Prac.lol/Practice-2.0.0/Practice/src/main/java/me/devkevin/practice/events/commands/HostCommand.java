package me.devkevin.practice.events.commands;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.events.EventState;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.events.sumo.SumoEvent;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 22/08/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class HostCommand extends PracticeCommand {

    @Command(name = "hostevent", permission = Rank.GOLD)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (args.length < 1) {
            player.sendMessage(CC.RED + "Usage: /hostevent <event>");
            return;
        }

        String eventName = args[0];

        if (eventName == null) {
            return;
        }

        if (Practice.getInstance().getEventManager().getByName(eventName) == null) {
            player.sendMessage(CC.RED + "That event doesn't exist.");
            player.sendMessage(CC.RED + "Available events: Sumo, OITC");
            return;
        }

        if(System.currentTimeMillis() < Practice.getInstance().getEventManager().getCooldown()) {
            player.sendMessage(CC.RED + "There is a cooldown. Event can't start at this moment.");
            return;
        }

        PracticeEvent event = Practice.getInstance().getEventManager().getByName(eventName);
        if (event.getState() != EventState.UNANNOUNCED) {
            player.sendMessage(CC.RED + "There is currently an active event.");
            return;
        }

        boolean eventBeingHosted = Practice.getInstance().getEventManager().getEvents().values().stream().anyMatch(e -> e.getState() != EventState.UNANNOUNCED);
        if (eventBeingHosted) {
            player.sendMessage(CC.RED + "There is currently an active event.");
            return;
        }


        String toSend = CC.YELLOW + CC.BOLD + "(Event) " + CC.GREEN + "" + event.getName() + " is starting soon. " + CC.GRAY + "[Click to Join]";
        String toSendDonor = CC.GRAY + "[" + profile.getGrant().getRank().getColor() + CC.BOLD + "*" + CC.GRAY + "] " + profile.getGrant().getRank().getColor() + CC.BOLD + player.getName() + CC.WHITE + " is hosting a " + profile.getGrant().getRank().getColor() + CC.BOLD + event.getName() +  " Event. " + CC.GRAY + "[Click to Join]";


        Clickable message = new Clickable(player.hasPermission("practice.donors.gold") ? toSendDonor : toSend,
                CC.GRAY + "Click to join this event.",
                "/join " + event.getName());
        Practice.getInstance().getServer().getOnlinePlayers().forEach(message::sendToPlayer);

        if (player.hasPermission("practice.admin")) {
            event.setLimit(event instanceof SumoEvent ? 100 : 50);
        } else if(player.hasPermission("practice.donors.udrop")) {
            event.setLimit(event instanceof SumoEvent ? 50 : 30);
        } else if(player.hasPermission("practice.donors.diamond")) {
            event.setLimit(event instanceof SumoEvent ? 40 : 25);
        } else if(player.hasPermission("practice.donors.emerald")) {
            event.setLimit(event instanceof SumoEvent ? 35 : 25);
        } else if(player.hasPermission("practice.donors.gold")) {
            event.setLimit(event instanceof SumoEvent ? 30 : 20);
        } else {
            event.setLimit(30);
        }

        Practice.getInstance().getEventManager().hostEvent(event, player);
    }
}
