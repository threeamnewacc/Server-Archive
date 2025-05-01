package net.badlion.survivalgames.listeners;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.survivalgames.SGPlayer;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.inventories.SelectionChestInventory;
import net.badlion.survivalgames.tasks.SupplyDropTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AlivePlayerListener implements Listener {

	//private Map<TNTPrimed, Player> primedTNT = new HashMap<>();

	private Map<Player, Location> openedChests = new HashMap<>();

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerBreakBlockEvent(BlockBreakEvent event) {
		if (event.getPlayer().isOp()) return;

		if (MPG.getInstance().getMPGGame().getGameState().ordinal() < MPGGame.GameState.GAME_COUNTDOWN.ordinal()) return;

		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
		if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			Material material = event.getBlock().getType();
			if (material == Material.LONG_GRASS || material == Material.YELLOW_FLOWER || material == Material.RED_ROSE
					|| material == Material.VINE || material == Material.CROPS || material == Material.DOUBLE_PLANT
					|| material == Material.SAPLING || material == Material.DEAD_BUSH || material == Material.BROWN_MUSHROOM
					|| material == Material.RED_MUSHROOM || material == Material.POTATO || material == Material.MELON_STEM
					|| material == Material.PUMPKIN_STEM || material == Material.WEB || material == Material.LEAVES
					|| material == Material.LEAVES_2) {
				event.setCancelled(false);
				return;
			}
		}

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerPlaceBlockEvent(BlockPlaceEvent event) {
		if (event.getPlayer().isOp()) return;

		if (MPG.getInstance().getMPGGame().getGameState().ordinal() != MPGGame.GameState.GAME.ordinal()
				&& MPG.getInstance().getMPGGame().getGameState().ordinal() != MPGGame.GameState.DEATH_MATCH.ordinal()) {
			return;
		}

		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
		if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			/*if (event.getBlock().getType() == Material.TNT) {
				Location location = event.getBlock().getLocation().add(0, 1, 0);

				// Place primed TNT
				TNTPrimed tntPrimed = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
				tntPrimed.setFuseTicks(40);

				// Keep a record of who primed the TNT
				this.primedTNT.put(tntPrimed, event.getPlayer());

				event.getBlock().setType(Material.AIR);

				event.setCancelled(false);
			}*/

			if (event.getBlock().getType() == Material.WEB || event.getBlock().getType() == Material.BOAT) {
				event.setCancelled(false);
			}
		}
	}

	/*@EventHandler
	public void onEntityDamageByTNTEvent(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof TNTPrimed) {
			Player player = (Player) event.getEntity();
			Player primer = this.primedTNT.remove(event.getDamager());

			// Primer should never be null
			if (primer == null) {
				throw new RuntimeException("TNT primer is null in onEntityDamageByTNTEvent()!");
			}

			// Damage the player before TNT damage applies so the primer is set as the player's killer
			player.damage(0D, primer);
		}
	}*/

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onBlockIgniteEvent(BlockIgniteEvent event) {
		if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return;

		ItemStack item = event.getPlayer().getItemInHand();

		// Two uses per flint and steel, reduce durability by 32 per use
		item.setDurability((short) (item.getDurability() + 32));
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerOpenChest(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block != null && (block.getType() == Material.CHEST || block.getType() == Material.ENDER_CHEST)) {
			// Always cancel in this case
			event.setCancelled(true);
			event.setUseInteractedBlock(Event.Result.DENY);

			SGPlayer sgPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());

			if (sgPlayer.getState() != MPGPlayer.PlayerState.PLAYER) return;

			// Get supply drop chest
			SupplyDropTask.SupplyDrop supplyDrop = SurvivalGames.getInstance().getSGGame().getSupplyDropChest(block.getLocation());

			// Is this a supply drop chest?
			if (supplyDrop != null) {
				// Is this an ender chest?
				if (block.getType() == Material.ENDER_CHEST) {
					if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
						// Click on supply block chest
						supplyDrop.clickChest();
					} else {
						event.getPlayer().sendMessage(ChatColor.RED + "Rapidly left click the supply drop to break it open!");
					}
				} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					// Check for a race condition where a player spam right clicks a chest
					if (!this.openedChests.containsKey(event.getPlayer())) {
						// Store the chest they're opening (used for spoofing animation and sound)
						this.openedChests.put(event.getPlayer(), block.getLocation());

						// Open supply drop inventory
						SurvivalGames.getInstance().getSGGame().openSupplyDrop(event.getPlayer(), sgPlayer, supplyDrop);
					}
				}
				return;
			}

			// Normal chests
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					// Check for a race condition where a player spam right clicks a chest
					if (!this.openedChests.containsKey(event.getPlayer())) {
						// Store the chest they're opening (used for spoofing animation and sound)
						this.openedChests.put(event.getPlayer(), block.getLocation());

						// Open chest inventory
						SurvivalGames.getInstance().getSGGame().openChest(event.getPlayer(), sgPlayer, block.getLocation());
					}
			}
		} else if (event.getItem() != null && ItemStackUtil.equals(event.getItem(), SelectionChestInventory.getSelectionChestItem())
				&& (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			event.setCancelled(true);

			// Open selection chest inventory
			SelectionChestInventory.openSelectionChestInventory(event.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		// Is this a chest inventory?
		if (event.getInventory().getName().toLowerCase().contains("tier")) {
			// Get the location of the chest they're opening
			Location location = this.openedChests.remove((Player) event.getPlayer());

			// Is this player the one viewer of the chest?
			if (event.getInventory().getViewers().size() == 1) {
				// Broadcast chest open sound
				location.getWorld().playSound(location, EnumCommon.getEnumValueOf(Sound.class, "CHEST_CLOSE", "BLOCK_CHEST_CLOSE"), 0.5F, (float) Math.random() * 0.1F + 0.9F);

				// Create chest close packet
				Object blockActionPacket = TinyProtocolReferences.invokeBlockActionPacketConstructor(location.getBlockX(),
						location.getBlockY(), location.getBlockZ(), TinyProtocolReferences.getNMSBlock(location.getBlock()), 1, 0);

				// Broadcast to everyone
				for (Player pl : Bukkit.getOnlinePlayers()) {
					Gberry.protocol.sendPacket(pl, blockActionPacket);
				}
			} else {
				// Play chest open sound only to the player opening it
				// Play chest open sound only to the player closing it
				// Play chest open sound only to the player closing it
				((Player) event.getPlayer()).playSound(location, EnumCommon.getEnumValueOf(Sound.class, "CHEST_CLOSE", "BLOCK_CHEST_CLOSE"), 0.5F, (float) Math.random() * 0.1F + 0.9F);
			}
		}
	}

}