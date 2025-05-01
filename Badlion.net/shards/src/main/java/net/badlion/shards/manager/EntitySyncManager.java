package net.badlion.shards.manager;

import net.badlion.shards.ShardPlugin;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EntitySyncManager {

    private final ShardPlugin plugin;

    private Map<UUID, Set<Integer>> playersTrackingEntities = new HashMap<>();

    private Map<UUID, Entity> entitiesWaitingForPassengerPlayer = new HashMap<>();
    private Map<Integer, Entity> entitiesWaitingForPassengerEntity = new HashMap<>();

    private Set<Integer> transferedEntityIds = new HashSet<>();

    public EntitySyncManager(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    public Map<Integer, Entity> getEntitiesWaitingForPassengerEntity() {
        return entitiesWaitingForPassengerEntity;
    }

    public Map<UUID, Entity> getEntitiesWaitingForPassengerPlayer() {
        return entitiesWaitingForPassengerPlayer;
    }

    public Set<Integer> getTransferedEntityIds() {
        return transferedEntityIds;
    }

    public boolean isTrackingEntity(UUID playerId, Integer entityId) {
        if(!playersTrackingEntities.containsKey(playerId)) {
            return false;
        }
        if(playersTrackingEntities.get(playerId).contains(entityId)) {
            return true;
        }
        return false;
    }

    public void setTrackingEntity(UUID playerId, Integer entityId) {
        if(!playersTrackingEntities.containsKey(playerId)) {
            playersTrackingEntities.put(playerId, new HashSet<>());
        }
        playersTrackingEntities.get(playerId).add(entityId);
    }

    public void removeEntity(Integer entityId) {
        for (Map.Entry<UUID, Set<Integer>> entry : this.playersTrackingEntities.entrySet()) {
            if (entry.getValue().contains(entityId)) {
               entry.getValue().remove(entityId);
            }
        }
    }

    public Map<UUID, Set<Integer>> getPlayersTrackingEntities() {
        return playersTrackingEntities;
    }
}
