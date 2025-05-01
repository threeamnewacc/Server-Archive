package net.badlion.survivalgames.managers;

import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.SGWorld;
import net.badlion.survivalgames.tasks.MapVotingTask;
import net.badlion.worldrotator.GWorld;

import java.util.*;

public class SGMapManager {

    public static final int MIN_PLAYERS = SurvivalGames.getInstance().getConfig().getInt("sg.min_players"); // have a name string in the GameMode class? gameModeName + "_min_players" ????
    public static final int NUM_OF_MAP_CHOICES = SurvivalGames.getInstance().getConfig().getInt("sg.num_of_map_choices");

    public static boolean votingEnabled = false;

    public static SGWorld lastMap = null;
    private static List<SGWorld> worlds = new ArrayList<>();

    private static List<SGWorld> voteWorlds = new ArrayList<>();
    private static Map<SGWorld, Integer> voteWorldsVotes = new HashMap<>();
    public static Map<UUID, Integer> playerToVote = new HashMap<>();

    private static Random random = new Random();

    public static void initialize() {
        ArrayList<GWorld> worlds = SurvivalGames.getInstance().getWorldRotator().getWorlds(); // get all possible worlds
        for (GWorld gWorld : worlds) {
            SGWorld sgWorld = new SGWorld(gWorld);
            SGMapManager.worlds.add(sgWorld);
        }
    }

    public static void startMapVoting() {
        // We got enough players, let them vote on random maps
        SGMapManager.setVotingEnabled(true);
        for (int i = 0; i < SGMapManager.NUM_OF_MAP_CHOICES; i++) { // Loop through until we have the total number of map choices available
            boolean found = false;
            do {
                SGWorld randomWorld = SGMapManager.worlds.get(SGMapManager.random.nextInt(SGMapManager.worlds.size()));
                if (!SGMapManager.voteWorlds.contains(randomWorld) && randomWorld != SGMapManager.lastMap) {
                    SGMapManager.voteWorlds.add(randomWorld);
                    SGMapManager.voteWorldsVotes.put(randomWorld, 0); // Store the world and it's number of vote: default 0
                    found = true;
                }
            } while (!found);
        }

        MapVotingTask mapVoteTask = new MapVotingTask();
        mapVoteTask.runTaskTimer(SurvivalGames.getInstance(), 20, 20);

        SurvivalGames.getInstance().setState(SurvivalGames.SGState.VOTING);
    }

    public static void resetVoteOptions() {
        SGMapManager.voteWorlds.clear();
        SGMapManager.voteWorldsVotes.clear();
        SGMapManager.playerToVote.clear();
    }

    public static SGWorld getSGWorldFromName(String worldName) {
        for (SGWorld sgWorld : SGMapManager.worlds) {
            if (sgWorld.getgWorld().getInternalName().equalsIgnoreCase(worldName)) {
                return sgWorld;
            }
        }
        return null;
    }

    public static List<SGWorld> getVoteWorlds() {
        return SGMapManager.voteWorlds;
    }

    public static Map<SGWorld, Integer> getVoteWorldsVotes() {
        return voteWorldsVotes;
    }

    public static List<SGWorld> getWorlds() {
        return worlds;
    }

    public static boolean isVotingEnabled() {
        return votingEnabled;
    }

    public static void setVotingEnabled(boolean votingEnabled) {
        SGMapManager.votingEnabled = votingEnabled;
    }
}
