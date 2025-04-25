package us.zonix.hcfactions.mobstack;

import us.zonix.hcfactions.FactionsPlugin;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import us.zonix.hcfactions.FactionsPlugin;

import java.util.List;
import java.util.Random;

public class MobStackListeners implements Listener {

    private static final List<String> NO_AI_TYPES = FactionsPlugin.getInstance().getMainConfig().getStringList("MOB_STACKING.NO_AI");

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        MobStack stack = MobStack.getByNearby(entity);

        if (NO_AI_TYPES.contains(entity.getType().name())) {
            if (!(entity.getLocation().getWorld().getEnvironment().equals(World.Environment.THE_END))) {
                MobStack.removeIntelligence(entity);
            }
        }

        if (entity.getType() == EntityType.SPIDER && (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)) {
            event.setCancelled(true);
            return;
        }


        if (entity.getType() == EntityType.CREEPER && (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)) {
            if (new Random().nextInt(10) != 1) {
                event.setCancelled(true);
            }
        }

        if (entity instanceof Monster && entity.getType() != EntityType.CREEPER && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
        }

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            return;
        }

        if (MobStack.isValid(entity.getType())) {
            if (stack == null || stack.getCount() >= FactionsPlugin.getInstance().getMainConfig().getInt("MOB_STACKING.MAX_COUNT")) {
                new MobStack(entity);
            } else {
                stack.setCount(stack.getCount() + 1);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        MobStack stack = MobStack.getByEntity(entity);

        if (stack != null) {
            stack.setCount(stack.getCount() - 1);
        }
    }

    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof LivingEntity  && MobStack.isValid(entity.getType())) {
                MobStack stack = MobStack.getByEntity((LivingEntity) entity);
                if (stack != null) {

                    for (int i = 0; i < stack.getCount(); i++) {
                        stack.getEntity().getWorld().spawnEntity(stack.getEntity().getLocation(), stack.getEntity().getType());
                    }

                    stack.getEntity().remove();

                    MobStack.getStacks().remove(stack);
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof LivingEntity && !(entity instanceof Player) && MobStack.isValid(entity.getType())) {
                MobStack stack = MobStack.getByNearby((LivingEntity) entity);

                if (NO_AI_TYPES.contains(entity.getType().name())) {
                    if (!(entity.getLocation().getWorld().getEnvironment().equals(World.Environment.THE_END))) {
                        MobStack.removeIntelligence((LivingEntity) entity);
                    }
                }

                if (stack != null && stack.getCount() < FactionsPlugin.getInstance().getMainConfig().getInt("MOB_STACKING.MAX_COUNT")) {
                    entity.remove();
                    stack.setCount(stack.getCount() + 1);
                } else {
                    new MobStack((LivingEntity) entity);
                }
            }
        }
    }

}
