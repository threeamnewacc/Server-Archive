package net.badlion.arenalobby.states.kits;

import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.rulesets.CustomRuleSet;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.arenalobby.helpers.KitCreationHelper;
import net.badlion.arenalobby.inventories.kitcreation.CustomKitCreationInventories;
import net.badlion.arenalobby.inventories.kitcreation.KitMultiSaveLoadInventory;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.statemachine.GState;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class KitCreationState extends GState<Group> implements Listener {

	private Map<Player, KitCreationHelper.KitCreator> kitCreators = new HashMap<>();

	public KitCreationState() {
		super("kit", "they are creating a kit.", GroupStateMachine.getInstance());
	}

	public void addKitCreator(Player player, KitCreationHelper.KitCreator kitCreator) {
		this.kitCreators.put(player, kitCreator);
	}

	public KitCreationHelper.KitCreator getKitCreator(Player player) {
		return this.kitCreators.get(player);
	}

	public KitCreationHelper.KitCreator removeKitCreator(Player player) {
		return this.kitCreators.remove(player);
	}

	@Override
	public void before(Group element) {
		super.before(element);

		if (!this.getKitCreator(element.getLeader()).getKitRuleSet().usesCustomChests()) {
			element.getLeader().sendFormattedMessage("{0}Cannot edit kit contents or enchants in this kit, can only move items around.", ChatColor.YELLOW);
		}

		// Force packets to be updated
		KitCreationHelper.KitCreator kitCreator = this.getKitCreator(element.getLeader());

		// Is it the pressure plate at the spawn location?
		if (!kitCreator.areSignsUpdated()) {
			kitCreator.updateSigns();
		}
		if (element.getLeader().isFlying() || element.getLeader().getAllowFlight()) {
			element.getLeader().setAllowFlight(false);
			element.getLeader().sendFormattedMessage("{0}Flight Mode Disabled", ChatColor.RED);
		}
	}

	@Override
	public void after(Group element) {
		super.after(element);

		Player player = element.players().get(0);

		// Clear stuff in memory
		this.removeKitCreator(player);

		ConcurrentLinkedQueue<Location> locations = ArenaLobby.getInstance().getBlockedBlockChangeLocations().get(player);

		// Remove location if we're currently blocking packets
		if (locations != null) {
			locations.remove(KitCreationHelper.getLoadDefaultKitSignLocation());
			locations.remove(KitCreationHelper.getClearInventorySignLocation());
		}

		// Resend this since there really is a sign here (fixes bugs when going back into this state), (byte) 5 represents sign rotation
		player.sendBlockChange(KitCreationHelper.getLoadDefaultKitSignLocation(), Material.WALL_SIGN, (byte) 5);

		// Resend this since there really is a sign here (fixes bugs when going back into this state), (byte) 5 represents sign rotation
		player.sendBlockChange(KitCreationHelper.getClearInventorySignLocation(), Material.WALL_SIGN, (byte) 5);

		// Do this here manually
		ArenaLobby.getInstance().healAndTeleportToSpawn(player);
	}

	public static void teleportToKitCreationArea(Player player) {
		player.setFallDistance(0F);
		player.teleport(ArenaLobby.getInstance().getKitCreationLocation());

		player.sendFormattedMessage("{0}Welcome to the Kit Creation area, open the chest to customize your kit!", ChatColor.AQUA);
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		GroupStateMachine.kitCreationState.removeKitCreator(event.getPlayer());
	}


	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Group group = ArenaLobby.getInstance().getPlayerGroup(player);
		if (this.contains(group)) {
			event.getItemDrop().remove();
		}
	}

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void playerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Group group = ArenaLobby.getInstance().getPlayerGroup(player);
		if (this.contains(group)) {
			// Always cancel the event
			event.setCancelled(true);

			// Update inventory a tick later because we cancel
			BukkitUtil.updateInventory(player);

			Block block = event.getClickedBlock();
			if (block != null) {
				if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if (block.getType().equals(Material.WALL_SIGN) || block.getType().equals(Material.SIGN_POST)) {
						KitCreationHelper.KitCreator kitCreator = this.getKitCreator(player);
						if (KitCreationHelper.isSaveKitSignLocation(block)) { // Save kit sign
							if (kitCreator.getKitRuleSet() instanceof CustomRuleSet) {
								if (kitCreator.getKitRuleSet().getClass() == CustomRuleSet.class) {  // Custom kits
									// Open the select custom kit to load inventory
									CustomKitCreationInventories.openCustomKitSaveInventory(player, KitRuleSet.customRuleSet);
								}
							} else {
								if (player.hasPermission("badlion.donator")) {
									KitMultiSaveLoadInventory.openKitMultiSaveLoadInventory(player, kitCreator.getKitRuleSet());
								} else {
									KitCommon.saveKit(player, kitCreator.getKitRuleSet(), 0);
								}
							}
						} else if (KitCreationHelper.isLoadKitSignLocation(block)) { // Load kit sign
							if (kitCreator.getKitRuleSet() instanceof CustomRuleSet) {
								if (kitCreator.getKitRuleSet().getClass() == CustomRuleSet.class) { // Custom kits
									// Open the select custom kit to load inventory
									CustomKitCreationInventories.openCustomKitLoadInventory(player, KitRuleSet.customRuleSet);
								}
							} else {
								if (player.hasPermission("badlion.donator")) {
									KitMultiSaveLoadInventory.openKitMultiSaveLoadInventory(player, kitCreator.getKitRuleSet());
								} else {
									KitCommon.loadKit(player, kitCreator.getKitRuleSet(), 0);
								}
							}
						} else if (KitCreationHelper.isLoadDefaultKitSignLocation(block)) { // Load default kit sign
							// Fail-safe
							if (!(kitCreator.getKitRuleSet() instanceof CustomRuleSet)) {
								// Note: If creating custom kit, won't be able to see this sign
								KitCommon.loadDefaultKit(player, kitCreator.getKitRuleSet(), true);
							}
						} else if (KitCreationHelper.isClearInventorySignLocation(block)) {
							// Fail-safe
							if (kitCreator.getKitRuleSet().usesCustomChests()) {
								player.getInventory().clear();
								player.getInventory().setArmorContents(new ItemStack[4]);

								player.updateInventory();

								player.sendFormattedMessage("{0}Inventory cleared", ChatColor.YELLOW);
							}
						} else if (KitCreationHelper.checkAndHandleEnchantSigns(block, player)) { // Enchantment sign
							// We handle everything internally for enchants
						} else if (KitCreationHelper.isSpawnSignLocation(block)) { // Spawn sign
							// Transfer to lobby state
							try {
								player.sendFormattedMessage("{0}Teleporting to spawn...", ChatColor.AQUA);

								this.transition(GroupStateMachine.lobbyState, group);
							} catch (IllegalStateTransitionException e) {
								ArenaLobby.getInstance().somethingBroke(player, group);
							}
						}
					} else if (KitCreationHelper.isKitChest(block)) { // Kit chest
						event.setCancelled(true);
						event.setUseInteractedBlock(Event.Result.DENY);

						KitCreationHelper.KitCreator kitCreator = this.getKitCreator(player);
						if (kitCreator.getKitRuleSet().usesCustomChests()) {
							KitCreationHelper.openKitInventory(player);
						} else {
							player.sendFormattedMessage("{0}Cannot edit items in {1} kit.", ChatColor.RED, kitCreator.getKitRuleSet().getName());
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void inventoryDragEvent(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		Group group = ArenaLobby.getInstance().getPlayerGroup(player);
		if (this.contains(group)) {
			if (KitCreationHelper.isKitInventory(event.getView().getTopInventory())) {
				for (Integer rawSlot : event.getRawSlots()) {
					if (rawSlot < 54) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void inventoryClickEvent(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Group group = ArenaLobby.getInstance().getPlayerGroup(player);
		if (this.contains(group)) {
			ItemStack item = event.getCurrentItem();

			//if (item == null || item.getType().equals(Material.AIR)) return; - Cancel because inventory stuff

			event.setCancelled(true);

			if (KitCreationHelper.isKitInventory(event.getView().getTopInventory())) {
				// Clicked main menu item?
				if (KitCreationHelper.isWeaponsItem(item)) {
					KitCreationHelper.openCustomKitWeaponInventory(player);
				} else if (KitCreationHelper.isArmorItem(item)) {
					KitCreationHelper.openCustomKitArmorInventory(player);
				} else if (KitCreationHelper.isFoodItem(item)) {
					KitCreationHelper.openCustomKitFoodInventory(player);
				} else if (KitCreationHelper.isPotionsItem(item)) {
					KitCreationHelper.openCustomKitPotionsInventory(player);
				} else if (KitCreationHelper.isOtherItem(item)) {
					KitCreationHelper.openCustomKitOtherInventory(player);
				} else if (SmellyInventory.isBackInventoryItem(item)) { // They are in one of the custom inventories
					KitCreationHelper.openCustomKitDefaultInventory(player);
				} else if (SmellyInventory.isCloseInventoryItem(item)) {
					BukkitUtil.closeInventory(player);
				} else {
					// Did they click in their own inventory?
					if (event.getRawSlot() > 53 || event.getRawSlot() == -999) { // -999 = Outside of inventory
						if (event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
							event.setCancelled(false);
						}
						return;
					}

					if (item == null || item.getType().equals(Material.AIR)) return;

					// Give them that item
					ItemStack clickedItem = new ItemStack(item);
					clickedItem.setAmount(item.getMaxStackSize());

					if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
							|| event.getAction() == InventoryAction.HOTBAR_SWAP
							|| event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
						player.getInventory().addItem(clickedItem);

						// Update inventory
						BukkitUtil.updateInventory(player);
					} else {
						player.setItemOnCursor(clickedItem);
					}
				}
			} else if (KitCreationHelper.isEnchantmentSelectionInventory(event.getView().getTopInventory())) {
				if (SmellyInventory.isCloseInventoryItem(item)) {
					BukkitUtil.closeInventory(player);
				} else {
					if (item == null || item.getType().equals(Material.AIR)) return;

					if (event.getRawSlot() > 17) return;

					KitCreationHelper.handleEnchant(player, item);

					BukkitUtil.closeInventory(player);
				}
			} else if (item != null && item.getType() != Material.AIR) { // Gberry - Added to stop ppl from trying to save/load custom kits that don't exist
				// Uncancel if none of these inventories or actions
				if ((event.getAction().equals(InventoryAction.DROP_ALL_CURSOR)
						|| event.getAction().equals(InventoryAction.DROP_ONE_SLOT))
						&& !this.getKitCreator(player).getKitRuleSet().usesCustomChests()) {
					player.updateInventory();
				} else {
					event.setCancelled(false);
				}
			} else {
				// Uncancel if none of these inventories or actions
				if ((event.getAction().equals(InventoryAction.DROP_ALL_CURSOR)
						|| event.getAction().equals(InventoryAction.DROP_ONE_SLOT))
						&& !this.getKitCreator(player).getKitRuleSet().usesCustomChests()) {
					player.updateInventory();
				} else {
					event.setCancelled(false);
				}
			}
		}
	}

}
