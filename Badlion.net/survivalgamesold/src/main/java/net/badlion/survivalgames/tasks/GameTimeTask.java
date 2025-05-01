package net.badlion.survivalgames.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import net.badlion.survivalgames.SGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameTimeTask extends BukkitRunnable {

    public static int seconds = 0;
    public static int minutes = 20;

    public static int ALIVE_PLAYERS = 0;
    public static int SPECTATOR_PLAYERS = 0;

    @Override
    public void run() {
        if (SurvivalGames.getInstance().getState() == SurvivalGames.SGState.END) {
            // Clean up game and store information etc etc
            GameTimeTask.handleCounts();
            this.cancel();
            return;
        }

        // Only count down if we are going to be positive
        if (minutes != 0 || seconds != 0) {
            if (seconds == 0) {
                --minutes;
                seconds = 60;
            }

            --seconds;
        }

        if (minutes == 10 && seconds == 0) {
            Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "All of the chests have been refilled!");
            SurvivalGames.getInstance().getGame().setChestsRefilled(true);
            SurvivalGames.getInstance().getGame().fillAllChests();
        }

        if (minutes == 0 && seconds == 0 && !SurvivalGames.getInstance().getGame().isDeathMatch()) {
            SurvivalGames.getInstance().getGame().setDeathMatch(true);
            SurvivalGames.getInstance().getGame().startDeathMatch();
        } else if (minutes == 0 && seconds == 0 && SurvivalGames.getInstance().getGame().isDeathMatch()) {
            // Go through the alive players, remove any that are offline and randomly damage one of the remaining players
            Iterator<SGPlayer> iterator = SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE).iterator();
            List<Player> players = new ArrayList<>();
            while (iterator.hasNext()) {
                SGPlayer sgPlayer = iterator.next();
                Player p = SurvivalGames.getInstance().getServer().getPlayer(sgPlayer.getUuid());
                if (p == null) {
                    SurvivalGames.getInstance().getGame().handleDeath(sgPlayer);
                } else {
                    players.add(p);
                }
            }

            if (players.size() == 1) {
                SurvivalGames.getInstance().getGame().checkForEndGame();
            } else if (players.size() == 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
            } else {
                Collections.shuffle(players);
                players.get(0).damage(4.0);
            }
        }

        GameTimeTask.handleCounts();
    }

    private static void handleCounts() {
        GameTimeTask.ALIVE_PLAYERS = 0;
        GameTimeTask.SPECTATOR_PLAYERS = 0;

        for (Player pl : SurvivalGames.getInstance().getServer().getOnlinePlayers()) {
            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(pl.getUniqueId());
            if (sgPlayer.getState() == SGPlayer.State.ALIVE) {
                GameTimeTask.ALIVE_PLAYERS++;
            } else {
                GameTimeTask.SPECTATOR_PLAYERS++;
            }
        }

        // Update scoreboards
        for (Player pl : SurvivalGames.getInstance().getServer().getOnlinePlayers()) {
            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(pl.getUniqueId());
            sgPlayer.updateScoreboard();
        }
    }

    public static String niceTime() {
        StringBuilder builder = new StringBuilder();
        builder.append(' ');
        if (minutes < 10) {
            builder.append('0');
        }
        builder.append(minutes);
        builder.append(':');

        if (seconds < 10) {
            builder.append('0');
        }
        builder.append(seconds);

        return builder.toString();
    }

}
