package net.badlion.shards.manager;

import net.badlion.shards.ShardPlugin;
import net.badlion.shards.grpc.ShardInstanceClient;
import net.badlion.shards.type.PlayerData;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerSyncManager {

    private final ShardPlugin plugin;

    private Map<UUID, Set<UUID>> playersTrackingPlayers = new HashMap<>();

    private Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    private Set<UUID> playersBeingSent = new HashSet<>();

    private Set<Location> recivedBlockLocations = new HashSet<>();

    public PlayerSyncManager(ShardPlugin plugin){
        this.plugin = plugin;
    }

    public Map<UUID, Set<UUID>> getPlayersTrackingPlayers() {
        return playersTrackingPlayers;
    }

    public boolean isTrackingPlayer(UUID playerId, UUID otherPlayerId) {
        if(!this.playersTrackingPlayers.containsKey(playerId)) {
            return false;
        }
        if(this.playersTrackingPlayers.get(playerId).contains(otherPlayerId)) {
            return true;
        }
        return false;
    }

    public void setTrackingPlayer(UUID playerId, UUID otherPlayerId) {
        if(!this.playersTrackingPlayers.containsKey(playerId)) {
            this.playersTrackingPlayers.put(playerId, new HashSet<>());
        }
        this.playersTrackingPlayers.get(playerId).add(otherPlayerId);
    }

    public void removeTrackingPlayer(UUID playerId) {
        for (Map.Entry<UUID, Set<UUID>> entry : this.playersTrackingPlayers.entrySet()) {
            if (entry.getValue().contains(playerId)) {
                entry.getValue().remove(playerId);
            }
        }
    }



    public void putPlayerData(UUID uuid, PlayerData playerData){
        this.playerDataMap.put(uuid, playerData);
    }

    public Map<UUID, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }

    public Set<UUID> getPlayersBeingSent() {
        return playersBeingSent;
    }

    public Set<Location> getRecivedBlockLocations() {
        return recivedBlockLocations;
    }
}
