package net.badlion.gcheat.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.Potion;

import java.util.*;

public class FoodFixListener implements Listener {

    private Set<Material> unallowedItems = new HashSet<>();

    public FoodFixListener() {
        this.unallowedItems.add(Material.WOOD_AXE);
        this.unallowedItems.add(Material.WOOD_SWORD);
        this.unallowedItems.add(Material.STONE_AXE);
        this.unallowedItems.add(Material.STONE_SWORD);
        this.unallowedItems.add(Material.IRON_AXE);
        this.unallowedItems.add(Material.IRON_SWORD);
        this.unallowedItems.add(Material.GOLD_AXE);
        this.unallowedItems.add(Material.GOLD_SWORD);
        this.unallowedItems.add(Material.DIAMOND_AXE);
        this.unallowedItems.add(Material.DIAMOND_SWORD);
        this.unallowedItems.add(Material.MUSHROOM_SOUP);
        this.unallowedItems.add(Material.APPLE);
        this.unallowedItems.add(Material.COOKED_FISH);
        this.unallowedItems.add(Material.MELON);
        this.unallowedItems.add(Material.COOKED_BEEF);
        this.unallowedItems.add(Material.COOKED_CHICKEN);
        this.unallowedItems.add(Material.GRILLED_PORK);
        this.unallowedItems.add(Material.CARROT);
        this.unallowedItems.add(Material.BAKED_POTATO);
        this.unallowedItems.add(Material.PUMPKIN_PIE);
        this.unallowedItems.add(Material.GOLDEN_APPLE);
        this.unallowedItems.add(Material.GOLDEN_CARROT);
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onPlayerEatWhileLookingAtFence(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getClickedBlock().getType() == Material.FENCE || event.getClickedBlock().getType() == Material.NETHER_FENCE)) {
            if (event.getItem() != null && (this.unallowedItems.contains(event.getItem().getType())
                    || (event.getItem().getType() == Material.POTION && !Potion.fromItemStack(event.getItem()).isSplash()))) {
                event.setCancelled(true);
            }
        }
    }

}
