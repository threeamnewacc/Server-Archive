// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.check.impl.range;

import java.util.Iterator;
import club.mineman.antigamingchair.check.ICheck;
import org.bukkit.entity.Player;
import java.util.LinkedList;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.AntiGamingChair;
import java.util.Deque;
import club.mineman.antigamingchair.check.AbstractCheck;

public class KillAuraD extends AbstractCheck<double[]>
{
    private final Deque<Double> distances;
    
    public KillAuraD(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData, double[].class);
        this.distances = new LinkedList<Double>();
    }
    
    @Override
    public void handleCheck(final Player player, final double[] array) {
        this.distances.addLast(array[0] - array[1]);
        if (this.distances.size() == 30) {
            int n = 0;
            double n2 = 0.0;
            for (final double doubleValue : this.distances) {
                if (doubleValue < -1.0) {
                    continue;
                }
                ++n;
                n2 += doubleValue;
            }
            final double n3 = n2 / n;
            double checkVl = this.playerData.getCheckVl(this);
            if (n3 > -0.2 && n > 10) {
                this.plugin.getAlertsManager().forceAlert(String.format("failed KillAura Check D (Development). %.3f. %.2f. VL %.2f.", n3, -0.2, checkVl), player);
            }
            else {
                checkVl -= 0.3;
            }
            this.playerData.setCheckVl(checkVl, this);
            this.distances.clear();
        }
    }
}
