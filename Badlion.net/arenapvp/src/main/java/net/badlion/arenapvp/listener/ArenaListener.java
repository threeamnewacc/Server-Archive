package net.badlion.arenapvp.listener;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.arenas.Arena;
import net.badlion.arenapvp.manager.ArenaManager;
import net.badlion.arenapvp.manager.MatchManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.gberry.Gberry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockStoneFormEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ArenaListener implements Listener {

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerFishEvent(PlayerFishEvent event) {
		if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			event.setExpToDrop(0);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		event.setDroppedExp(0);
	}


	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();
			if (arrow.getShooter() instanceof Player) {
				Player player = (Player) arrow.getShooter();

				// Player could have logged off
				if (Gberry.isPlayerOnline(player)) {
					event.getEntity().remove();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		if (event.getPlayer() != null) {
			Team team = ArenaPvP.getInstance().getPlayerTeam(event.getPlayer());
			if (MatchManager.getActiveMatches().containsKey(team)) {
				Match match = MatchManager.getActiveMatches().get(team);
				if (match != null) {
					Arena arena = match.getArena();
					if (arena != null && !match.isOver()) {
						if (!arena.containsBlockRemoved(event.getBlock(), event.getPlayer())) {
							arena.addBlockPlaced(event.getBlock(), event.getPlayer());

							// Fixes grass turning into dirt because of block on top of grass
							if (event.getBlockAgainst().getType() == Material.GRASS) {
								arena.addBlockRemoved(event.getBlockAgainst(), event.getPlayer());
								return;
							}

							Block block = event.getBlock().getRelative(0, -1, 0);
							if (block.getType() == Material.GRASS) {
								arena.addBlockRemoved(block, event.getPlayer());
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (event.getPlayer() != null) {
			Team team = ArenaPvP.getInstance().getPlayerTeam(event.getPlayer());
			if (MatchManager.getActiveMatches().containsKey(team)) {
				Match match = MatchManager.getActiveMatches().get(team);
				if (match != null && !match.isOver()) {
					Arena arena = match.getArena();
					if (arena != null) {
						// LagSpike 100k chunk bug #BlameSmelly Track arena, not the fucking player
						ArenaManager.brokenBlocks.put(event.getBlock(), arena);

						if (!arena.containsBlockPlaced(event.getBlock(), event.getPlayer()) && !arena.containsBlockRemoved(event.getBlock(), event.getPlayer())) {
							arena.addBlockRemoved(event.getBlock(), event.getPlayer());
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockStoneFormEvent(BlockStoneFormEvent event) {
		// Cancel it because if we just set to cobble, it gets set to stone after this event is called
		event.setCancelled(true);

		// Try to add the block below as a changed block (grass -> dirt b/c of block on top)
		Arena arena = ArenaManager.liquidBlocks.get(event.getBlock());
		if (arena != null && !arena.isCleaning()) {
			event.getBlock().setType(Material.COBBLESTONE);
			Block blockUnder = event.getBlock().getRelative(0, -1, 0);
			if (blockUnder.getType() == Material.GRASS) {
				arena.addBlockRemoved(blockUnder, null);
			}
		}

	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockFromToEvent(BlockFromToEvent event) {
		Arena arena = ArenaManager.liquidBlocks.get(event.getBlock());
		if (arena != null && !arena.isCleaning()) {
			event.setCancelled(false);

			ArenaManager.liquidBlocks.put(event.getToBlock(), arena);

			if (event.getToBlock().getType() != Material.WATER && event.getToBlock().getType() != Material.STATIONARY_WATER
					&& event.getToBlock().getType() != Material.LAVA && event.getToBlock().getType() != Material.STATIONARY_LAVA
					&& !arena.containsBlockPlaced(event.getToBlock()) && !arena.containsBlockRemoved(event.getToBlock())) {
				// Possible fix?
				if (event.getToBlock().getType() == Material.AIR) {
					arena.addBlockPlaced(event.getToBlock());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		if (event.getPlayer() != null) {
			Team team = ArenaPvP.getInstance().getPlayerTeam(event.getPlayer());
			if (MatchManager.getActiveMatches().containsKey(team)) {
				Match match = MatchManager.getActiveMatches().get(team);
				if (match != null && !match.isOver()) {
					Arena arena = match.getArena();
					if (arena != null) {
						Block block = event.getBlockClicked().getRelative(event.getBlockFace());

						ArenaManager.liquidBlocks.put(block, arena);

						if (!arena.containsBlockRemoved(block, event.getPlayer())) {
							arena.addBlockPlaced(block, event.getPlayer());

							// Fixes grass turning into dirt because of block on top of grass
							Block blockUnder = block.getRelative(0, -1, 0);
							if (blockUnder.getType() == Material.GRASS) {
								arena.addBlockRemoved(blockUnder, event.getPlayer());
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onFlintAndSteelIgniteEvent(BlockIgniteEvent event) {
		if (event.getPlayer() != null) {
			Team team = ArenaPvP.getInstance().getPlayerTeam(event.getPlayer());
			if (MatchManager.getActiveMatches().containsKey(team)) {
				Match match = MatchManager.getActiveMatches().get(team);
				if (match != null && !match.isOver()) {
					Arena arena = match.getArena();
					if (arena != null) {
						if (!arena.containsBlockRemoved(event.getBlock(), event.getPlayer())) {
							arena.addBlockPlaced(event.getBlock(), event.getPlayer());
						}
					}
				}
			}
		}
	}
}
