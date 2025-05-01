package net.badlion.mpg.managers;

import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MPGTeamManager {

    // Used LinkedHashSet to maintain order
    private static Set<MPGTeam> teams = new LinkedHashSet<>();

    public static void storeTeam(MPGTeam mpgTeam) {
        MPGTeamManager.teams.add(mpgTeam);
    }

    public static boolean removeTeam(MPGTeam mpgTeam) {
        return MPGTeamManager.teams.remove(mpgTeam);
    }

    public static void removeAllTeams() {
        // Faster than calling .clear()
        MPGTeamManager.teams = new HashSet<>();
    }

    /**
     * Returns all possible teams
     */
    public static List<MPGTeam> getAllMPGTeams() {
        List<MPGTeam> mpgTeams = new ArrayList<>();
        mpgTeams.addAll(MPGTeamManager.teams);
        return Collections.unmodifiableList(mpgTeams);
    }

	/**
	 * Get the MPGTeam associated with a team color
	 */
	public static MPGTeam getTeamFromColor(ChatColor color) {
		for (MPGTeam mpgTeam : MPGTeamManager.teams) {
			if (mpgTeam.getColor() == color) {
				return mpgTeam;
			}
		}

		return null;
	}

    public static void assignRandomTeam(MPGPlayer mpgPlayer) {
        if (MPGTeamManager.teams.size() == 0) {
            throw new RuntimeException("No teams when trying to assign a random team for " + mpgPlayer);
        }

        List<MPGTeam> mpgTeams = new ArrayList<>();
        mpgTeams.addAll(MPGTeamManager.teams);

        Collections.shuffle(mpgTeams);

        // Remove current team since we are going to remove it
        if (mpgPlayer.getTeam().isEmpty()) {
            MPGTeamManager.teams.remove(mpgPlayer.getTeam());
        }

        MPGTeam mpgTeam = mpgTeams.get(0);
        mpgTeam.add(mpgPlayer);
        mpgPlayer.setTeam(mpgTeam);
    }

	public static void updateTeamRespawnLocations() {
		for (MPGTeam team : MPGTeamManager.getAllMPGTeams()) {
			team.updateRespawnLocation();
		}
	}

}
