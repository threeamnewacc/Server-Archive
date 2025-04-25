package us.zonix.practice.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.v1_8_R3.EntityEnderPearl;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderPearl;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;

public class EnderpearlListener implements Listener {
    private Map<EnderPearl, Location> validLocations;
    private Practice plugin;

    public EnderpearlListener(Practice plugin) {
        this.plugin = plugin;
        this.validLocations = new HashMap<>();
        this.runCheck();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            this.validLocations.put((EnderPearl)event.getEntity(), event.getEntity().getLocation());
        }
    }

    @EventHandler(
        priority = EventPriority.LOWEST
    )
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == TeleportCause.ENDER_PEARL) {
            EnderPearl pearl = this.lookupPearl(event.getPlayer(), event.getTo());
            if (pearl != null) {
                Location validLocation = this.validLocations.get(pearl);
                if (validLocation != null) {
                    validLocation.setPitch(event.getPlayer().getLocation().getPitch());
                    validLocation.setYaw(event.getPlayer().getLocation().getYaw());
                    event.setTo(validLocation);
                }
            }
        }
    }

    private void runCheck() {
        (new BukkitRunnable() {
            public void run() {
                Iterator<Entry<EnderPearl, Location>> iterator = EnderpearlListener.this.validLocations.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<EnderPearl, Location> entry = iterator.next();
                    EnderPearl pearlEntity = entry.getKey();
                    if (pearlEntity.isDead()) {
                        iterator.remove();
                    } else {
                        EntityEnderPearl entityEnderPearl = ((CraftEnderPearl)pearlEntity).getHandle();
                        World worldServer = entityEnderPearl.world;
                        if (worldServer.getCubes(entityEnderPearl, entityEnderPearl.getBoundingBox().grow(0.25, 0.25, 0.25)).isEmpty()) {
                            entry.setValue(pearlEntity.getLocation());
                        }
                    }
                }
            }
        }).runTaskTimerAsynchronously(this.plugin, 1L, 1L);
    }

    private EnderPearl lookupPearl(Player player, Location to) {
        double distance = Double.MAX_VALUE;
        EnderPearl canidate = null;

        for (EnderPearl enderpearl : this.validLocations.keySet()) {
            double sqrt = to.distanceSquared(enderpearl.getLocation());
            if (enderpearl.getShooter() == player && sqrt < distance) {
                distance = sqrt;
                canidate = enderpearl;
            }
        }

        return canidate;
    }
}
