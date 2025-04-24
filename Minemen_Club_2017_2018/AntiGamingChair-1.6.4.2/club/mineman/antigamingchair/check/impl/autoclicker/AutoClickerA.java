// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.check.impl.autoclicker;

import club.mineman.antigamingchair.check.ICheck;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;

public class AutoClickerA extends PacketCheck
{
    private int swings;
    private int movements;
    private long lastSwing;
    
    public AutoClickerA(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }
    
    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInArmAnimation && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 110L && System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L && !this.playerData.isDigging() && !this.playerData.isPlacing()) {
            ++this.swings;
            this.lastSwing = System.currentTimeMillis();
        }
        else if (packet instanceof PacketPlayInFlying && this.swings > 0) {
            ++this.movements;
            if (this.movements == 20) {
                if (this.swings > 20 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "failed Auto Clicker Check A. " + this.swings + ".")) {
                    final int violations = this.playerData.getViolations(this, 60000L);
                    if (!this.playerData.isBanning() && violations > 3) {
                        this.ban(player, "Auto Clicker Check A");
                    }
                }
                if (System.currentTimeMillis() - this.lastSwing <= 350L) {
                    this.playerData.setCps(this.swings);
                }
                final int n = 0;
                this.movements = n;
                this.swings = n;
            }
        }
    }
}
