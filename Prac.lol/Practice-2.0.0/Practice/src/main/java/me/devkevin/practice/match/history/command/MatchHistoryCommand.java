package me.devkevin.practice.match.history.command;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.match.history.MatchLocatedData;
import me.devkevin.practice.match.history.menu.MatchHistoryMenu;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.Cooldown;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Copyright 03/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class MatchHistoryCommand extends PracticeCommand {

    private final Practice plugin = Practice.getInstance();

    @Override @Command(name = "matchhistory", aliases = {"matchhist"}, inGameOnly = true, description = "match history command.", usage = "&cUsage: /matchhistory <player>")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(PracticeLang.PLAYER_NOT_FOUND, args[0]));
            return;
        }

        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        if (!profile.getPlayerCommandCooldown().hasExpired()) {
            player.sendMessage("&cYou must wait &l"+
                    profile.getPlayerCommandCooldown().getTimeMilisLeft() +
                    " " +
                    profile.getPlayerCommandCooldown().getContextLeft() +
                    " &cbefore using that command again.");

            return;
        }

        profile.setPlayerCommandCooldown(new Cooldown(5));
        List<MatchLocatedData> matchHistory = this.plugin.getMatchLocatedData().getMatchesByUser(target.getUniqueId());
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            new MatchHistoryMenu(target.getUniqueId(), matchHistory).openMenu(player);
        });

    }
}
