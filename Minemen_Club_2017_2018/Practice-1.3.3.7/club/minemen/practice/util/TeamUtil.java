// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.util;

import org.bukkit.entity.Player;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.tournament.TournamentTeam;
import club.minemen.practice.Practice;
import java.util.UUID;
import club.minemen.practice.team.KillableTeam;

public class TeamUtil
{
    public static String getNames(final KillableTeam team) {
        String names = "";
        for (int i = 0; i < team.getPlayers().size(); ++i) {
            final UUID teammateUUID = team.getPlayers().get(i);
            final Player teammate = Practice.getInstance().getServer().getPlayer(teammateUUID);
            String name = "";
            if (teammate == null) {
                if (team instanceof TournamentTeam) {
                    name = ((TournamentTeam)team).getPlayerName(teammateUUID);
                }
            }
            else {
                name = teammate.getName();
            }
            final int players = team.getPlayers().size();
            if (teammate != null) {
                names = names + CC.SECONDARY + name + CC.PRIMARY + ((players - 1 == i) ? "" : ((players - 2 == i) ? (((players > 2) ? "," : "") + " and ") : ", "));
            }
        }
        return names;
    }
}
