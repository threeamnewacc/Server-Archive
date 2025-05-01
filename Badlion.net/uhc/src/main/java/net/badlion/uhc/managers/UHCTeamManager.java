package net.badlion.uhc.managers;

import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;

import java.util.*;

public class UHCTeamManager {

    private static Set<UHCTeam> teams = new HashSet<>();

    public static boolean addUHCTeam(UHCTeam uhcTeam) {
        return UHCTeamManager.teams.add(uhcTeam);
    }

    public static boolean removeUHCTeam(UHCTeam uhcTeam) {
        return UHCTeamManager.teams.remove(uhcTeam);
    }

    /**
     * Returns all possible teams
     */
    public static List<UHCTeam> getAllUHCTeams() {
        List<UHCTeam> uhcTeams = new ArrayList<>();
        uhcTeams.addAll(teams);
        return Collections.unmodifiableList(uhcTeams);
    }

    /**
     * Get all teams that are playing
     */
    public static List<UHCTeam> getAllAlivePlayingTeams() {
        List<UHCTeam> uhcTeams = new ArrayList<>();
        for (UHCTeam uhcTeam : UHCTeamManager.teams) {
            boolean isPlaying = false;
            for (UUID uuud : uhcTeam.getUuids()) {
                UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuud);

                // Skip this team
                if (uhcPlayer.getState() == UHCPlayer.State.MOD || uhcPlayer.getState() == UHCPlayer.State.HOST) {
                    break;
                }

                if (uhcPlayer.getState().ordinal() < UHCPlayer.State.DEAD.ordinal()) {
                    isPlaying = true;
                }
            }

            if (isPlaying) {
                uhcTeams.add(uhcTeam);
            }
        }

        return Collections.unmodifiableList(uhcTeams);
    }

}
