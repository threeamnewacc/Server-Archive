package net.badlion.survivalgames.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.SGGame;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGMapManager;
import net.badlion.survivalgames.SGWorld;
import net.badlion.survivalgames.gamemodes.ClassicGameMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class MapVotingTask extends BukkitRunnable {

    public static int VOTE_TIME_IN_SECONDS = 30;
    private int count = 0;

    @Override
    public void run() {
        if (count % 10 == 0) {
            Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.GOLD + "Vote for the next map :");

            for (int i = 0; i < SGMapManager.NUM_OF_MAP_CHOICES; i++) {
                Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "Map " + ChatColor.GOLD + (i + 1) + ": " + ChatColor.RED + SGMapManager.getVoteWorlds().get(i).getgWorld().getNiceWorldName() + ChatColor.DARK_GREEN + " currently has " + SGMapManager.getVoteWorldsVotes().get(SGMapManager.getVoteWorlds().get(i)) + " votes");
            }
            Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "Use the " + ChatColor.GOLD + "/vote \"<map number>\"" + ChatColor.DARK_GREEN + " command to cast your vote!");
            Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "There are " + ChatColor.BLUE + (MapVotingTask.VOTE_TIME_IN_SECONDS - this.count) + ChatColor.BLUE + " seconds remaining to vote.");
        } else if (this.count >= 25) {
            Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "There are " + ChatColor.BLUE + (MapVotingTask.VOTE_TIME_IN_SECONDS - this.count) + ChatColor.BLUE + " seconds remaining to vote.");
        }

        ++count;

        if (this.count >= MapVotingTask.VOTE_TIME_IN_SECONDS) {
            this.cancel();

            int numOfVotes = 0;
            SGWorld highestVotedWorld = null;

            for (int i = 0; i < SGMapManager.NUM_OF_MAP_CHOICES; i++) {
                if (SGMapManager.getVoteWorldsVotes().get(SGMapManager.getVoteWorlds().get(i)) > numOfVotes) {
                    numOfVotes = SGMapManager.getVoteWorldsVotes().get(SGMapManager.getVoteWorlds().get(i));
                    highestVotedWorld = SGMapManager.getVoteWorlds().get(i);
                }
            }

            SGMapManager.setVotingEnabled(false);
            SurvivalGames.getInstance().setState(SurvivalGames.SGState.PRE_START); // Always force here, if we actually start it wil fix itself
            SGMapManager.resetVoteOptions();

            if (Bukkit.getOnlinePlayers().size() < SGMapManager.MIN_PLAYERS) {
                Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.RED + "Not enough players to start a game at the moment. Disregarding poll.");
            } else if (numOfVotes == 0) {
                Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.RED + "No map was chosen, starting a new vote.");
                this.startNewVotingTask();
            } else {
                // TODO: Make this dynamic Gamemode crap
                new SGGame(new ClassicGameMode(), highestVotedWorld);
            }
        }
    }

    private void startNewVotingTask() {
        SurvivalGames.getInstance().getServer().getScheduler().runTaskLater(SurvivalGames.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!SGMapManager.isVotingEnabled()) {
                    SGMapManager.startMapVoting();
                }
            }
        }, 20 * 5);
    }
}
