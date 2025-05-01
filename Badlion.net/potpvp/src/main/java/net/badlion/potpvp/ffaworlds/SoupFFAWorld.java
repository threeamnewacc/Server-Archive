package net.badlion.potpvp.ffaworlds;

import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SoupFFAWorld extends KitFFAWorld implements Listener {

    private ItemStack[] soups = new ItemStack[54];

    public SoupFFAWorld(ItemStack ffaItem, KitRuleSet kitRuleSet) {
        super(ffaItem, kitRuleSet);

        for (int i = 0; i < 54; i++) {
            this.soups[i] = new ItemStack(Material.MUSHROOM_SOUP);
        }

        this.kitInventory = new SmellyInventory(new SoupKitSelectorScreenHandler(), 18, ChatColor.AQUA + ChatColor.BOLD.toString() + "Choose Kit");
    }

    @Override
    public void startGame() {
        this.spawn = new Location(PotPvP.getInstance().getServer().getWorld("world"), 38.5, 84.5, 881.5);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.players.contains(player)) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.WALL_SIGN
                && !((Sign) event.getClickedBlock().getState()).getLine(1).isEmpty()) {
                Inventory soupInventory = Bukkit.createInventory(null, 54, "Soup Refill");
                soupInventory.setContents(this.soups);
                player.openInventory(soupInventory);
            }
        }
    }

    @EventHandler
    public void onPlayerDrinkSoupEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.players.contains(player)) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getItem() != null && event.getItem().getType() == Material.MUSHROOM_SOUP) {
                    if (!player.isDead()) {
                        if (player.getHealth() < 20) {
                            if (player.getHealth() + 7 < 20) {
                                player.setHealth(player.getHealth() + 7);
                                player.getItemInHand().setType(Material.BOWL);
                                player.getItemInHand().setItemMeta(null);
                            } else {
                                player.setHealth(20);
                                player.getItemInHand().setType(Material.BOWL);
                                player.getItemInHand().setItemMeta(null);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHealthRegenEvent(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (this.players.contains(player)) {
                if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (this.players.contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    private static class SoupKitSelectorScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {

        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }

}
