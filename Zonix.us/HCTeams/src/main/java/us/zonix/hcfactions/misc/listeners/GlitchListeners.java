package us.zonix.hcfactions.misc.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.zonix.hcfactions.FactionsPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.zonix.hcfactions.FactionsPlugin;

public class GlitchListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            if (event.getPlayer().getLocation().getBlockY() > event.getBlock().getLocation().getBlockY()) {
                event.getPlayer().teleport(event.getPlayer().getLocation());
                event.getPlayer().setVelocity(new Vector());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getPlayer().setVelocity(new Vector(0, -0.25, 0));
                    }
                }.runTaskLaterAsynchronously(FactionsPlugin.getInstance(), 4L);
            }
        }
    }

    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleMoveEvent(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Boat) {
            if (event.getVehicle().getLocation().getBlock().getType() != Material.AIR) {
                if (event.getVehicle().getPassenger() != null) {
                    event.getVehicle().getPassenger().eject();
                    event.getVehicle().getPassenger().teleport(event.getVehicle().getLocation().setDirection(event.getVehicle().getPassenger().getLocation().getDirection()));
                }

                event.getVehicle().remove();
                event.getVehicle().getWorld().dropItemNaturally(event.getVehicle().getLocation(), new ItemStack(Material.BOAT));
            }
        }
    }

    @EventHandler
    public void onVehicleDestroyEvent(VehicleDestroyEvent event) {
        if (event.getVehicle() instanceof Boat) {
            if (event.getVehicle().getPassenger() != null) {
                event.getVehicle().getPassenger().eject();
                event.getVehicle().getPassenger().teleport(event.getVehicle().getLocation().setDirection(event.getVehicle().getPassenger().getLocation().getDirection()));
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        event.setCancelled(true);
    }


    @EventHandler
    public void onEntityDamageEvent(EntityDamageByEntityEvent event) {

        if(event.getDamager() instanceof Monster && event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }


}
