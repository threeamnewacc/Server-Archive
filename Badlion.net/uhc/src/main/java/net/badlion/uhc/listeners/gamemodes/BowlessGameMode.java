package net.badlion.uhc.listeners.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BowlessGameMode implements GameMode {

    @EventHandler
    public void onBowCrafted(CraftItemEvent event) {
        if (event.getRecipe().getResult().getType() == Material.BOW) {
            event.getInventory().setResult(new ItemStack(Material.AIR));

            ((Player) event.getView().getPlayer()).sendMessage(ChatColor.RED + "You cannot craft bows");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onUseBow(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.BOW) {
            event.getPlayer().setItemInHand(null);
            event.getPlayer().updateInventory();
	        event.getPlayer().sendFormattedMessage(ChatColor.RED + "Bows are not allowed");
        }
    }

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Bowless");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Bows cannot be crafted");
        lore.add(ChatColor.AQUA + "- Bows aren't dropped by mobs");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @Override
    public void unregister() {
        CraftItemEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);
    }

}
