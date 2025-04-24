// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.listeners;

import org.bukkit.event.EventHandler;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import club.minemen.practice.Practice;
import org.bukkit.event.Listener;

public class InventoryListener implements Listener
{
    private final Practice plugin;
    
    public InventoryListener() {
        this.plugin = Practice.getInstance();
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        if (!player.getGameMode().equals((Object)GameMode.CREATIVE)) {
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData.getPlayerState() == PlayerState.SPAWN) {
                event.setCancelled(true);
            }
        }
    }
}
