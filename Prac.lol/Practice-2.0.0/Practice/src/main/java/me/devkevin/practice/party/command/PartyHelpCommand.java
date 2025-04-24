package me.devkevin.practice.party.command;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 31/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PartyHelpCommand extends PracticeCommand {

    private final Practice plugin = Practice.getInstance();

    String[] LINE = new String[] {
            PracticeLang.line,
            CC.YELLOW + "Party Commands:",
            "",
            CC.AQUA + "/party create",
            CC.AQUA + "/party leave",
            CC.AQUA + "/party join",
            CC.AQUA + "/party info",
            CC.AQUA + "/party help",
            "",
            CC.YELLOW + "Party Leader Commands:",
            "",
            CC.AQUA + "/party open",
            CC.AQUA + "/party lock",
            CC.AQUA + "/party kick (player)",
            CC.AQUA + "/party invite (player)",
            CC.AQUA + "/party setlimit (amount)",
            PracticeLang.line
    };

    String[] LINE_2 = new String[] {
            PracticeLang.line,
            CC.YELLOW + "Party Commands:",
            "",
            CC.AQUA + "/party create",
            CC.AQUA + "/party leave",
            CC.AQUA + "/party join",
            CC.AQUA + "/party info",
            CC.AQUA + "/party help",
            PracticeLang.line
    };

    @Command(name = "party", aliases = {"p", "t", "team", "party.help", "p.help", "t.help", "team.help"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            player.sendMessage(LINE);
        } else {
            player.sendMessage(LINE_2);
        }
    }
}
