package net.badlion.survivalgames.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import net.badlion.survivalgames.SGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathMatchCountdownTask extends BukkitRunnable {

    public static int DEATH_MATCH_TIME = 30;
    private int count = 0;

    @Override
    public void run() {
        if (this.count == DeathMatchCountdownTask.DEATH_MATCH_TIME) {
            SurvivalGames.getInstance().getGame().startDeathMatch();
            this.cancel();
        } else if (SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE).size() == 1) {
            this.cancel();
        } else if (this.count >= 25) {
            Gberry.broadcastMessageNoBalance(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "DeathMatch" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + " Players will be teleported in " + ChatColor.GREEN + (DeathMatchCountdownTask.DEATH_MATCH_TIME - this.count) + ChatColor.GOLD + " seconds");
        } else if (this.count % 10 == 0) {
            Gberry.broadcastMessageNoBalance(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "DeathMatch" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + " Players will be teleported in " + ChatColor.GREEN + (DeathMatchCountdownTask.DEATH_MATCH_TIME - this.count) + ChatColor.GOLD + " seconds");
        }

        ++count;

    }

}
