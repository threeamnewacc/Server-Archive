package net.badlion.uhc.listeners;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.events.BorderShrinkEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.util.ScatterUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerItemsDroppedFromDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.HashSet;
import java.util.Set;

public class WorldListener implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        // Don't let chunks unload until the match starts...helps with the lag spike issue
        if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) { // GGuard handles this in the lobby, but not during the uhc countdown
		// FUCK YOU SMELLY
		if (e.getPlayer().isOp()) {
			return;
		}

		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBorderShrinkEvent(BorderShrinkEvent event) {
        // For special game modes
        if (event.isOverride()) {
            return;
        }

        // TP people out of nether
        if (event.getNewRadius() == 500) {
            World world = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NETHER_NAME);
            Set<UHCTeam> uhcTeams = new HashSet<>();
            for (Player p : world.getPlayers()) {
                UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(p.getUniqueId());

                // Skip specs/mods
                if (uhcPlayer.getState() != UHCPlayer.State.PLAYER) {
                    continue;
                }

                uhcTeams.add(uhcPlayer.getTeam());
            }

            ScatterUtils.scatterTeams(uhcTeams, 500);
        }

        // From 1000 -> 500 and 500 -> 100 re-scatter teams who are too far out
        if (event.getNewRadius() == 500 || event.getNewRadius() == 100) {
            World world = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NAME);

            Set<UHCTeam> uhcTeams = new HashSet<>();
            for (Player p : world.getPlayers()) {
                // Outside of the border
                if (p.getLocation().getX() < -event.getNewRadius() || p.getLocation().getX() > event.getNewRadius()
                        || p.getLocation().getZ() < -event.getNewRadius() || p.getLocation().getZ() > event.getNewRadius()) {
                    UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(p.getUniqueId());

                    // Skip specs/mods
                    if (uhcPlayer.getState() != UHCPlayer.State.PLAYER) {
                        continue;
                    }

                    uhcTeams.add(uhcPlayer.getTeam());
                }
            }

            ScatterUtils.scatterTeams(uhcTeams, event.getNewRadius());
        }
    }

    @EventHandler
    public void onPlayerEnterNetherEvent(PlayerPortalEvent event) {
        if (BadlionUHC.getInstance().getBorderShrinkTask() != null) {
            if (BadlionUHC.getInstance().getBorderShrinkTask().currentRadius <= 500) {
                if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
                    if (event.getPlayer() != null) {
	                    event.getPlayer().sendMessage(ChatColor.RED + "Nether is now disabled.");
                    }

	                event.setCancelled(true);
                }
            }
        }
    }

    // Force items outside of the world border inside
    @EventHandler
    public void onItemsDropOutsideOfWorldEvent(PlayerItemsDroppedFromDeathEvent event) {
        for (Item item : event.getItemsDroppedOnDeath()) {
            double x = item.getLocation().getX();
            double z = item.getLocation().getZ();

            double newX = 0;
            double newZ = 0;

            int radius = BadlionUHC.getInstance().getWorldBorder().GetWorldBorder(BadlionUHC.UHCWORLD_NAME).getRadiusX();
            if (x > 0 && x > radius) {
                newX = radius - 2;
            } else if (x < 0 && x < -radius) {
                newX = -radius + 2;
            }

            if (z > 0 && z > radius) {
                newZ = radius - 2;
            } else if (z < 0 && z < -radius) {
                newZ = -radius + 2;
            }

            if (newX != 0 || newZ != 0) {
                Location newLocation = item.getLocation().clone();

                if (newX != 0) {
                    newLocation.setX(newX);
                }

                if (newZ != 0) {
                    newLocation.setZ(newZ);
                }

                newLocation.setY(newLocation.getY() + 1);
                item.teleport(newLocation);
            }
        }
    }

    /*@EventHandler(priority=EventPriority.LAST)
    public void debug1(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getLocation().getBlockX() < 16 && event.getClickedBlock().getLocation().getBlockX() >= -15 &&
                    event.getClickedBlock().getLocation().getBlockZ() < 16 && event.getClickedBlock().getLocation().getBlockZ() >= -15) {
                Player p = BadlionUHC.getInstance().getServer().getPlayer("MasterGberry");
                if (p != null) {
                    p.sendMessage("Event for block interact is " + event.isCancelled());
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LAST)
    public void debug1(BlockBreakEvent event) {
        if (event.getBlock().getLocation().getBlockX() < 16 && event.getBlock().getLocation().getBlockX() >= -15 &&
                    event.getBlock().getLocation().getBlockZ() < 16 && event.getBlock().getLocation().getBlockZ() >= -15) {
            Player p = BadlionUHC.getInstance().getServer().getPlayer("MasterGberry");
            if (p != null) {
                p.sendMessage("Event for block break is " + event.isCancelled());
            }
        }
    }*/

}
