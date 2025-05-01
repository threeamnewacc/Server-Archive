package net.badlion.gberry.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.PlayerRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class LimitWorldTask extends BukkitRunnable implements Listener {

    public LimitWorldTask() {
        Gberry.plugin.getServer().getPluginManager().registerEvents(this, Gberry.plugin);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTo() != null) {
            if (event.getTo().getX() > 0 && event.getTo().getX() > Gberry.blockLimit) {
                event.getTo().setX(Gberry.blockLimit);
            }

            if (event.getTo().getX() < 0 && event.getTo().getX() < -Gberry.blockLimit) {
                event.getTo().setX(-Gberry.blockLimit);
            }

            if (event.getTo().getZ() > 0 && event.getTo().getZ() > Gberry.blockLimit) {
                event.getTo().setZ(Gberry.blockLimit);
            }

            if (event.getTo().getZ() < 0 && event.getTo().getZ() < -Gberry.blockLimit) {
                event.getTo().setZ(-Gberry.blockLimit);
            }
        }
    }

    public void run() {
        Gberry.distributeTask(Gberry.plugin, new PlayerRunnable() {
            @Override
            public void run(Player player) {
                Location newLocation = player.getLocation().clone();
                boolean flag = false;

                if (player.getLocation().getX() > 0 && player.getLocation().getX() > Gberry.blockLimit) {
                    newLocation.setX(Gberry.blockLimit);
                    flag = true;
                }

                if (player.getLocation().getX() < 0 && player.getLocation().getX() < -Gberry.blockLimit) {
                    newLocation.setX(-Gberry.blockLimit);
                    flag = true;
                }

                if (player.getLocation().getZ() > 0 && player.getLocation().getZ() > Gberry.blockLimit) {
                    newLocation.setZ(Gberry.blockLimit);
                    flag = true;
                }

                if (player.getLocation().getZ() < 0 && player.getLocation().getZ() < -Gberry.blockLimit) {
                    newLocation.setZ(-Gberry.blockLimit);
                    flag = true;
                }

                if (flag) {
                    player.teleport(newLocation);
                }
            }
        });
    }

}
