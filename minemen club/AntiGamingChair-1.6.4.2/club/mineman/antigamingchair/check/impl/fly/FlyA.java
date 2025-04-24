// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.check.impl.fly;

import java.util.Iterator;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import club.mineman.antigamingchair.check.ICheck;
import club.mineman.paper.event.PlayerUpdatePositionEvent;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PositionCheck;

public class FlyA extends PositionCheck
{
    public FlyA(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }
    
    @Override
    public void handleCheck(final Player player, final PlayerUpdatePositionEvent event) {
        int vl = (int)this.playerData.getCheckVl(this);
        if (!player.getAllowFlight() && !player.isInsideVehicle() && !this.playerData.isInLiquid() && !this.playerData.isOnGround() && this.playerData.getVelocityV() == 0 && event.getTo().getY() > event.getFrom().getY()) {
            final double distance = event.getTo().getY() - this.playerData.getLastGroundY();
            double limit = 2.0;
            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                for (final PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType().equals((Object)PotionEffectType.JUMP)) {
                        final int level = effect.getAmplifier() + 1;
                        limit += Math.pow(level + 4.2, 2.0) / 16.0;
                        break;
                    }
                }
            }
            if (distance > limit) {
                if (++vl >= 10 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "failed Fly Check A. VL " + vl + ".")) {
                    final int violations = this.playerData.getViolations(this, 60000L);
                    if (!this.playerData.isBanning() && violations > 8) {
                        this.ban(player, "Fly Check A");
                    }
                }
            }
            else {
                vl = 0;
            }
        }
        else {
            vl = 0;
        }
        this.playerData.setCheckVl(vl, this);
    }
}
