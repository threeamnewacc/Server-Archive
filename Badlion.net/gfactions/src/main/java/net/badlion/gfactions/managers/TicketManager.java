package net.badlion.gfactions.managers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SpawnEgg;

import java.util.List;

public class TicketManager {

	/*
	 * The format of a "Note" is going to be something like this:
	 * DisplayName: "Note for "
	 * Lore:
	 * 	Line 1: Number of items in a stack + Name again
	 *  Line 2: Durability of item (if applicable)
	 *  Line 3: Enchantments (if applicable)
	 *  Line 4: TBD
	 */
	public static String niceMaterialName(String material) {
		String [] words = material.replace('_', ' ').toLowerCase().split(" ");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			if (i > 0) {
				builder.append(" ");
			}
			builder.append(words[i].substring(0, 1).toUpperCase());
			builder.append(words[i].substring(1));
		}

		return builder.toString();
	}

	public static String originalMaterialName(String niceName) {
		return niceName.toUpperCase().replace(' ', '_');
	}

	/*public static ItemStack createNoteFromItemStack(ItemStack item) {
		ItemStack note = new ItemStack(Material.PAPER);
		ItemMeta meta = note.getItemMeta();
		List<String> lines = new ArrayList<String>();
		meta.setDisplayName(ChatColor.DARK_PURPLE + "Voucher for " + TicketManager.niceMaterialName(item.getType().name()));

		if (item.getType() == Material.POTION) {
			// Potions (splash and nonsplash)
			Potion potion = Potion.fromItemStack(item);
			Collection<PotionEffect> effects = potion.getEffects();
			String potionEffect = null;
			for (PotionEffect effect : effects) {
				potionEffect = effect.getType().getName();
			}
			// TODO: Material.GLASS_BOTTLE short 1 for XP?
			lines.add(item.getAmount() + " " + (potion.isSplash() ? "Splash" : "Drinkable"));
		} else if (item.getType() == Material.MONSTER_EGG) {
			// Monster eggs
			SpawnEgg egg = (SpawnEgg) item.getData();
			lines.add(item.getAmount() + " " + TicketManager.niceMaterialName(egg.getSpawnedType().name()) + " Monster Egg");
		} else if (item.getType() == Material.ANVIL) {
			// Anvil's are weird
			String anvilDmg = null;
			if (item.getData().getData() == 1) {
				anvilDmg = "Slightly Damaged";
			} else if (item.getData().getData() == 2) {
				anvilDmg = "Very Damaged";
			} else {
				anvilDmg = "Not Damaged";
			}
			lines.add(item.getAmount() + " " + anvilDmg + " Anvil");
		} else if (item.getType() == Material.GOLDEN_APPLE) {
			// Apple/opple
			if (item.getDurability() == 1) {
				lines.add(item.getAmount() + " Opple");
			} else {
				lines.add(item.getAmount() + " Golden Apple");
			}
		} else {
			// Regular items
			lines.add(item.getAmount() + " " + TicketManager.niceMaterialName(item.getType().name()));
			lines.add("Durability: " + (item.getType().getMaxDurability() - item.getDurability()));
		}

		// Set the lore we made
		meta.setLore(lines);
		note.setItemMeta(meta);

		return note;
	}*/

	public static ItemStack toItemStackFromNote(ItemStack note) {
		// Sanity check
		if (note.getType() != Material.PAPER) {
			return null;
		}

		ItemMeta meta = note.getItemMeta();

		// Sanity check
		if (!meta.getDisplayName().startsWith(ChatColor.DARK_PURPLE + "Voucher for")) {
			return null;
		}

		// Break off the name and create an itemstack
		String nameOfItemMaterial = meta.getDisplayName().split(ChatColor.DARK_PURPLE + "Voucher for ")[1]; // second half
		ItemStack item = new ItemStack(Material.valueOf(originalMaterialName(nameOfItemMaterial)));
		List<String> lines = meta.getLore();
		String [] firstLine = lines.get(0).split(" ", 2);

		// Get the # of items
		try {
			item.setAmount(Integer.parseInt(firstLine[0]));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}

		if (item.getType() == Material.POTION) {
			// Potions (splash and nonsplash)
		} else if (item.getType() == Material.MONSTER_EGG) {
			// Monster eggs
			SpawnEgg egg = (SpawnEgg) item.getData();
			egg.setSpawnedType(EntityType.valueOf(TicketManager.originalMaterialName(firstLine[1].split("[a-zA-Z0-9 ]Monster Egg")[0])));

			// Monster eggs are weird with # of items
			try {
				item = egg.toItemStack(Integer.parseInt(firstLine[0]));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return null;
			}
		} else if (item.getType() == Material.ANVIL) {
			// Anvil's are weird
			String anvilDmg = firstLine[1].split("[a-zA-Z0-9 ]Anvil")[0];
			if (anvilDmg.equals("Slightly Damaged")) {
				MaterialData md = item.getData();
				md.setData((byte) 1);
				try {
					item = md.toItemStack(Integer.parseInt(firstLine[0]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return null;
				}
			} else if (anvilDmg.equals("Very Damaged")) {
				MaterialData md = item.getData();
				md.setData((byte) 2);
				try {
					item = md.toItemStack(Integer.parseInt(firstLine[0]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return null;
				}
			} else if (anvilDmg.equals("Not Damaged")) {
				// do nothing
			}
		} else if (item.getType() == Material.GOLDEN_APPLE) {
			// Apple/opple
			if (firstLine[1].equals("Opple")) {
				item.setDurability((short) 1);
			}
		} else {
			// Regular items
			item.setType(Material.valueOf(TicketManager.originalMaterialName(TicketManager.originalMaterialName(firstLine[1]))));
			String [] secondLine = lines.get(1).split(" ");
			try {
				item.setDurability((short) (item.getType().getMaxDurability() - Short.parseShort(secondLine[1])));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return null;
			}
		}

		return item;
	}

}
