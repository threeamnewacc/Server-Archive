package net.badlion.uhc.listeners.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RodlessGameMode implements GameMode {

    @EventHandler
    public void onFishingRodCrafted(CraftItemEvent event) {
        if (event.getRecipe().getResult().getType() == Material.FISHING_ROD) {
            event.getInventory().setResult(new ItemStack(Material.AIR));

            ((Player) event.getView().getPlayer()).sendMessage(ChatColor.RED + "You cannot craft fishing rods");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onUseFishingRod(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.FISHING_ROD) {
            event.getPlayer().setItemInHand(null);
            event.getPlayer().sendFormattedMessage(ChatColor.RED + "Fishing rods are not allowed");
            event.getPlayer().updateInventory();
        }
    }

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.FISHING_ROD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Rodless");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Fishing rods are not allowed");

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
