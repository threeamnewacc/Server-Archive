package net.badlion.skywars.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.UnregistrableListener;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.skywars.SkyWars;
import net.badlion.skywars.inventories.KitSelectorInventory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;
import java.util.UUID;

public class SkyWarsPreGameListener implements Listener, UnregistrableListener {

    private Iterator<Location> it;
    private ItemStack bookItem = new ItemStack(Material.BOOK);

    public SkyWarsPreGameListener() {
        if (!Gberry.serverName.contains("test")) {
            this.it = SkyWars.getInstance().getCurrentGame().getWorld().getSpawnLocations().iterator();
        }

        ItemMeta meta = this.bookItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Select a kit");
        this.bookItem.setItemMeta(meta);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Gberry.serverName.contains("test")) {
            MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
            mpgPlayer.setUsername(event.getPlayer().getName());

            // If game has not started and we are a player
            if (MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.PRE_GAME
                        && mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
                // Set their inventory
                event.getPlayer().getInventory().clear();
                event.getPlayer().getInventory().addItem(this.bookItem);
                event.getPlayer().updateInventory();

                // Teleport to teammate first
                for (UUID uuid : mpgPlayer.getTeam().getUUIDs()) {
                    Player pl = SkyWars.getInstance().getServer().getPlayer(uuid);
                    if (pl != null) {
                        if (!pl.getWorld().getName().equals("world")) {
                            event.getPlayer().teleport(pl);
                            return;
                        }
                    }
                }

                // Get a fresh spawn
                if (it.hasNext()) {
                    event.getPlayer().teleport(it.next());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSelectKitLoad(PlayerInteractEvent event) {
        event.setCancelled(true);

        if (event.getItem() != null) {
            if (event.getItem().getType() == Material.BOOK) {
                KitSelectorInventory.openKitInventory(event.getPlayer());
            }
        }
    }

    public void unregister() {
        PlayerJoinEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
    }

    public ItemStack getBookItem() {
        return bookItem;
    }

}
