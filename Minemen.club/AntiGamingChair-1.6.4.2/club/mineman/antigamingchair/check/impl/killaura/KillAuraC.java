// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.check.impl.killaura;

import net.minecraft.server.v1_8_R3.Entity;
import club.mineman.antigamingchair.location.CustomLocation;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import club.mineman.antigamingchair.check.ICheck;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import club.mineman.antigamingchair.util.MathUtil;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.AntiGamingChair;
import java.util.UUID;
import club.mineman.antigamingchair.check.checks.PacketCheck;

public class KillAuraC extends PacketCheck
{
    private UUID lastTarget;
    
    public KillAuraC(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }
    
    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInFlying && !this.playerData.isAllowTeleport()) {
            final PacketPlayInFlying packetPlayInFlying = (PacketPlayInFlying)packet;
            if (packetPlayInFlying.h() && packetPlayInFlying.g() && this.lastTarget != null) {
                final CustomLocation lastMovePacket = this.playerData.getLastMovePacket();
                final CustomLocation lastPlayerPacket = this.playerData.getLastPlayerPacket(this.lastTarget, MathUtil.pingFormula(this.playerData.getPing()) + 2);
                if (lastPlayerPacket == null) {
                    return;
                }
                final double distanceBetweenAngles = MathUtil.getDistanceBetweenAngles(lastMovePacket.getYaw(), MathUtil.getRotationFromPosition(this.playerData.getLastMovePacket(), lastPlayerPacket)[1]);
                final double distanceBetweenAngles2 = MathUtil.getDistanceBetweenAngles(lastMovePacket.getPitch(), MathUtil.getRotationFromPosition(this.playerData.getLastMovePacket(), lastPlayerPacket)[0]);
                if ((distanceBetweenAngles < 0.0 || distanceBetweenAngles2 > 0.0) && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, String.format("failed Kill Aura Check C. BY %.2f. BP %.2f.", distanceBetweenAngles, distanceBetweenAngles2))) {
                    final int violations = this.playerData.getViolations(this, 60000L);
                    if (!this.playerData.isBanning() && violations > 6) {
                        this.ban(player, "Kill Aura Check C");
                    }
                }
            }
        }
        else if (packet instanceof PacketPlayInUseEntity) {
            final PacketPlayInUseEntity packetPlayInUseEntity = (PacketPlayInUseEntity)packet;
            if (packetPlayInUseEntity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                final Entity a = packetPlayInUseEntity.a(((CraftPlayer)player).getHandle().getWorld());
                if (a instanceof EntityPlayer) {
                    this.lastTarget = ((Player)a.getBukkitEntity()).getUniqueId();
                }
            }
        }
    }
}
