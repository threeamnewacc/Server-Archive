// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.commands.subcommands;

import java.beans.ConstructorProperties;
import club.mineman.core.util.finalutil.CC;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.AntiGamingChair;

public class AlertsCommand implements SubCommand
{
    private final AntiGamingChair plugin;
    
    @Override
    public void execute(final Player player, final Player target, final String[] args) {
        this.plugin.getAlertsManager().toggleAlerts(player);
        player.sendMessage(this.plugin.getAlertsManager().hasAlertsToggled(player) ? (CC.GREEN + "Subscribed to AGC alerts.") : (CC.RED + "Unsubscribed from AGC alerts."));
    }
    
    @ConstructorProperties({ "plugin" })
    public AlertsCommand(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }
}
