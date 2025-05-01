package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.tasks.TrappedPortalTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPostPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TrappedPortalListener implements Listener {
	
	private GFactions plugin;
	
	public TrappedPortalListener(GFactions plugin) {
		this.plugin = plugin;
	}
	
	// Pulled info from : https://github.com/Ribesg/NPlugins/blob/master/NWorld/src/main/java/fr/ribesg/bukkit/nworld/world/GeneralWorld.java

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
			@Override
			public void run() {
				if (event.getPlayer().getLocation().getBlock().getType() == Material.PORTAL) {
					event.getPlayer().teleport(TrappedPortalListener.this.plugin.getSpawnLocation());
					event.getPlayer().sendMessage(ChatColor.GOLD + "The server has detected a possible trapped portal, you have been teleported to spawn.");
				}
			}
		}, 1);
	}
	
	@EventHandler
	public void onTeleport(final PlayerPostPortalEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {

			// Prevent trapped portals
			if (event.getTo().getWorld().getName().equals("world") || event.getTo().getWorld().getName().equals("world_nether")) {
				// Always check just in case
				new TrappedPortalTask(this.plugin, event.getPlayer()).runTaskTimer(this.plugin, 20L, 20L);
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
