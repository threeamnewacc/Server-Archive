package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.PlayerRunnable;
import net.badlion.gberry.UnregistrableListener;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.GameStartEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NineSlotGameMode implements GameMode {

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.WORKBENCH);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Nine Slot");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- You can only use your hotbar for items (9 slots)");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "http://reddit.com/u/climbing";
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        // Add and remove if needed
        event.getPlayer().getInventory().addItem(event.getItem().getItemStack());
        event.getItem().remove();
        event.setCancelled(true);

        this.cleanInventory(event.getPlayer());
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Gberry.distributeTask(BadlionUHC.getInstance(), new PlayerRunnable() {
                    @Override
                    public void run(Player player) {
                        NineSlotGameMode.this.cleanInventory(player);
                    }
                });
            }

        }.runTaskTimer(BadlionUHC.getInstance(), 20, 20);
    }

    private void cleanInventory(Player player) {
        for (int i = 9; i < 36; i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                player.getInventory().setItem(i, null);
                player.getWorld().dropItemNaturally(player.getLocation().add(0, 2, 0), itemStack);
            }
        }
    }

    @Override
    public void unregister() {
        PlayerPickupItemEvent.getHandlerList().unregister(this);
        GameStartEvent.getHandlerList().unregister(this);
    }

}
