package me.devkevin.practice.panel.commands.impl;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Copyright 15/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class GotoEventCommand extends PracticeCommand {

    @Override @Command(name = "gotoevent", aliases = {"eventsworld", "eventworld", "tpevent"}, permission = Rank.ADMIN, usage = "&cUsage: /gotoevent")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Location eventWorld = Bukkit.getWorld("event").getSpawnLocation();
        player.teleport(eventWorld);
        player.sendMessage(CC.translate("&aYou have teleported to the events world."));
    }
}
