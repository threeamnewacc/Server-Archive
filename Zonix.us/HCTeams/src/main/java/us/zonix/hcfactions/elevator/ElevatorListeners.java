package us.zonix.hcfactions.elevator;

import us.zonix.hcfactions.FactionsPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

public class ElevatorListeners implements Listener {

    @EventHandler
    public void onEntityLeaveEvent(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Elevator elevator = Elevator.getByEntity(event.getDismounted());

            if (elevator != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Block block = event.getDismounted().getLocation().clone().add(0, -1, 0).getBlock();

                        Location toTeleport;
                        if (!(block.isEmpty()) && block.getType() == Material.SOUL_SAND) {
                            toTeleport = elevator.getLocation(true);
                        } else {
                            toTeleport = elevator.getLocation(true);
                        }

                        if (toTeleport != null) {
                            player.teleport(toTeleport.setDirection(player.getLocation().getDirection()));
                        }

                    }
                }.runTaskAsynchronously(FactionsPlugin.getInstance());
            }
        }

    }

}
