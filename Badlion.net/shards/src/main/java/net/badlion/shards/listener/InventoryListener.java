package net.badlion.shards.listener;

import net.badlion.shards.ShardPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class InventoryListener implements Listener {

    private final ShardPlugin plugin;

    public InventoryListener(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(this.plugin.getPlayerSyncManager().getPlayersBeingSent().contains(player.getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(this.plugin.getPlayerSyncManager().getPlayersBeingSent().contains(player.getUniqueId())){
            event.setCancelled(true);
        }
    }

}
