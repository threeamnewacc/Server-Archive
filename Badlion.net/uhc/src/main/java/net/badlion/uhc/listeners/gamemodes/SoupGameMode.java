package net.badlion.uhc.listeners.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SoupGameMode implements GameMode {

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.MUSHROOM_SOUP);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Soup");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Mushroom Soup heals you for 2 hearts");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @EventHandler
    public void onPlayerDrinkSoupEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.MUSHROOM_SOUP) {
                if (!player.isDead()) {
                    if (player.getHealth() < 20) {
                        if (player.getHealth() + 4 < 20) {
                            player.setHealth(player.getHealth() + 4);
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

    @Override
    public void unregister() {
        PlayerInteractEvent.getHandlerList().unregister(this);
    }

}
