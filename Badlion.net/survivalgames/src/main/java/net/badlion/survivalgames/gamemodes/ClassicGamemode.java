package net.badlion.survivalgames.gamemodes;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.survivalgames.inventories.SelectionChestInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ClassicGamemode extends Gamemode {

	@Override
	public ItemStack getTierItem(int tier) {
		int rarity = this.random.nextInt(100);

		switch (tier) {
			case 1:
				if (rarity < 40) {
					int i = this.random.nextInt(8);
					switch (i) {
						case 0:
							return new ItemStack(Material.BREAD, this.random.nextInt(2) + 1);
						case 1:
							return new ItemStack(Material.PUMPKIN_PIE, this.random.nextInt(2) + 1);
						case 2:
							return new ItemStack(Material.COOKIE, this.random.nextInt(2) + 1);
						case 3:
							return new ItemStack(Material.CARROT_ITEM, this.random.nextInt(2) + 1);
						case 4:
							return new ItemStack(Material.BAKED_POTATO, this.random.nextInt(2) + 1);
						case 5:
							return new ItemStack(Material.GOLD_SWORD);
						case 6:
							return new ItemStack(Material.WOOD_SWORD);
						case 7:
							return new ItemStack(Material.GOLD_AXE);
					}
				} else if (rarity < 75) {
					int i = this.random.nextInt(9);
					switch (i) {
						case 0:
							return new ItemStack(Material.ARROW, 2);
						case 1:
							return new ItemStack(Material.IRON_INGOT);
						case 2:
							return new ItemStack(Material.FLINT, 1);
						case 3:
							return new ItemStack(Material.FEATHER, this.random.nextInt(3) + 1);
						case 4:
							return new ItemStack(Material.STICK, this.random.nextInt(3) + 1);
						case 5:
							return new ItemStack(Material.LEATHER_HELMET);
						case 6:
							return new ItemStack(Material.LEATHER_BOOTS);
						case 7:
							return new ItemStack(Material.BOW);
						case 8:
							return new ItemStack(Material.STONE_AXE);
					}
				} else {
					int i = this.random.nextInt(4);
					switch (i) {
						case 0:
							return new ItemStack(Material.FISHING_ROD);
						case 1:
							return new ItemStack(Material.STONE_SWORD);
						case 2:
							return new ItemStack(Material.LEATHER_CHESTPLATE);
						case 3:
							return new ItemStack(Material.LEATHER_LEGGINGS);
					}
				}
			case 2:
				if (rarity < 40) {
					int i = this.random.nextInt(9);
					switch (i) {
						case 0:
							return new ItemStack(Material.GRILLED_PORK, this.random.nextInt(2) + 1);
						case 1:
							return new ItemStack(Material.GOLD_HELMET);
						case 2:
							return new ItemStack(Material.GOLD_CHESTPLATE);
						case 3:
							return new ItemStack(Material.GOLD_LEGGINGS);
						case 4:
							return new ItemStack(Material.GOLD_BOOTS);
						case 5:
							return new ItemStack(Material.STONE_SWORD);
						case 6:
							return new ItemStack(Material.ARROW, 3);
						case 7:
							return new ItemStack(Material.BOW);
						case 8:
							return new ItemStack(Material.BAKED_POTATO, 2);
					}
				} else if (rarity < 70) {
					int i = this.random.nextInt(8);
					switch (i) {
						case 0:
							return new ItemStack(Material.CHAINMAIL_HELMET);
						case 1:
							return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
						case 2:
							return new ItemStack(Material.CHAINMAIL_LEGGINGS);
						case 3:
							return new ItemStack(Material.CHAINMAIL_BOOTS);
						case 4:
							return new ItemStack(Material.STICK, 2);
						case 5:
							return new ItemStack(Material.IRON_AXE);
						case 6:
							return new ItemStack(Material.GOLDEN_CARROT, this.random.nextInt(2) + 2);
						case 7:
							return new ItemStack(Material.IRON_INGOT, this.random.nextInt(2) + 1);
					}
				} else if (rarity < 94) {
					int i = this.random.nextInt(6);
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
						case 5:
							return new ItemStack(Material.WEB, this.random.nextInt(2) + 1);
						//case 6:
						//	return new ItemStack(Material.TNT, this.random.nextInt(2) + 1);
					}
				} else if (rarity == 95) {
					// 1% chance
					return SelectionChestInventory.getSelectionChestItem();
				} else {
					int i = this.random.nextInt(2);
					switch (i) {
						case 0:
							return new ItemStack(Material.DIAMOND);
						case 1:
							return new ItemStack(Material.GOLDEN_APPLE);
					}
				}
			case 3:
				int i = this.random.nextInt(14);
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
						return new ItemStack(Material.IRON_SWORD);
					case 5:
						return new ItemStack(Material.STONE_SWORD);
					case 6:
						return new ItemStack(Material.GOLDEN_APPLE, 1);
					case 7:
						return new ItemStack(Material.WEB, this.random.nextInt(2) + 2);
					case 8:
						return SelectionChestInventory.getSelectionChestItem();
					case 9:
						return new ItemStack(Material.GOLDEN_CARROT, this.random.nextInt(2) + 3);
					case 10:
						return new ItemStack(Material.CHAINMAIL_HELMET);
					case 11:
						return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
					case 12:
						return new ItemStack(Material.CHAINMAIL_LEGGINGS);
					case 13:
						return new ItemStack(Material.CHAINMAIL_BOOTS);
					//case 10:
					//	return new ItemStack(Material.TNT, this.random.nextInt(2) + 2);
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
			case 3:
				return 0;
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
			case 3:
				return 4;
		}

		return -1;
	}

    @Override
    public void handleDeath(LivingEntity died) {
        // Do nothing special
    }

    @EventHandler
    public void onPrepareItemCraftEvent(PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult().getType() == Material.FLINT_AND_STEEL) {
	        ItemStack item = new ItemStack(Material.FLINT_AND_STEEL);
	        event.getInventory().setResult(item);
        }
    }

	@EventHandler
	public void onCraftItemEvent(CraftItemEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (MPGPlayerManager.getMPGPlayer(player).getState() != MPGPlayer.PlayerState.PLAYER) return;

		if (event.getRecipe().getResult().getType() == Material.BUCKET) {
			player.sendMessage(ChatColor.RED + "Cannot craft buckets in SG.");
			event.setCancelled(true);
		}
	}

	public String getName() {
		return "Classic";
	}

	@Override
	public MPGKit getDefaultKit() {
		return null;
	}

}
