package us.zonix.hcfactions.itemdye;

import org.bukkit.ChatColor;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class ItemDyeListeners implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (itemStack != null && itemStack.getType() == Material.EXP_BOTTLE && event.getAction().name().contains("RIGHT")) {
            if (itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().startsWith(ChatColor.BLUE.toString())) {
                int level = Integer.parseInt(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).substring(0, itemStack.getItemMeta().getDisplayName().indexOf(' ') - 1).trim());
                player.setLevel(player.getLevel() + level);
                event.setCancelled(true);

                if (itemStack.getAmount() > 1) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                } else {
                    player.setItemInHand(new ItemStack(Material.AIR));
                }

            }
        }

    }

    @EventHandler
    public void onItemCraftEvent(CraftItemEvent event) {
        if (event.getRecipe().getResult().getType() == Material.EXP_BOTTLE) {
            for (HumanEntity entity : event.getViewers()) {
                ((Player)entity).setLevel(0);
                ((Player)entity).setExp(0);
            }
        }
    }

    @EventHandler
    public void onPrepareItemCraftEvent(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() == Material.EXP_BOTTLE) {

            int xp = 0;
            for (HumanEntity entity : event.getViewers()) {
                Player player = (Player) entity;
                if (player.getLevel() == 0 || !Profile.getByUuid(player.getUniqueId()).getRank().isAboveOrEqual(Rank.SILVER)) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    return;
                } else {
                    xp = player.getLevel();
                    break;
                }
            }

            for (int i = 1; i < event.getInventory().getSize(); i++) {
                ItemStack item = event.getInventory().getItem(i);

                if (item != null && item.getType() == Material.GLASS_BOTTLE && item.getAmount() > 1) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    return;
                }

            }

            event.getInventory().setResult(new ItemBuilder(Material.EXP_BOTTLE).name(ChatColor.BLUE + "" + xp + " experience levels").build());
        }

        if (recipe.getResult().hasItemMeta() && recipe.getResult().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.MAGIC + "ITEM_DYE")) {
            ItemStack result = event.getInventory().getItem(5);

            if (result == null || result.getType() == Material.AIR) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }

            if (!result.getItemMeta().hasDisplayName()) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }

            ItemDye dye = ItemDye.getByData(event.getInventory().getItem(8).getDurability());

            if (dye == null) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }

            event.getInventory().setResult(dye.getResult(result));
        }

    }

}
