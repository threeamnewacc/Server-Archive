package net.badlion.gguard.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class ToolListener implements Listener {

    private static final int COMPASS_DISTANCE = 100;

    @EventHandler
    public void onCompassClick(PlayerInteractEvent event) {
	    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            if (event.getItem() != null && event.getItem().getType() == Material.COMPASS) {
                Player player = event.getPlayer();
                event.setCancelled(true);
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Location loc = traceThroughBlocks(
                            center(event.getClickedBlock().getLocation()),
                            player.getLocation().getDirection());
                    if (loc != null) {
                        loc.setDirection(player.getLocation().getDirection());
                        player.teleport(loc);
                    } else {
                        player.sendMessage(ChatColor.RED + "No free space found");
                    }
                } else if (event.getAction() == Action.LEFT_CLICK_AIR) {
                    Location loc = traceThroughAir(
                            player.getEyeLocation(),
                            player.getLocation().getDirection());
                    if (loc != null) {
                        loc.setDirection(player.getLocation().getDirection());
                        player.teleport(loc);
                    } else {
                        player.sendMessage(ChatColor.RED + "No block found in sight");
                    }
                } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    Location loc = traceThroughBlocks(event.getClickedBlock().getLocation(), new Vector(0, 1, 0));
                    if (loc != null) {
                        loc.setDirection(player.getLocation().getDirection());
                        player.teleport(loc);
                    }
                }
            }
        }
    }

    private Location traceThroughBlocks(Location loc, Vector direction) {
        for (int i = 0; i < COMPASS_DISTANCE; i++) {
            loc.add(direction);
            // dont go under the world
            if (loc.getY() < 0) {
                return null;
            }
            Block block = loc.getBlock();
            // try to find a free block plus another above it
            if (!isSolid(block) && !isSolid(block.getRelative(BlockFace.UP))) {
                return center(loc);
            }
        }
        return null;
    }

    private Location traceThroughAir(Location loc, Vector direction) {
        for (int i = 0; i < COMPASS_DISTANCE; i++) {
            loc.add(direction);
            // went out of the world?
            if (loc.getY() < 0 || loc.getY() > 255) {
                return null;
            }
            Block block = loc.getBlock();
            if (isSolid(block)) {
                // loc stepped into a solid block, so step back one
                loc.subtract(direction);
                return center(loc);
            }
        }
        return null;
    }

    // return a copy of the location, but in the center of the block
    private Location center(Location loc) {
        return new Location(
                loc.getWorld(),
                loc.getBlockX() + 0.5,
                loc.getBlockY(),
                loc.getBlockZ() + 0.5,
                loc.getYaw(),
                loc.getPitch());
    }

    private boolean isSolid(Block block) {
        // put this in a method incase we need to code exceptions to Material.isSolid
        return block.getType().isSolid();
    }

}
