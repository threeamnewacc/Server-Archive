package net.badlion.gguard.listeners;

import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class BlockListener implements Listener {
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerEmptyBucket(PlayerBucketEmptyEvent event) {
		if (event.getPlayer().isOp()) {
			return;
		}
		Block block = event.getBlockClicked();
		Location location = block.getLocation();
        // Cheat and add 0.1 to fix blocks
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location.add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowedBucketPlacements()) {
			if (event.getPlayer() != null) {
				if (GGuard.VERBOSE) {
					event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to use buckets here.");
				}
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockMoveEvent(BlockFromToEvent event) {
		Block block = event.getToBlock();
		Location location = block.getLocation();
        // Cheat and add 0.1 to fix blocks
        ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location.add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowBlockMovement()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onBucketUsage(PlayerInteractEvent event) {
		if (event.getPlayer().isOp()) {
			return;
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getPlayer().getItemInHand() != null) {
				if (event.getPlayer().getItemInHand().getType() == Material.BUCKET) {
					Block block = event.getClickedBlock();
					Location location = block.getLocation();
                    // Cheat and add 0.1 to fix blocks
                    ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location.add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
					if (region != null && !region.isAllowedBucketPlacements()) {
						if (GGuard.VERBOSE) {
							event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to do this here.");
						}
						event.setUseInteractedBlock(Event.Result.DENY);
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBucketFill(PlayerBucketFillEvent event) {
		if (event.getPlayer().isOp()) {
			return;
		}

		Location location = event.getBlockClicked().getLocation();
        // Cheat and add 0.1 to fix blocks
        ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location.add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowedBucketPlacements()) {
			if (GGuard.VERBOSE) {
				event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to do this here.");
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().isOp()) {
			return;
		}

		Block block = event.getBlock();
		Location location = block.getLocation();
        // Cheat and add 0.1 to fix blocks
        ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location.add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowBrokenBlocks()) {
			if (event.getPlayer() != null) {
				if (GGuard.VERBOSE) {
					event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to break blocks here.");
				}
			}
			event.setCancelled(true);
		}

		if (region != null && region.getDisallowedBreakBlocks() != null && !region.getDisallowedBreakBlocks().isEmpty()) {
			for (String id : region.getDisallowedBreakBlocks().split(",")) {
				String[] ids = id.split(":");
				Integer blockId, blockData;
				if (ids.length == 2) {
					blockId = Integer.parseInt(ids[0]);
					blockData = Integer.parseInt(ids[1]);
				} else {
					blockId = Integer.parseInt(ids[0]);
					blockData = null;
				}

				if (block.getState().getTypeId() == blockId) {
					if (blockData != null) {
						if (block.getState().getData().getItemTypeId() == blockData) {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				}

			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().isOp()) {
			return;
		}

		Block block = event.getBlock();
		Location location = block.getLocation();
        // Cheat and add 0.1 to fix blocks
        ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location.add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowPlacedBlocks()) {
            if (block.getType() == Material.FIRE && (region.isAllowFire() || region.isAllowFireIgniteByPlayer())) {
                return;
            }

			if (event.getPlayer() != null) {
				if (GGuard.VERBOSE) {
					event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to place blocks here.");
				}
			}
			event.setCancelled(true);
		}

		if (region != null && region.getDisallowedPlaceBlocks() != null && !region.getDisallowedPlaceBlocks().isEmpty()) {
			for (String id : region.getDisallowedPlaceBlocks().split(",")) {
				String[] ids = id.split(":");
				Integer blockId, blockData;
				if (ids.length == 2) {
					blockId = Integer.parseInt(ids[0]);
					blockData = Integer.parseInt(ids[1]);
				} else {
					blockId = Integer.parseInt(ids[0]);
					blockData = null;
				}

				if (block.getState().getTypeId() == blockId) {
					if (blockData != null) {
						if (block.getState().getData().getItemTypeId() == blockData) {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				}

			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null && event.getPlayer().isOp()) {
            return;
        }

		Block block = event.getBlock();
        Location location = block.getLocation();
        // Cheat and add 0.1 to fix blocks
        ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location.add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());

        if (event.getPlayer() != null) {
            if (region != null && !region.isAllowFireIgniteByPlayer()) {
                event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to ignite blocks here.");
                event.setCancelled(true);
            }
        } else {
            if (region != null && !region.isAllowFire()) {
                event.setCancelled(true);
            }
        }
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockBurn(BlockBurnEvent event) {
		Block block = event.getBlock();
        Location location = block.getLocation();
        // Cheat and add 0.1 to fix blocks
        ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location.add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowFire()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockPostionExtend(BlockPistonExtendEvent event) {
        // Cheat and add 0.1 to fix blocks
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowPistonUsage()) {
			event.setCancelled(true);
			return;
		}
		
		for (Block block : event.getBlocks()) {
			// Get the location that this block will end up
			// Thanks t00thpick1 for telling me that the BlockFace contains the difference in blocks
			Location newLocation = new Location(block.getLocation().getWorld(), block.getLocation().getBlockX() + event.getDirection().getModX(), 
					block.getLocation().getY() + event.getDirection().getModY(), block.getLocation().getBlockZ() + event.getDirection().getModZ());

				
			// Check original block location
            // Cheat and add 0.1 to fix blocks
			region = GGuard.getInstance().getProtectedRegion(block.getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowPistonUsage()) {
				event.setCancelled(true);
				return;
			}

            // Cheat and add 0.1 to fix blocks
			region = GGuard.getInstance().getProtectedRegion(newLocation.add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
			// Check new block location
			if (region != null && !region.isAllowPistonUsage()) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR) 
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        // Cheat and add 0.1 to fix blocks
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getRetractLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowPistonUsage()) {
			event.setCancelled(true);
			return;
		}

        // Cheat and add 0.1 to fix blocks
		region = GGuard.getInstance().getProtectedRegion(event.getBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowPistonUsage()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onEndermanMoveBlock(EntityChangeBlockEvent event) {
		if (event.getEntity() instanceof Enderman) {
			Block block = event.getBlock();
            // Cheat and add 0.1 to fix blocks
			ProtectedRegion region = GGuard.getInstance().getProtectedRegion(block.getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowEndermanMoveBlocks()) {
				// Base it on the block location
				event.setCancelled(true);
			}
		}
	}

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlantGrow(BlockGrowEvent event) {
        // Cheat and add 0.1 to fix blocks
        ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
        if (region != null && !region.isAllowPlantGrowth()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlantSpread(BlockSpreadEvent event) {
	    Material sourceType = event.getSource().getType();

	    // Plant spread?
	    if (sourceType != Material.FIRE && sourceType != Material.LAVA && sourceType != Material.STATIONARY_LAVA) {
		    // Cheat and add 0.1 to fix blocks
		    ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		    if (region != null && !region.isAllowPlantSpread()) {
			    event.setCancelled(true);
		    }
	    } else { // Fire spread
		    // Cheat and add 0.1 to fix blocks
		    ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		    if (region != null && !region.isAllowFireSpread()) {
			    event.setCancelled(true);
		    }
	    }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onStructureGrow(StructureGrowEvent event) {
        if (event.getPlayer() != null && event.getPlayer().isOp())  {
            return;
        }

        // Cheat and add 0.1 to fix blocks
        ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
        if (region != null && !region.isAllowPlantGrowth()) {
            event.setCancelled(true);
            if (event.getPlayer() != null) {
                event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to grow plants here.");
            }
        }
    }

	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockChangedByEntity(EntityChangeBlockEvent event) {
		// Let block's still fall naturally
		if (event.getEntityType() == EntityType.FALLING_BLOCK) {
			return;
		}

		// This stops grass from being eaten etc
        // Cheat and add 0.1 to fix blocks
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowBlockChangesByEntities()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onLeafDecay(LeavesDecayEvent event) {
        // Cheat and add 0.1 to fix blocks
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowLeafDecay()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onBoneMealUse(PlayerInteractEvent event) {
		if (event.getPlayer().isOp()) {
			return;
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			// Bone meal
			if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.INK_SACK && player.getItemInHand().getData().getData() == 15) {
                // Cheat and add 0.1 to fix blocks
				ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getClickedBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
				if (region != null && !region.isAllowBoneMealUsage()) {
					player.sendMessage(ChatColor.RED + "Cannot use bone meal here.");
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void soilTrample(PlayerInteractEvent event) {
		if (event.getPlayer().isOp()) {
			return;
		}

		if (event.getAction() == Action.PHYSICAL) {
			ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getClickedBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowPlantTrampling()) {
				if (event.getClickedBlock().getType() == Material.SOIL) { // Disable soil trampling
					event.setCancelled(true);
				}
			}
		}
	}

}
