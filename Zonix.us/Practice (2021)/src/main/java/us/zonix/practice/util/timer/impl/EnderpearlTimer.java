package us.zonix.practice.util.timer.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import us.zonix.practice.util.timer.PlayerTimer;

public class EnderpearlTimer extends PlayerTimer implements Listener {
    public EnderpearlTimer() {
        super("Enderpearl", TimeUnit.SECONDS.toMillis(15L));
    }

    @Override
    protected void handleExpiry(Player player, UUID playerUUID) {
        super.handleExpiry(player, playerUUID);
        if (player != null) {
            ;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.hasItem()) {
            Player player = event.getPlayer();
            if (event.getItem().getType() == Material.ENDER_PEARL) {
                long cooldown = this.getRemaining(player);
                if (cooldown > 0L) {
                    event.setCancelled(true);
                    player.sendMessage(
                        ChatColor.RED
                            + "You cannot use this for another "
                            + ChatColor.RED.toString()
                            + ChatColor.BOLD
                            + DurationFormatUtils.formatDurationWords(cooldown, true, true).replace(" ", ChatColor.RESET.toString() + ChatColor.RED + " ")
                            + "."
                    );
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onPearlLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof EnderPearl) {
            Player player = (Player)event.getEntity().getShooter();
            this.setCooldown(player, player.getUniqueId());
        }
    }

    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        if (event.getCause() == TeleportCause.ENDER_PEARL) {
            Player player = event.getPlayer();
            if (this.getRemaining(player) != 0L && event.isCancelled()) {
                this.clearCooldown(player);
            }

            event.getTo().setX((double)event.getTo().getBlockX() + 0.5);
            event.getTo().setZ((double)event.getTo().getBlockZ() + 0.5);
            if (event.getTo().getBlock().getType() != Material.AIR) {
                event.getTo().setY(event.getTo().getY() - (event.getTo().getY() - (double)event.getTo().getBlockY()));
            }
        }
    }
}
