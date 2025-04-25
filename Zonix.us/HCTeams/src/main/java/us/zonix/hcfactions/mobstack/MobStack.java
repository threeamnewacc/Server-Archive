package us.zonix.hcfactions.mobstack;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import us.zonix.hcfactions.FactionsPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import us.zonix.hcfactions.FactionsPlugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class MobStack {

    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static Set<MobStack> stacks = new HashSet<>();
    private static final int SEARCH_SIZE = 15;

    @Getter @Setter private LivingEntity entity;
    @Getter @Setter private int count;

    public MobStack(LivingEntity entity, int count) {
        this.entity = entity;
        this.count = count;

        stacks.add(this);

        update();
    }

    public MobStack(LivingEntity entity) {
        this(entity, 1);
    }

    public void setCount(int count) {
        if (count <= 0) {
            stacks.remove(this);
            return;
        }

        if (this.count > count) {
            entity = (LivingEntity) entity.getLocation().getWorld().spawnEntity(entity.getLocation(), entity.getType());
        }

        this.count = count;

        update();
    }

    private void update() {
        if (count == 1) {
            entity.setCustomName("");
        } else {
            entity.setCustomName(main.getLanguageConfig().getString("MOB_STACKING.DISPLAY_NAME").replace("%COUNT%", count + ""));
        }

    }

    public static MobStack getByNearby(LivingEntity entity) {
        for (Entity nearby : entity.getNearbyEntities(SEARCH_SIZE, SEARCH_SIZE, SEARCH_SIZE)) {
            if (nearby instanceof LivingEntity) {
                if (nearby.getEntityId() == entity.getEntityId()) continue;
                if (nearby.getType() != entity.getType()) continue;

                MobStack stack = getByEntity((LivingEntity) nearby);
                if (stack != null && stack.getCount() < main.getMainConfig().getInt("MOB_STACKING.MAX_COUNT")) {
                    return stack;
                }
            }
        }

        return null;
    }

    public static void stack() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof LivingEntity  && MobStack.isValid(entity.getType())) {
                    if (entity.getType() == EntityType.PLAYER) continue;
                    if (getByEntity((LivingEntity) entity) != null) continue;

                    MobStack stack = MobStack.getByNearby((LivingEntity) entity);
                    if (stack != null && stack.getCount() < main.getMainConfig().getInt("MOB_STACKING.MAX_COUNT")) {
                        entity.remove();
                        stack.setCount(stack.getCount() + 1);
                    } else {
                        removeIntelligence((LivingEntity) entity);
                        new MobStack((LivingEntity) entity);
                    }
                }
            }
        }

        for (MobStack stack : stacks) {
            if (!stack.getEntity().isValid()) {
                stack.setEntity((LivingEntity) stack.getEntity().getWorld().spawnEntity(stack.getEntity().getLocation(), stack.getEntity().getType()));
                removeIntelligence(stack.getEntity());
                stack.setCount(stack.getCount());
            }

        }

    }

    public static void removeIntelligence(LivingEntity entity) {
        EntityInsentient entityLiving;
        try {
            entityLiving = ((CraftCreature) entity).getHandle();
        } catch (Exception ex) {
            return;
        }

        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            Field goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
            Field targetSelector = EntityInsentient.class.getDeclaredField("targetSelector");

            goalSelector.setAccessible(true);
            targetSelector.setAccessible(true);
            bField.setAccessible(true);
            cField.setAccessible(true);

            bField.set(goalSelector.get(entityLiving), new UnsafeList<PathfinderGoalSelector>());
            bField.set(targetSelector.get(entityLiving), new UnsafeList<PathfinderGoalSelector>());
            cField.set(goalSelector.get(entityLiving), new UnsafeList<PathfinderGoalSelector>());
            cField.set(targetSelector.get(entityLiving), new UnsafeList<PathfinderGoalSelector>());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void unStack() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof LivingEntity && MobStack.isValid(entity.getType())) {
                    MobStack stack = MobStack.getByEntity((LivingEntity) entity);
                    if (stack != null) {

                        for (int i = 1; i < stack.getCount(); i++) {
                            stack.getEntity().getWorld().spawnEntity(stack.getEntity().getLocation(), stack.getEntity().getType());
                        }

                        stack.getEntity().remove();

                        MobStack.getStacks().remove(stack);
                    }
                }
            }
        }
    }

    public static boolean isValid(EntityType type) {
        return !main.getMainConfig().getString("MOB_STACKING.EXCLUDED_MOBS").contains(type.name());
    }

    public static MobStack getByEntity(LivingEntity entity) {
        for (MobStack stack : stacks) {
            if (stack.getEntity().getEntityId() == entity.getEntityId()) {
                return stack;
            }
        }
        return null;
    }

    public static Set<MobStack> getStacks() {
        return stacks;
    }
}
