package net.badlion.survivalgames.tasks;

import net.badlion.survivalgames.SGPlayer;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DeathMatchNoLeavingBoundaryTask extends BukkitRunnable {

    public static int MAX_DISTANCE = SurvivalGames.getInstance().getGame().getGWorld().getYml().getInt("deathmatch_radius");

    private Map<UUID, Location> locationMap;

    public DeathMatchNoLeavingBoundaryTask(Map<UUID, Location> locationMap) {
        this.locationMap = locationMap;
    }

    @Override
    public void run() {
        Set<SGPlayer> sgPlayers = SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE);
        for (SGPlayer sgPlayer : sgPlayers) {
            Player player = SurvivalGames.getInstance().getServer().getPlayer(sgPlayer.getUuid());
            if (player != null) {
                Location prevLocation = this.locationMap.get(player.getUniqueId());
                if (player.getLocation().distance(SurvivalGames.getInstance().getGame().getSgWorld().getDeathMatchCenterLocation()) > DeathMatchNoLeavingBoundaryTask.MAX_DISTANCE) {
                    player.teleport(prevLocation);
                    player.sendMessage(ChatColor.RED + "Cannot leave the death match area!");
                } else {
                    this.locationMap.put(player.getUniqueId(), player.getLocation());
                }
            }
        }
    }


}
