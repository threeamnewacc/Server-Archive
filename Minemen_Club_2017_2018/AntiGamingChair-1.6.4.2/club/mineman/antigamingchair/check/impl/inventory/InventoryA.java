// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.check.impl.inventory;

import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;

public class InventoryA extends PacketCheck
{
    public InventoryA(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }
    
    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!this.playerData.isInventoryOpen() && packet instanceof PacketPlayInWindowClick && ((PacketPlayInWindowClick)packet).a() == 0) {
            this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "failed Inventory Check A (Experimental).");
        }
    }
}
