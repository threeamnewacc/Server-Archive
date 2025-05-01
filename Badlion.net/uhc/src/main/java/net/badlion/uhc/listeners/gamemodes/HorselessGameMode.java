package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.UnregistrableListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HorselessGameMode implements GameMode {

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.SADDLE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Horseless");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- You cannot tame horses");
        lore.add(ChatColor.AQUA + "- You cannot tame donkeys");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @EventHandler
    public void onPlayerInteractWithHorse(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.HORSE) {
            event.setCancelled(true);
        }
    }

    @Override
    public void unregister() {
        PlayerInteractEntityEvent.getHandlerList().unregister(this);
    }

}
