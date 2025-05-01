package net.badlion.gfactions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Faction;
import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerPostPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.ArrayList;
import java.util.List;

public class NetherListener implements Listener {
	
	private GFactions plugin;
	private Player lastPlayerToEnterPortal;
	
	public NetherListener(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getLocation().getWorld().getName().equals("world_nether")) {
			if (event.getBlock().getType() == Material.MOB_SPAWNER) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "Cannot place spawners in the nether.");
			} else if (event.getBlock().getType() == Material.BED_BLOCK || event.getBlock().getType() == Material.BED) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Cannot place beds in the nether.");
            }
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.MOB_SPAWNER && !event.getPlayer().isOp() && event.getBlock().getWorld().getName().equals("world_nether")) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority= EventPriority.FIRST)
	public void onSilkSpawnerThing(EntityExplodeEvent event) {
		List<Block> blocks = event.blockList();
		List<Block> blocksToRemove = new ArrayList<>();
		for (Block block : blocks) {
            if (block.getLocation().getWorld().getName().equals("world_nether")) {
                if (block.getType() == Material.MOB_SPAWNER) {
                    blocksToRemove.add(block);
                }
            }
		}

		// Don't allow the blocks to be blown up
		for (Block block : blocksToRemove) {
			event.blockList().remove(block);
		}
	}

	@EventHandler
	public void onSilkSpawnTouchBlock(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.MOB_SPAWNER) {
				if (event.getClickedBlock().getWorld().getName().equals("world_nether")) {
					event.getPlayer().sendMessage(ChatColor.RED + "Cannot interact with mob spawners in the nether.");
					event.setCancelled(true);
					event.setUseInteractedBlock(Event.Result.DENY);
				}
			}
		}
	}

	/*@EventHandler
	public void onNetherPortalSpawnInMainWorld(PlayerPortalEvent event) {
	 	Location newPortalLocation = event.getPortalTravelAgent().findPortal(event.getTo());
		org.bukkit.Bukkit.getLogger().info(newPortalLocation.toString());
		org.bukkit.Bukkit.getLogger().info(event.getTo().toString());
		// HARDCODED CUZ I DONT TRUST THE SYSTEM
		if (newPortalLocation != null
					&& newPortalLocation.getWorld().getName().equals("world")
					&& newPortalLocation.getX() >= this.plugin.getWarZoneMinX() - 10
					&& newPortalLocation.getX() <= this.plugin.getWarZoneMaxX() + 10
					&& newPortalLocation.getZ() >= this.plugin.getWarZoneMinZ() - 10
					&& newPortalLocation.getX() <= this.plugin.getWarZoneMaxZ() + 10) {
			event.setCancelled(true);
		}
	}*/

    @EventHandler
    public void onEntityEnterPortal(EntityPortalEvent event) {
        EntityType type = event.getEntityType();
        if (type.equals(EntityType.MINECART) || type.equals(EntityType.MINECART_CHEST) || type.equals(EntityType.MINECART_COMMAND)
                || type.equals(EntityType.MINECART_FURNACE) || type.equals(EntityType.MINECART_HOPPER) || type.equals(EntityType.MINECART_MOB_SPAWNER)
                || type.equals(EntityType.MINECART_TNT)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerEnterPortal(PlayerPortalEvent event) {
        this.lastPlayerToEnterPortal = event.getPlayer();
    }

	@EventHandler
	public void onPlayerGoThroughPortal(PlayerPostPortalEvent event) {
		this.lastPlayerToEnterPortal = null;
	}

	@EventHandler
	public void onNetherPortalCreate(PortalCreateEvent event) {
        if (event.getWorld().getName().equals("world")) {
			ArrayList<Block> blocks = event.getBlocks();
			for (Block block : blocks) {
				Location newPortalLocation = block.getLocation();
				if (newPortalLocation.getX() >= this.plugin.getWarZoneMinX() - 10
				   && newPortalLocation.getX() <= this.plugin.getWarZoneMaxX() + 10
				   && newPortalLocation.getZ() >= this.plugin.getWarZoneMinZ() - 10
				   && newPortalLocation.getZ() <= this.plugin.getWarZoneMaxZ() + 10) {
					event.setCancelled(true);

					if (this.lastPlayerToEnterPortal != null) {
						this.lastPlayerToEnterPortal.sendMessage(ChatColor.RED + "Cannot create a portal to get to the War Zone.");
					}
					return;
				}
			}
		}

        if (event.getWorld().getName().equals("world")) {
			// Check to see if last plays is in nether or not
			if (this.lastPlayerToEnterPortal != null && this.lastPlayerToEnterPortal.getLocation().getWorld().getName().equals("world_nether")) {
                ArrayList<Block> blocks = event.getBlocks();
                for (Block block : blocks) {
                    Faction faction = Board.getFactionAt(block);
                    if (!faction.getId().equals("0")) {
                        event.setCancelled(true);
                        this.lastPlayerToEnterPortal.sendMessage(ChatColor.RED + "Cannot create a portal that goes into a faction's land from the nether.");
                        return;
                    }
                }
            }
        }
	}

}
