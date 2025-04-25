package us.zonix.hcfactions.enchantmentlimiter;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public class EnchantmentLimiterListeners implements Listener {

    @EventHandler
    public void onEnchantItemtEvent(EnchantItemEvent event) {
        for (Enchantment enchantment : new HashSet<>(event.getEnchantsToAdd().keySet())) {
            int limit = EnchantmentLimiter.getInstance().getEnchantmentLimit(enchantment);
            int level = event.getEnchantsToAdd().get(enchantment);

            if (level > limit) {

                if (limit == 0) {
                    event.getEnchantsToAdd().remove(enchantment);
                } else {
                    event.getEnchantsToAdd().remove(enchantment);
                    event.getEnchantsToAdd().put(enchantment, limit);
                }

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        if (inventory.getType() == InventoryType.ANVIL && event.getRawSlot() == 2) {
            ItemStack result = inventory.getItem(2);
            if (result != null) {
                event.setCancelled(true);

                for (Enchantment enchantment : new HashSet<>(result.getEnchantments().keySet())) {
                    int limit = EnchantmentLimiter.getInstance().getEnchantmentLimit(enchantment);
                    int level = result.getEnchantmentLevel(enchantment);

                    if (level > limit) {

                        if (limit == 0) {
                            result.removeEnchantment(enchantment);
                        } else {
                            result.removeEnchantment(enchantment);
                            result.addEnchantment(enchantment, limit);
                        }

                        player.updateInventory();
                    }
                }

                inventory.setItem(0, new ItemStack(Material.AIR));
                inventory.setItem(1, new ItemStack(Material.AIR));
                inventory.setItem(2, new ItemStack(Material.AIR));

                if (event.getClick().name().contains("SHIFT")) {
                    player.getInventory().addItem(result);
                } else {
                    event.setCursor(result);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();

        if (player != null) {
            ItemStack itemStack = player.getItemInHand();

            if (itemStack != null && itemStack.getType() != Material.AIR && itemStack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) > 0) {
                int level = (itemStack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) + 1) * 2;

                event.setDroppedExp(event.getDroppedExp() * level);
            }

        }

    }

}
