package me.devkevin.practice.command.staff;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright 12/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class OngoingMatchesCommand extends PracticeCommand {

    @Override @Command(name = "ongoing", permission = Rank.ADMIN, usage = "&cUsage: /ongoing <player>")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        player.sendMessage(CC.translate("&7&l ▸ &6Ongoing Matches&7: &f" + (getPlugin().getMatchManager().getMatches().values().size() >= 1 ? getPlugin().getMatchManager().getMatches().values().size() : "&cNone")));
        if (getPlugin().getMatchManager().getMatches().values().size() >= 1) {
            player.sendMessage(" ");
        }

        for (Match match : getPlugin().getMatchManager().getMatches().values()) {
            MatchTeam teamA = match.getTeams().get(0);
            MatchTeam teamB = match.getTeams().get(1);

            if (teamB == null) {
                continue;
            }

            boolean teamAParty = teamA.getAlivePlayers().size() > 1;
            boolean teamBParty = (teamB == null ? false : teamB.getAlivePlayers().size() > 1);

            String teamANames = (teamAParty ? teamA.getLeaderName() + "'s Party" : teamA.getLeaderName());
            String teamBNames = "";
            if (teamBParty) {
                teamBNames = (teamBParty ? teamB.getLeaderName() + "'s Party" : teamB.getLeaderName());
            }

            List<String> strings = Arrays.asList(
                    CC.SB_BAR,
                    "&7&lMatch Info",
                    "&6&l &fDuration&7: &f" + match.getDuration(),
                    "&6&l &fArena&7: &f" + match.getArena().getName(),
                    "&6&l &fKit&7: &f" + match.getKit().getName(),
                    CC.SB_BAR
            );

            Clickable clickable = new Clickable(CC.translate("&f ● &a" + teamANames + "&7 vs &c" + teamBNames + " &f&l\u239c &a[Click to Spectate]"),
                    StringUtils.join(CC.translate(strings), "\n"),
                    "/spectate " + teamA.getLeaderName()
            );

            clickable.sendToPlayer(player);
        }

        player.sendMessage(CC.CHAT_BAR);
    }
}
