package me.devkevin.practice.arena.chunk.listener;

import me.devkevin.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 18/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ArenaPlayerChunkLimiter implements Listener {

    // The amount of entities that can be in a chunk at once.
    // Entity Types: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html

    private final Map<EntityType, Integer> limits = new HashMap<>();

    public ArenaPlayerChunkLimiter(Practice plugin) {
        for (EntityType type : EntityType.values()) {
            // If the entity cannot be spawned in the world, continue
            if (!type.isSpawnable()) {
                continue;
            }
            int limit = Integer.getInteger(type.getName(), -1);

            // If there is no limit for the Entity type, continue
            if (limit == -1) {
                continue;
            }

            // Settings the limit for the Entity type
            limits.put(type, limit);
            plugin.getLogger().info(type.name() + " Limit: " + limit);
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntitySpawn(EntitySpawnEvent event) {
        this.handleEntitySpawn(event, event.getEntityType(), event.getLocation().getChunk());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHangingSpawn(HangingPlaceEvent event) {
        this.handleEntitySpawn(event, event.getEntity().getType(), event.getBlock().getChunk());
    }

    /**
     * Handle entity spawning for the given {@link Cancellable} event with the given
     * {@link EntityType} at the given {@link Chunk}.
     *
     * @param event the spawn entity event
     * @param entityType the type of the entity spawned
     * @param chunk the chunk the entity was spawned
     */
    private void handleEntitySpawn(Cancellable event, EntityType entityType, Chunk chunk) {
        if (event.isCancelled()) { // If another plugin cancels this event, return
            return;
        }
        Integer limit = limits.get(entityType);
        if (limit == null) { // If the spawned entity type has no limit, return
            return;
        }
        // Loop through all the entities in the given chunk and count the amount
        // of entities with the same entity type as the one provided
        int entities = 0;
        for (Entity chunkEntity : chunk.getEntities()) {
            if (chunkEntity.getType() == entityType) {
                entities++;
            }
        }
        // If the limit has been reached for this chunk, cancel the event
        if (entities > limit) {
            event.setCancelled(true);
        }
    }
}
