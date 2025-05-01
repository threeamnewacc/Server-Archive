package net.badlion.gfactions.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KitListener implements Listener {

    private static Map<Enchantment, Integer> maxEnchants = new HashMap<>();
    private static Set<Short> potions = new HashSet<>();

    public KitListener() {
        KitListener.maxEnchants.put(Enchantment.PROTECTION_ENVIRONMENTAL, -1);
        KitListener.maxEnchants.put(Enchantment.PROTECTION_EXPLOSIONS, -1);
        KitListener.maxEnchants.put(Enchantment.PROTECTION_FIRE, -1);
        KitListener.maxEnchants.put(Enchantment.PROTECTION_PROJECTILE, -1);
        KitListener.maxEnchants.put(Enchantment.PROTECTION_FALL, 4);

        KitListener.maxEnchants.put(Enchantment.ARROW_KNOCKBACK, -1);
        KitListener.maxEnchants.put(Enchantment.ARROW_DAMAGE, 2);
        KitListener.maxEnchants.put(Enchantment.ARROW_FIRE, 1);
        KitListener.maxEnchants.put(Enchantment.ARROW_INFINITE, 1);

        KitListener.maxEnchants.put(Enchantment.DAMAGE_ALL, 1);
        KitListener.maxEnchants.put(Enchantment.DAMAGE_ARTHROPODS, 1);
        KitListener.maxEnchants.put(Enchantment.DAMAGE_UNDEAD, 1);

        KitListener.maxEnchants.put(Enchantment.FIRE_ASPECT, 1);

        KitListener.maxEnchants.put(Enchantment.KNOCKBACK, -1);

        /**
         * Potions that are not allowed are listed below
         */

        // Strength
        KitListener.potions.add((short) 8201);
        KitListener.potions.add((short) 8233);
        KitListener.potions.add((short) 8265);
        KitListener.potions.add((short) 16393);
        KitListener.potions.add((short) 16425);
        KitListener.potions.add((short) 16457);

        // Regen
        KitListener.potions.add((short) 8193);
        KitListener.potions.add((short) 8225);
        KitListener.potions.add((short) 8257);
        KitListener.potions.add((short) 16385);
        KitListener.potions.add((short) 16417);
        KitListener.potions.add((short) 16449);

        // Invisibility
        KitListener.potions.add((short) 8238);
        KitListener.potions.add((short) 8270);
        KitListener.potions.add((short) 16430);
        KitListener.potions.add((short) 16462);

        // Weakness
        //KitListener.potions.add((short) 8232);
        KitListener.potions.add((short) 8264);
        //KitListener.potions.add((short) 16424);
        KitListener.potions.add((short) 16456);

        // Instant Damage
        KitListener.potions.add((short) 8268);
        KitListener.potions.add((short) 8236);
        KitListener.potions.add((short) 16460);
        KitListener.potions.add((short) 16428);

        // Poison
        KitListener.potions.add((short) 8196);
        KitListener.potions.add((short) 8228);
        KitListener.potions.add((short) 8260);
        KitListener.potions.add((short) 16388);
        KitListener.potions.add((short) 16420);
        KitListener.potions.add((short) 16452);
    }

    @EventHandler
    public void onPlayerUseItem(InventoryClickEvent event) {
        if (event.getCurrentItem() != null) {
            ItemStack item = event.getCurrentItem();

            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                Integer max = KitListener.maxEnchants.get(entry.getKey());

                if (max != null) {
                    if (max == -1) {
                        item.removeEnchantment(entry.getKey());

                        if (event.getWhoClicked() instanceof Player) {
                            ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "This item's enchantments have been reduced to match the map's kit limitations.");
                        }
                    } else if (entry.getValue() > max) {
                        item.removeEnchantment(entry.getKey());
                        item.addEnchantment(entry.getKey(), max);

                        if (event.getWhoClicked() instanceof Player) {
                            ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "This item's enchantments have been reduced to match the map's kit limitations.");
                        }
                    }
                }
            }

            if (item.getType() == Material.POTION) {
                if (KitListener.potions.contains(item.getDurability())) {
                    item.setType(Material.AIR);
                    if (event.getWhoClicked() instanceof Player) {
                        ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "This potion is not allowed for this map.");
                    }
                } else if (item.getDurability() == (short) 16420 || item.getDurability() == (short) 16452) {
                    item.setDurability((short) 16388);
                    if (event.getWhoClicked() instanceof Player) {
                        ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Only Poison I (0:33) is allowed.");
                    }
                } else if (item.getDurability() == (short) 16458) {
                    item.setDurability((short) 16426);
                    if (event.getWhoClicked() instanceof Player) {
                        ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Only Slowness I (1:07) is allowed.");
                    }
                }
            } else if (item.getType() == Material.GOLDEN_APPLE && item.getDurability() == (short) 1) {
                item.setDurability((short) 0);
                if (event.getWhoClicked() instanceof Player) {
                    ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "God Apples are not permitted.");
                }
            }

            event.setCurrentItem(item);
        }
    }

    @EventHandler
    public void onPlayerPickUpItem(PlayerPickupItemEvent event) {
        if (event.getItem() != null) {
            ItemStack item = event.getItem().getItemStack();

            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                Integer max = KitListener.maxEnchants.get(entry.getKey());

                if (max != null) {
                    if (max == -1) {
                        item.removeEnchantment(entry.getKey());

                        event.getPlayer().sendMessage(ChatColor.RED + "This item's enchantments have been reduced to match the map's kit limitations.");
                    } else if (entry.getValue() > max) {
                        item.removeEnchantment(entry.getKey());
                        item.addEnchantment(entry.getKey(), max);

                        event.getPlayer().sendMessage(ChatColor.RED + "This item's enchantments have been reduced to match the map's kit limitations.");
                    }
                }
            }

            if (item.getType() == Material.POTION) {
                if (KitListener.potions.contains(item.getDurability())) {
                    item.setType(Material.AIR);
                    event.getPlayer().sendMessage(ChatColor.RED + "This potion is not allowed for this map.");
                } else if (item.getDurability() == (short) 16420 || item.getDurability() == (short) 16452) {
                    item.setDurability((short) 16388);
                    event.getPlayer().sendMessage(ChatColor.RED + "Only Poison I (0:33) is allowed.");
                } else if (item.getDurability() == (short) 16458) {
                    item.setDurability((short) 16426);
                    event.getPlayer().sendMessage(ChatColor.RED + "Only Slowness I (1:07) is allowed.");
                }
            } else if (item.getType() == Material.GOLDEN_APPLE && item.getDurability() == (short) 1) {
                item.setDurability((short) 0);
                event.getPlayer().sendMessage(ChatColor.RED + "God Apples are not permitted.");
            }
        }
    }


}
