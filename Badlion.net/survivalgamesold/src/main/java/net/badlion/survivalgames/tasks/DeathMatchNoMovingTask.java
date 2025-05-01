package net.badlion.survivalgames.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.SGPlayer;
import net.badlion.survivalgames.managers.SGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DeathMatchNoMovingTask extends BukkitRunnable {

    private Map<UUID, Location> locationMap;
    private int count = 10 * 20;

    public DeathMatchNoMovingTask(Map<UUID, Location> locationMap) {
        this.locationMap = locationMap;
    }

    @Override
    public void run() {
        if (count == 0) {
            Gberry.broadcastMessageNoBalance(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "DeathMatch" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + " FIGHT!");
            SurvivalGames.getInstance().setState(SurvivalGames.SGState.DEATH_MATCH);
            new EndGameTask().runTaskTimer(SurvivalGames.getInstance(), 20, 20);

            // No deathmatch arena
            if (!SurvivalGames.getInstance().getGame().getGWorld().getYml().getBoolean("deathmatch_arena")) {
                new DeathMatchNoLeavingBoundaryTask(this.locationMap).runTaskTimer(SurvivalGames.getInstance(), 0, 5);
            }

            GameTimeTask.minutes = 3;
            GameTimeTask.seconds = 0;

            for (SGPlayer sgPlayer : SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE)) {
                if (sgPlayer.getKills() == 0) {
                    sgPlayer.setNoKillsBeforeDM();
                }
            }

            this.cancel();
            return;
        } else if (SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE).size() == 1) {
            this.cancel();
            return;
        } else if (count % 20 == 0) {
            Gberry.broadcastMessageNoBalance(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "DeathMatch" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + " Will start in " + ChatColor.GREEN + (this.count / 20) + ChatColor.GOLD + " seconds");
        }

        Set<SGPlayer> sgPlayers = SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE);
        for (SGPlayer sgPlayer : sgPlayers) {
            Player player = SurvivalGames.getInstance().getServer().getPlayer(sgPlayer.getUuid());
            if (player != null) {
                Location prevLocation = this.locationMap.get(player.getUniqueId());
                if (player.getLocation().getBlockX() != prevLocation.getBlockX() || player.getLocation().getBlockZ() != prevLocation.getBlockZ()) {
                    Location newLocation = player.getLocation();
                    newLocation.setX(prevLocation.getX());
                    newLocation.setZ(prevLocation.getZ());

                    player.teleport(newLocation);
                }
            }
        }

        count--;
    }

}
