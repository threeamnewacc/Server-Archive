package me.devkevin.practice.match.menu.command;

import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.match.menu.MatchDetailsMenu;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Copyright 09/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class InvCommand extends PracticeCommand {

    private final Practice plugin = Practice.getInstance();

    private final Pattern UUID_PATTERN = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");

    @Command(name = "inventory", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();


        if (args.length == 0) {
            return;
        }

        if (!args[0].matches(UUID_PATTERN.pattern())) {
            player.sendMessage(PracticeLang.INV_NOT_FOUND);
            return;
        }

        MatchDetailsMenu menu = this.plugin.getMatchDetailSnapshot().getSnapshot(UUID.fromString(args[0]));
        if (menu == null) {
            player.sendMessage(PracticeLang.INV_NOT_FOUND);
        } else {
            player.openInventory(menu.getInventoryUI().getCurrentPage());
        }
    }
}
