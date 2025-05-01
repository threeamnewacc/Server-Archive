package net.badlion.ffa.gamemodes;

import net.badlion.arenacommon.event.KitLoadEvent;
import net.badlion.arenacommon.util.ItemStackUtil;
import net.badlion.combattag.events.CombatTagDropInventoryEvent;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.ffa.FFA;
import net.badlion.ffa.listeners.EnderPearlListener;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.gguard.GGuard;
import net.badlion.gguard.PolygonRegion;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.PlayerItemsDroppedFromDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SGGamemode extends Gamemode {

	public static final List<String> REGIONS = new ArrayList<>();

	//public static final Map<String, List<Location>> REGION_CHESTS = new HashMap<>();

	private static final double REGION_PLAYER_DAMAGE = 4;

	private static final double REGION_CLOSE_THRESHOLD = 30D;
	private static final double REGION_OPEN_THRESHOLD = 50D;

	// In milliseconds
	private static final int TIER_1_REFILL_TIME = 30000;
	private static final int TIER_2_REFILL_TIME = 60000;

	private MPGKit sgKit;

	private Map<Location, Inventory> tier1Chests = new HashMap<>();
	private Map<Location, Inventory> tier2Chests = new HashMap<>();

	private Map<Player, Location> openedChests = new HashMap<>();

	private Map<Inventory, Long> chestRefillTimes = new HashMap<>();

	public SGGamemode() {
		// Register enderpearl listener
		FFA.getInstance().getServer().getPluginManager().registerEvents(new EnderPearlListener(), FFA.getInstance());

		ItemStack[] armorContents = new ItemStack[4];
		ItemStack[] inventoryContents = new ItemStack[36];

		armorContents[1] = this.createSoulboundItem(Material.LEATHER_LEGGINGS);

		inventoryContents[0] = this.createSoulboundItem(Material.WOOD_SWORD);
		inventoryContents[1] = new ItemStack(Material.ENDER_PEARL);

		// Create SG kit
		this.sgKit = new MPGKit(null, "sgffa", 0, "sgffa", "sgffa", null, inventoryContents, armorContents);

		// Initialize regions (Must be in order of opening)
		SGGamemode.REGIONS.add("Farm");
		SGGamemode.REGIONS.add("Forest");
		SGGamemode.REGIONS.add("Castle_Courtyard");
		SGGamemode.REGIONS.add("Castle");

		/*SGGamemode.REGION_CHESTS.put("Farm", new ArrayList<Location>());
		SGGamemode.REGION_CHESTS.put("Forest", new ArrayList<Location>());
		SGGamemode.REGION_CHESTS.put("Castle", new ArrayList<Location>());
		SGGamemode.REGION_CHESTS.put("Castle_Courtyard", new ArrayList<Location>());*/

		// Start the region manager task
		BukkitUtil.runTaskTimer(new RegionManagerTask(), 200L);
	}

	private boolean isSoulboundItem(ItemStack item) {
		return item != null && item.getType() != Material.AIR && item.getItemMeta().hasLore()
				&& item.getItemMeta().getLore().get(0).equals(ChatColor.GOLD.toString() + "Soulbound" );
	}

	private ItemStack createSoulboundItem(Material type) {
		return ItemStackUtil.createItem(type, null, ChatColor.GOLD.toString() + "Soulbound");
	}

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		ItemStackUtil.addUnbreakingToWeapons(event.getPlayer());
		ItemStackUtil.addUnbreakingToArmor(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (MPGPlayerManager.getMPGPlayer(event.getPlayer()).getState() != MPGPlayer.PlayerState.PLAYER) return;

		// Are they using a flint and steel?
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.ENDER_CHEST) {
				event.setCancelled(true);

				// Check for a race condition where a player spam right clicks a chest
				if (!this.openedChests.containsKey(event.getPlayer())) {
					// Store the chest they're opening (used for spoofing animation and sound)
					this.openedChests.put(event.getPlayer(), event.getClickedBlock().getLocation());

					// Open chest inventory
					this.openChest(event.getPlayer(), event.getClickedBlock().getLocation());
				}
			}
		}
	}

	@EventHandler
	public void onCraftItemEvent(CraftItemEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (MPGPlayerManager.getMPGPlayer(player).getState() != MPGPlayer.PlayerState.PLAYER) return;

		if (event.getRecipe().getResult().getType() == Material.DIAMOND_HELMET
				|| event.getRecipe().getResult().getType() == Material.DIAMOND_CHESTPLATE
				|| event.getRecipe().getResult().getType() == Material.DIAMOND_LEGGINGS
				|| event.getRecipe().getResult().getType() == Material.DIAMOND_BOOTS) {
			player.sendMessage(ChatColor.RED + "Cannot craft diamond armor in SG FFA World.");
			event.setCancelled(true);
		} else if (event.getRecipe().getResult().getType() == Material.BUCKET) {
			player.sendMessage(ChatColor.RED + "Cannot craft buckets in SG FFA World.");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPrepareItemCraftEvent(PrepareItemCraftEvent event) {
		if (event.getRecipe().getResult().getType() == Material.FLINT_AND_STEEL) {
			ItemStack item = new ItemStack(Material.FLINT_AND_STEEL);
			//event.getInventory().setResult(item);
		}
	}

	@EventHandler(priority=EventPriority.LAST, ignoreCancelled = true)
	public void onBlockIgniteEvent(BlockIgniteEvent event) {
		if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
			ItemStack item = event.getPlayer().getItemInHand();

			// Two uses per flint and steel, reduce durability by 32 per use
			item.setDurability((short) (item.getDurability() + 32));
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		// Remove soulbound items
		if (this.isSoulboundItem(event.getItemDrop().getItemStack())) {
			event.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onPlayerItemsDroppedFromDeathEvent(PlayerItemsDroppedFromDeathEvent event) {
		// Don't drop weapons/armor
		for (Item item : event.getItemsDroppedOnDeath()) {
			if (this.isSoulboundItem(item.getItemStack())) {
				item.remove();
			}
		}
	}

	@EventHandler
	public void onCombatTagDropInventoryEvent(CombatTagDropInventoryEvent event) {
		// Clear all soulbound items
		for (int i = 0; i < event.getArmor().length; i++) {
			if (this.isSoulboundItem(event.getArmor()[i])) {
				event.getArmor()[i] = null;
			}
		}

		for (int i = 0; i < event.getInventory().length; i++) {
			if (this.isSoulboundItem(event.getInventory()[i])) {
				event.getInventory()[i] = null;
			}
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
					// TODO: THIS MIGHT CAUSE LAG ON HIGH-PLAYER SERVERS
					Gberry.protocol.sendPacket(pl, blockActionPacket);
				}
			} else {
				// Play chest open sound only to the player closing it
				((Player) event.getPlayer()).playSound(location, EnumCommon.getEnumValueOf(Sound.class, "CHEST_CLOSE", "BLOCK_CHEST_CLOSE"), 0.5F, (float) Math.random() * 0.1F + 0.9F);
			}

			// Is the chest empty?
			if (this.isChestEmpty(event.getInventory())) {
				// Does the chest have no refill time yet?
				if (!this.chestRefillTimes.containsKey(event.getInventory())) {
					// This chest needs to be refilled, add this chest to our refill map
					this.chestRefillTimes.put(event.getInventory(), System.currentTimeMillis());
				}
			}

			// Clear all soulbound items
			for (int i = 0; i < event.getInventory().getContents().length; i++) {
				if (this.isSoulboundItem(event.getInventory().getContents()[i])) {
					event.getInventory().setItem(i, null);
				}
			}
		}
	}

	private void openChest(final Player player, Location location) {
		Inventory inventory = this.tier1Chests.get(location);

		if (inventory == null) inventory = this.tier2Chests.get(location);

		Long refillTime = this.chestRefillTimes.get(inventory);

		// Does this chest need to be refilled?
		if (refillTime != null) {
			int tier;

			// Figure out the tier of this chest
			if (inventory.getName().contains("1")) {
				// Tier 1
				tier = 1;
				refillTime += SGGamemode.TIER_1_REFILL_TIME;
			} else if (inventory.getName().contains("2")) {
				// Tier 2
				tier = 2;
				refillTime += SGGamemode.TIER_2_REFILL_TIME;
			} else {
				throw new RuntimeException("Unknown tier for chest inventory " + inventory.getName());
			}

			if (refillTime <= System.currentTimeMillis()) {
				this.fillChest(inventory, tier);

				this.chestRefillTimes.remove(inventory);
			}
		}

		// Has anybody else opened the chest?
		if (inventory.getViewers().isEmpty()) {
			// Broadcast chest open sound
			location.getWorld().playSound(location, EnumCommon.getEnumValueOf(Sound.class, "CHEST_OPEN", "BLOCK_CHEST_OPEN"), 0.5F, (float) Math.random() * 0.1F + 0.9F);

			// Create chest open packet
			Object blockActionPacket = TinyProtocolReferences.invokeBlockActionPacketConstructor(location.getBlockX(),
					location.getBlockY(), location.getBlockZ(), TinyProtocolReferences.getNMSBlock(location.getBlock()), 1, 1);

			// Broadcast to everyone
			for (Player pl : Bukkit.getOnlinePlayers()) {
				// TODO: THIS MIGHT CAUSE LAG ON HIGH-PLAYER SERVERS
				Gberry.protocol.sendPacket(pl, blockActionPacket);
			}
		} else {
			// Play chest open sound only to the player opening it
			player.playSound(location, EnumCommon.getEnumValueOf(Sound.class, "CHEST_OPEN", "BLOCK_CHEST_OPEN"), 0.5F, (float) Math.random() * 0.1F + 0.9F);
		}

		// Open the chest
		BukkitUtil.openInventory(player, inventory);
	}

	private boolean isChestEmpty(Inventory inventory) {
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}
		}

		return true;
	}

	public void fillChest(Inventory inventory, int tier) {
		int numberSlots = inventory.getSize() - 1;

		// Num of items based on game mode
		int numOfItems = this.getNumOfTierRandom(tier) + this.getNumOfTierGuaranteed(tier);

		Set<Integer> slotsUsed = new HashSet<>();

		// Fill currently used slots
		for (int i = 0; i < inventory.getContents().length; i++) {
			ItemStack itemStack = inventory.getItem(i);
			if (itemStack != null && itemStack.getType() != Material.AIR) {
				slotsUsed.add(i);
			}
		}

		Set<Material> itemsUsed = new HashSet<>();

		// Handle random items
		for (int i = 0; i < numOfItems; i++) {
			// Is the inventory already full?
			if (slotsUsed.size() == inventory.getSize()) break;

			// Get a random slot
			int slot;
			do {
				slot = Gberry.generateRandomInt(0, numberSlots);
			} while (slotsUsed.contains(slot));

			slotsUsed.add(slot);

			// Get a unique new item
			ItemStack itemStack;
			do {
				itemStack = this.getTierItem(tier);
			} while (itemsUsed.contains(itemStack.getType()));

			itemsUsed.add(itemStack.getType());
			inventory.setItem(slot, itemStack);
		}

		// Handle common items
		List<ItemStack> commonItems = this.getCommonTierItems(tier);
		if (commonItems != null) {
			for (ItemStack itemStack : commonItems) {
				// Is the inventory already full?
				if (slotsUsed.size() == inventory.getSize()) break;

				// Get a random slot
				int slot;
				do {
					slot = Gberry.generateRandomInt(0, numberSlots);
				} while (slotsUsed.contains(slot));

				inventory.setItem(slot, itemStack);
			}
		}
	}

	@Override
	public ItemStack getTierItem(int tier) {
		int rarity = this.random.nextInt(100);

		switch (tier) {
			case 1:
				if (rarity < 40) {
					int i = random.nextInt(5);
					switch (i) {
						case 0:
							return new ItemStack(Material.BREAD, random.nextInt(2) + 1);
						case 1:
							return new ItemStack(Material.PUMPKIN_PIE, random.nextInt(2) + 1);
						case 2:
							return new ItemStack(Material.COOKIE, random.nextInt(2) + 1);
						case 3:
							return new ItemStack(Material.CARROT_ITEM, random.nextInt(2) + 1);
						case 4:
							return new ItemStack(Material.GOLD_SWORD);
					}
				} else if (rarity < 75) {
					int i = random.nextInt(8);
					switch (i) {
						case 0:
							return new ItemStack(Material.ARROW, 2);
						case 1:
							return new ItemStack(Material.IRON_INGOT, 1);
						case 2:
							return new ItemStack(Material.FLINT, random.nextInt(3) + 1);
						case 3:
							return new ItemStack(Material.FEATHER, random.nextInt(3) + 1);
						case 4:
							return new ItemStack(Material.STICK, random.nextInt(3) + 1);
						case 5:
							return new ItemStack(Material.LEATHER_HELMET);
						case 6:
							return new ItemStack(Material.LEATHER_BOOTS);
						case 7:
							return new ItemStack(Material.BOW);
					}
				} else {
					int i = random.nextInt(3);
					switch (i) {
						case 0:
							return new ItemStack(Material.FISHING_ROD);
						case 1:
							return new ItemStack(Material.STONE_SWORD);
						case 2:
							return new ItemStack(Material.LEATHER_CHESTPLATE);
					}
				}
			case 2:
				if (rarity < 40) {
					int i = random.nextInt(11);
					switch (i) {
						case 0:
							return new ItemStack(Material.GRILLED_PORK);
						case 1:
							return new ItemStack(Material.GOLDEN_CARROT);
						case 2:
							return new ItemStack(Material.GOLD_HELMET);
						case 3:
							return new ItemStack(Material.GOLD_CHESTPLATE);
						case 4:
							return new ItemStack(Material.GOLD_LEGGINGS);
						case 5:
							return new ItemStack(Material.GOLD_BOOTS);
						case 6:
							return new ItemStack(Material.STONE_SWORD);
						case 7:
							return new ItemStack(Material.ARROW, 4);
						case 8:
							return new ItemStack(Material.BOW);
						case 9:
							return new ItemStack(Material.STICK, 2);
						case 10:
							return new ItemStack(Material.IRON_INGOT);
					}
				} else if (rarity < 75) {
					int i = random.nextInt(4);
					switch (i) {
						case 0:
							return new ItemStack(Material.CHAINMAIL_HELMET);
						case 1:
							return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
						case 2:
							return new ItemStack(Material.CHAINMAIL_LEGGINGS);
						case 3:
							return new ItemStack(Material.CHAINMAIL_BOOTS);
					}
				} else if (rarity < 90) {
					int i = random.nextInt(5);
					switch (i) {
						case 0:
							return new ItemStack(Material.IRON_HELMET);
						case 1:
							return new ItemStack(Material.IRON_CHESTPLATE);
						case 2:
							return new ItemStack(Material.IRON_LEGGINGS);
						case 3:
							return new ItemStack(Material.IRON_BOOTS);
						case 4:
							return new ItemStack(Material.FLINT_AND_STEEL);
					}
				} else {int i = random.nextInt(2);
					switch (i) {
						case 0:
							return new ItemStack(Material.DIAMOND);
						case 1:
							return new ItemStack(Material.GOLDEN_APPLE);
					}
				}
		}

		return null;
	}

	@Override
	public List<ItemStack> getCommonTierItems(int tier) {
		return null;
	}

	@Override
	public int getNumOfTierRandom(int tier) {
		switch (tier) {
			case 1:
				return Gberry.generateRandomInt(0, 2);
			case 2:
				return Gberry.generateRandomInt(0, 1);
		}

		return -1;
	}

	@Override
	public int getNumOfTierGuaranteed(int tier) {
		switch (tier) {
			case 1:
				return 4;
			case 2:
				return 3;
		}

		return -1;
	}

    @Override
    public void handleDeath(LivingEntity died) {

    }

	public String getName() {
		return "SG";
	}

	@Override
	public MPGKit getDefaultKit() {
		return this.sgKit;
	}

	public Map<Location, Inventory> getTier1Chests() {
		return this.tier1Chests;
	}

	public Map<Location, Inventory> getTier2Chests() {
		return this.tier2Chests;
	}

	private class RegionManagerTask extends BukkitRunnable {

		private int openRegions = 0;

		@Override
		public void run() {
			// Note: If the interval of this task changes, change this
			boolean regionRecentlyClosed = false;

			// Check player count to see if we should open or close a region
			int playerCount = FFA.getInstance().getServer().getOnlinePlayers().size();

			double n = playerCount / SGGamemode.REGION_OPEN_THRESHOLD;

			// Do we need to open up more regions?
			if (Math.floor(n) > this.openRegions) {
				// Do we still have regions to open?
				if (this.openRegions != SGGamemode.REGIONS.size()) {
					String regionToOpen = SGGamemode.REGIONS.get(this.openRegions++);

					// Broadcast that this region has opened
					Gberry.broadcastMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "The " + regionToOpen.replaceAll("_", " ") + " region has opened!");

					// Replace bedrock with chests
					/*for (Location location : SGGamemode.REGION_CHESTS.get(regionToOpen)) {
						location.getBlock().setType(Material.CHEST);
					}*/
				}
			} else if (n < this.openRegions - (1 - (SGGamemode.REGION_CLOSE_THRESHOLD / SGGamemode.REGION_OPEN_THRESHOLD))) { // Do we need to close regions?
				// Do we have any regions open?
				if (this.openRegions > 0) {
					regionRecentlyClosed = true;

					String regionToClose = SGGamemode.REGIONS.get(--this.openRegions);

					// Broadcast that this region has opened
					Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + ChatColor.BOLD.toString() + "The " + regionToClose.replaceAll("_", " ") + " region has closed!");

					// Replace chests with bedrock
					/*for (Location location : SGGamemode.REGION_CHESTS.get(regionToClose)) {
						location.getBlock().setType(Material.BEDROCK);
					}*/
				}
			}

			// Iterate through all closed regions
			for (int i = 0; i < SGGamemode.REGIONS.size(); i++) {
				// Is this region open?
				if (i + 1 <= this.openRegions) continue;

				String regionName = SGGamemode.REGIONS.get(i);

				// Find players in this region
				for (Player player : FFA.getInstance().getServer().getOnlinePlayers()) {
					PolygonRegion region = GGuard.getInstance().getPolygonRegion(player.getLocation());

					if (region != null && region.getRegionName().equalsIgnoreCase(regionName)) {
						// Did a region recently close?
						if (regionRecentlyClosed) {
							// Send a message to the player warning them that they're in a closed region
							player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "The region you are in has just closed, you have ten seconds to leave until you take damage!");
						} else {
							// Damage the player just a lil wee bit
							player.damage(SGGamemode.REGION_PLAYER_DAMAGE);

							// Send a message to the player warning them that they're in a closed region
							player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You are in a closed region, you will take damage until you leave!");
						}
					}
				}
			}

		}

	}

}
