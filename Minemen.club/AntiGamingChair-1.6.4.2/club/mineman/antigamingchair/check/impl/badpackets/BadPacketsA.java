// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.check.impl.badpackets;

import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;

public class BadPacketsA extends PacketCheck
{
    private int streak;
    
    public BadPacketsA(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }
    
    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (player.getVehicle() == null && packet instanceof PacketPlayInFlying) {
            if (((PacketPlayInFlying)packet).g()) {
                this.streak = 0;
            }
            else if (++this.streak > 20 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "failed Bad Packets Check A.") && !this.playerData.isBanning()) {
                this.ban(player, "Bad Packets Check A");
            }
        }
    }
}
