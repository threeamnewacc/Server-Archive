package net.badlion.survivalgames.tasks;

import net.badlion.survivalgames.SurvivalGames;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class EndGameTask extends BukkitRunnable {

    public static int FORCE_END = 60 * 5;
    public static int count = 0;
    public static boolean actuallyEnded;
    public static int endCount = -1;

    @Override
    public void run() {
        if (EndGameTask.endCount > 0 && EndGameTask.count == EndGameTask.endCount) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
        } else if (EndGameTask.actuallyEnded && EndGameTask.endCount == -1) {
            EndGameTask.endCount = EndGameTask.count + 10;
        } else if (EndGameTask.count == FORCE_END) {
            // TODO: Force winners/stats/etc
            SurvivalGames.getInstance().getGame().saveStatsAndEndGame();
        } else {
            EndGameTask.count++;
        }
    }

}
