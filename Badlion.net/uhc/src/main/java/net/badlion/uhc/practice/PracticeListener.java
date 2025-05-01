package net.badlion.uhc.practice;

import net.badlion.gberry.UnregistrableListener;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.events.ServerStateChangeEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PracticeListener implements Listener, UnregistrableListener {

	// Override the other stuff
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageEvent(EntityDamageEvent event) {
		// First check if the game is pre-game
		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.PRE_START) {
			return;
		}

		if (!(event.getEntity() instanceof Player)) return;

		// Did a player damage them?
		if (event instanceof EntityDamageByEntityEvent) {
			if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player) {
				UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(((EntityDamageByEntityEvent) event).getDamager().getUniqueId());

				// Is a spec/mod trying to damage this player?
				if (uhcPlayer.getState() != UHCPlayer.State.PLAYER) {
					System.out.println("OKKK999");
					event.setCancelled(true);
					return;
				}
			}
		}

		Player player = (Player) event.getEntity();
		UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());

		// Race condition when logging in or some shit
		if (uhcPlayer == null) {
			return;
		}

		// Check they are in practice
		if (!PracticeManager.isInPractice(uhcPlayer)) {
			return;
		}

		// Did they take fall damage?
		if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			// Did they fall over 100 blocks?
			if (player.getFallDistance() > 100F) {
				// They fell from their spawn point, cancel damage
				event.setCancelled(true);
				return;
			}
		}

		event.setCancelled(false);
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		// First check if the game is pre-game
		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.PRE_START) {
			return;
		}

		// Check they are in practice
		if (!PracticeManager.isInPractice(UHCPlayerManager.getUHCPlayer(event.getEntity().getUniqueId()))) {
			return;
		}

		// Remove death message
		event.setDeathMessage(null);

		// Don't drop stuff
		event.getDrops().clear();
		event.setDroppedExp(0);

		// Messages and fancy shit
		if (event.getEntity().getKiller() != null) {
			event.getEntity().getKiller().sendMessage(ChatColor.GREEN + "You killed " + event.getEntity().getName());
			event.getEntity().sendMessage(ChatColor.RED + "You were killed by " + event.getEntity().getKiller().getName());

			// Give the killer a golden head
			event.getEntity().getKiller().getInventory().addItem(ItemStackUtil.createGoldenHead());
		} else {
			event.getEntity().sendMessage(ChatColor.RED + "You died!");
		}
	}

	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		// First check if the game is pre-game
		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.PRE_START) {
			return;
		}

		// Check if they are in practice
		if (!PracticeManager.isInPractice(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))) {
			return;
		}

		// Teleport them to the arena spawn location
		event.setRespawnLocation(PracticeManager.getArena().getRandomSpawnLocation());

		// Give them the kit again
		PracticeManager.getArena().getKit().giveItems(event.getPlayer());
	}

	@EventHandler
	public void onGameCountdownStartEvent(ServerStateChangeEvent event) {
		if (event.getNewState() == BadlionUHC.BadlionUHCState.COUNTDOWN) {
			PracticeManager.endPractice();
		}
	}

	@EventHandler
	public void onGameStartEvent(GameStartEvent event) {
		// Game started unregister this listener
		this.unregister();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		PracticeManager.removePlayer(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()), false);
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		// Check they are in practice
		if (!PracticeManager.isInPractice(UHCPlayerManager.getUHCPlayer(event.getWhoClicked().getUniqueId()))) {
			return;
		}

		// Allow them to click in their inventory
		event.setCancelled(false);
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		// First check if the game is pre-game
		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.PRE_START) {
			return;
		}

		// Check if they are in practice
		if (!PracticeManager.isInPractice(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))) {
			return;
		}

		Material material = event.getBlock().getType();
		if (material == Material.LONG_GRASS || material == Material.YELLOW_FLOWER || material == Material.RED_ROSE
				|| material == Material.VINE || material == Material.CROPS || material == Material.DOUBLE_PLANT
				|| material == Material.SAPLING || material == Material.DEAD_BUSH || material == Material.BROWN_MUSHROOM
				|| material == Material.RED_MUSHROOM || material == Material.POTATO || material == Material.MELON_STEM
				|| material == Material.PUMPKIN_STEM || material == Material.WEB || material == Material.LEAVES
				|| material == Material.LEAVES_2 || material == Material.SUGAR_CANE_BLOCK || material == Material.CACTUS) {
			event.setCancelled(false);
		} else {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		// First check if the game is pre-game
		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.PRE_START) {
			return;
		}

		// Check if they are in practice
		if (!PracticeManager.isInPractice(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockDropItemsEvent(BlockDropItemsEvent event) {
		// First check if the game is pre-game
		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.PRE_START) {
			return;
		}

		// Clear all block drops
		for (Item item : event.getItems()) {
			item.remove();
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		// First check if the game is pre-game
		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.PRE_START) {
			return;
		}

		// Check if they are in practice
		if (!PracticeManager.isInPractice(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))) {
			return;
		}

		// Cancel empty bucket interacting
		if (event.getItem() != null && event.getItem().getType() == Material.BUCKET) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWaterBucketPlaceEvent(PlayerBucketEmptyEvent event) {
		// First check if the game is pre-game
		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.PRE_START) {
			return;
		}

		// Check if they are in practice
		if (!PracticeManager.isInPractice(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))) {
			return;
		}

		event.setCancelled(true);
		event.getPlayer().updateInventory();

		final Block block = event.getBlockClicked().getRelative(event.getBlockFace());

		// Set to water
		block.setType(Material.STATIONARY_WATER);

		// Remove water a tick later to allow for maximum MLG
		BukkitUtil.runTaskNextTick(new Runnable() {
			@Override
			public void run() {
				// Set back to air
				block.setType(Material.AIR);
			}
		});
	}

	@Override
	public void unregister() {
		EntityDamageEvent.getHandlerList().unregister(this);
		PlayerDeathEvent.getHandlerList().unregister(this);
		PlayerRespawnEvent.getHandlerList().unregister(this);
		ServerStateChangeEvent.getHandlerList().unregister(this);
		GameStartEvent.getHandlerList().unregister(this);
		PlayerQuitEvent.getHandlerList().unregister(this);
		InventoryClickEvent.getHandlerList().unregister(this);
		BlockBreakEvent.getHandlerList().unregister(this);
		BlockPlaceEvent.getHandlerList().unregister(this);
		BlockDropItemsEvent.getHandlerList().unregister(this);
		PlayerInteractEvent.getHandlerList().unregister(this);
		PlayerBucketEmptyEvent.getHandlerList().unregister(this);
	}

}
