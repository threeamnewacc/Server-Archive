package us.zonix.practice.util;

import java.util.UUID;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.team.KillableTeam;
import us.zonix.practice.tournament.TournamentTeam;

public class TeamUtil {
    public static String getNames(KillableTeam team) {
        String names = "";

        for (int i = 0; i < team.getPlayers().size(); i++) {
            UUID teammateUUID = team.getPlayers().get(i);
            Player teammate = Practice.getInstance().getServer().getPlayer(teammateUUID);
            String name = "";
            if (teammate == null) {
                if (team instanceof TournamentTeam) {
                    name = ((TournamentTeam)team).getPlayerName(teammateUUID);
                }
            } else {
                name = teammate.getName();
            }

            int players = team.getPlayers().size();
            if (teammate != null) {
                names = names + name + (players - 1 == i ? "" : (players - 2 == i ? (players > 2 ? "," : "") + " & " : ", "));
            }
        }

        return names;
    }
}
