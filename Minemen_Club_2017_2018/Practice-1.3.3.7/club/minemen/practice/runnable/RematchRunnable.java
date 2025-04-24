// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.runnable;

import java.beans.ConstructorProperties;
import club.minemen.practice.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import club.minemen.practice.player.PlayerState;
import java.util.UUID;
import club.minemen.practice.Practice;

public class RematchRunnable implements Runnable
{
    private final Practice plugin;
    private final UUID playerUUID;
    
    @Override
    public void run() {
        final Player player = this.plugin.getServer().getPlayer(this.playerUUID);
        if (player != null) {
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData != null && playerData.getPlayerState() == PlayerState.SPAWN && this.plugin.getMatchManager().isRematching(player.getUniqueId()) && this.plugin.getPartyManager().getParty(player.getUniqueId()) == null) {
                player.getInventory().setItem(3, (ItemStack)null);
                player.getInventory().setItem(6, (ItemStack)null);
                player.updateInventory();
                playerData.setRematchID(-1);
            }
            this.plugin.getMatchManager().removeRematch(this.playerUUID);
        }
    }
    
    @ConstructorProperties({ "playerUUID" })
    public RematchRunnable(final UUID playerUUID) {
        this.plugin = Practice.getInstance();
        this.playerUUID = playerUUID;
    }
}
