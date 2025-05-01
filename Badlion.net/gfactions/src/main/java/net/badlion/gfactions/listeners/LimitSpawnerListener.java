package net.badlion.gfactions.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.*;

public class LimitSpawnerListener implements Listener {
	
	public static int LIMIT_SPAWNER = 50;
	private Map<Chunk, Integer> mobSpawnerMap;
	private Map<Entity, Chunk> entityChunkMap = new HashMap<>();
	private HashSet<EntityType> entitiesFromSpawner = new HashSet<>();
	
	public LimitSpawnerListener() {
		this.mobSpawnerMap = new HashMap<Chunk, Integer>();
		this.entitiesFromSpawner.add(EntityType.ZOMBIE);
		this.entitiesFromSpawner.add(EntityType.SPIDER);
		this.entitiesFromSpawner.add(EntityType.CAVE_SPIDER);
		this.entitiesFromSpawner.add(EntityType.SKELETON);
		this.entitiesFromSpawner.add(EntityType.CHICKEN);
		this.entitiesFromSpawner.add(EntityType.SHEEP);
		this.entitiesFromSpawner.add(EntityType.PIG);
		this.entitiesFromSpawner.add(EntityType.COW);
		this.entitiesFromSpawner.add(EntityType.WOLF);
		this.entitiesFromSpawner.add(EntityType.SLIME);
		this.entitiesFromSpawner.add(EntityType.BLAZE);
		this.entitiesFromSpawner.add(EntityType.ENDERMAN);
		this.entitiesFromSpawner.add(EntityType.CREEPER);
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		// No bats or ocelots
		EntityType type = event.getEntityType();
		if (type == EntityType.BAT || type == EntityType.OCELOT) {
			event.setCancelled(true);
			return;
		}
		
		if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BREEDING || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG
					|| event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DISPENSE_EGG) {
			List<Entity> entities = event.getEntity().getNearbyEntities(20, 20, 20);
			int count = 0;
			for (Entity entity : entities) {
				if (entity.getType() == EntityType.COW || entity.getType() == EntityType.CHICKEN
						|| entity.getType() == EntityType.PIG || entity.getType() == EntityType.SHEEP)
					count++;
			}
			
			// Too many other animals around...cancel
			if (count >= LIMIT_SPAWNER) {
				event.setCancelled(true);
			}
		} else if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
			// TODO: If this becomes to inefficient we need to just check 32 blocks Y down
			List<Entity> entities = event.getEntity().getNearbyEntities(20, 32, 20);
			int count = 0;
			for (Entity entity : entities) {
				if (this.entitiesFromSpawner.contains(entity.getType())) {
					// Too many other animals around...cancel, moved this logic up to early escape
					if (++count >= LIMIT_SPAWNER) {
						event.setCancelled(true);
						return;
					}
				}
			}

			Location location = event.getLocation();
			Chunk chunk = location.getChunk();
			if (this.mobSpawnerMap.containsKey(chunk)) {
				// If there are already too many from this spawner stop the event
				if (this.mobSpawnerMap.get(chunk) >= LIMIT_SPAWNER) {
					event.setCancelled(true);
					return;
				}
				
				// One more for this spawner
				this.mobSpawnerMap.put(chunk, this.mobSpawnerMap.get(chunk) + 1);
			} else {
				// First to spawn from this spawner
				this.mobSpawnerMap.put(chunk, 1);
			}

			// Store it cuz we made it this far
			this.entityChunkMap.put(event.getEntity(), chunk);
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (this.entitiesFromSpawner.contains(entity.getType()) )	{
			Chunk chunk = this.entityChunkMap.get(event.getEntity());
			if (chunk != null) {
				Integer mobs = this.mobSpawnerMap.get(chunk);
				if (mobs != null) {
					if (mobs == 0) {
						this.mobSpawnerMap.remove(chunk);
					} else {
						this.mobSpawnerMap.put(chunk, this.mobSpawnerMap.get(chunk) - 1);
					}
				}
			}
			this.entityChunkMap.remove(entity);
		}
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		this.mobSpawnerMap.remove(event.getChunk());
		Entity [] entities = event.getChunk().getEntities();
		for (Entity entity : entities) {
			this.entityChunkMap.remove(entity);
		}
	}

}
