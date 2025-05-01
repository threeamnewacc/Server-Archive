package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import org.bukkit.event.Listener;

public class SpongeListener implements Listener {

    private GFactions plugin;

    public SpongeListener(GFactions plugin) {
        this.plugin = plugin;
    }

//    @EventHandler
//    public void waterFlow(BlockFromToEvent e) {
//        if (e.getBlock().getType().equals(Material.WATER) || e.getBlock().getType().equals(Material.STATIONARY_WATER)) {
//            // Look for a nearby sponge
//            int x = e.getBlock().getX();
//            int y = e.getBlock().getY();
//            int z = e.getBlock().getZ();
//
//            World world = e.getBlock().getWorld();
//            for (int cx = -3; cx <= 3; cx++) {
//                for (int cy = -3; cy <= 3; cy++) {
//                    for (int cz = -3; cz <= 3; cz++) {
//                        if (world.getBlockAt(x + cx, y + cy, z + cz).getType().equals(Material.SPONGE)) {
//                            e.setCancelled(true);
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @EventHandler(ignoreCancelled = true)
//    public void spongePlace(BlockPlaceEvent e) {
//        if (e.getBlock().getType().equals(Material.SPONGE)) {
//            // GG WATER IN 5X5
//            int x = e.getBlock().getX();
//            int y = e.getBlock().getY();
//            int z = e.getBlock().getZ();
//
//            World world = e.getBlock().getWorld();
//            for (int cx = -2; cx <= 2; cx++) {
//                for (int cy = -2; cy <= 2; cy++) {
//                    for (int cz = -2; cz <= 2; cz++) {
//                        Block block = world.getBlockAt(x + cx, y + cy, z + cz);
//                        if (block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER)) {
//                            block.setType(Material.AIR);
//                        }
//                    }
//                }
//            }
//        } else if (e.getBlock().getType().equals(Material.STATIONARY_WATER) || e.getBlock().getType().equals(Material.WATER)) {
//            // Look for a nearby sponge
//            int x = e.getBlock().getX();
//            int y = e.getBlock().getY();
//            int z = e.getBlock().getZ();
//
//            World world = e.getBlock().getWorld();
//            for (int cx = -3; cx <= 3; cx++) {
//                for (int cy = -3; cy <= 3; cy++) {
//                    for (int cz = -3; cz <= 3; cz++) {
//                        Block block = world.getBlockAt(x + cx, y + cy, z + cz);
//                        if (block.getType().equals(Material.SPONGE)) {
//                            e.getBlock().setType(Material.AIR);
//                        }
//                    }
//                }
//            }
//        }
//    }

}
