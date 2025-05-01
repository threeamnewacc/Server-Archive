package net.badlion.gfactions;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;

public class RandomItemGenerator {
	
	private GFactions plugin;

	public RandomItemGenerator(GFactions plugin) {
		this.plugin = plugin;
	}

    public ArrayList<ItemStack>  generateGodWeapon(int numOfItems) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int x = 0; x < numOfItems; x++) {
            ItemStack item = null;

            String displayName = "";
            int randomItem = this.plugin.generateRandomInt(0,1);
            switch(randomItem) {
                case 0:
                    displayName = ChatColor.DARK_PURPLE + "God Bow";
                    item = new ItemStack(Material.BOW);
                    item.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
                break;
                case 1:
                    displayName = ChatColor.DARK_PURPLE + "God Sword";
                    item = new ItemStack(Material.DIAMOND_SWORD);
                    item.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                    break;
            }

            // Add unbreaking 3
            item.addEnchantment(Enchantment.DURABILITY, 3);

            // Get bitmask
            int enchants = this.plugin.generateRandomInt(0, 1); // Lowest: 00000 Highest: 00001

            // Get intensities + add to lore
            String intensities = "";
            ArrayList<String> lore = new ArrayList<String>();

            // Extra Damage
            if ((enchants & 0x1) > 0) {
                int intensity = this.plugin.generateRandomInt(1, 15);
                intensities = intensities + ":" + intensity;
                lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + intensity + "% Extra Damage");
            }

            // Pick one random potion effect enchantment
            // Make all this stuff last for 1 second
            int n = this.plugin.generateRandomInt(0, 3);
            switch(n) {
                case 0: // Bleed (bit 5)
                    enchants = enchants + 16;
                    intensities = intensities + ":3";
                    lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + "5% chance of 3s of bleed on hit");
                    break;
                case 1: // Nausea (bit 4)
                    enchants = enchants + 8;
                    intensities = intensities + ":3";
                    lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + "5% chance of 3s of nausea on hit");
                    break;
                case 2: // Slowness (bit 3)
                    enchants = enchants + 4;
                    intensities = intensities + ":3";
                    lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + "5% chance of 3s of slowness on hit");
                    break;
                case 3: // Weakness (bit 2)
                    enchants = enchants + 2;
                    intensities = intensities + ":3";
                    lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + "5% chance of 3s of weakness on hit");
                    break;
            }

            intensities = intensities.substring(1);

            // Add bitmask stuff to last line of lore
            lore.add(enchants + " " + intensities);

            ItemMeta meta = item.getItemMeta();

            // Set display name
            meta.setDisplayName(displayName);

            // Set lore
            meta.setLore(lore);
            item.setItemMeta(meta);
            items.add(item);
        }
        return items;
    }

    public ArrayList<ItemStack>  generateGodArmor(int numOfItems) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int x = 0; x < numOfItems; x++) {
            ItemStack item = null;

            String displayName = "";
            boolean boots = false;
            int randomItem = this.plugin.generateRandomInt(0,3);
            switch(randomItem) {
                case 0:
                    displayName = ChatColor.DARK_PURPLE + "God Helmet";
                    item = new ItemStack(Material.DIAMOND_HELMET);
                    break;
                case 1:
                    displayName = ChatColor.DARK_PURPLE + "God Chestplate";
                    item = new ItemStack(Material.DIAMOND_CHESTPLATE);
                    break;
                case 2:
                    displayName = ChatColor.DARK_PURPLE + "God Leggings";
                    item = new ItemStack(Material.DIAMOND_LEGGINGS);
                    break;
                case 3:
                    boots = true;
                    displayName = ChatColor.DARK_PURPLE + "God Boots";
                    item = new ItemStack(Material.DIAMOND_BOOTS);
                    break;
            }

            // Add protection 4
            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

            // Add unbreaking 3
            item.addEnchantment(Enchantment.DURABILITY, 3);

            // Get bitmask
            int enchants = this.plugin.generateRandomInt(1, 7); // Lowest: 000 Highest: 111

            // Get intensities + add to lore
            String intensities = "";
            ArrayList<String> lore = new ArrayList<String>();

            // Pick one random potion effect enchantment
            int intensity;
            int n = this.plugin.generateRandomInt(0, 3);
            switch(n) {
                case 0: // % chance to avoid nausea (bit 7)
                    enchants = enchants + 64;
                    intensity = this.plugin.generateRandomInt(1, 100);
                    intensities = intensities + ":" + intensity;
                    lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + intensity + "% chance to avoid nausea");
                    break;
                case 1: // % chance to avoid poison (bit 6)
                    enchants = enchants + 32;
                    intensity = this.plugin.generateRandomInt(1, 100);
                    intensities = intensities + ":" + intensity;
                    lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + intensity + "% chance to avoid poison");
                    break;
                case 2: // % chance to avoid slowness (bit 5)
                    enchants = enchants + 16;
                    intensity = this.plugin.generateRandomInt(1, 100);
                    intensities = intensities + ":" + intensity;
                    lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + intensity + "% chance to avoid slowness");
                    break;
                case 3: // % chance to avoid weakness (bit 4)
                    enchants = enchants + 8;
                    intensity = this.plugin.generateRandomInt(1, 100);
                    intensities = intensities + ":" + intensity;
                    lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + intensity + "% chance to avoid weakness");
                    break;
            }

            // % chance to block damage
            if ((enchants & 0x4) > 0) {
                intensity = this.plugin.generateRandomInt(1, 5);
                intensities = intensities + ":" + intensity;
                lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + intensity + "% chance to block damage");
            }

            // % chance to reflect damage
            if ((enchants & 0x2) > 0) {
                intensity = this.plugin.generateRandomInt(1, 3);
                intensities = intensities + ":" + intensity;
                lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + intensity + "% chance to reflect damage");
            }

            // No fall damage if fall distance < intensity
            if ((enchants & 0x1) > 0) {
                if (boots) {
                    intensity = this.plugin.generateRandomInt(50, 100);
                    intensities = intensities + ":" + intensity;
                    lore.add(ChatColor.RESET + ChatColor.WHITE.toString() + intensity + " block falls and under are negated");
                } else {
                    enchants = enchants - 0x1;
                }
            }

            intensities = intensities.substring(1);

            // Add bitmask stuff to last line of lore
            lore.add(enchants + " " + intensities);

            ItemMeta meta = item.getItemMeta();

            // Set display name
            meta.setDisplayName(displayName);

            // Set lore
            meta.setLore(lore);
            item.setItemMeta(meta);
            items.add(item);
        }
        return items;
    }

	public ArrayList<ItemStack> generateRandomSuperRareItem(int numOfItems) {
		return this.generateRandomSuperRareItem(numOfItems, false);
	}

	public ArrayList<ItemStack> generateRandomSuperRareItem(int numOfItems, boolean isVoting) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();

		if (!isVoting) {
			items.add(new ItemStack(Material.DIAMOND, 40));
            //items.add(new ItemStack(Material.TNT, 30));
            items.add(new ItemStack(Material.EXP_BOTTLE, 64));
            items.add(new ItemStack(Material.EXP_BOTTLE, 64));
		} else {
            items.add(new ItemStack(Material.DIAMOND, 2));
            items.add(new ItemStack(Material.TNT, 1));
        }

		for (int i = 0; i < numOfItems; i++)
		{
			int randomItem = plugin.generateRandomInt(1, 14);
			switch (randomItem) {
				//case 0:
				//	items.add(new ItemStack(Material.MONSTER_EGGS, 10));
				//	break;
				case 1:
					for (int j = 0; i < 10; i ++) {
						items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					}
					break;
				case 2:
					items.add(new ItemStack(Material.BEACON, this.plugin.generateRandomInt(1, 2)));
					break;
				case 3:
					for (int j = 0; i < 7; i ++) {
						items.add(new ItemStack(Material.ENDER_PEARL, 16));
					}
					break;
				case 4:
					EntityType[] validEntities = new EntityType [] {EntityType.CREEPER};
					SpawnEgg egg = new SpawnEgg(validEntities[this.plugin.generateRandomInt(0, validEntities.length - 1)]);
					items.add(egg.toItemStack(4));
					break;
				case 5:
					items.add(new ItemStack(Material.SULPHUR, 64));
					items.add(new ItemStack(Material.FERMENTED_SPIDER_EYE, 64));
					items.add(new ItemStack(Material.GLOWSTONE, 64));
					items.add(new ItemStack(Material.REDSTONE_BLOCK, 64));
					items.add(new ItemStack(Material.BLAZE_ROD, 64));
					items.add(new ItemStack(Material.SPECKLED_MELON, 64));
					items.add(new ItemStack(Material.SLIME_BALL, 64));
					break;
				case 6:
					ItemStack diaHelm = new ItemStack(Material.DIAMOND_HELMET);
					ItemStack diaChest = new ItemStack(Material.DIAMOND_CHESTPLATE);
					ItemStack diaLeg = new ItemStack(Material.DIAMOND_LEGGINGS);
					ItemStack diaBoots = new ItemStack(Material.DIAMOND_BOOTS);
					ItemStack diaSword = new ItemStack(Material.DIAMOND_SWORD);

					diaHelm.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
					diaChest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
					diaLeg.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
					diaBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

					diaHelm.addEnchantment(Enchantment.DURABILITY, 3);
					diaChest.addEnchantment(Enchantment.DURABILITY, 3);
					diaLeg.addEnchantment(Enchantment.DURABILITY, 3);
					diaBoots.addEnchantment(Enchantment.DURABILITY, 3);

					diaSword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
					diaSword.addEnchantment(Enchantment.DURABILITY, 3);
					diaSword.addEnchantment(Enchantment.FIRE_ASPECT, 2);

					items.add(diaHelm);
					items.add(diaChest);
					items.add(diaLeg);
					items.add(diaBoots);
					items.add(diaSword);
					break;
				case 7:
					items.add(new ItemStack(Material.TNT, 64));
					items.add(new ItemStack(Material.TNT, 64));
					break;
				case 8:
					items.add(new ItemStack(Material.DIAMOND_BLOCK, this.plugin.generateRandomInt(20, 64)));
					break;
				case 9:
					items.add(new ItemStack(Material.GOLDEN_APPLE, this.plugin.generateRandomInt(1, 8), (short)1));
					break;
				case 10:
					items.add(new ItemStack(Material.GOLD_BLOCK, this.plugin.generateRandomInt(10, 22)));
					break;
				case 11:
					int tmp = plugin.generateRandomInt(1, 2);
					if (tmp == 1) {
						items.add(generateGodWeapon(1).get(0));
					} else {
						items.add(generateGodArmor(1).get(0));
					}
					break;
				case 12:
					items.add(new ItemStack(Material.DIAMOND_BOOTS, 1));
					items.add(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
					items.add(new ItemStack(Material.DIAMOND_HELMET, 1));
					items.add(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
					items.add(new ItemStack(Material.DIAMOND_SWORD, 1));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.DIAMOND_BOOTS, 1));
					items.add(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
					items.add(new ItemStack(Material.DIAMOND_HELMET, 1));
					items.add(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
					items.add(new ItemStack(Material.DIAMOND_SWORD, 1));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.DIAMOND_BOOTS, 1));
					items.add(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
					items.add(new ItemStack(Material.DIAMOND_HELMET, 1));
					items.add(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
					items.add(new ItemStack(Material.DIAMOND_SWORD, 1));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					break;
				case 13:
					break;
				case 14:
					int type = plugin.generateRandomInt(1, 100);
					EntityType entityType;
					if (type < 30) {
						entityType = EntityType.SPIDER;
					} else if (type < 70) {
						entityType = EntityType.ZOMBIE;
					} else if (type < 90) {
						entityType = EntityType.CAVE_SPIDER;
					} else {
						entityType = EntityType.SKELETON;
					}

					ItemStack item = new ItemStack(Material.MOB_SPAWNER, 1);
					ItemMeta meta = item.getItemMeta();

					List<String> lore = new ArrayList<String>();

					String loreString = entityType.toString();
					loreString = loreString.substring(0, 1).toUpperCase() + loreString.substring(1).toLowerCase();
					loreString = loreString + " Spawner";
					lore.add(loreString);

					meta.setLore(lore);
					item.setItemMeta(meta);

					items.add(item);
					break;
			}
		}

		return items;
	}

	public ArrayList<ItemStack> generateRandomRareItem(int numOfItems) {
		return this.generateRandomRareItem(numOfItems, false);
	}

	public ArrayList<ItemStack> generateRandomRareItem(int numOfItems, boolean isVoting) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		int num;

		if (!isVoting) {
			items.add(new ItemStack(Material.DIAMOND, 30));
            //items.add(new ItemStack(Material.TNT, 20));
            items.add(new ItemStack(Material.EXP_BOTTLE, 64));
            items.add(new ItemStack(Material.EXP_BOTTLE, 32));
		} else {
            items.add(new ItemStack(Material.DIAMOND, 2));
            items.add(new ItemStack(Material.TNT, 1));
        }

		DyeColor [] dyes = new DyeColor [] {DyeColor.BLACK, DyeColor.BLUE, DyeColor.BROWN, DyeColor.CYAN, DyeColor.GRAY, DyeColor.GREEN, DyeColor.LIGHT_BLUE,
												   DyeColor.LIME, DyeColor.MAGENTA, DyeColor.ORANGE, DyeColor.PINK, DyeColor.PURPLE, DyeColor.RED, DyeColor.SILVER, DyeColor.WHITE, DyeColor.YELLOW};

		for (int i = 0; i < numOfItems; i++) {
			int randomItem = plugin.generateRandomInt(0, 12);
			switch (randomItem) {
				case 0:
					items.add(new ItemStack(Material.GOLDEN_APPLE, this.plugin.generateRandomInt(1, 5) , (short)1));
					break;
				case 1:
					items.add(new ItemStack(Material.DIAMOND_BLOCK, this.plugin.generateRandomInt(4, 10)));
					break;
				case 2:
					items.add(new ItemStack(Material.GOLD_BLOCK, this.plugin.generateRandomInt(4, 12)));
					break;
				case 3:
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					items.add(new ItemStack(Material.OBSIDIAN, 64));
					break;
				case 4:
					items.add(new ItemStack(Material.GOLD_ORE, 32));
					items.add(new ItemStack(Material.GOLD_ORE, 32));
					break;
				case 5:
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					break;
				case 6:
					items.add(new ItemStack(Material.HOPPER, this.plugin.generateRandomInt(1,3)));
					break;
				case 7:
					items.add(new ItemStack(Material.GLOWSTONE, 64));
					items.add(new ItemStack(Material.GLOWSTONE, 64));
					items.add(new ItemStack(Material.GLOWSTONE, 64));
					break;
				case 8:
					items.add(new ItemStack(Material.ENDER_STONE, 32));
					items.add(new ItemStack(Material.QUARTZ_ORE, 32));
					items.add(new ItemStack(Material.MYCEL, 32));
					items.add(new ItemStack(Material.LAPIS_BLOCK, 16));
					items.add(new ItemStack(Material.ICE, 32));
					items.add(new ItemStack(Material.PACKED_ICE, 32));
					items.add(new ItemStack(Material.SADDLE, this.plugin.generateRandomInt(1, 4)));
					Wool wool = new Wool(dyes[this.plugin.generateRandomInt(0, dyes.length - 1)]);
					items.add(wool.toItemStack(32));
					items.add(new ItemStack(Material.STAINED_GLASS_PANE, 30, dyes[this.plugin.generateRandomInt(0, dyes.length -1 )].getData()));
					items.add(new ItemStack(Material.STAINED_CLAY, 30, dyes[this.plugin.generateRandomInt(0, dyes.length -1)].getData()));
					items.add(new ItemStack(Material.STAINED_GLASS, 30, dyes[this.plugin.generateRandomInt(0, dyes.length - 1)].getData()));
					items.add(new ItemStack(Material.JUKEBOX, this.plugin.generateRandomInt(1, 10)));
					items.add(new ItemStack(Material.SPONGE, this.plugin.generateRandomInt(1, 12)));
					items.add(new ItemStack(Material.HAY_BLOCK, 24));
					items.add(new ItemStack(Material.CACTUS, 64));
					items.add(new ItemStack(Material.CACTUS, 64));
					items.add(new ItemStack(Material.CACTUS, 64));
					items.add(new ItemStack(Material.CACTUS, 64));
					items.add(new ItemStack(Material.CACTUS, 64));
					break;
				case 9:
					items.add(new ItemStack(Material.DIAMOND_BOOTS, 1));
					items.add(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
					items.add(new ItemStack(Material.DIAMOND_HELMET, 1));
					items.add(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
					items.add(new ItemStack(Material.DIAMOND_SWORD, 1));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					items.add(new ItemStack(Material.EXP_BOTTLE, 64));
					break;
				case 10:
					items.add(new ItemStack(Material.ENDER_PEARL, 16));
					items.add(new ItemStack(Material.ENDER_PEARL, 16));
					items.add(new ItemStack(Material.ENDER_PEARL, 16));
					items.add(new ItemStack(Material.ENDER_PEARL, 16));
					break;
				case 11:
					items.add(new ItemStack(Material.FERMENTED_SPIDER_EYE, 24));
					items.add(new ItemStack(Material.GLOWSTONE, 24));
					items.add(new ItemStack(Material.REDSTONE_BLOCK, 16));
					items.add(new ItemStack(Material.BLAZE_ROD, 15));
					items.add(new ItemStack(Material.SULPHUR, 12));
					items.add(new ItemStack(Material.SPECKLED_MELON, 12));
					items.add(new ItemStack(Material.SLIME_BALL, 10));
					break;
				case 12:
					items.add(new ItemStack(Material.TNT, 25));
					break;
			}
		}
		return items;
	}

	public ArrayList<ItemStack> generateRandomCommonItem(int numOfItems) {
		return this.generateRandomCommonItem(numOfItems, false);
	}

	public ArrayList<ItemStack> generateRandomCommonItem(int numOfItems, boolean isVoting) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		int num;

		DyeColor [] dyes = new DyeColor [] {DyeColor.BLACK, DyeColor.BLUE, DyeColor.BROWN, DyeColor.CYAN, DyeColor.GRAY, DyeColor.GREEN, DyeColor.LIGHT_BLUE,
												   DyeColor.LIME, DyeColor.MAGENTA, DyeColor.ORANGE, DyeColor.PINK, DyeColor.PURPLE, DyeColor.RED, DyeColor.SILVER, DyeColor.WHITE, DyeColor.YELLOW};

		if (!isVoting) {
			items.add(new ItemStack(Material.DIAMOND, 20));
            //items.add(new ItemStack(Material.TNT, 10));
            items.add(new ItemStack(Material.EXP_BOTTLE, 64));
		} else {
            items.add(new ItemStack(Material.DIAMOND, 2));
            items.add(new ItemStack(Material.TNT, 1));
        }

		for (int i = 0; i < numOfItems; i++) {
			int randomItem = plugin.generateRandomInt(0, 11);
			switch (randomItem) {
				case 0:
					num = this.plugin.generateRandomInt(1, 2);
					switch (num) {
						case 1:
							items.add(new ItemStack(Material.BROWN_MUSHROOM, 12));
							items.add(new ItemStack(Material.PORK, 48));
							break;
						case 2:
							items.add(new ItemStack(Material.RED_MUSHROOM, 12));
							items.add(new ItemStack(Material.BAKED_POTATO, 48));
							break;
					}
					switch (num) {
						case 1:
							items.add(new ItemStack(Material.COOKED_CHICKEN, 32));
							items.add(new ItemStack(Material.COOKED_BEEF, 48));
							break;
						case 2:
							items.add(new ItemStack(Material.BREAD, 32));
							items.add(new ItemStack(Material.COOKED_FISH, 12, (short)this.plugin.generateRandomInt(0, 3)));
							break;
					}
					break;
				case 1:
					Wool wool = new Wool(dyes[this.plugin.generateRandomInt(0, dyes.length - 1)]);
					items.add(wool.toItemStack(32));
					break;
				case 2:
					items.add(new ItemStack(Material.REDSTONE_ORE, 32));
					break;
				case 3:
					num = this.plugin.generateRandomInt(0, 2);
					items.add(new ItemStack(Material.ANVIL, this.plugin.generateRandomInt(5, 10), (short)num));
					items.add(new ItemStack(Material.ENCHANTMENT_TABLE, 1));
					items.add(new ItemStack(Material.NOTE_BLOCK, this.plugin.generateRandomInt(1, 10)));
					items.add(new ItemStack(Material.JUKEBOX, this.plugin.generateRandomInt(1, 10)));
					items.add(new ItemStack(Material.SPONGE, this.plugin.generateRandomInt(1, 12)));
					break;
				case 4:
					items.add(new ItemStack(Material.POWERED_RAIL, 24));
					items.add(new ItemStack(Material.RAILS, 32));
					items.add(new ItemStack(Material.MINECART));
					items.add(new ItemStack(Material.ACTIVATOR_RAIL, 24));
					break;
				case 5:
					items.add(new ItemStack(Material.COAL_BLOCK, 64));
				case 6:
					items.add(new ItemStack(Material.ENDER_PEARL, 16));
					items.add(new ItemStack(Material.ENDER_PEARL, 16));
					items.add(new ItemStack(Material.ENDER_PEARL, 16));
					break;
				case 7:
					items.add(new ItemStack(Material.LEASH, 3));
					break;
				case 8:
					items.add(new ItemStack(Material.TNT, 20));
					break;
				case 9:
					items.add(new ItemStack(Material.FERMENTED_SPIDER_EYE, 12));
					items.add(new ItemStack(Material.GLOWSTONE_DUST, 12));
					items.add(new ItemStack(Material.REDSTONE, 12));
					items.add(new ItemStack(Material.BLAZE_ROD, 12));
					items.add(new ItemStack(Material.SULPHUR, 12));
					items.add(new ItemStack(Material.SPECKLED_MELON, 12));
					items.add(new ItemStack(Material.SLIME_BALL, 12));
					break;
				case 10:
					items.add(new ItemStack(Material.DIAMOND_BOOTS, 1));
					items.add(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
					items.add(new ItemStack(Material.DIAMOND_HELMET, 1));
					items.add(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
					break;
				case 11:
					items.add(new ItemStack(Material.ENDER_STONE, 10));
					items.add(new ItemStack(Material.QUARTZ_ORE, 10));
					items.add(new ItemStack(Material.MYCEL, 6));
					items.add(new ItemStack(Material.LAPIS_BLOCK, 4));
					items.add(new ItemStack(Material.ICE, 8));
					items.add(new ItemStack(Material.PACKED_ICE, 8));
					items.add(new ItemStack(Material.SADDLE, this.plugin.generateRandomInt(1, 2)));
					wool = new Wool(dyes[this.plugin.generateRandomInt(0, dyes.length - 1)]);
					items.add(wool.toItemStack(8));
					items.add(new ItemStack(Material.STAINED_GLASS_PANE, 10, dyes[this.plugin.generateRandomInt(0, dyes.length -1 )].getData()));
					items.add(new ItemStack(Material.STAINED_CLAY, 10, dyes[this.plugin.generateRandomInt(0, dyes.length -1)].getData()));
					items.add(new ItemStack(Material.STAINED_GLASS, 10, dyes[this.plugin.generateRandomInt(0, dyes.length - 1)].getData()));
					items.add(new ItemStack(Material.JUKEBOX, this.plugin.generateRandomInt(1, 2)));
					items.add(new ItemStack(Material.SPONGE, this.plugin.generateRandomInt(1, 2)));
					items.add(new ItemStack(Material.HAY_BLOCK, 4));
					break;
			}
		}
		return items;
	}
	
	public ArrayList<ItemStack> generateRandomTrashItem(int numOfItems) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		int num;

		DyeColor [] dyes = new DyeColor [] {DyeColor.BLACK, DyeColor.BLUE, DyeColor.BROWN, DyeColor.CYAN, DyeColor.GRAY, DyeColor.GREEN, DyeColor.LIGHT_BLUE,
												   DyeColor.LIME, DyeColor.MAGENTA, DyeColor.ORANGE, DyeColor.PINK, DyeColor.PURPLE, DyeColor.RED, DyeColor.SILVER, DyeColor.WHITE, DyeColor.YELLOW};
		
		for (int i = 0; i < numOfItems; i++) {
			int randomItem = plugin.generateRandomInt(0, 10);
			switch (randomItem) {
				case 0:
					items.add(new ItemStack(Material.DIRT, 64));
					break;
				case 1:
					items.add(new ItemStack(Material.SAND, 64));
					break;
				case 2:
					items.add(new ItemStack(Material.SAPLING, 12, (short)this.plugin.generateRandomInt(0, 5)));
					break;
				case 3:
					//items.add(new ItemStack(Material.LEAVES, 64, (short)this.plugin.generateRandomInt(0, 3)));
					items.add(new ItemStack(Material.WATCH, 64));
					break;
				case 4:
					items.add(new ItemStack(Material.CACTUS, 64));
					break;
				case 5:
					items.add(new ItemStack(Material.CLAY, 64));
					break;
				case 6:
					items.add(new ItemStack(Material.PUMPKIN_PIE, 20));
					break;
				case 7:
					items.add(new ItemStack(Material.NETHERRACK, 32));
					break;
				case 8:
					items.add(new ItemStack(Material.HAY_BLOCK, 12));
					break;
				case 9:
					items.add(new ItemStack(Material.SEEDS, 64));
					break;
				case 10:
					items.add(new ItemStack(Material.COMPASS, 64));
					break;
				/*case 11:
					items.add(new ItemStack(Material.WATCH, 64));
					break;
				case 12:
					num = this.plugin.generateRandomInt(1, 9);
					switch (num) {
						case 1:
							items.add(new ItemStack(Material.WOOD_AXE, 1));
							break;
						case 2:
							items.add(new ItemStack(Material.LEATHER_BOOTS, 1));
							break;
						case 3:
							items.add(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
							break;
						case 4:
							items.add(new ItemStack(Material.LEATHER_HELMET, 1));
							break;
						case 5:
							items.add(new ItemStack(Material.WOOD_HOE, 1));
							break;
						case 6:
							items.add(new ItemStack(Material.LEATHER_LEGGINGS, 1));
							break;
						case 7:
							items.add(new ItemStack(Material.WOOD_PICKAXE, 1));
							break;
						case 8:
							items.add(new ItemStack(Material.WOOD_SPADE, 1));
							break;
						case 9:
							items.add(new ItemStack(Material.WOOD_SWORD, 1));
							break;
					}
					break;*/
			}
		}
	
		return items;
	}

	// Dont bother using
	public ArrayList<ItemStack> generateRandomVoteItem(int numOfItems) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		int num;

		DyeColor [] dyes = new DyeColor [] {DyeColor.BLACK, DyeColor.BLUE, DyeColor.BROWN, DyeColor.CYAN, DyeColor.GRAY, DyeColor.GREEN, DyeColor.LIGHT_BLUE,
												   DyeColor.LIME, DyeColor.MAGENTA, DyeColor.ORANGE, DyeColor.PINK, DyeColor.PURPLE, DyeColor.RED, DyeColor.SILVER, DyeColor.WHITE, DyeColor.YELLOW};

		for (int i = 0; i < numOfItems; i++) {
			int randomItem = plugin.generateRandomInt(0, 10);
			switch (randomItem) {
				case 0:
					items.add(new ItemStack(Material.DIAMOND, plugin.generateRandomInt(5, 15)));
					break;
				case 1:
					items.add(new ItemStack(Material.GOLD_ORE, plugin.generateRandomInt(5, 15)));
					break;
				case 2:
					items.add(new ItemStack(Material.GOLDEN_APPLE, 1));
					break;
				case 3:
					items.add(new ItemStack(Material.DIAMOND_BLOCK, plugin.generateRandomInt(1,2)));
					break;
				case 4:
					items.add(new ItemStack(Material.ANVIL, 5));
					break;
				case 5:
					items.add(new ItemStack(Material.FERMENTED_SPIDER_EYE, 3));
					items.add(new ItemStack(Material.GLOWSTONE_DUST, 12));
					items.add(new ItemStack(Material.REDSTONE, 12));
					items.add(new ItemStack(Material.BLAZE_ROD, 3));
					items.add(new ItemStack(Material.SULPHUR, 4));
					items.add(new ItemStack(Material.SPECKLED_MELON, 3));
					items.add(new ItemStack(Material.SLIME_BALL, 3));
					break;
				case 6:
					items.add(new ItemStack(Material.ENDER_STONE, 10));
					items.add(new ItemStack(Material.QUARTZ_ORE, 10));
					items.add(new ItemStack(Material.MYCEL, 6));
					items.add(new ItemStack(Material.LAPIS_BLOCK, 4));
					items.add(new ItemStack(Material.ICE, 8));
					items.add(new ItemStack(Material.PACKED_ICE, 8));
					items.add(new ItemStack(Material.SADDLE, this.plugin.generateRandomInt(1, 2)));
					Wool wool = new Wool(dyes[this.plugin.generateRandomInt(0, dyes.length - 1)]);
					items.add(wool.toItemStack(8));
					items.add(new ItemStack(Material.STAINED_GLASS_PANE, 10, dyes[this.plugin.generateRandomInt(0, dyes.length -1 )].getData()));
					items.add(new ItemStack(Material.STAINED_CLAY, 10, dyes[this.plugin.generateRandomInt(0, dyes.length -1)].getData()));
					items.add(new ItemStack(Material.STAINED_GLASS, 10, dyes[this.plugin.generateRandomInt(0, dyes.length - 1)].getData()));
					items.add(new ItemStack(Material.JUKEBOX, this.plugin.generateRandomInt(1, 2)));
					items.add(new ItemStack(Material.SPONGE, this.plugin.generateRandomInt(1, 2)));
					items.add(new ItemStack(Material.HAY_BLOCK, 4));
					break;
				case 7:
					items.add(new ItemStack(Material.EXP_BOTTLE, plugin.generateRandomInt(32,64)));
					break;
				case 8:
					items.add(new ItemStack(Material.EXP_BOTTLE, plugin.generateRandomInt(32,64)));
					items.add(new ItemStack(Material.EXP_BOTTLE, plugin.generateRandomInt(32,64)));
					items.add(new ItemStack(Material.EXP_BOTTLE, plugin.generateRandomInt(32,64)));
					break;
				case 9:
					items.add(new ItemStack(Material.ENDER_PEARL, plugin.generateRandomInt(3,16)));
					break;
				case 10:
					items.add(new ItemStack(Material.ENDER_PEARL, plugin.generateRandomInt(3,16)));
					items.add(new ItemStack(Material.ENDER_PEARL, plugin.generateRandomInt(3,16)));
					break;
			}
		}

		return items;
	}
}
