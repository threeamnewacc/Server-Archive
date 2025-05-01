package net.badlion.uhc.listeners;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.commands.handlers.GenerateSpawnsCommandHandler;
import net.badlion.uhc.tasks.TrappedPortalTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPostPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TrappedPortalListener implements Listener {

	// Pulled info from : https://github.com/Ribesg/NPlugins/blob/master/NWorld/src/main/java/fr/ribesg/bukkit/nworld/world/GeneralWorld.java

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (event.getPlayer().getLocation().getBlock().getType() == Material.PORTAL) {
					event.getPlayer().teleport(GenerateSpawnsCommandHandler.getNewLocation());
					event.getPlayer().sendMessage(ChatColor.GOLD + "The server has detected a possible trapped portal, you have been teleported to a random location.");
				}
			}
		}, 1);
	}
	
	@EventHandler
	public void onTeleport(final PlayerPostPortalEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {

			// Prevent trapped portals
			if (event.getTo().getWorld().getName().equals(BadlionUHC.UHCWORLD_NAME) || event.getTo().getWorld().getName().equals(BadlionUHC.UHCWORLD_NETHER_NAME)) {
				// Always check just in case
				new TrappedPortalTask(event.getFrom(), event.getPlayer()).runTaskTimer(BadlionUHC.getInstance(), 20L, 20L);

				final Location to = event.getTo();
                Block block = to.getBlock();
                // What if the portal material is behind (4 checks)
                Block block2 = to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1);
                if (block2.getType() == Material.PORTAL) {
                    // break blocks left and right
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ() - 1).setType(Material.AIR);

                    // These are the blocks above
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);

                    // These are the 2x2 on either side of the portal
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);

                    // These are the corner blocks
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);

                    // Nether fences
                    for (int i = 0; i < 4; i++) {
                        // Nether brick fences 2x2
                        to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ()).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ()).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);

                        // Corner nether fences
                        to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);
                    }

                    return;
                }
                block2 = to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() + 1);
                if (block2.getType() == Material.PORTAL) {
                    // break blocks left and right
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ() + 1).setType(Material.AIR);

                    // These are the blocks above
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);

                    // These are the 2x2 on either side of the portal
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);

                    // These are the corner blocks
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);

                    // Nether fences
                    for (int i = 0; i < 4; i++) {
                        // Nether brick fences 2x2
                        to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ()).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ()).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);

                        // Corner nether fences
                        to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
                    }
                    return;
                }
                block2 = to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ());
                if (block2.getType() == Material.PORTAL) {
                    // break blocks left and right
                    to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 2, block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 2, block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ() - 1).setType(Material.AIR);

                    // These are the blocks above
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);

                    // These are the 2x2 on either side of the portal
                    to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);

                    // These are the corner blocks
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);

                    // Nether fences
                    for (int i = 0; i < 4; i++) {
                        // Nether brick fences 2x2
                        to.getWorld().getBlockAt(block.getX(), block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX(), block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);

                        // Corner nether fences
                        to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                    }

                    // Copyright signs nigga
                    /*Block b = to.getWorld().getBlockAt(block.getX() + 2, block.getY() + 1, block.getZ());
                    b.setType(Material.WALL_SIGN);
                    b.setData((byte) 0);
                    Sign sign = (Sign) b.getState();
                    sign.setLine(0, "Badlion Network");
                    sign.setLine(1, "Anti Trapped");
                    sign.setLine(2, "Portal");
                    sign.setLine(3, "Copyright 2015");
                    sign.update();
                    b = to.getWorld().getBlockAt(block.getX() - 3, block.getY() + 1, block.getZ());
                    b.setType(Material.WALL_SIGN);
                    b.setData((byte) 3);
                    sign = (Sign) b.getState();
                    sign.setLine(0, "Badlion Network");
                    sign.setLine(1, "Anti Trapped");
                    sign.setLine(2, "Portal");
                    sign.setLine(3, "Copyright 2015");
                    sign.update();*/

                    return;
                }
                block2 = to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ());
                if (block2.getType() == Material.PORTAL) {
                    // break blocks left and right
                    to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 2, block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 2, block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ() - 1).setType(Material.AIR);

                    // These are the blocks above
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);

                    // These are the 2x2 on either side of the portal
                    to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);

                    // These are the corner blocks
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
                    to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);

                    // Nether fences
                    for (int i = 0; i < 4; i++) {
                        // Nether brick fences 2x2
                        to.getWorld().getBlockAt(block.getX(), block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX(), block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);

                        // Corner nether fences
                        to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                        to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                    }

                    return;
                }
                // Break blocks in front of them
                if (yawToFace(to.getYaw(), false) == BlockFace.NORTH) {
                    to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
                } else if (yawToFace(to.getYaw(), false) == BlockFace.EAST) {
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
                } else if (yawToFace(to.getYaw(), false) == BlockFace.SOUTH) {
                    to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() + 1).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
                } else if (yawToFace(to.getYaw(), false) == BlockFace.WEST) {
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ()).setType(Material.AIR);
                    to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
                }
			}
		}
	}
	
	public static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    public static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
   
    /**
    * Gets the horizontal Block Face from a given yaw angle<br>
    * This includes the NORTH_WEST faces
    *
    * @param yaw angle
    * @return The Block Face of the angle
    */
    public static BlockFace yawToFace(float yaw) {
        return yawToFace(yaw, true);
    }
 
    /**
    * Gets the horizontal Block Face from a given yaw angle
    *
    * @param yaw angle
    * @param useSubCardinalDirections setting, True to allow NORTH_WEST to be returned
    * @return The Block Face of the angle
    */
    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return radial[Math.round(yaw / 45f) & 0x7];
        } else {
            return axis[Math.round(yaw / 90f) & 0x3];
        }
    }
    
    /** Little structure used to return 3 values. */
    private class PortalEventResult {

            public final Location destination;
            public final boolean  useTravelAgent;
            public final boolean  cancelEvent;

            private PortalEventResult(final Location destination, final boolean useTravelAgent, final boolean cancelEvent) {
                    this.destination = destination;
                    this.useTravelAgent = useTravelAgent;
                    this.cancelEvent = cancelEvent;
            }
    }

    /*private PortalEventResult handlePortalEvent(final Location fromLocation, final PlayerTeleportEvent.TeleportCause teleportCause, final TravelAgent portalTravelAgent) {
            // In case of error or other good reasons
            final PortalEventResult cancel = new PortalEventResult(null, false, true);

            final World fromWorld = fromLocation.getWorld();
            final String worldName = fromWorld.getName();
            final World.Environment sourceWorldEnvironment = fromWorld.getEnvironment();
            final GeneralWorld world = plugin.getWorlds().get(worldName);

            if (GeneralWorld.WorldType.isStock(world)) {
                    // Do not override any Bukkit behaviour
                    return null;
            }

            if (teleportCause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
                    if (sourceWorldEnvironment == World.Environment.NORMAL) {
                            // NORMAL => NETHER
                            final AdditionalWorld normalWorld = (AdditionalWorld) world;
                            final AdditionalSubWorld netherWorld = normalWorld.getNetherWorld();
                            if (netherWorld == null) {
                                    return cancel;
                            }
                            final Location averageDestination = normalToNetherLocation(netherWorld.getBukkitWorld(), fromLocation);
                            final Location actualDestination = portalTravelAgent.findOrCreate(averageDestination);
                            return new PortalEventResult(actualDestination, true, false);
                    } else if (sourceWorldEnvironment == World.Environment.NETHER) {
                            // NETHER => NORMAL
                            final AdditionalSubWorld netherWorld = (AdditionalSubWorld) world;
                            final AdditionalWorld normalWorld = netherWorld.getParentWorld();
                            if (normalWorld == null) {
                                    return cancel;
                            }
                            final Location averageDestination = netherToNormalLocation(normalWorld.getBukkitWorld(), fromLocation);
                            final Location actualDestination = portalTravelAgent.findOrCreate(averageDestination);
                            return new PortalEventResult(actualDestination, true, false);
                    } else if (sourceWorldEnvironment == World.Environment.THE_END) {
                            // END => NETHER
                            // Buggy in Vanilla, do not handle and prevent bugs.
                            return cancel;
                    }
            } else if (teleportCause == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
                    if (sourceWorldEnvironment == World.Environment.NORMAL) {
                            // NORMAL => END
                            final AdditionalWorld normalWorld = (AdditionalWorld) world;
                            final AdditionalSubWorld endWorld = normalWorld.getEndWorld();
                            if (endWorld == null) {
                                    return cancel;
                            }
                            final Location actualDestination = getEndLocation(endWorld.getBukkitWorld());
                            portalTravelAgent.createPortal(actualDestination);
                            return new PortalEventResult(actualDestination, true, false);
                    } else if (sourceWorldEnvironment == World.Environment.NETHER) {
                            // NETHER => END (WTF)
                            // Not possible in Vanilla, do not handle and prevent eventual bugs.
                            return cancel;
                    } else if (sourceWorldEnvironment == World.Environment.THE_END) {
                            // END => NORMAL
                            // Just teleport to spawn
                            final AdditionalSubWorld endWorld = (AdditionalSubWorld) world;
                            final AdditionalWorld normalWorld = endWorld.getParentWorld();
                            if (normalWorld == null) {
                                    return cancel;
                            }
                            final Location actualDestination = normalWorld.getSpawnLocation().toBukkitLocation();
                            return new PortalEventResult(actualDestination, false, false);
                    }
            }
            return null;
    }*/

    /**
     * Given a Location in a Normal World and a Nether World, this builds the
     * corresponding Location in the Nether world.
     *
     * @param world      The destination World
     * @param originalLocation The source Location
     *
     * @return The destination Location
     */
    private Location normalToNetherLocation(final World world, final Location originalLocation) {
            if (world == null || originalLocation == null) {
                    return null;
            }

            // Get original coordinates
            double x = originalLocation.getX();
            double y = originalLocation.getY();
            double z = originalLocation.getZ();

            // Transform them
            x /= 8;
            y /= 2;
            z /= 8;

            // Create the Location and return it
            return new Location(world, x, y, z);
    }

    /**
     * Given a Location in a Nether World and a Normal World, this builds the
     * corresponding Location in the Normal world.
     *
     * @param world        The destination World
     * @param originalLocation The source Location
     *
     * @return The destination Location
     */
    private Location netherToNormalLocation(final World world, final Location originalLocation) {
            if (world == null || originalLocation == null) {
                    return null;
            }

            // Get original coordinates
            double x = originalLocation.getX();
            double y = originalLocation.getY();
            double z = originalLocation.getZ();

            // Transform them
            x *= 8;
            y *= 2;
            z *= 8;

            // Try to be on the ground !
            y = Math.min(y, originalLocation.getWorld().getHighestBlockYAt((int) x, (int) z));

            // Create the Location and return it
            return new Location(world, x, y, z);
    }

    /**
     * Builds a new spawn Location for this End world
     *
     * @param endWorld The End World
     *
     * @return The spawn / warp Location
     */
    private Location getEndLocation(final World endWorld) {
            return endWorld == null ? null : new Location(endWorld, 100, 50, 0);
    }

}
