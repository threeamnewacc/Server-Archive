package me.devkevin.practice.util;

import me.devkevin.practice.Practice;
import me.devkevin.practice.match.team.KillableTeam;
import me.devkevin.practice.tournament.team.TournamentTeam;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Copyright 19/03/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class TeamUtil {

    public static String getNames(KillableTeam team) {
        String names = "";

        for (int i = 0; i < team.getPlayers().size(); i++) {
            UUID teammateUUID = team.getPlayers().get(i);
            Player teammate = Practice.getInstance().getServer().getPlayer(teammateUUID);
            String name = "";

            if (teammate == null) {
                if (team instanceof TournamentTeam) {
                    name = ((TournamentTeam) team).getPlayerName(teammateUUID);
                }
            } else {
                name = teammate.getName();
            }

            int players = team.getPlayers().size();

            if (teammate != null) {
                names += name + (((players - 1) == i) ? "" : ((players - 2) == i) ? (players > 2 ? "," : "") + " & " : ", ");
            }
        }

        return names;
    }
}
