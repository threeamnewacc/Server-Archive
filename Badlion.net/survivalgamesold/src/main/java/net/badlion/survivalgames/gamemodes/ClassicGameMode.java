package net.badlion.survivalgames.gamemodes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ClassicGameMode implements GameMode, Listener {

    private Random random = new Random();

    /**
     * Get a Tier 1 Item
     */
    public ItemStack getTier1Item() {
        int rarity = random.nextInt(100);

        if (rarity < 40) {
            int i = random.nextInt(7);
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
                case 5:
                    return new ItemStack(Material.WOOD_SWORD);
                case 6:
                    return new ItemStack(Material.GOLD_AXE);
            }
        } else if (rarity < 75) {
            int i = random.nextInt(9);
            switch (i) {
                case 0:
                    return new ItemStack(Material.ARROW, 2);
                case 1:
                    return new ItemStack(Material.IRON_INGOT, 1);
                case 2:
                    return new ItemStack(Material.FLINT, 1);
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
                case 8:
                    return new ItemStack(Material.STONE_AXE);
            }
        } else {
            int i = random.nextInt(4);
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

        return null;
    }

    /**
     * Get a Tier 2 Item
     */
    public ItemStack getTier2Item() {
        int rarity = random.nextInt(100);

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
                    return new ItemStack(Material.ARROW, 3);
                case 8:
                    return new ItemStack(Material.BOW);
                case 9:
                    return new ItemStack(Material.STICK, 2);
                case 10:
                    return new ItemStack(Material.IRON_INGOT);
            }
        } else if (rarity < 75) {
            int i = random.nextInt(6);
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
                    return new ItemStack(Material.BOAT);
                case 5:
                    return new ItemStack(Material.IRON_AXE);
            }
        } else if (rarity < 95) {
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
                    ItemStack item = new ItemStack(Material.FLINT_AND_STEEL);
                    int dur = 63 - random.nextInt(2);
                    item.setDurability((short) dur);
                    return item;
            }
        } else {int i = random.nextInt(2);
            switch (i) {
                case 0:
                    return new ItemStack(Material.DIAMOND);
                case 1:
                    return new ItemStack(Material.GOLDEN_APPLE);
            }
        }

        return null;
    }

    /**
     * Do anything special for a death
     */
    public void handleDeath(Player died) {
        // Do nothing special
    }

    @EventHandler
    public void onPlayerCraftFlintAndSteel(PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult().getType() == Material.FLINT_AND_STEEL) {
	        ItemStack item = new ItemStack(Material.FLINT_AND_STEEL);
	        item.setDurability((short) 61);
	        event.getInventory().setResult(item);
        }
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onPlayerLightBlockOnFire(BlockIgniteEvent event) {
        if (event.getBlock().getType() != Material.AIR) {
            event.setCancelled(true);
        } else if (event.getBlock().getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {
            event.setCancelled(true);
        }
    }

    /**
     * Return name
     */
    public String name() {
        return "Classic";
    }

}
