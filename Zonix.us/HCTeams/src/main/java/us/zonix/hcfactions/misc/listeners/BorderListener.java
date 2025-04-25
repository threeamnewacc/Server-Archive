package us.zonix.hcfactions.misc.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.*;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.FactionsPlugin;

public class BorderListener implements Listener {

    private static final int WORLD_BORDER = FactionsPlugin.getInstance().getMainConfig().getInt("FACTION_GENERAL.WORLD_BORDER");
    private static final int NETHER_BORDER = FactionsPlugin.getInstance().getMainConfig().getInt("FACTION_GENERAL.NETHER_BORDER");
    private static final int END_BORDER = FactionsPlugin.getInstance().getMainConfig().getInt("FACTION_GENERAL.END_BORDER");
    private static final int BORDER_OFFSET_TELEPORTS = 50;

    public static boolean isWithinBorder(final Location location) {

        int borderSize = 0;

        if(location.getWorld().getEnvironment() == World.Environment.NORMAL) {
            borderSize = WORLD_BORDER;
        } else if(location.getWorld().getEnvironment() == World.Environment.NETHER) {
            borderSize = NETHER_BORDER;
        } else if(location.getWorld().getEnvironment() == World.Environment.THE_END) {
            borderSize = END_BORDER;
        }

        return Math.abs(location.getBlockX()) <= borderSize && Math.abs(location.getBlockZ()) <= borderSize;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreaturePreSpawn(final CreatureSpawnEvent event) {

        if(!isWithinBorder(event.getLocation())) {
            event.setCancelled(true);
        }

        if(event.getEntityType() == EntityType.HORSE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketFillEvent event) {
        if(!isWithinBorder(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot fill buckets past the border.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        if(!isWithinBorder(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot empty buckets past the border.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if(!isWithinBorder(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks past the border.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if(!isWithinBorder(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks past the border.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Location to = event.getTo();

        if(!isWithinBorder(to)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot go past the border.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        final Location to = event.getTo();
        if(!isWithinBorder(to)) {
            final PlayerTeleportEvent.TeleportCause cause = event.getCause();
            if(cause != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL || (cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && isWithinBorder(event.getFrom()))) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot go past the border.");
            } else {
                final World.Environment toEnvironment = to.getWorld().getEnvironment();
                if(toEnvironment != World.Environment.NORMAL) {
                    return;
                }
                final int x = to.getBlockX();
                final int z = to.getBlockZ();
                final int borderSize = FactionsPlugin.getInstance().getMainConfig().getInt("FACTION_GENERAL.WORLD_BORDER");
                boolean extended = false;
                if(Math.abs(x) > borderSize) {
                    to.setX((x > 0) ? (borderSize - BORDER_OFFSET_TELEPORTS) : (-borderSize + BORDER_OFFSET_TELEPORTS));
                    extended = true;
                }
                if(Math.abs(z) > borderSize) {
                    to.setZ((z > 0) ? (borderSize - BORDER_OFFSET_TELEPORTS) : (-borderSize + BORDER_OFFSET_TELEPORTS));
                    extended = true;
                }
                if(extended) {
                    to.add(0.5, 0.0, 0.5);
                    event.setTo(to);
                    event.getPlayer().sendMessage(ChatColor.RED + "This portals travel location was over the border. It has been moved inwards.");
                }
            }
        }
    }
}
