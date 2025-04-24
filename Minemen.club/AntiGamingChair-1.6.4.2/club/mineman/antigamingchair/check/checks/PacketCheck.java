// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.check.checks;

import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.AntiGamingChair;
import net.minecraft.server.v1_8_R3.Packet;
import club.mineman.antigamingchair.check.AbstractCheck;

public abstract class PacketCheck extends AbstractCheck<Packet>
{
    public PacketCheck(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData, Packet.class);
    }
}
