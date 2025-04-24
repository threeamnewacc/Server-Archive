package me.devkevin.practice.events.commands;

import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 26/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class HostEventCommand extends PracticeCommand {

    private final Practice plugin = Practice.getInstance();

    @Command(name = "host")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();


        player.openInventory(this.plugin.getHostMenu().getHostMenu().getCurrentPage());
    }
}
