package me.devkevin.practice.kit.command;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.command.CommandSender;

/**
 * Copyright 17/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class KitsCommand extends PracticeCommand {

    @Command(name = "kits", permission = Rank.DEVELOPER, inGameOnly = true)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        sender.sendMessage(PracticeLang.line);
        sender.sendMessage(CC.translate("&4&lKits List &7(&7" + Practice.getInstance().getKitManager().getKits().size() + "&c in total&7)"));
        sender.sendMessage("");
        for (Kit kits : Practice.getInstance().getKitManager().getKits()) {
            sender.sendMessage(CC.translate(" &c&l" + kits.getName() + " &7(" + (kits.isEnabled() ? "&aEnabled" : "&cDisabled") + "&7)"
                    + " &7(" + (kits.isRanked() ? "&aEnabled" : "&cDisabled") + "&7"));
        }

        sender.sendMessage(PracticeLang.line);
    }
}
