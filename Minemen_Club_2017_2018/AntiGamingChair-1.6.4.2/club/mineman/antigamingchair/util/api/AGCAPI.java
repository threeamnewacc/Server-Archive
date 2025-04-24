// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.util.api;

import club.mineman.antigamingchair.check.impl.range.RangeC;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.client.ClientType;
import club.mineman.antigamingchair.AntiGamingChair;
import org.bukkit.entity.Player;

public final class AGCAPI
{
    private AGCAPI() {
    }
    
    public static boolean isCheatBreaker(final Player player) {
        final PlayerData playerData = AntiGamingChair.getInstance().getPlayerDataManager().getPlayerData(player);
        return playerData != null && playerData.getClient() == ClientType.CHEAT_BREAKER;
    }
    
    public static int getPing(final Player player) {
        final PlayerData playerData = AntiGamingChair.getInstance().getPlayerDataManager().getPlayerData(player);
        if (playerData != null) {
            return (int)playerData.getPing();
        }
        return 0;
    }
    
    public static void spawnEntityAtCursor(final Player player) {
        final PlayerData playerData = AntiGamingChair.getInstance().getPlayerDataManager().getPlayerData(player);
        if (playerData != null) {
            final RangeC rangeC = playerData.getCheck(RangeC.class);
            if (rangeC == null) {
                return;
            }
            rangeC.spawnEntity(player);
        }
    }
    
    public static void destroyEntityAtCursor(final Player player) {
        final PlayerData playerData = AntiGamingChair.getInstance().getPlayerDataManager().getPlayerData(player);
        if (playerData != null) {
            final RangeC rangeC = playerData.getCheck(RangeC.class);
            if (rangeC == null) {
                return;
            }
            rangeC.destroyEntity(player);
        }
    }
}
