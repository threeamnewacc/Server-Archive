// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.handler;

import club.minemen.practice.match.Match;
import club.minemen.practice.player.PlayerData;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import club.minemen.practice.match.MatchState;
import club.minemen.practice.player.PlayerState;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import club.minemen.practice.Practice;
import club.minemen.spigot.handler.MovementHandler;

public class CustomMovementHandler implements MovementHandler
{
    private final Practice plugin;
    
    public CustomMovementHandler() {
        this.plugin = Practice.getInstance();
    }
    
    public void handleUpdateLocation(final Player player, final Location to, final Location from, final PacketPlayInFlying packetPlayInFlying) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            return;
        }
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if ((match.getKit().isSpleef() || match.getKit().isSumo()) && (to.getX() != from.getX() || to.getZ() != from.getZ()) && match.getMatchState() == MatchState.STARTING) {
                player.teleport(from);
                ((CraftPlayer)player).getHandle().playerConnection.checkMovement = false;
            }
        }
    }
    
    public void handleUpdateRotation(final Player player, final Location location, final Location location1, final PacketPlayInFlying packetPlayInFlying) {
    }
}
