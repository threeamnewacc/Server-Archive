// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.manager;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.data.PlayerData;
import java.util.UUID;
import java.util.Map;

public class PlayerDataManager
{
    private final Map<UUID, PlayerData> playerDataMap;
    private final AntiGamingChair plugin;
    
    public void addPlayerData(final Player player) {
        this.playerDataMap.put(player.getUniqueId(), new PlayerData(this.plugin));
    }
    
    public void removePlayerData(final Player player) {
        this.playerDataMap.remove(player.getUniqueId());
    }
    
    public PlayerData getPlayerData(final Player player) {
        return this.playerDataMap.get(player.getUniqueId());
    }
    
    @ConstructorProperties({ "plugin" })
    public PlayerDataManager(final AntiGamingChair plugin) {
        this.playerDataMap = new HashMap<UUID, PlayerData>();
        this.plugin = plugin;
    }
}
